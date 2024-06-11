package lms.book.management.entity;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class BookDTO {

    private int bookId;
    private String ISBN;

    @NotBlank(message = "Invalid Title: Empty Title")
    @NotNull(message = "Invalid Title: Title is NULL")
    private String title;
    @NotBlank(message = "Invalid Author: Empty Author")
    @NotNull(message = "Invalid Author: Author is NULL")
    private String author;

    private int copies;

    public int getBookId() {
        return bookId;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getISBN() {
        return ISBN;
    }

    public void setISBN(String ISBN) {
        this.ISBN = ISBN;
    }

    public int getCopies() {
        return copies;
    }

    public void setCopies(int copies) {
        this.copies = copies;
    }
}
