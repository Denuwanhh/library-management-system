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

    public Book getBookByBookId(int bookId) {
        return bookRepository.findById(bookId).orElseThrow(() -> new LMSResourceNotFoundException("Book not found with id " + bookId));
    }

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
                throw new LMSResourceNotFoundException("Not boot available.");
            }
        }else {
            throw new LMSBadRequestException("Invalid User or Book.");
        }

        return newBookRegistry;
    }
    @CircuitBreaker(name = "DEFAULT-USER", fallbackMethod = "getDefaultUser")
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

    public User getDefaultUser(){
        return new User(-1, "Admin", "admin@lms.com", UserStatus.ACTIVE);
    }

    public BookRegistry getBookByBookRegistryID(int bookRegistryID) {
        return bookRegistryRepository.findById(bookRegistryID).orElseThrow(() -> new LMSResourceNotFoundException("Book Registry not found with id " + bookRegistryID));
    }
}
