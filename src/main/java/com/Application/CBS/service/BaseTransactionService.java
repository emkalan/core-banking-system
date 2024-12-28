package com.Application.CBS.service;

import com.Application.CBS.dto.GenericResponse;
import com.Application.CBS.dto.TransactionGenericRequest;
import com.Application.CBS.model.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class BaseTransactionService {

    @Autowired
    EntityFinderService entityFinderService;

    public GenericResponse requestTransaction(TransactionGenericRequest request) {
        GenericResponse response = new GenericResponse();
        try {
            TranType tranType = entityFinderService.findTranTypeByTypeCode(request.getTranTypeCode());

            if(null != tranType.getTypeCode()) {
                Account senderAccount = entityFinderService.findByAccountNumber(request.getSenderAccountNumber());
                Account receiverAccount = entityFinderService.findByAccountNumber(request.getReceiverAccountNumber());

                if(null == senderAccount.getAccountName() || null == receiverAccount.getAccountNumber()) {
                    response.setResponseCode(HttpStatus.NOT_FOUND.value());
                    response.setResponseMessage("Invalid account number provided!");
                    return response;
                }

                String tranxId = generateTranId();

                //Calculation of fees and distributing to distributors

                if(tranType.isFeeApplicable()) {
                    List<TranFee> tranFees = entityFinderService.findAllTranFee(tranType);
                    if (tranFees.isEmpty()) {
                        response.setResponseCode(HttpStatus.NOT_FOUND.value());
                        response.setResponseMessage("Couldn't find transaction fee!");
                        return response;
                    }
                    //Calculating Total fees from different fee charges:

                    double calculatedFeeCharge = 0.0;

                    if(tranFees.size()>1) {
                        for(TranFee tranFee: tranFees) {
                            if ((tranFee.isFeeAmountFixed())) {
                                calculatedFeeCharge = tranFee.getFeeFixedAmount();
                            }

                            if(tranFee.isFeeAmountPercentage()) {
                                calculatedFeeCharge = ((tranFee.getFeePercentageAmount())/100)*(request.getAmount());
                                if (tranFee.isMaximumApplicable()) {
                                    double maxCharge = tranFee.getFeeMaximumAmount();
                                    if(calculatedFeeCharge > maxCharge) {
                                        calculatedFeeCharge = maxCharge;
                                    }
                                }
                            }
                            calculatedFeeCharge++;
                        }
                        double senderCurrentBalance = senderAccount.getBalance();
                        if(request.getAmount()+calculatedFeeCharge>senderCurrentBalance) {
                            response.setResponseCode(HttpStatus.BAD_REQUEST.value());
                            response.setResponseMessage("Not enough balance for this transaction.");
                            return response;
                        }
                    }

                    //Transaction process for Fee

                    double feeCharge = 0.0;

                    for(TranFee tranFee: tranFees) {
                        if ((tranFee.isFeeAmountFixed())) {
                            feeCharge = tranFee.getFeeFixedAmount();
                        }

                        if(tranFee.isFeeAmountPercentage()) {
                            feeCharge = ((tranFee.getFeePercentageAmount())/100)*(request.getAmount());
                            if (tranFee.isMaximumApplicable()) {
                                double maxCharge = tranFee.getFeeMaximumAmount();
                                if(feeCharge > maxCharge) {
                                    feeCharge = maxCharge;
                                }
                            }
                        }
                        //Checking Wallet Balance before fee transactions: Total tran amount

                        double totalTranAmount = feeCharge + request.getAmount();
                        double currentBalance = senderAccount.getBalance();

                        if(totalTranAmount>currentBalance) {
                            response.setResponseCode(HttpStatus.BAD_REQUEST.value());
                            response.setResponseMessage("Not Enough Balance for this transaction!");
                            return response;
                        }

                        List<TranFeeDistribution> tranFeeDistributions = entityFinderService.findAllDistributions(tranFee);

                        if(tranFeeDistributions.isEmpty()) {
                            response.setResponseCode(HttpStatus.NOT_FOUND.value());
                            response.setResponseMessage("Couldn't find transaction distributions!");
                            return response;
                        }

                        double totalPercentage =0.0;
                        for(TranFeeDistribution tranFeeDistribution:tranFeeDistributions) {
                            totalPercentage += tranFeeDistribution.getFeePercentage();
                        }
                        if (totalPercentage != 100.0) {
                            response.setResponseCode(HttpStatus.BAD_REQUEST.value());
                            response.setResponseMessage("Improper transaction distribution is configured!");
                            return response;
                        }
                        //Initiate actual fee transactions:


                        for(TranFeeDistribution tranFeeDistribution:tranFeeDistributions) {
                            Transaction transaction = new Transaction();
                            Account tranDistributionAccount = entityFinderService.findByAccountNumber(tranFeeDistribution.getDistributorAccountNumber());

                            double distributedFee = feeCharge*(tranFeeDistribution.getFeePercentage()/100);
                            senderAccount.setBalance(senderAccount.getBalance()-distributedFee);
                            entityFinderService.accountRepository.save(senderAccount);

                            transaction.setCreatedTime(LocalDateTime.now());
                            transaction.setTransactionId(tranxId);
                            transaction.setTransactionMode(Transaction.TransactionType.WITHDRAW);
                            transaction.setTransactionStatus(Transaction.TransactionStatus.COMPLETED);
                            transaction.setAmount(distributedFee);
                            transaction.setActionType(Transaction.ActionType.DEBIT);
                            transaction.setSenderAccountNumber(request.getSenderAccountNumber());
                            transaction.setReceiverAccountNumber(tranFeeDistribution.getDistributorAccountNumber());
                            transaction.setRemarks(request.getRemarks());
                            transaction.setCompletedTime(LocalDateTime.now());
                            entityFinderService.transactionRepository.save(transaction);

                            tranDistributionAccount.setBalance(tranDistributionAccount.getBalance() + distributedFee);
                            entityFinderService.accountRepository.save(tranDistributionAccount);
                        }
                    }
                }

                receiverAccount.setBalance(receiverAccount.getBalance()+ request.getAmount());
                entityFinderService.accountRepository.save(receiverAccount);
                senderAccount.setBalance(senderAccount.getBalance() - request.getAmount());
                entityFinderService.accountRepository.save(senderAccount);

                Transaction transaction = new Transaction();

                transaction.setCreatedTime(LocalDateTime.now());
                transaction.setTransactionId(tranxId);
                transaction.setTransactionMode(Transaction.TransactionType.valueOf(request.getTransactionMode()));
                transaction.setTransactionStatus(Transaction.TransactionStatus.COMPLETED);
                transaction.setAmount(request.getAmount());
                transaction.setActionType(Transaction.ActionType.valueOf(request.getActionType()));
                transaction.setSenderAccountNumber(request.getSenderAccountNumber());
                transaction.setReceiverAccountNumber(request.getReceiverAccountNumber());
                transaction.setRemarks(request.getRemarks());
                transaction.setCompletedTime(LocalDateTime.now());
                entityFinderService.transactionRepository.save(transaction);

                response.setResponseCode(HttpStatus.OK.value());
                response.setResponseMessage("Transaction successful. ID: " + tranxId );

            } else {
                response.setResponseCode(HttpStatus.NOT_FOUND.value());
                response.setResponseMessage("Couldn't find transaction type!");
            }
            return response;

        } catch (Exception e) {
            //StringWriter sw = new StringWriter();
            //e.printStackTrace(new PrintWriter(sw));
            //String stackTrace = sw.toString();
            response.setResponseCode(HttpStatus.UNPROCESSABLE_ENTITY.value());
            response.setResponseMessage("Failed to complete the transaction.");
            return  response;
        }
    }

    private String generateTranId() {
        final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        final int RANDOM_LENGTH = 5;
        SecureRandom random = new SecureRandom();
        StringBuilder id = new StringBuilder();
        String timestamp = String.valueOf(Instant.now().toEpochMilli()).substring(2,13);
        id.append(timestamp);

        // Append random characters (5 characters)
        for (int i = 0; i < RANDOM_LENGTH; i++) {
            int index = random.nextInt(CHARACTERS.length());
            id.append(CHARACTERS.charAt(index));
        }
        return id.toString();
    }

    public GenericResponse processTranRequest(TransactionGenericRequest request) {
        return null;
    }
}
