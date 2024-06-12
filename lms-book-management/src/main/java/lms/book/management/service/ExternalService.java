package lms.book.management.service;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lms.book.management.entity.User;
import lms.book.management.entity.UserStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Service
public class ExternalService {

    @Autowired
    private RestTemplate restTemplate;

    /**
     * This method responsible to return User by calling LMS-USER-MANAGEMENT service
     * @param userId
     * @return User object
     */
    @CircuitBreaker(name = "lms-user-service", fallbackMethod = "getDefaultUser")
    public User getUserByUserId(int userId){
        User user = null;
        try {
            ResponseEntity<User> response = restTemplate.exchange("http://LMS-USER-MANAGEMENT/user-management/users/" + userId, HttpMethod.GET, null, User.class);
            user = response.getBody();
        }catch (HttpClientErrorException ex){
            if(ex.getStatusCode() != HttpStatus.NOT_FOUND){
                throw ex;
            }
        }
        return user;
    }

    /**
     * Provide default user for Circuit Breaker
     * @return User
     */
    public User getDefaultUser(IllegalStateException ex){
        return new User(-1, "N/A", "N/A", UserStatus.ACTIVE);
    }

}
