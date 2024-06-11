package lms.book.management.entity;

public record User(int userID, String name, String email, UserStatus userStatus) {
}
