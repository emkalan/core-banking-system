package com.Application.CBS.controller;

import com.Application.CBS.dto.CustomerCreateRequest;
import com.Application.CBS.dto.CustomerEditRequest;
import com.Application.CBS.dto.GenericResponse;
import com.Application.CBS.service.CustomerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequestMapping("/customer")
public class CustomerController {

    @Autowired
    CustomerService customerService;
    @ResponseBody
    @PostMapping("/v1/create-customer")
    public ResponseEntity<GenericResponse> checkDarkMode(@RequestBody CustomerCreateRequest request) {
        log.info("Customer create request: {}",request);
        return ResponseEntity.ok(customerService.createCustomer(request));
    }
    @ResponseBody
    @PostMapping("/v1/edit-customer")
    public ResponseEntity<GenericResponse> editCustomer(@RequestBody CustomerEditRequest request) {
        log.info("Customer edit request {}",request);
        return ResponseEntity.ok(customerService.editCustomer(request));
    }
}