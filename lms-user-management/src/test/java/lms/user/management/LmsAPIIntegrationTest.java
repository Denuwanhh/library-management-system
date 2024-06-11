package lms.user.management;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lms.user.management.entity.User;
import lms.user.management.entity.UserRepository;
import lms.user.management.service.UserManagementService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class LmsAPIIntegrationTest {
    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserManagementService userManagementService;

    private static HttpHeaders headers;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeAll
    public static void init() {
        headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
    }
    private String createURLWithPort() {
        return "http://localhost:" + port;
    }

    @Test
    @Sql(statements = "INSERT INTO lms_user_t (userid, name, email) VALUES (2, 'Denuwan', 'denuwan@gmail.com')", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(statements = "DELETE FROM lms_user_t WHERE userid=2", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void when_Pass_Valid_ID_Should_Return_User() {
        HttpEntity<String> entity = new HttpEntity<>(null, headers);

        ResponseEntity<User> response = restTemplate.exchange(
                createURLWithPort() + "/user-management/users/2", HttpMethod.GET, entity, new ParameterizedTypeReference<User>(){});

        User user = response.getBody();
        assert user != null;
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Denuwan", user.getName());
    }

    @Test
    public void when_Pass_Invalid_ID_Should_Return_Not_Found() {
        HttpEntity<String> entity = new HttpEntity<>(null, headers);

        ResponseEntity<User> response = restTemplate.exchange(
                createURLWithPort() + "/user-management/users/12", HttpMethod.GET, entity, new ParameterizedTypeReference<User>(){});

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void when_Pass_Invalid_Value_Should_Return_Bad_Request() {
        HttpEntity<String> entity = new HttpEntity<>(null, headers);

        ResponseEntity<User> response = restTemplate.exchange(
                createURLWithPort() + "/user-management/users/abc", HttpMethod.GET, entity, new ParameterizedTypeReference<User>(){});

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    @Sql(statements = "DELETE FROM lms_user_t WHERE userid=3", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void when_Pass_Valid_User_Should_Create_New_User() throws JsonProcessingException {

        User user = new User();
        user.setUserID(3);
        user.setName("Kamal");
        user.setEmail("kamal@gmail.com");

        HttpEntity<String> entity = new HttpEntity<>(objectMapper.writeValueAsString(user), headers);
        ResponseEntity<User> response = restTemplate.exchange(
                createURLWithPort() + "/user-management/users" , HttpMethod.POST, entity, User.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());

        User userRes = Objects.requireNonNull(response.getBody());

        assertEquals(userRes.getName(), "Kamal");
        assertEquals(userRes.getEmail(), "kamal@gmail.com");
        assertEquals(userRes.getUserID(), 3);
    }

    @Test
    public void when_Pass_Invalid_User_Should_Throw_An_Error() throws JsonProcessingException {

        User user = new User();
        user.setUserID(3);
        user.setName("Kamal");
        user.setEmail("kamalgmail.com");

        HttpEntity<String> entity = new HttpEntity<>(objectMapper.writeValueAsString(user), headers);
        ResponseEntity<User> response = restTemplate.exchange(
                createURLWithPort() + "/user-management/users" , HttpMethod.POST, entity, User.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    @Sql(statements = "INSERT INTO lms_user_t (userid, name, email) VALUES (2, 'Denuwan', 'denuwan@gmail.com')", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(statements = "DELETE FROM lms_user_t WHERE userid=2", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void when_Pass_Duplicate_User_ID_Should_Throw_An_Error() throws JsonProcessingException {

        User user = new User();
        user.setUserID(2);
        user.setName("Kamal");
        user.setEmail("kamal@gmail.com");

        HttpEntity<String> entity = new HttpEntity<>(objectMapper.writeValueAsString(user), headers);
        ResponseEntity<User> response = restTemplate.exchange(
                createURLWithPort() + "/user-management/users" , HttpMethod.POST, entity, User.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    @Sql(statements = "INSERT INTO lms_user_t (userid, name, email) VALUES (2, 'Denuwan', 'denuwan@gmail.com')", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(statements = "DELETE FROM lms_user_t WHERE userid=2", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void when_Pass_Duplicate_User_email_Should_Throw_An_Error() throws JsonProcessingException {

        User user = new User();
        user.setUserID(3);
        user.setName("Kamal");
        user.setEmail("denuwan@gmail.com");

        HttpEntity<String> entity = new HttpEntity<>(objectMapper.writeValueAsString(user), headers);
        ResponseEntity<User> response = restTemplate.exchange(
                createURLWithPort() + "/user-management/users" , HttpMethod.POST, entity, User.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
}
