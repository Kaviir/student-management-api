package in.strikes.studentmanagement.repository;

import in.strikes.studentmanagement.entity.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student, Long> {

    // findBy + fieldName + condition
    Optional<Student> findByIdAndDeletedIsFalse(Long id);

    Page<Student> findByDeletedIsFalse(Pageable pageable);

    // Duplicate checks (DB ke unique constraint ke saath consistent: soft-deleted records bhi count hote hain)
    boolean existsByEmail(String email);

    boolean existsByRollNo(int rollNo);

    boolean existsByEmailAndIdNot(String email, Long id);

    boolean existsByRollNoAndIdNot(int rollNo, Long id);
}
