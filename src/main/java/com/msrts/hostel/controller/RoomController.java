package com.msrts.hostel.controller;

import com.msrts.hostel.exception.ErrorConstants;
import com.msrts.hostel.model.Error;
import com.msrts.hostel.model.Response;
import com.msrts.hostel.model.RoomDto;
import com.msrts.hostel.service.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/api/v1/room")
public class RoomController {
    @Autowired
    private RoomService roomService;

    @GetMapping(path = "/rooms-by-hostel-id", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<List<RoomDto>> getAllRoomsByHostelId(@RequestParam(required = true) Long hostelId,
                                                               @RequestParam(defaultValue = "0") int page,
                                                               @RequestParam(defaultValue = "10") int size,
                                                               @RequestParam(defaultValue = "id,desc") String[] sort) {
        Response<List<RoomDto>> response = new Response<>();
        Pageable pagingSort = PageRequest.of(page, size, Sort.by(sort));
        if (hostelId == 0 || hostelId < 0) {
            Error error = new Error("INVALID_INPUT_ID", ErrorConstants.INVALID_INPUT_ID);
            response.setErrors(List.of(error));
            return response;
        }
        return roomService.findAllRoomsByHostelId(hostelId, response, pagingSort);
    }

    @PostMapping(path = "/create-room", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public Response<RoomDto> createRoom(@RequestBody RoomDto roomDto) {
        Response<RoomDto> response = new Response<>();
        if(roomDto==null) {
            response.setErrors(List.of(new Error("INVALID_REQUEST", ErrorConstants.INVALID_REQUEST)));
            return response;
        }
        return roomService.createRoom(roomDto, response);
    }

    @PutMapping(path = "/modify-room/{id}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public Response<RoomDto> modifyRoom(@PathVariable("id") Long id, @RequestBody RoomDto roomDto) {
        Response<RoomDto> response = new Response<>();
        if(id== null || roomDto==null) {
            response.setErrors(List.of(new Error("INVALID_REQUEST", ErrorConstants.INVALID_REQUEST)));
            return response;
        }
        return roomService.modifyRoom(id, roomDto, response);
    }


    @DeleteMapping(path = "/delete-room-by-id/{id}")
    public Response<String> deleteRoomById(@PathVariable(value = "id") Long id) {
        Response<String> response = new Response<>();
        if(id == null) {
            response.setErrors(List.of(new Error("INVALID_INPUT_ID",ErrorConstants.INVALID_INPUT_ID)));
            return response;
        }
        return roomService.deleteRoomById(id, response);
    }
}
