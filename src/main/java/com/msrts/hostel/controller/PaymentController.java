package com.msrts.hostel.controller;

import com.msrts.hostel.exception.ErrorConstants;
import com.msrts.hostel.model.Error;
import com.msrts.hostel.model.PaymentDto;
import com.msrts.hostel.model.Response;
import com.msrts.hostel.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/payment")
@RequiredArgsConstructor
@CrossOrigin(exposedHeaders="Access-Control-Allow-Origin")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @GetMapping(path = "/payments-by-tenant-id", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<List<PaymentDto>> getAllPaymentsByTenantId(@RequestParam(required = true) Long tenantId,
                                                           @RequestParam(defaultValue = "0") int page,
                                                           @RequestParam(defaultValue = "10") int size,
                                                           @RequestParam(defaultValue = "id") String[] sort) {
        Response<List<PaymentDto>> response = new Response<>();
        Pageable pagingSort = PageRequest.of(page, size, Sort.by(sort));
        if (tenantId == 0 || tenantId < 0) {
            Error error = new Error("INVALID_INPUT_ID", ErrorConstants.INVALID_INPUT_ID);
            response.setErrors(List.of(error));
            return response;
        }
        return paymentService.getAllPaymentsByTenantId(tenantId, response, pagingSort);
    }

    @PostMapping(path = "/create-payment", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public Response<PaymentDto> createPaymentRecord(@RequestBody PaymentDto paymentDto) {
        Response<PaymentDto> response = new Response<>();
        if(paymentDto==null) {
            response.setErrors(List.of(new Error("INVALID_REQUEST", ErrorConstants.INVALID_REQUEST)));
            return response;
        }
        return paymentService.createPayment(paymentDto, response);
    }

    @GetMapping(path = "/payments-by-hostel-id-and-time-period", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<List<PaymentDto>> getAllPaymentsByHostelIdAndTimePeriod(@RequestParam(required = true) Long hostelId,
                                                                            @RequestParam(defaultValue = "CURRENT_MONTH",required = true) String timePeriod,
                                                                            @RequestParam(defaultValue = "0") int page,
                                                                            @RequestParam(defaultValue = "10") int size,
                                                                            @RequestParam(defaultValue = "id") String[] sort) {
        Response<List<PaymentDto>> response = new Response<>();
        Pageable pageable = PageRequest.of(page, size, Sort.by(sort));
        if (hostelId == 0 || hostelId < 0) {
            response.setErrors(List.of(new Error("INVALID_INPUT_ID", ErrorConstants.INVALID_INPUT_ID)));
            return response;
        }

        return paymentService.getPaymentsByHostelIdAndTimePeriod(hostelId, timePeriod, response, pageable);

    }

    @PutMapping(path = "modify-payment/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<PaymentDto> modifyPaymentById(@PathVariable("id") Long id, PaymentDto paymentDto) {
        Response<PaymentDto> response = new Response<>();
        if(id == null || id >= 0 || paymentDto == null) {
            response.setErrors(List.of(new Error("INVALID_INPUT_ID", ErrorConstants.INVALID_INPUT_ID)));
            return response;
        }

        return paymentService.modifyPayment(id, paymentDto, response);
    }

    @DeleteMapping(path = "/delete-payment/{id}")
    public Response<String> deletePaymentById(@PathVariable("id") Long id){
        Response<String> response = new Response<>();
        if(id == null || id <= 0) {
            response.setErrors(List.of(new Error("INVALID_INPUT_ID", ErrorConstants.INVALID_INPUT_ID)));
            return response;
        }

        return paymentService.deletePaymentById(id, response);
    }

}
