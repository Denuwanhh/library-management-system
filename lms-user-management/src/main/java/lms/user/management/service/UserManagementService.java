package lms.user.management.service;

import lms.user.management.entity.User;
import lms.user.management.entity.UserDTO;
import lms.user.management.entity.UserRepository;
import lms.user.management.entity.UserStatus;
import lms.user.management.exception.LMSBadRequestException;
import lms.user.management.exception.LMSResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserManagementService {

    @Autowired
    private UserRepository userRepository;


    @Cacheable(value="user", key="#userId")
    public User getUserByUserId(int userId) {
        return userRepository.findById(userId).orElseThrow(() -> new LMSResourceNotFoundException("User not found with id " + userId));
    }

    public User createNewUser(UserDTO userRequest) {

        if(userRepository.findByEmail(userRequest.getEmail()).size() > 0 || userRepository.findById(userRequest.getUserID()).isPresent()){
            throw new LMSBadRequestException("User already exist.");
        }

        User user = new User();
        user.setUserID(userRequest.getUserID());
        user.setName(userRequest.getName());
        user.setEmail(userRequest.getEmail());
        user.setUserStatus(UserStatus.ACTIVE);

        return userRepository.save(user);
    }
}
