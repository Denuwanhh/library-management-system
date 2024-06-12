package lms.book.management.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lms.book.management.entity.Book;
import lms.book.management.entity.BookDTO;
import lms.book.management.entity.BookRegistry;
import lms.book.management.entity.User;
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
@RequestMapping(value = "book-management/book-registries")
public class BookRegistryManagementController {

    final Logger LOGGER = LoggerFactory.getLogger(BookRegistryManagementController.class);

    @Autowired
    private BookManagementService bookManagementService;

    @Operation(summary = "Get registry record by passing registry-id", description = "This method provide registry object belongs to registry-id.")
    @GetMapping("/{registry-id}")
    public ResponseEntity<BookRegistry> getBookByBookRegistryID(@PathVariable("registry-id") int bookRegistryID) {
        ResponseEntity<BookRegistry> responseEntity = null;
        try{
            responseEntity = ResponseEntity.ok(bookManagementService.getBookByBookRegistryID(bookRegistryID));
            LOGGER.info(String.join(" | ","GET", "book-management/book-registries", String.valueOf(bookRegistryID)), responseEntity.getBody());
        }catch (LMSResourceNotFoundException ex){
            LOGGER.error(String.join(" | ", "GET", "book-management/book-registries", String.valueOf(bookRegistryID)), ex);
            throw ex;
        }
        return responseEntity;
    }

    @Operation(summary = "Create new registry record", description = "This method facilitate to create new registry record with validation.")
    @PostMapping()
    public ResponseEntity<BookRegistry> createNewBookRegistry(@RequestBody BookRegistry bookRegistry) {
        ResponseEntity<BookRegistry> responseEntity = null;
        try{
            responseEntity = new ResponseEntity<BookRegistry>(bookManagementService.createNewBookRegistry(bookRegistry), HttpStatus.CREATED);
            LOGGER.info(String.join(" | ","POST", "book-management/books", bookRegistry.toString()), responseEntity.getBody());
        }catch (LMSBadRequestException ex){
            LOGGER.error(String.join(" | ", "POST", "book-management/books", bookRegistry.toString()), ex);
            throw ex;
        }
        return responseEntity;
    }

}
