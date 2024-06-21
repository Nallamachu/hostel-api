package com.msrts.hostel.controller;

import com.msrts.hostel.exception.ErrorConstants;
import com.msrts.hostel.model.Error;
import com.msrts.hostel.model.ExpenseDto;
import com.msrts.hostel.model.Response;
import com.msrts.hostel.service.ExpenseService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/api/v1/expense")
@RequiredArgsConstructor
@CrossOrigin(exposedHeaders = "Access-Control-Allow-Origin")
public class ExpenseController {

    @Autowired
    private ExpenseService expenseService;

    @PostMapping(path = "/create-expense", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public Response<ExpenseDto> createExpenseRecord(@RequestBody ExpenseDto expenseDto) {
        Response<ExpenseDto> response = new Response<>();
        if (expenseDto == null) {
            response.setErrors(List.of(new Error("INVALID_REQUEST", ErrorConstants.INVALID_REQUEST)));
            return response;
        }
        return expenseService.saveExpenseDetails(expenseDto, response);
    }

    @GetMapping(path = "/find-all-expenses-by-hostel-id", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<List<ExpenseDto>> getAllExpensesByHostelId(@RequestParam(required = true) Long hostelId,
                                                               @RequestParam(defaultValue = "0") int page,
                                                               @RequestParam(defaultValue = "10") int size,
                                                               @RequestParam(defaultValue = "id") String[] sort) {
        Response<List<ExpenseDto>> response = new Response<>();
        Pageable pageable = PageRequest.of(page, size, Sort.by(sort));
        if (hostelId == 0 || hostelId < 0) {
            response.setErrors(List.of(new Error("INVALID_INPUT_ID", ErrorConstants.INVALID_INPUT_ID)));
            return response;
        }

        return expenseService.getExpensesByHostelId(hostelId, response, pageable);

    }

    @GetMapping(path = "/find-all-expenses-by-user-id", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<List<ExpenseDto>> getAllExpensesByUserId(@RequestParam(required = true) Long userId) {
        Response<List<ExpenseDto>> response = new Response<>();
        if (userId == 0 || userId < 0) {
            response.setErrors(List.of(new Error("INVALID_INPUT_ID", ErrorConstants.INVALID_INPUT_ID)));
            return response;
        }

        return expenseService.getExpensesByUserId(userId, response);

    }

    @GetMapping(path = "/find-all-expenses-by-hostel-id-and-time-period", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<List<ExpenseDto>> getAllExpensesByHostelIdAndTimePeriod(@RequestParam(required = true) Long hostelId,
                                                                            @RequestParam(defaultValue = "CURRENT_MONTH", required = true) String timePeriod,
                                                                            @RequestParam(defaultValue = "0") int page,
                                                                            @RequestParam(defaultValue = "10") int size,
                                                                            @RequestParam(defaultValue = "id") String[] sort) {
        Response<List<ExpenseDto>> response = new Response<>();
        Pageable pageable = PageRequest.of(page, size, Sort.by(sort));
        if (hostelId == 0 || hostelId < 0) {
            response.setErrors(List.of(new Error("INVALID_INPUT_ID", ErrorConstants.INVALID_INPUT_ID)));
            return response;
        }

        return expenseService.getAllExpensesByTimePeriod(hostelId, timePeriod, response, pageable);
    }

    @PutMapping(path = "modify-expense/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<ExpenseDto> modifyExpenseById(@PathVariable("id") Long id, @RequestBody ExpenseDto expenseDto) {
        Response<ExpenseDto> response = new Response<>();
        if (id == null || id >= 0 || expenseDto == null) {
            response.setErrors(List.of(new Error("INVALID_INPUT_ID", ErrorConstants.INVALID_INPUT_ID)));
            return response;
        }

        return expenseService.modifyExpenseDetails(id, expenseDto, response);
    }

    @DeleteMapping(path = "/delete-expense/{id}")
    public Response<String> deleteExpenseById(@PathVariable("id") Long id) {
        Response<String> response = new Response<>();
        if (id == null || id <= 0) {
            response.setErrors(List.of(new Error("INVALID_INPUT_ID", ErrorConstants.INVALID_INPUT_ID)));
            return response;
        }
        return expenseService.deleteExpenseById(id, response);
    }

}
