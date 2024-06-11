package lms.book.management.entity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRegistryRepository extends JpaRepository<BookRegistry, Integer> {
    List<BookRegistry> findByBookRegistryIDAndIsReturn(int bookRegistryID, boolean isReturn);
}
