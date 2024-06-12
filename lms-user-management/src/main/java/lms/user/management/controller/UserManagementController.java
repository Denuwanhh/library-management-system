package lms.user.management.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lms.user.management.entity.User;
import lms.user.management.entity.UserDTO;
import lms.user.management.exception.LMSBadRequestException;
import lms.user.management.exception.LMSResourceNotFoundException;
import lms.user.management.service.UserManagementService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "user-management/users")
public class UserManagementController {

    final Logger LOGGER = LoggerFactory.getLogger(UserManagementController.class);

    @Autowired
    private UserManagementService userManagementService;

    @Operation(summary = "Get User by passing User ID", description = "This method provide user details based on user ID.")
    @GetMapping("/{user-id}")
    public ResponseEntity<User> getUserByUserId(@PathVariable("user-id") int userId) {
        ResponseEntity<User> responseEntity = null;
        try{
            responseEntity = ResponseEntity.ok(userManagementService.getUserByUserId(userId));
            LOGGER.info(String.join(" | ","GET", "user-management/users", String.valueOf(userId)), responseEntity);
        }catch (LMSResourceNotFoundException ex){
            LOGGER.error(String.join(" | ", "GET", "user-management/users", String.valueOf(userId)), ex);
            throw ex;
        }
        return responseEntity;
    }

    @Operation(summary = "Create new user", description = "This method facilitate to create new user.")
    @PostMapping()
    public ResponseEntity<User> createNewUser(@RequestBody @Valid UserDTO userRequest) {
        ResponseEntity<User> responseEntity = null;
        try{
            responseEntity = new ResponseEntity<>(userManagementService.createNewUser(userRequest), HttpStatus.CREATED);
            LOGGER.info(String.join(" | ","POST", "user-management/users", userRequest.toString()), responseEntity);
        }catch (LMSBadRequestException ex){
            LOGGER.error(String.join(" | ", "POST", "user-management/users", userRequest.toString()), ex);
            throw ex;
        }
        return responseEntity;
    }

    @Operation(summary = "Update user", description = "This method facilitate to update user data.")
    @PatchMapping("/{user-id}")
    public ResponseEntity<User> updateUserDetails(@PathVariable("user-id") int userId, @RequestBody User user) {
        ResponseEntity<User> responseEntity = null;
        try{
            responseEntity = new ResponseEntity<>(userManagementService.updateUserDetails(userId, user), HttpStatus.OK);
            LOGGER.info(String.join(" | ","PATCH", "user-management/users", user.toString()), responseEntity);
        }catch (LMSBadRequestException ex){
            LOGGER.error(String.join(" | ", "PATCH", "user-management/users", user.toString()), ex);
            throw ex;
        }
        return responseEntity;
    }
}
