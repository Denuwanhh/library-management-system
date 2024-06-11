package lms.book.management.entity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, Integer> {

    List<Book> findByISBN(String Isbn);
    List<Book> findByTitle(String Isbn);
}
