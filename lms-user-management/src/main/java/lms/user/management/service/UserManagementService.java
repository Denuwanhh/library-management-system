package lms.user.management.service;

import lms.user.management.entity.User;
import lms.user.management.entity.UserDTO;
import lms.user.management.entity.UserRepository;
import lms.user.management.entity.UserStatus;
import lms.user.management.exception.LMSBadRequestException;
import lms.user.management.exception.LMSResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserManagementService {

    @Autowired
    private UserRepository userRepository;

    /**
     * This method responsible for return user belongs to given user ID
     * @param userId
     * @return User object, if not available throw an exception
     */
    @Cacheable(value="user", key="#userId")
    public User getUserByUserId(int userId) {
        return userRepository.findById(userId).orElseThrow(() -> new LMSResourceNotFoundException("User not found with id " + userId));
    }

    /**
     * This method responsible for create new user. Validate the user ID & email already exist or not.
     * @param userRequest
     * @return User object
     */
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

    /**
     * This method use to modify user object
     * @param userId
     * @param user
     * @return User object, or throw an error
     */
    @CachePut(value="user", key="#userId")
    public User updateUserDetails(int userId, User user) {
        User excistingUser = userRepository.findById(userId).orElseThrow(() -> new LMSBadRequestException("User not found with id " + userId));

        excistingUser.setEmail(user.getEmail() == null || user.getEmail().isEmpty() ? excistingUser.getEmail() : user.getEmail());
        excistingUser.setName(user.getName() == null || user.getName().isEmpty() ? excistingUser.getName() : user.getName());
        excistingUser.setUserStatus(user.getUserStatus() == null ? excistingUser.getUserStatus() : user.getUserStatus());

        return userRepository.save(excistingUser);
    }
}
