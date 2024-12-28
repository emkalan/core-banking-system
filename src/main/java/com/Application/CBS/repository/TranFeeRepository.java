package com.Application.CBS.repository;

import com.Application.CBS.model.TranFee;
import com.Application.CBS.model.TranType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TranFeeRepository extends JpaRepository<TranFee, Integer> {
    List<TranFee> findByTranType(TranType tranType);
}
