package com.msrts.hostel.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.msrts.hostel.entity.Hostel;
import com.msrts.hostel.entity.Room;
import com.msrts.hostel.entity.Tenant;
import com.msrts.hostel.exception.ErrorConstants;
import com.msrts.hostel.model.Error;
import com.msrts.hostel.model.Response;
import com.msrts.hostel.model.TenantDto;
import com.msrts.hostel.repository.HostelRepository;
import com.msrts.hostel.repository.RoomRepository;
import com.msrts.hostel.repository.TenantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

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
        if(tenantDto.getRoom() != null && tenantDto.getRoom().getId()!=null) {
            Optional<Room> room = roomRepository.findById(tenantDto.getRoom().getId());
            if(room.isEmpty()) {
                response.setErrors(List.of(new Error("INVALID_INPUT_ID", ErrorConstants.INVALID_INPUT_ID + " for Room")));
                return response;
            }

            Number activeTenantsCount = roomRepository.activeTenantCountByRoomId(room.get().getId());
            if(activeTenantsCount != null && room.get().getCapacity() > activeTenantsCount.longValue()) {
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
        return response;
    }

    public Response<List<TenantDto>> getAllActiveTenantsByHostelId(Long hostelId, Response<List<TenantDto>> response) {
        List<Tenant> tenantList = new ArrayList<>();
        Optional<Hostel> optionalHostel = hostelRepository.findById(hostelId);
        if(optionalHostel.isPresent()) {
            Set<Room> rooms = optionalHostel.get().getRooms();
            if(!rooms.isEmpty()) {
                for(Room room: rooms) {
                    tenantList.addAll(room.getTenants().stream().filter(Tenant::isActive).toList());
                }
            }
        } else {
            response.setErrors(List.of(new Error("ERROR_HOSTEL_NOT_FOUND", ErrorConstants.ERROR_HOSTEL_NOT_FOUND)));
        }

        if(!tenantList.isEmpty()) {
            List<TenantDto> tenantDtos = tenantList.stream().map(tenant -> objectMapper.convertValue(tenant, TenantDto.class)).toList();
            response.setData(tenantDtos);
        }
        return  response;
    }

    public Response<List<TenantDto>> getTenantsByNameContains(String name, Response<List<TenantDto>> response, Pageable pageable) {
        List<Tenant> tenants = tenantRepository.findAllTenantsByGivenNameContains(name, pageable);
        if(!tenants.isEmpty()) {
            List<TenantDto> tenantDtos = tenants.stream().map(tenant -> objectMapper.convertValue(tenant, TenantDto.class)).toList();
            response.setData(tenantDtos);
        }
        return response;
    }

    public Response<List<TenantDto>> getTenantsByIdNumberContains(String idNumber, Response<List<TenantDto>> response, Pageable pageable) {
        List<Tenant> tenants = tenantRepository.findAllTenantsByGivenIdProofContains(idNumber, pageable);
        if(!tenants.isEmpty()) {
            List<TenantDto> tenantDtos = tenants.stream().map(tenant -> objectMapper.convertValue(tenant, TenantDto.class)).toList();
            response.setData(tenantDtos);
        }
        return response;
    }

    public Response<TenantDto> modifyTenant(Long id, TenantDto tenantDto, Response<TenantDto> response) {
        Optional<Tenant> optionalTenant = tenantRepository.findById(id);
        if(optionalTenant.isPresent()) {
            Tenant tenant = getTenant(tenantDto, optionalTenant.get());
            tenant = tenantRepository.save(tenant);
            tenantDto = objectMapper.convertValue(tenant, TenantDto.class);
            response.setData(tenantDto);
        } else {
            response.setErrors(List.of(new Error("ERROR_TENANT_NOT_FOUND", ErrorConstants.ERROR_TENANT_NOT_FOUND)));
        }
        return response;
    }

    private static Tenant getTenant(TenantDto tenantDto, Tenant tenant) {
        tenant.setFirstName(tenantDto.getFirstName());
        tenant.setMiddleName(tenantDto.getMiddleName());
        tenant.setLastName(tenantDto.getLastName());
        tenant.setMobile(tenantDto.getMobile());
        tenant.setEntryDate(tenantDto.getEntryDate());
        tenant.setExitDate(tenantDto.getExitDate());
        tenant.setIdProof(tenantDto.getIdProof());
        tenant.setIdType(tenantDto.getIdType());
        tenant.setRoom(tenantDto.getRoom());
        tenant.setActive(tenantDto.isActive());
        tenant.setAddress(tenantDto.getAddress());
        return tenant;
    }

    public Response<String> deleteTenantById(Long id, Response<String> response) {
        Optional<Tenant> optionalTenant = tenantRepository.findById(id);
        if(optionalTenant.isPresent()) {
            Tenant tenant = optionalTenant.get();
            tenant.setActive(false);
            tenantRepository.save(tenant);
            response.setData(getTenantFullName(tenant) + " deleted successfully");
        } else {
            response.setErrors(List.of(new Error("ERROR_TENANT_NOT_FOUND", ErrorConstants.ERROR_TENANT_NOT_FOUND + id)));
        }
        return response;
    }

    private String getTenantFullName(Tenant tenant) {
        String fullName = tenant.getFirstName();
        fullName = fullName + (tenant.getMiddleName() != null ? " " + tenant.getMiddleName():"");
        fullName = fullName + (tenant.getLastName() != null ? " " + tenant.getLastName():"");
        return fullName;
    }

}
