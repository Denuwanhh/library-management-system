package lms.user.management;

import lms.user.management.controller.UserManagementController;
import lms.user.management.entity.User;
import lms.user.management.service.UserManagementService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(UserManagementController.class)
public class UserManagementControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserManagementService userManagementService;

    private User user;

    @BeforeEach
    public void setTestData(){
        user = new User();
        user.setUserID(1);
        user.setEmail("denuwan@gmail.com");
        user.setName("Denuwan");
    }

    @Test
    public void when_Pass_Valid_ID_Should_Return_User() throws Exception {
        when(userManagementService.getUserByUserId(1))
                .thenReturn(user);

        mockMvc.perform(get("/user-management/users/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name", is("Denuwan")))
                .andExpect(jsonPath("$.email", is("denuwan@gmail.com")))
                .andExpect(jsonPath("$.userID", is(1)));
    }

    @Test
    public void when_Pass_Invalid_ID_Should_Return_Not_Found() throws Exception {
        when(userManagementService.getUserByUserId(1))
                .thenReturn(user);

        mockMvc.perform(get("/user-management/users/12"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    public void when_Pass_Invalid_Value_Should_Return_Bad_Request() throws Exception {
        when(userManagementService.getUserByUserId(1))
                .thenReturn(user);

        mockMvc.perform(get("/user-management/users/abc"))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

}
