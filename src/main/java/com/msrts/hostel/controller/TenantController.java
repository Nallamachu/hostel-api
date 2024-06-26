package com.msrts.hostel.controller;

import com.msrts.hostel.exception.ErrorConstants;
import com.msrts.hostel.model.Error;
import com.msrts.hostel.model.Response;
import com.msrts.hostel.model.TenantDto;
import com.msrts.hostel.service.TenantService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/api/v1/tenant")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", exposedHeaders = "Access-Control-Allow-Origin")
public class TenantController {
    private static final Logger LOGGER = LoggerFactory.getLogger(TenantController.class);
    @Autowired
    private TenantService tenantService;

    @PostMapping(path = "/create-tenant", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public Response<TenantDto> createTenant(@RequestBody TenantDto tenantDto) {
        Response<TenantDto> response = new Response<>();
        if (tenantDto == null) {
            response.setErrors(List.of(new Error("INVALID_REQUEST", ErrorConstants.INVALID_REQUEST)));
            return response;
        }
        return tenantService.createTenant(tenantDto, response);
    }

    @GetMapping(path = "/tenants-by-hostel-id", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<List<TenantDto>> getAllTenantsByHostelId(@RequestParam(required = true) Long hostelId,
                                                             @RequestParam(defaultValue = "0") int page,
                                                             @RequestParam(defaultValue = "10") int size,
                                                             @RequestParam(defaultValue = "id") String[] sort) {
        LOGGER.info("Start of getAllTenantsByHostelId {0}", hostelId);
        Response<List<TenantDto>> response = new Response<>();
        if (hostelId == 0 || hostelId < 0) {
            response.setErrors(List.of(new Error("INVALID_INPUT_ID", ErrorConstants.INVALID_INPUT_ID)));
            return response;
        }
        Pageable pagingSort = PageRequest.of(page, size, Sort.by(sort));
        LOGGER.info("Calling service of getAllTenantsByHostelId");
        return tenantService.getAllActiveTenantsByHostelId(hostelId, pagingSort, response);
    }

    @GetMapping(path = "/tenants-by-user-id", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<List<TenantDto>> getAllTenantsByUserId(@RequestParam(required = true) Long userId) {
        LOGGER.info("Start of getAllActiveTenantsByUserId"+ userId);
        Response<List<TenantDto>> response = new Response<>();
        if (userId == 0 || userId < 0) {
            response.setErrors(List.of(new Error("INVALID_INPUT_ID", ErrorConstants.INVALID_INPUT_ID)));
            return response;
        }

        LOGGER.info("Calling service of getAllActiveTenantsByUserId");
        return tenantService.getAllActiveTenantsByUserId(userId, response);
    }

    @GetMapping(path = "/tenants-by-room-id", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<List<TenantDto>> getAllTenantsByRoomId(@RequestParam(required = true) Long roomId) {
        LOGGER.info("Start of getAllTenantsByRoomId"+ roomId);
        Response<List<TenantDto>> response = new Response<>();
        if (roomId == 0 || roomId < 0) {
            response.setErrors(List.of(new Error("INVALID_INPUT_ID", ErrorConstants.INVALID_INPUT_ID)));
            return response;
        }

        LOGGER.info("Calling service of getAllTenantsByRoomId");
        return tenantService.getAllActiveTenantsByRoomId(roomId, response);
    }

    @GetMapping(path = "/tenants-by-room-no", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<List<TenantDto>> getAllTenantsByRoomNo(@RequestParam(required = true) Long roomNo) {
        LOGGER.info("Start of getAllTenantsByRoomNo"+ roomNo);
        Response<List<TenantDto>> response = new Response<>();
        if (roomNo == 0 || roomNo < 0) {
            response.setErrors(List.of(new Error("INVALID_INPUT_ID", ErrorConstants.INVALID_INPUT_ID)));
            return response;
        }

        LOGGER.info("Calling service of getAllTenantsByRoomNo");
        return tenantService.getAllActiveTenantsByRoomNo(roomNo, response);
    }

    @GetMapping(path = "/search-tenant-by-name-or-government-id-number", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<List<TenantDto>> searchTenantByNameOrGovernmentProof(@RequestParam(required = false) String name,
                                                                         @RequestParam(required = false) String idNumber,
                                                                         @RequestParam(defaultValue = "0") int page,
                                                                         @RequestParam(defaultValue = "10") int size,
                                                                         @RequestParam(defaultValue = "id") String[] sort) {
        Response<List<TenantDto>> response = new Response<>();
        Pageable pageable = PageRequest.of(page, size, Sort.by(sort));
        if (name == null && idNumber == null) {
            response.setErrors(List.of(new Error("ERROR_NAME_OR_ID_NUMBER_MANDATORY", ErrorConstants.ERROR_NAME_OR_ID_NUMBER_MANDATORY)));
            return response;
        }

        return (idNumber == null)
                ? tenantService.getTenantsByNameContains(name, response, pageable)
                : tenantService.getTenantsByGovernmentIdNumber(idNumber, response, pageable);
    }

    @PutMapping(path = "/modify-tenant/{id}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public Response<TenantDto> modifyTenant(@PathVariable("id") Long id, @RequestBody TenantDto tenantDto) {
        Response<TenantDto> response = new Response<>();
        if (id == null || tenantDto == null) {
            response.setErrors(List.of(new Error("INVALID_REQUEST", ErrorConstants.INVALID_REQUEST)));
            return response;
        }
        return tenantService.modifyTenant(id, tenantDto, response);
    }

    @DeleteMapping(path = "/delete-tenant-by-id/{id}")
    public Response<String> deleteTenantById(@PathVariable(value = "id") Long id) {
        Response<String> response = new Response<>();
        if (id == null) {
            response.setErrors(List.of(new Error("INVALID_INPUT_ID", ErrorConstants.INVALID_INPUT_ID)));
            return response;
        }
        return tenantService.deleteTenantById(id, response);
    }

    @GetMapping(path = "/tenants-count-by-room-id", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<Number> getAllActiveTenantsByRoomId(@RequestParam(required = true) Long roomId) {
        LOGGER.info("Start of getAllActiveTenantsByRoomId"+ roomId);
        Response<Number> response = new Response<>();
        if (roomId <= 0) {
            response.setErrors(List.of(new Error("INVALID_INPUT_ID", ErrorConstants.INVALID_INPUT_ID)));
            return response;
        }

        LOGGER.info("Calling service of getAllActiveTenantsByRoomId");
        return tenantService.getActiveTenantsCountByRoomId(roomId, response);
    }
}
