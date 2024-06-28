package com.msrts.hostel.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.msrts.hostel.entity.Room;
import com.msrts.hostel.entity.Tenant;
import com.msrts.hostel.exception.ErrorConstants;
import com.msrts.hostel.model.Error;
import com.msrts.hostel.model.Response;
import com.msrts.hostel.model.RoomDto;
import com.msrts.hostel.model.TenantDto;
import com.msrts.hostel.repository.HostelRepository;
import com.msrts.hostel.repository.RoomRepository;
import com.msrts.hostel.repository.TenantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class TenantService {
    @Autowired
    private TenantRepository tenantRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private HostelRepository hostelRepository;

    @Autowired
    private ObjectMapper objectMapper;

    public Response<TenantDto> createTenant(TenantDto tenantDto, Response<TenantDto> response) {
        try {
            if (tenantDto.getRoom() != null && tenantDto.getRoom().getId() != null) {
                Optional<Room> room = roomRepository.findById(tenantDto.getRoom().getId());
                if (room.isEmpty()) {
                    response.setErrors(List.of(new Error("INVALID_INPUT_ID", ErrorConstants.INVALID_INPUT_ID + " for Room")));
                    return response;
                }

                Number activeTenantsCount = tenantRepository.activeTenantCountByRoomId(room.get().getId());
                if (activeTenantsCount != null && room.get().getCapacity() > activeTenantsCount.longValue()) {
                    Tenant tenant = objectMapper.convertValue(tenantDto, Tenant.class);
                    tenant = tenantRepository.save(tenant);
                    tenantDto = objectMapper.convertValue(tenant, TenantDto.class);
                    response.setData(tenantDto);

                } else {
                    response.setErrors(List.of(new Error("ERROR_ROOM_IS_FULL", ErrorConstants.ERROR_ROOM_IS_FULL)));
                    return response;
                }
            } else {
                response.setErrors(List.of(new Error("INVALID_REQUEST", ErrorConstants.INVALID_REQUEST)));
                return response;
            }
        } catch (RuntimeException ex) {
            response.setErrors(Arrays.asList(new Error("RUNTIME_EXCEPTION", ex.getMessage())));
        }
        return response;
    }

    public Response<List<TenantDto>> getAllActiveTenantsByHostelId(Long hostelId, Pageable pageable, Response<List<TenantDto>> response) {
        try {
            Page<Tenant> tenants = tenantRepository.findAllActiveTenantsByHostelId(hostelId, pageable);
            if (!tenants.isEmpty()) {
                List<TenantDto> tenantDtoList = tenants.stream()
                        .map(tenant -> objectMapper.convertValue(tenant, TenantDto.class))
                        .toList();
                response.setData(tenantDtoList);
            }
        } catch (RuntimeException ex) {
            response.setErrors(Arrays.asList(new Error("RUNTIME_EXCEPTION", ex.getMessage())));
        }
        return response;
    }

    public Response<List<TenantDto>> getAllActiveTenantsByUserId(Long userId, Response<List<TenantDto>> response) {
        try {
            List<Tenant> tenants = tenantRepository.findAllActiveTenantsByUserId(userId);
            if (!tenants.isEmpty()) {
                List<TenantDto> tenantDtoList = tenants.stream()
                        .map(tenant -> objectMapper.convertValue(tenant, TenantDto.class))
                        .toList();
                response.setData(tenantDtoList);
            }
        } catch (RuntimeException ex) {
            response.setErrors(Arrays.asList(new Error("RUNTIME_EXCEPTION", ex.getMessage())));
        }
        return response;
    }

    public Response<List<TenantDto>> getAllActiveTenantsByRoomId(Long roomId, Response<List<TenantDto>> response) {
        try {
            List<Tenant> tenants = tenantRepository.findAllTenantsByRoomId(roomId);
            if (!tenants.isEmpty()) {
                List<TenantDto> tenantDtoList = tenants.stream()
                        .map(tenant -> objectMapper.convertValue(tenant, TenantDto.class))
                        .toList();
                response.setData(tenantDtoList);
            }
        } catch (RuntimeException ex) {
            response.setErrors(Arrays.asList(new Error("RUNTIME_EXCEPTION", ex.getMessage())));
        }
        return response;
    }

    public Response<List<TenantDto>> getTenantsByNameContains(String name, Response<List<TenantDto>> response, Pageable pageable) {
        try {
            Page<Tenant> tenants = tenantRepository.findAllTenantsByGivenNameContains(name, name, name, pageable);
            if (!tenants.isEmpty()) {
                List<TenantDto> tenantDtoList = tenants.stream().map(tenant -> objectMapper.convertValue(tenant, TenantDto.class)).toList();
                response.setData(tenantDtoList);
            }
        } catch (RuntimeException ex) {
            response.setErrors(Arrays.asList(new Error("RUNTIME_EXCEPTION", ex.getMessage())));
        }
        return response;
    }

    public Response<List<TenantDto>> getTenantsByGovernmentIdNumber(String idNumber, Response<List<TenantDto>> response, Pageable pageable) {
        try {
            Page<Tenant> tenants = tenantRepository.findAllTenantsByGivenIdNumber(idNumber, pageable);
            if (!tenants.isEmpty()) {
                List<TenantDto> tenantDtoList = tenants.stream().map(tenant -> objectMapper.convertValue(tenant, TenantDto.class)).toList();
                response.setData(tenantDtoList);
            }
        } catch (RuntimeException ex) {
            response.setErrors(Arrays.asList(new Error("RUNTIME_EXCEPTION", ex.getMessage())));
        }
        return response;
    }

    public Response<TenantDto> modifyTenant(Long id, TenantDto tenantDto, Response<TenantDto> response) {
        try {
            Number activeTenantsCount = tenantRepository.activeTenantCountByRoomId(tenantDto.getRoom().getId());
            if (activeTenantsCount != null && tenantDto.getRoom().getCapacity() > activeTenantsCount.longValue()) {
                Optional<Tenant> optionalTenant = tenantRepository.findById(id);
                if (optionalTenant.isPresent()) {
                    Tenant tenant = getTenant(tenantDto, optionalTenant.get());
                    tenant = tenantRepository.save(tenant);
                    tenantDto = objectMapper.convertValue(tenant, TenantDto.class);
                    response.setData(tenantDto);
                } else {
                    response.setErrors(List.of(new Error("ERROR_TENANT_NOT_FOUND", ErrorConstants.ERROR_TENANT_NOT_FOUND)));
                }
            } else {
                response.setErrors(List.of(new Error("ERROR_ROOM_IS_FULL", ErrorConstants.ERROR_ROOM_IS_FULL)));
            }
        } catch (RuntimeException ex) {
            response.setErrors(Arrays.asList(new Error("RUNTIME_EXCEPTION", ex.getMessage())));
        }
        return response;
    }

    private Tenant getTenant(TenantDto tenantDto, Tenant tenant) {
        tenant.setFirstName(tenantDto.getFirstName());
        tenant.setMiddleName(tenantDto.getMiddleName());
        tenant.setLastName(tenantDto.getLastName());
        tenant.setMobile(tenantDto.getMobile());
        tenant.setEntryDate(tenantDto.getEntryDate());
        tenant.setExitDate(tenantDto.getExitDate());
        tenant.setIdNumber(tenantDto.getIdNumber());
        tenant.setIdType(tenantDto.getIdType());
        tenant.setRoom(getRoomObject(tenantDto.getRoom()));
        tenant.setActive(tenantDto.isActive());
        tenant.setAddress(tenantDto.getAddress());
        return tenant;
    }

    private Room getRoomObject(RoomDto roomDto){
        return objectMapper.convertValue(roomDto, Room.class);
    }

    public Response<String> deleteTenantById(Long id, Response<String> response) {
        try {
            Optional<Tenant> optionalTenant = tenantRepository.findById(id);
            if (optionalTenant.isPresent()) {
                Tenant tenant = optionalTenant.get();
                tenant.setActive(false);
                tenantRepository.save(tenant);
                response.setData(getTenantFullName(tenant) + " deleted successfully");
            } else {
                response.setErrors(List.of(new Error("ERROR_TENANT_NOT_FOUND", ErrorConstants.ERROR_TENANT_NOT_FOUND + id)));
            }
        } catch (RuntimeException ex) {
            response.setErrors(Arrays.asList(new Error("RUNTIME_EXCEPTION", ex.getMessage())));
        }
        return response;
    }

    private String getTenantFullName(Tenant tenant) {
        String fullName = tenant.getFirstName();
        fullName = fullName + (tenant.getMiddleName() != null ? " " + tenant.getMiddleName() : "");
        fullName = fullName + (tenant.getLastName() != null ? " " + tenant.getLastName() : "");
        return fullName;
    }

    public Response<List<TenantDto>> getAllActiveTenantsByRoomNo(Long roomNo, Response<List<TenantDto>> response) {
        try {
            List<Tenant> tenants = tenantRepository.findAllTenantsByRoomNo(roomNo);
            if (!tenants.isEmpty()) {
                List<TenantDto> tenantDtoList = tenants.stream()
                        .map(tenant -> objectMapper.convertValue(tenant, TenantDto.class))
                        .toList();
                response.setData(tenantDtoList);
            }
        } catch (RuntimeException ex) {
            response.setErrors(Arrays.asList(new Error("RUNTIME_EXCEPTION", ex.getMessage())));
        }
        return response;
    }

    public Response<Number> getActiveTenantsCountByRoomId(Long roomId, Response response){
        try {
            Number count = tenantRepository.activeTenantCountByRoomId(roomId);
            response.setData(count);
        }catch (RuntimeException ex){
            response.setErrors(Arrays.asList(new Error("INVALID_INPUT_ID", ErrorConstants.INVALID_INPUT_ID)));
        }
        return response;
    }
}
