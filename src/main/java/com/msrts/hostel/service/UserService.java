package com.msrts.hostel.service;

import com.msrts.hostel.entity.User;
import com.msrts.hostel.model.ChangePasswordRequest;
import com.msrts.hostel.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final PasswordEncoder passwordEncoder;
    @Autowired
    private final UserRepository userRepository;

    public void changePassword(ChangePasswordRequest request, Principal connectedUser) {

        var user = (User) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();

        // check if the current password is correct
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new IllegalStateException("Wrong password");
        }
        // check if the two new passwords are the same
        if (!request.getNewPassword().equals(request.getConfirmationPassword())) {
            throw new IllegalStateException("Password are not the same");
        }

        // update the password
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));

        // save the new password
        userRepository.save(user);
    }

    public User getUserByIdOrEmail(Integer id, String email){
        if(id > 0) {
            Optional<User> optionalUser = userRepository.findById(id);
            return optionalUser.isPresent()? optionalUser.get(): null;
        } else if(email.trim().length()>0){
            return userRepository.findByEmail(email);
        } else {
            return null;
        }
    }

}
