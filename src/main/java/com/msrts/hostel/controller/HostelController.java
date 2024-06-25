package com.msrts.hostel.controller;

import com.msrts.hostel.exception.ErrorConstants;
import com.msrts.hostel.model.Error;
import com.msrts.hostel.model.HostelDto;
import com.msrts.hostel.model.Response;
import com.msrts.hostel.service.HostelService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/hostel")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", exposedHeaders = "Access-Control-Allow-Origin")
public class HostelController {
    @Autowired
    private HostelService hostelService;

    @GetMapping(path = "/find-all-hostels-by-user-id", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<List<HostelDto>> getAllHostelsByUserId(@RequestParam Integer userId,
                                                           @RequestParam(defaultValue = "0") int page,
                                                           @RequestParam(defaultValue = "10") int size,
                                                           @RequestParam(defaultValue = "id") String[] sort) {
        Response<List<HostelDto>> response = new Response<>();
        Pageable pagingSort = PageRequest.of(page, size, Sort.by(sort));
        if (userId == 0 || userId < 0) {
            response.setErrors(List.of(new Error("INVALID_INPUT_ID", ErrorConstants.INVALID_INPUT_ID)));
            return response;
        }
        return hostelService.findAllHostelsByUserId(response, userId, pagingSort);
    }

    @GetMapping(path = "/find-all-hostels-by-user-no-pagination", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<List<HostelDto>> getAllHostelsByUser(@RequestParam Integer userId) {
        Response<List<HostelDto>> response = new Response<>();
        if (userId == 0 || userId < 0) {
            response.setErrors(List.of(new Error("INVALID_INPUT_ID", ErrorConstants.INVALID_INPUT_ID)));
            return response;
        }
        return hostelService.findAllHostelsByUserIdNoPagination(response, userId);
    }

    @PostMapping(path = "/create-hostel", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<HostelDto> createHostel(@RequestBody HostelDto hostelDto) {
        Response<HostelDto> response = new Response<>();
        if (hostelDto == null) {
            response.setErrors(List.of(new Error("INVALID_REQUEST", ErrorConstants.INVALID_REQUEST)));
            return response;
        }

        return hostelService.createHostel(response, hostelDto);

    }

    @PutMapping(path = "/modify-hostel/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<HostelDto> modifyHostel(@PathVariable("id") Long id, @RequestBody HostelDto hostelDto) {
        Response<HostelDto> response = new Response<>();
        if (id == null || hostelDto == null) {
            response.setErrors(List.of(new Error("INVALID_REQUEST", ErrorConstants.INVALID_REQUEST)));
            return response;
        }

        return hostelService.modifyHostel(id, hostelDto, response);
    }

    @DeleteMapping(path = "/delete-hostel/{id}")
    public Response<String> deleteHostel(@PathVariable("id") Long id) {
        Response<String> response = new Response<>();
        if (id == null) {
            response.setErrors(List.of(new Error("INVALID_REQUEST", ErrorConstants.INVALID_REQUEST)));
            return response;
        }

        return hostelService.deleteHostel(id, response);
    }
}
