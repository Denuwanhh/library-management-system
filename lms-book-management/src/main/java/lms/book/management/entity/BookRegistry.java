package lms.book.management.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

import java.util.Date;

@Entity
@Table(name="lms_book_registry_t")
public class BookRegistry {

    @Id
    private int bookRegistryID;
    private int bookID;

    private int userID;

    private Date borrowDate;

    private boolean isReturn;

    @Transient
    private User user;

    public int getBookRegistryID() {
        return bookRegistryID;
    }

    public void setBookRegistryID(int bookRegistryID) {
        this.bookRegistryID = bookRegistryID;
    }

    public int getBookID() {
        return bookID;
    }

    public void setBookID(int bookID) {
        this.bookID = bookID;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public Date getBorrowDate() {
        return borrowDate;
    }

    public void setBorrowDate(Date borrowDate) {
        this.borrowDate = borrowDate;
    }

    public boolean isReturn() {
        return isReturn;
    }

    public void setReturn(boolean aReturn) {
        isReturn = aReturn;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
