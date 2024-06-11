package lms.book.management;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lms.book.management.entity.Book;
import lms.book.management.entity.BookDTO;
import lms.book.management.entity.BookRegistry;
import lms.book.management.entity.BookRepository;
import lms.book.management.service.BookManagementService;
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

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class LmsAPIIntegrationTests {
    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private BookManagementService bookManagementService;

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
    @Sql(statements = "INSERT INTO lms_book_t (bookID, isbn, title, author, copies) VALUES (2, '978-90-274-3964-3', 'Microservices-II', 'Sam Newman', 1)", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(statements = "DELETE FROM lms_book_t WHERE bookID=2", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void when_Pass_Valid_ID_Should_Return_Book() {
        HttpEntity<String> entity = new HttpEntity<>(null, headers);

        ResponseEntity<Book> response = restTemplate.exchange(
                createURLWithPort() + "/book-management/books/1", HttpMethod.GET, entity, new ParameterizedTypeReference<Book>(){});

        Book book = response.getBody();
        assert book != null;
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Microservices", book.getTitle());
        assertEquals("978-90-274-3964-2", book.getISBN());
        assertEquals("Sam Newman", book.getAuthor());
        assertEquals(1, book.getCopies());
    }

    @Test
    public void when_Pass_Invalid_ID_Should_Return_Not_Found() {
        HttpEntity<String> entity = new HttpEntity<>(null, headers);

        ResponseEntity<Book> response = restTemplate.exchange(
                createURLWithPort() + "/book-management/books/3", HttpMethod.GET, entity, new ParameterizedTypeReference<Book>(){});

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    @Sql(statements = "INSERT INTO lms_book_t (bookID, isbn, title, author, copies) VALUES (2, '978-90-274-3964-3', 'Microservices-II', 'Sam Newman', 1)", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(statements = "DELETE FROM lms_book_t WHERE bookID=2", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void when_Pass_Valid_Book_Should_Create_New_Copy() throws JsonProcessingException {

        Book book = new Book();
        book.setCopies(2);
        book.setISBN("978-90-274-3964-3");
        book.setTitle("Microservices-II");
        book.setAuthor("Sam Newman");

        HttpEntity<String> entity = new HttpEntity<>(objectMapper.writeValueAsString(book), headers);
        ResponseEntity<List<Book>> response = restTemplate.exchange(
                createURLWithPort() + "/book-management/books" , HttpMethod.POST, entity, new ParameterizedTypeReference<>() {
                });

        assertEquals(HttpStatus.CREATED, response.getStatusCode());

        List<Book> bookList = response.getBody();
        assert bookList != null;
        bookList.forEach(b -> {
            assertEquals(3, b.getCopies());
        });
    }

    @Test
    @Sql(statements = "DELETE FROM lms_book_t WHERE bookID=3", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void when_Pass_Valid_Book_Should_Create_New_Book() throws JsonProcessingException {

        Book book = new Book();
        book.setBookID(3);
        book.setCopies(2);
        book.setISBN("978-90-274-3964-4");
        book.setTitle("Microservices-II");
        book.setAuthor("Sam Newman");

        HttpEntity<String> entity = new HttpEntity<>(objectMapper.writeValueAsString(book), headers);
        ResponseEntity<List<Book>> response = restTemplate.exchange(
                createURLWithPort() + "/book-management/books" , HttpMethod.POST, entity, new ParameterizedTypeReference<>() {
                });

        assertEquals(HttpStatus.CREATED, response.getStatusCode());

        List<Book> bookList = response.getBody();
        assert bookList != null;
        bookList.forEach(b -> {
            assertEquals(2, b.getCopies());
        });
    }

    @Test
    public void when_Pass_Invalid_Book_Should_Return_Error() throws JsonProcessingException {

        BookDTO book = new BookDTO();
        book.setBookId(1);
        book.setCopies(2);
        book.setISBN("978-90-274-3964-4");
        book.setTitle("Microservices-II");
        book.setAuthor("Sam Newman");

        HttpEntity<String> entity = new HttpEntity<>(objectMapper.writeValueAsString(book), headers);
        ResponseEntity<Object> response = restTemplate.exchange(
                createURLWithPort() + "/book-management/books" , HttpMethod.POST, entity, new ParameterizedTypeReference<>() {
                });

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void when_Pass_Invalid_Book_Name_Should_Return_Error() throws JsonProcessingException {

        BookDTO book = new BookDTO();
        book.setBookId(5);
        book.setCopies(2);
        book.setISBN("978-90-274-3964-4");
        book.setAuthor("Sam Newman");

        HttpEntity<String> entity = new HttpEntity<>(objectMapper.writeValueAsString(book), headers);
        ResponseEntity<Object> response = restTemplate.exchange(
                createURLWithPort() + "/book-management/books" , HttpMethod.POST, entity, new ParameterizedTypeReference<>() {
                });

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

    }

    @Test
    @Sql(statements = "INSERT INTO lms_book_registry_t (bookRegistryID, bookID, userID, borrowDate, isReturn) VALUES (2, 1, 1, PARSEDATETIME('12/03/2024','dd/MM/yyyy'), false);", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(statements = "DELETE FROM lms_book_registry_t WHERE bookRegistryID=2", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void when_Pass_Valid_ID_Should_Return_Book_Registry() {
        HttpEntity<String> entity = new HttpEntity<>(null, headers);

        ResponseEntity<BookRegistry> response = restTemplate.exchange(
                createURLWithPort() + "/book-management/book-registries/2", HttpMethod.GET, entity, new ParameterizedTypeReference<BookRegistry>(){});

        BookRegistry bookRegistry = response.getBody();
        assert bookRegistry != null;
        assertEquals(HttpStatus.OK, response.getStatusCode());

        assertEquals(1, bookRegistry.getBookID());
        assertEquals(1, bookRegistry.getUserID());
        assertFalse(bookRegistry.isReturn());
    }

    @Test
    @Sql(statements = "INSERT INTO lms_book_t (bookID, isbn, title, author, copies) VALUES (2, '978-90-274-3964-3', 'Microservices-II', 'Sam Newman', 1)", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(statements = "DELETE FROM lms_book_t WHERE bookID=2", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Sql(statements = "DELETE FROM lms_book_registry_t WHERE bookRegistryID=2", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void when_Book_Available_Should_Create_Registry_Entry() throws JsonProcessingException {

        BookRegistry bookRegistry = new BookRegistry();
        bookRegistry.setBookRegistryID(2);
        bookRegistry.setBookID(2);
        bookRegistry.setUserID(1);

        HttpEntity<String> entity = new HttpEntity<>(objectMapper.writeValueAsString(bookRegistry), headers);
        ResponseEntity<BookRegistry> response = restTemplate.exchange(
                createURLWithPort() + "/book-management/book-registries" , HttpMethod.POST, entity, new ParameterizedTypeReference<>() {
                });

        assertEquals(HttpStatus.CREATED, response.getStatusCode());

        BookRegistry bookRegistryRes = response.getBody();
        assert bookRegistryRes != null;
        assertEquals(2, bookRegistryRes.getBookID());
        assertEquals(1, bookRegistryRes.getUserID());
    }

    @Test
    @Sql(statements = "INSERT INTO lms_book_t (bookID, isbn, title, author, copies) VALUES (2, '978-90-274-3964-3', 'Microservices-II', 'Sam Newman', 0)", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(statements = "DELETE FROM lms_book_t WHERE bookID=2", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Sql(statements = "DELETE FROM lms_book_registry_t WHERE bookRegistryID=2", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void when_Book_Not_Available_Should_Return_Error() throws JsonProcessingException {

        BookRegistry bookRegistry = new BookRegistry();
        bookRegistry.setBookRegistryID(2);
        bookRegistry.setBookID(2);
        bookRegistry.setUserID(1);

        HttpEntity<String> entity = new HttpEntity<>(objectMapper.writeValueAsString(bookRegistry), headers);
        ResponseEntity<BookRegistry> response = restTemplate.exchange(
                createURLWithPort() + "/book-management/book-registries" , HttpMethod.POST, entity, new ParameterizedTypeReference<>() {
                });

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void when_Book_Not_Exist_Should_Return_Error() throws JsonProcessingException {

        BookRegistry bookRegistry = new BookRegistry();
        bookRegistry.setBookRegistryID(2);
        bookRegistry.setBookID(2);
        bookRegistry.setUserID(1);

        HttpEntity<String> entity = new HttpEntity<>(objectMapper.writeValueAsString(bookRegistry), headers);
        ResponseEntity<BookRegistry> response = restTemplate.exchange(
                createURLWithPort() + "/book-management/book-registries" , HttpMethod.POST, entity, new ParameterizedTypeReference<>() {
                });

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void when_User_Not_Exist_Should_Return_Error() throws JsonProcessingException {

        BookRegistry bookRegistry = new BookRegistry();
        bookRegistry.setBookRegistryID(2);
        bookRegistry.setBookID(2);
        bookRegistry.setUserID(2);

        HttpEntity<String> entity = new HttpEntity<>(objectMapper.writeValueAsString(bookRegistry), headers);
        ResponseEntity<BookRegistry> response = restTemplate.exchange(
                createURLWithPort() + "/book-management/book-registries" , HttpMethod.POST, entity, new ParameterizedTypeReference<>() {
                });

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    @Sql(statements = "INSERT INTO lms_book_t (bookID, isbn, title, author, copies) VALUES (2, '978-90-274-3964-3', 'Microservices-II', 'Sam Newman', 0)", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(statements = "DELETE FROM lms_book_t WHERE bookID=2", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Sql(statements = "INSERT INTO lms_book_registry_t (bookRegistryID, bookID, userID, borrowDate, isReturn) VALUES (2, 2, 1, PARSEDATETIME('12/03/2024','dd/MM/yyyy'), false);", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(statements = "DELETE FROM lms_book_registry_t WHERE bookRegistryID=2", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void when_User_Have_BR_Entry_Should_Return_Error() throws JsonProcessingException {

        BookRegistry bookRegistry = new BookRegistry();
        bookRegistry.setBookRegistryID(3);
        bookRegistry.setBookID(2);
        bookRegistry.setUserID(1);

        HttpEntity<String> entity = new HttpEntity<>(objectMapper.writeValueAsString(bookRegistry), headers);
        ResponseEntity<BookRegistry> response = restTemplate.exchange(
                createURLWithPort() + "/book-management/book-registries" , HttpMethod.POST, entity, new ParameterizedTypeReference<>() {
                });

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
}
