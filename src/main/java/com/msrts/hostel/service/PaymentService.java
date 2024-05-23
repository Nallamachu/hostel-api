package com.msrts.hostel.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.msrts.hostel.entity.Payment;
import com.msrts.hostel.exception.ErrorConstants;
import com.msrts.hostel.model.Error;
import com.msrts.hostel.model.PaymentDto;
import com.msrts.hostel.model.Response;
import com.msrts.hostel.model.TenantDto;
import com.msrts.hostel.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Optional;

@Service
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private TenantService tenantService;
    
    @Autowired
    private ObjectMapper objectMapper;

    @Transactional
    public Response<PaymentDto> createPayment(PaymentDto paymentDto, Response<PaymentDto> response) {
        if(paymentDto.getAmount() <= 0) {
            response.setErrors(List.of(new Error("INVALID_AMOUNT", ErrorConstants.INVALID_AMOUNT)));
            return response;
        }
        Payment payment = objectMapper.convertValue(paymentDto, Payment.class);
        payment = paymentRepository.save(payment);
        if(payment.getId() != null) {
            paymentDto = objectMapper.convertValue(payment, PaymentDto.class);
            response.setData(paymentDto);
        }
        return response;
    }

    public Response<List<PaymentDto>> getAllPaymentsByTenantId(Long tenantId, Response<List<PaymentDto>> response, Pageable pageable) {
        List<Payment> payments = paymentRepository.findAllByTenantId(tenantId, pageable);
        if(!payments.isEmpty()) {
            List<PaymentDto> paymentDtos = payments.stream().map(payment -> objectMapper.convertValue(payment, PaymentDto.class)).toList();
            response.setData(paymentDtos);
        }
        return response;
    }

    @Transactional
    public Response<List<PaymentDto>> getPaymentsByHostelIdAndTimePeriod(Long hostelId, String timePeriod, Response<List<PaymentDto>> response, Pageable pageable) {
        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate now = LocalDate.now();
        List<Payment> payments = null;
        Response<List<TenantDto>> tenantResponse = tenantService.getAllActiveTenantsByHostelId(hostelId, new Response<>());
        if(!tenantResponse.getErrors().isEmpty()){
            response.setErrors(tenantResponse.getErrors());
            return response;
        }
        List<Long> tenantIds = tenantResponse.getData().stream().map(TenantDto::getId).toList();

        if(!tenantIds.isEmpty() && timePeriod.equalsIgnoreCase(ErrorConstants.TIME_PERIOD_LAST_MONTH)){
            String startDate = now.minusMonths(1).with(TemporalAdjusters.firstDayOfMonth()).format(format);
            String endDate = now.minusMonths(1).with(TemporalAdjusters.lastDayOfMonth()).format(format);
            payments = paymentRepository.findAllPaymentsByTenantIdsAndTimePeriod(
                    tenantIds,
                    startDate,
                    endDate,
                    pageable);
        } else if(!tenantIds.isEmpty() && timePeriod.equalsIgnoreCase(ErrorConstants.TIME_PERIOD_CURRENT_MONTH)) {
            String startDate = now.with(TemporalAdjusters.firstDayOfMonth()).format(format);
            payments = paymentRepository.findAllPaymentsByTenantIdsAndTimePeriod(
                    tenantIds,
                    startDate,
                    now.format(format),
                    pageable);
        } else {
            response.setErrors(List.of(new Error("INVALID_TIME_PERIOD", ErrorConstants.INVALID_TIME_PERIOD)));
        }

        if(payments != null) {
            List<PaymentDto> paymentDtos = payments.stream().map(expense -> objectMapper.convertValue(expense, PaymentDto.class)).toList();
            response.setData(paymentDtos);
        }
        return response;
    }

    public Response<PaymentDto> modifyPayment(Long id, PaymentDto paymentDto, Response<PaymentDto> response) {
        Optional<Payment> optionalPayment = paymentRepository.findById(id);
        if(optionalPayment.isPresent()) {
            Payment payment = optionalPayment.get();
            payment.setPaymentType(paymentDto.getPaymentType());
            payment.setAmount(paymentDto.getAmount());
            payment.setStartDate(paymentDto.getStartDate());
            payment.setEndDate(paymentDto.getEndDate());
            payment.setTransactionType(paymentDto.getTransactionType());
            payment = paymentRepository.save(payment);
            paymentDto = objectMapper.convertValue(payment, PaymentDto.class);
            response.setData(paymentDto);
        } else {
            response.setErrors(List.of(new Error("ERROR_PAYMENT_NOT_FOUND", ErrorConstants.ERROR_PAYMENT_NOT_FOUND + id)));
        }
        return response;
    }

    public Response<String> deletePaymentById(Long id, Response<String> response) {
        paymentRepository.deleteById(id);
        response.setData("Payment deleted with the id of " + id);
        return response;
    }

}
