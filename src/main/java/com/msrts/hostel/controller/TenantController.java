package com.msrts.hostel.controller;

import com.msrts.hostel.exception.ErrorConstants;
import com.msrts.hostel.model.Error;
import com.msrts.hostel.model.Response;
import com.msrts.hostel.model.TenantDto;
import com.msrts.hostel.model.TenantDto;
import com.msrts.hostel.service.TenantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/api/v1/tenant")
public class TenantController {
    @Autowired
    private TenantService tenantService;

    @PostMapping(path = "/create-tenant", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public Response<TenantDto> createTenant(@RequestBody TenantDto tenantDto) {
        Response<TenantDto> response = new Response<>();
        if(tenantDto==null) {
            response.setErrors(List.of(new Error("INVALID_REQUEST", ErrorConstants.INVALID_REQUEST)));
            return response;
        }
        return tenantService.createTenant(tenantDto, response);
    }

    @GetMapping(path = "/tenants-by-hostel-id", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<List<TenantDto>> getAllTenantsByHostelId(@RequestParam(required = true) Long hostelId) {
        Response<List<TenantDto>> response = new Response<>();
        if (hostelId == 0 || hostelId < 0) {
            response.setErrors(List.of(new Error("INVALID_INPUT_ID", ErrorConstants.INVALID_INPUT_ID)));
            return response;
        }

        return tenantService.getAllActiveTenantsByHostelId(hostelId, response);
    }

    @GetMapping(path = "/search-tenant-by-name-or-government-id-number", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<List<TenantDto>> searchTenantByNameOrGovernmentProof(@RequestParam(required = true, defaultValue = "NAME") String type,
                                                             @RequestParam(required = true) String value,
                                                             @RequestParam(defaultValue = "0") int page,
                                                             @RequestParam(defaultValue = "10") int size,
                                                             @RequestParam(defaultValue = "id,desc") String[] sort) {
        Response<List<TenantDto>> response = new Response<>();
        Pageable pageable = PageRequest.of(page, size, Sort.by(sort));
        if (type == null || value == null) {
            response.setErrors(List.of(new Error("INVALID_INPUT_ID", ErrorConstants.INVALID_INPUT_ID)));
            return response;
        }

        return type.equalsIgnoreCase("NAME")
                ?tenantService.getTenantsByNameContains(value, response, pageable)
                :tenantService.getTenantsByIdNumberContains(value, response,pageable);
    }

    @PutMapping(path = "/modify-room/{id}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public Response<TenantDto> modifyTenant(@PathVariable("id") Long id, @RequestBody TenantDto tenantDto) {
        Response<TenantDto> response = new Response<>();
        if(id == null || tenantDto == null) {
            response.setErrors(List.of(new Error("INVALID_REQUEST", ErrorConstants.INVALID_REQUEST)));
            return response;
        }
        return tenantService.modifyTenant(id, tenantDto, response);
    }

    @DeleteMapping(path = "/delete-tenant-by-id/{id}")
    public Response<String> deleteTenantById(@PathVariable(value = "id") Long id) {
        Response<String> response = new Response<>();
        if(id==null) {
            response.setErrors(List.of(new Error("INVALID_INPUT_ID",ErrorConstants.INVALID_INPUT_ID)));
            return response;
        }
        return tenantService.deleteTenantById(id, response);
    }
}
