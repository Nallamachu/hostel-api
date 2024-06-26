package com.msrts.hostel.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.msrts.hostel.entity.Hostel;
import com.msrts.hostel.entity.Room;
import com.msrts.hostel.entity.Tenant;
import com.msrts.hostel.exception.ErrorConstants;
import com.msrts.hostel.model.Error;
import com.msrts.hostel.model.Response;
import com.msrts.hostel.model.RoomDto;
import com.msrts.hostel.model.TenantDto;
import com.msrts.hostel.repository.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class RoomService {
    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private ObjectMapper objectMapper;

    public Response<RoomDto> createRoom(RoomDto roomDto, Response<RoomDto> response) {
        try {
            Room room = objectMapper.convertValue(roomDto, Room.class);
            room = roomRepository.save(room);
            roomDto = objectMapper.convertValue(room, RoomDto.class);
            response.setData(roomDto);
        } catch (RuntimeException ex) {
            response.setErrors(Arrays.asList(new Error("RUNTIME_EXCEPTION", ex.getMessage())));
        }
        return response;
    }

    public Response<List<RoomDto>> findAllRoomsByHostelId(Long hostelId, Response<List<RoomDto>> response, Pageable pageable) {
        try {
            Page<Room> rooms = roomRepository.findAllRoomsByHostelId(hostelId, pageable);
            if (!rooms.isEmpty()) {
                List<RoomDto> roomDtos = rooms.stream().map(room -> objectMapper.convertValue(room, RoomDto.class)).toList();
                response.setData(roomDtos);
            }
        } catch (RuntimeException ex) {
            response.setErrors(Arrays.asList(new Error("RUNTIME_EXCEPTION", ex.getMessage())));
        }
        return response;
    }

    public Response<List<RoomDto>> findAllRoomsByUserId(Long userId, Response<List<RoomDto>> response, Pageable pageable) {
        try {
            Page<Room> rooms = roomRepository.findAllRoomsIdByUserId(userId, pageable);
            if (!rooms.isEmpty()) {
                List<RoomDto> roomDtos = rooms.stream().map(room -> objectMapper.convertValue(room, RoomDto.class)).toList();
                response.setData(roomDtos);
            }
        } catch (RuntimeException ex) {
            response.setErrors(Arrays.asList(new Error("RUNTIME_EXCEPTION", ex.getMessage())));
        }
        return response;
    }

    public Response<String> deleteRoomById(Long roomId, Response<String> response) {
        try {
            Optional<Room> optionalRoom = roomRepository.findById(roomId);
            if (optionalRoom.isEmpty()) {
                response.setErrors(List.of(new Error("ERROR_ROOM_NOT_FOUND", ErrorConstants.ERROR_ROOM_NOT_FOUND)));
                return response;
            }

            Set<Tenant> tenantSet = optionalRoom.get().getTenants();
            if (!tenantSet.isEmpty()) {
                response.setErrors(List.of(new Error("ERROR_ROOM_NOT_EMPTY", ErrorConstants.ERROR_ROOM_NOT_EMPTY)));
                return response;
            }

            roomRepository.deleteById(roomId);
            response.setData("Room deleted with id of " + roomId);
        } catch (RuntimeException ex) {
            response.setErrors(Arrays.asList(new Error("RUNTIME_EXCEPTION", ex.getMessage())));
        }
        return response;
    }

    public Response<RoomDto> modifyRoom(Long id, RoomDto roomDto, Response<RoomDto> response) {
        try {
            Optional<Room> optionalRoom = roomRepository.findById(id);
            if (optionalRoom.isPresent()) {
                Room room = optionalRoom.get();
                room.setRoomNo(roomDto.getRoomNo());
                room.setCapacity(roomDto.getCapacity());
                room.setFloorNo(roomDto.getFloorNo());
                //room.setTenants(getTenantsSet(roomDto.getTenants()));
                room.setHostel(new Hostel(roomDto.getHostel().getId()));
                room = roomRepository.save(room);
                roomDto = objectMapper.convertValue(room, RoomDto.class);
                response.setData(roomDto);
            } else {
                response.setErrors(List.of(new Error("ERROR_ROOM_NOT_FOUND", ErrorConstants.ERROR_ROOM_NOT_FOUND)));
            }
        } catch (RuntimeException ex) {
            response.setErrors(Arrays.asList(new Error("RUNTIME_EXCEPTION", ex.getMessage())));
        }
        return response;
    }

    private Set<Tenant> getTenantsSet(Set<TenantDto> tenantDtoSet){
        return tenantDtoSet.stream().map(tenantDto -> objectMapper.convertValue(tenantDto, Tenant.class)).collect(Collectors.toSet());
    }
}
