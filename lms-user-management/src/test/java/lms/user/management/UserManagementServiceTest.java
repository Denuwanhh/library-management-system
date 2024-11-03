package lms.user.management;

import lms.user.management.entity.User;
import lms.user.management.entity.UserRepository;
import lms.user.management.entity.UserStatus;
import lms.user.management.service.UserManagementService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
public class UserManagementServiceTest {

    @Mock
    UserRepository userRepository;

    @InjectMocks
    UserManagementService userManagementService;

    @BeforeEach
    public void setUp(){}

    @AfterEach
    public void tearDown(){}

    @ParameterizedTest
    @CsvSource({"1,Denuwan,d@gmail.com,ACTIVE", "2,Kamal,k@gmail.com,ACTIVE"})
    @DisplayName("When Execute Get User By User Id Should Return Correct Users")
    @Order(1)
    public void whenExecuteGetUserByUserIdShouldReturnCorrectUsers(int userID, String name, String email, UserStatus userStatus){
        User user = new User();
        user.setUserID(userID);
        user.setName(name);
        user.setEmail(email);
        user.setUserStatus(userStatus);
        when(userRepository.findById(userID)).thenReturn(Optional.of(user));

        User actualUser = userManagementService.getUserByUserId(userID);
        assertEquals(userID, actualUser.getUserID());
        assertEquals(name, actualUser.getName());
        assertEquals(email, actualUser.getEmail());
        assertEquals(userStatus, actualUser.getUserStatus());
    }

}
