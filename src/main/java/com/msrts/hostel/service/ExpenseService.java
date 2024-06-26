package com.msrts.hostel.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.msrts.hostel.entity.Expense;
import com.msrts.hostel.exception.ErrorConstants;
import com.msrts.hostel.model.Error;
import com.msrts.hostel.model.ExpenseDto;
import com.msrts.hostel.model.Response;
import com.msrts.hostel.repository.ExpenseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class ExpenseService {
    @Autowired
    private ExpenseRepository expenseRepository;

    @Autowired
    private ObjectMapper objectMapper;

    public Response<ExpenseDto> saveExpenseDetails(ExpenseDto expenseDto, Response<ExpenseDto> response) {
        try {
            if (expenseDto != null && expenseDto.getAmount() <= 0) {
                response.setErrors(List.of(new Error("INVALID_AMOUNT", ErrorConstants.INVALID_AMOUNT)));
                return response;
            }

            Expense expense = objectMapper.convertValue(expenseDto, Expense.class);
            expense = expenseRepository.save(expense);
            if (expense.getId() != null) {
                expenseDto = objectMapper.convertValue(expense, ExpenseDto.class);
                response.setData(expenseDto);
            }
        } catch (RuntimeException ex) {
            response.setErrors(Arrays.asList(new Error("RUNTIME_EXCEPTION", ex.getMessage())));
        }
        return response;
    }

    public Response<List<ExpenseDto>> getExpensesByHostelId(Long hostelId, Response<List<ExpenseDto>> response, Pageable pageable) {
        try {
            List<Expense> expenses = expenseRepository.findAllAllExpensesByHostelId(hostelId, pageable);
            if (!expenses.isEmpty()) {
                List<ExpenseDto> expenseDtos = expenses.stream().map(expense -> objectMapper.convertValue(expense, ExpenseDto.class)).toList();
                response.setData(expenseDtos);
            }
        } catch (RuntimeException ex) {
            response.setErrors(Arrays.asList(new Error("RUNTIME_EXCEPTION", ex.getMessage())));
        }
        return response;
    }

    public Response<List<ExpenseDto>> getExpensesByUserId(Long userId, Response<List<ExpenseDto>> response) {
        try {
            YearMonth yearMonth = YearMonth.now();
            List<Expense> expenses = expenseRepository.findAllExpensesByUserIdAndTimePeriod(
                    userId,
                    LocalDate.of(yearMonth.getYear(), yearMonth.getMonth().getValue(), 1),
                    LocalDate.of(yearMonth.getYear(), yearMonth.getMonth().getValue(), yearMonth.atEndOfMonth().getDayOfMonth())
            );
            if (!expenses.isEmpty()) {
                List<ExpenseDto> expenseDtos = expenses.stream().map(expense -> objectMapper.convertValue(expense, ExpenseDto.class)).toList();
                response.setData(expenseDtos);
            }
        } catch (RuntimeException ex) {
            response.setErrors(Arrays.asList(new Error("RUNTIME_EXCEPTION", ex.getMessage())));
        }
        return response;
    }

    /*
     *   Time period can be LAST_MONTH, CURRENT_MONTH
     */
    public Response<List<ExpenseDto>> getAllExpensesByTimePeriod(Long hostelId, String timePeriod, Response<List<ExpenseDto>> response, Pageable pageable) {
        try {
            List<Expense> expenses = null;
            if (timePeriod.equalsIgnoreCase(ErrorConstants.TIME_PERIOD_LAST_MONTH)) {
                YearMonth yearMonth = YearMonth.now().minusMonths(1);
                expenses = expenseRepository.findAllExpensesByHostelIdAndTimePeriod(
                        hostelId,
                        LocalDate.of(yearMonth.getYear(), yearMonth.getMonth().getValue(), 1),
                        LocalDate.of(yearMonth.getYear(), yearMonth.getMonth().getValue(), yearMonth.atEndOfMonth().getDayOfMonth()),
                        pageable);
            } else if (timePeriod.equalsIgnoreCase(ErrorConstants.TIME_PERIOD_CURRENT_MONTH)) {
                YearMonth yearMonth = YearMonth.now();
                expenses = expenseRepository.findAllExpensesByHostelIdAndTimePeriod(
                        hostelId,
                        LocalDate.of(yearMonth.getYear(), yearMonth.getMonth().getValue(), 1),
                        LocalDate.of(yearMonth.getYear(), yearMonth.getMonth().getValue(), yearMonth.atEndOfMonth().getDayOfMonth()),
                        pageable);
            } else {
                response.setErrors(List.of(new Error("INVALID_TIME_PERIOD", ErrorConstants.INVALID_TIME_PERIOD)));
            }

            if (expenses != null) {
                List<ExpenseDto> expenseDtos = expenses.stream().map(expense -> objectMapper.convertValue(expense, ExpenseDto.class)).toList();
                response.setData(expenseDtos);
            }
        } catch (RuntimeException ex) {
            response.setErrors(Arrays.asList(new Error("RUNTIME_EXCEPTION", ex.getMessage())));
        }
        return response;
    }

    public Response<ExpenseDto> modifyExpenseDetails(Long id, ExpenseDto expenseDto, Response<ExpenseDto> response) {
        try {
            Optional<Expense> expenseOptional = expenseRepository.findById(id);
            if (expenseOptional.isPresent()) {
                Expense expense = expenseOptional.get();
                expense.setExpenseType(expenseDto.getExpenseType());
                expense.setDate(expenseDto.getDate());
                expense.setAmount(expenseDto.getAmount());
                expense.setDescription(expenseDto.getDescription());
                expense = expenseRepository.save(expense);
                expenseDto = objectMapper.convertValue(expense, ExpenseDto.class);
                response.setData(expenseDto);
            } else {
                response.setErrors(List.of(new Error("ERROR_EXPENSE_NOT_FOUND", ErrorConstants.ERROR_EXPENSE_NOT_FOUND + id)));
            }
        } catch (RuntimeException ex) {
            response.setErrors(Arrays.asList(new Error("RUNTIME_EXCEPTION", ex.getMessage())));
        }
        return response;
    }

    public Response<String> deleteExpenseById(Long id, Response<String> response) {
        try {
            expenseRepository.deleteById(id);
            response.setData("Expense details deleted successfully with the given id of " + id);
        } catch (RuntimeException ex) {
            response.setErrors(Arrays.asList(new Error("RUNTIME_EXCEPTION", ex.getMessage())));
        }
        return response;
    }
}
