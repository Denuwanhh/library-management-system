package lms.book.management.service;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lms.book.management.entity.*;
import lms.book.management.exception.LMSBadRequestException;
import lms.book.management.exception.LMSResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class BookManagementService {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private BookRegistryRepository bookRegistryRepository;

    /**
     * This method responsible for provide book entry related to the book id
     * @param bookId
     * @return Book if not available then throw Exception
     */
    public Book getBookByBookId(int bookId) {
        return bookRepository.findById(bookId).orElseThrow(() -> new LMSResourceNotFoundException("Book not found with id " + bookId));
    }

    /**
     * This method responsible for create new book entry. Book ID validation conduct.
     * And if same book available with same ISBN, then increase the number of copies
     * @param bookRequest
     * @return List<Book>
     */
    public List<Book> createNewBook(BookDTO bookRequest) {

        if(bookRepository.findById(bookRequest.getBookId()).isPresent()){
            throw new LMSBadRequestException("Book is already exist.");
        }

        List<Book> bookList = bookRepository.findByISBN(bookRequest.getISBN());

        bookList.forEach(book -> {
            int currentCount = book.getCopies();
            book.setCopies(currentCount + bookRequest.getCopies());
        });

        bookRepository.saveAll(bookList);

        return bookList;
    }

    /**
     * This method responsible for create new book registry record. First check the book & user is valid.
     * And then check user already have this book or not. After that check we have enough copies available or not.
     * @param bookRegistry
     * @return BookRegistry object
     */
    public BookRegistry createNewBookRegistry(BookRegistry bookRegistry) {
        BookRegistry newBookRegistry = null;

        User user = getUserByUserId(bookRegistry.getUserID());
        Optional<Book> book = bookRepository.findById(bookRegistry.getBookID());

        if(user != null && book.isPresent()){

            List<BookRegistry> bookRegistryList = bookRegistryRepository.findByBookRegistryIDAndIsReturn(bookRegistry.getBookID(), false);

            bookRegistryList.stream()
                    .filter(br -> br.getUserID() == bookRegistry.getUserID())
                    .findAny()
                    .ifPresent(value -> {
                        throw new LMSBadRequestException("This user already have this book");
                    });

            if(book.get().getCopies() - bookRegistryList.size() > 0) {
                bookRegistry.setBorrowDate(new Date());
                bookRegistry.setReturn(false);
                newBookRegistry = bookRegistryRepository.save(bookRegistry);
            }else {
                throw new LMSResourceNotFoundException("Not book available.");
            }
        }else {
            throw new LMSBadRequestException("Invalid User or Book.");
        }

        return newBookRegistry;
    }

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
    public User getDefaultUser(int userId, IllegalStateException e){
        return new User(userId, "N/A", "N/A", UserStatus.ACTIVE);
    }

    /**
     * This method responsible to return book registry entry related to ID.
     * @param bookRegistryID
     * @return BookRegistry object
     */
    public BookRegistry getBookByBookRegistryID(int bookRegistryID) {
        return bookRegistryRepository.findById(bookRegistryID)
                .map(br -> {
                    br.setUser(getUserByUserId(br.getUserID()));
                    return br;
                })
                .orElseThrow(() -> new LMSResourceNotFoundException("Book Registry not found with id " + bookRegistryID));
    }
}
