package lms.book.management.exception;

public class LMSBadRequestException extends RuntimeException {
    public LMSBadRequestException(String message) {
        super(message);
    }
}
