package com.msrts.hostel.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.msrts.hostel.entity.Address;
import com.msrts.hostel.entity.Hostel;
import com.msrts.hostel.entity.User;
import com.msrts.hostel.exception.ErrorConstants;
import com.msrts.hostel.model.Error;
import com.msrts.hostel.model.HostelDto;
import com.msrts.hostel.model.Response;
import com.msrts.hostel.repository.HostelRepository;
import com.msrts.hostel.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class HostelService {
    //private static final Logger log = LoggerFactory.getLogger(HostelService.class);

    @Autowired
    private HostelRepository hostelRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    public Response<List<HostelDto>> findAllHostelsByUserId(Response<List<HostelDto>> response, Integer userId, Pageable pageable){
        //log.info("Start of finding all hostels by current logged in user id");
        if(validateUser(userId)) {
            //log.error(ErrorConstants.ERROR_USER_NOT_FOUND);
            Error error = new Error("ERROR_USER_NOT_FOUND", ErrorConstants.ERROR_USER_NOT_FOUND);
            response.setErrors(List.of(error));
            return response;
        }

        List<Hostel> hostelList = hostelRepository.findAllHostelsByUserId(new User(userId), pageable);
        if(hostelList != null && !hostelList.isEmpty()) {
            List<HostelDto> hostelDtos = hostelList.stream()
                    .map(hostel -> objectMapper.convertValue(hostel, HostelDto.class)).toList();
            response.setData(hostelDtos);
        }
        //log.info("End of finding all hostels by current logged in user id");
        return response;
    }

    public Response<HostelDto> createHostel(Response<HostelDto> response, HostelDto hostelDto) {
        //log.info("Start of creating new hostel");
        Hostel hostel = objectMapper.convertValue(hostelDto, Hostel.class);
        hostel = hostelRepository.save(hostel);
        hostelDto = objectMapper.convertValue(hostel, HostelDto.class);
        if(hostelDto != null) {
            response.setData(hostelDto);
        }
        //log.info("End of creating new hostel");
        return response;
    }

    private boolean validateUser(Integer userId) {
        Optional<User> optionalUser = userRepository.findById(userId);
        return optionalUser.isEmpty();
    }

    public Response<HostelDto> modifyHostel(Long id, HostelDto hostelDto, Response<HostelDto> response) {
        Optional<Hostel> optionalHostel = hostelRepository.findById(id);
        if(optionalHostel.isPresent()) {
            Hostel hostel = optionalHostel.get();
            hostel.setName(hostelDto.getName());
            hostel.setType(hostelDto.getType());
            hostel.setActive(hostelDto.isActive());
            hostel.setRooms(hostelDto.getRooms());
            hostel.setOwner(hostelDto.getOwner());
            hostel.setAddress(objectMapper.convertValue(hostelDto.getAddress(), Address.class));
            hostel = hostelRepository.save(hostel);
            hostelDto = objectMapper.convertValue(hostel, HostelDto.class);
            response.setData(hostelDto);
        } else {
            response.setErrors(List.of(new Error("ERROR_HOSTEL_NOT_FOUND", ErrorConstants.ERROR_HOSTEL_NOT_FOUND)));
        }
        return response;
    }

    public Response<String> deleteHostel(Long id, Response<String> response) {
        Optional<Hostel> optionalHostel = hostelRepository.findById(id);
        if(optionalHostel.isPresent()) {
            Hostel hostel = optionalHostel.get();
            hostel.setActive(false);
            hostelRepository.save(hostel);
            response.setData(hostel.getName() + " hostel deleted successfully");
        } else {
            response.setErrors(List.of(new Error("ERROR_HOSTEL_NOT_FOUND", ErrorConstants.ERROR_HOSTEL_NOT_FOUND)));
        }
        return response;
    }
}
