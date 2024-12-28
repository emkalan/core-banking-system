package com.Application.CBS.service;

import com.Application.CBS.dto.GenericResponse;
import com.Application.CBS.dto.TransactionGenericRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class DepositWithdrawalService extends BaseTransactionService{
    GenericResponse response= new GenericResponse();

    @Override
    public GenericResponse processTranRequest(TransactionGenericRequest request) {
        GenericResponse response= new GenericResponse();
        return requestTransaction(request);
    }
}
