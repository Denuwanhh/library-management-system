package lms.book.management.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lms.book.management.entity.Book;
import lms.book.management.entity.BookDTO;
import lms.book.management.exception.LMSBadRequestException;
import lms.book.management.exception.LMSResourceNotFoundException;
import lms.book.management.service.BookManagementService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "book-management/books")
public class BookManagementController {
    final Logger LOGGER = LoggerFactory.getLogger(BookManagementController.class);

    @Autowired
    private BookManagementService bookManagementService;

    @Operation(summary = "Get book by passing book ID", description = "This method provide book object belongs to book ID")
    @GetMapping("/{book-id}")
    public ResponseEntity<Book> getBookByBookId(@PathVariable("book-id") int bookId) {
        ResponseEntity<Book> responseEntity = null;
        try{
            responseEntity = ResponseEntity.ok(bookManagementService.getBookByBookId(bookId));
            LOGGER.info(String.join(" | ","GET", "book-management/books", String.valueOf(bookId)), responseEntity.getBody());
        }catch (LMSResourceNotFoundException ex){
            LOGGER.error(String.join(" | ", "GET", "book-management/books", String.valueOf(bookId)), ex);
            throw ex;
        }
        return responseEntity;
    }

    @Operation(summary = "Create new book", description = "This method facilitate to create new book with validation")
    @PostMapping()
    public ResponseEntity<List<Book>> createNewBook(@RequestBody @Valid BookDTO bookRequest) {
        ResponseEntity<List<Book>> responseEntity = null;
        try{
            responseEntity = new ResponseEntity<>(bookManagementService.createNewBook(bookRequest), HttpStatus.CREATED);
            LOGGER.info(String.join(" | ","POST", "book-management/books", bookRequest.toString()), responseEntity.getBody());
        }catch (LMSBadRequestException ex){
            LOGGER.error(String.join(" | ", "POST", "book-management/books", bookRequest.toString()), ex);
            throw ex;
        }
        return responseEntity;
    }

}
