package com.msrts.hostel.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.msrts.hostel.entity.Payment;
import com.msrts.hostel.entity.Tenant;
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

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.Arrays;
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
        try {
            if (paymentDto.getAmount() <= 0) {
                response.setErrors(List.of(new Error("INVALID_AMOUNT", ErrorConstants.INVALID_AMOUNT)));
                return response;
            }
            Payment payment = objectMapper.convertValue(paymentDto, Payment.class);
            payment = paymentRepository.save(payment);
            if (payment.getId() != null) {
                paymentDto = objectMapper.convertValue(payment, PaymentDto.class);
                response.setData(paymentDto);
            }
        } catch (RuntimeException ex) {
            response.setErrors(Arrays.asList(new Error("RUNTIME_EXCEPTION", ex.getMessage())));
        }
        return response;
    }

    public Response<List<PaymentDto>> getAllPaymentsByTenantId(Long tenantId, Response<List<PaymentDto>> response, Pageable pageable) {
        try {
            List<Payment> payments = paymentRepository.findAllByTenantId(tenantId, pageable);
            if (!payments.isEmpty()) {
                List<PaymentDto> paymentDtos = payments.stream().map(payment -> objectMapper.convertValue(payment, PaymentDto.class)).toList();
                response.setData(paymentDtos);
            }
        } catch (RuntimeException ex) {
            response.setErrors(Arrays.asList(new Error("RUNTIME_EXCEPTION", ex.getMessage())));
        }
        return response;
    }

    @Transactional
    public Response<List<PaymentDto>> getPaymentsByHostelIdAndTimePeriod(Long hostelId, String timePeriod, Response<List<PaymentDto>> response, Pageable pageable) {
        try {
            List<Payment> payments = null;
            Response<List<TenantDto>> tenantResponse = tenantService.getAllActiveTenantsByHostelId(hostelId, null, new Response<>());
            if (tenantResponse.getErrors() != null) {
                response.setErrors(tenantResponse.getErrors());
                return response;
            }
            List<Long> tenantIds = tenantResponse.getData().stream().map(TenantDto::getId).toList();

            if (!tenantIds.isEmpty() && timePeriod.equalsIgnoreCase(ErrorConstants.TIME_PERIOD_LAST_MONTH)) {
                YearMonth yearMonth = YearMonth.now().minusMonths(1);
                System.out.println(LocalDateTime.of(yearMonth.getYear(), yearMonth.getMonth(), yearMonth.atEndOfMonth().getDayOfMonth(), 23, 59, 59));
                payments = paymentRepository.findAllPaymentsByTenantIdsAndTimePeriod(
                        tenantIds,
                        LocalDateTime.of(yearMonth.getYear(), yearMonth.getMonth().getValue(), 1, 0, 0, 0),
                        LocalDateTime.of(yearMonth.getYear(), yearMonth.getMonth().getValue(), yearMonth.atEndOfMonth().getDayOfMonth(), 23, 59, 59),
                        pageable);
            } else if (!tenantIds.isEmpty() && timePeriod.equalsIgnoreCase(ErrorConstants.TIME_PERIOD_CURRENT_MONTH)) {
                YearMonth yearMonth = YearMonth.now();
                System.out.println(LocalDateTime.of(yearMonth.getYear(), yearMonth.getMonth().getValue(), yearMonth.atEndOfMonth().getDayOfMonth(), 23, 59, 59));
                payments = paymentRepository.findAllPaymentsByTenantIdsAndTimePeriod(
                        tenantIds,
                        LocalDateTime.of(yearMonth.getYear(), yearMonth.getMonth().getValue(), 1, 0, 0, 0),
                        LocalDateTime.of(yearMonth.getYear(), yearMonth.getMonth().getValue(), yearMonth.atEndOfMonth().getDayOfMonth(), 23, 59, 59),
                        pageable);
            } else {
                response.setErrors(List.of(new Error("INVALID_TIME_PERIOD", ErrorConstants.INVALID_TIME_PERIOD)));
            }

            if (payments != null && payments.size() > 0) {
                List<PaymentDto> paymentDtos = payments.stream().map(expense -> objectMapper.convertValue(expense, PaymentDto.class)).toList();
                response.setData(paymentDtos);
            }
        } catch (RuntimeException ex) {
            response.setErrors(Arrays.asList(new Error("RUNTIME_EXCEPTION", ex.getMessage())));
        }
        return response;
    }

    @Transactional
    public Response<PaymentDto> modifyPayment(Long id, PaymentDto paymentDto, Response<PaymentDto> response) {
        try {
            Optional<Payment> optionalPayment = paymentRepository.findById(id);
            if (optionalPayment.isPresent()) {
                if(paymentDto.getTenant()==null) {
                    response.setErrors(Arrays.asList(new Error("ERROR_TENANT_NOT_FOUND", ErrorConstants.ERROR_TENANT_NOT_FOUND)));
                    return response;
                }

                Response<TenantDto> tenantDtoResponse = tenantService.modifyTenant(
                        paymentDto.getTenant().getId(), paymentDto.getTenant(),new Response<>());
                if(tenantDtoResponse.getErrors()!=null){
                    response.setErrors(Arrays.asList(new Error("ERROR_TENANT_NOT_SAVED", ErrorConstants.ERROR_TENANT_NOT_SAVED)));
                    return response;
                } else {
                    Payment payment = optionalPayment.get();
                    payment.setPaymentType(paymentDto.getPaymentType());
                    payment.setAmount(paymentDto.getAmount());
                    payment.setPaymentDate(paymentDto.getPaymentDate());
                    payment.setTransactionType(paymentDto.getTransactionType());
                    payment.setTenant(objectMapper.convertValue(tenantDtoResponse.getData(), Tenant.class));
                    payment = paymentRepository.save(payment);
                    paymentDto = objectMapper.convertValue(payment, PaymentDto.class);
                    response.setData(paymentDto);
                }

            } else {
                response.setErrors(List.of(new Error("ERROR_PAYMENT_NOT_FOUND", ErrorConstants.ERROR_PAYMENT_NOT_FOUND + id)));
            }
        } catch (RuntimeException ex) {
            response.setErrors(Arrays.asList(new Error("RUNTIME_EXCEPTION", ex.getMessage())));
        }
        return response;
    }

    public Response<String> deletePaymentById(Long id, Response<String> response) {
        try {
            paymentRepository.deleteById(id);
            response.setData("Payment deleted with the id of " + id);
        } catch (RuntimeException ex) {
            response.setErrors(Arrays.asList(new Error("RUNTIME_EXCEPTION", ex.getMessage())));
        }
        return response;
    }

}
