package in.strikes.studentmanagement.service;

import in.strikes.studentmanagement.dto.StudentRequestDTO;
import in.strikes.studentmanagement.dto.StudentResponseDTO;
import in.strikes.studentmanagement.entity.Student;
import in.strikes.studentmanagement.exception.DuplicateResourceException;
import in.strikes.studentmanagement.exception.StudentNotFoundException;
import in.strikes.studentmanagement.repository.StudentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StudentServiceTest {

    @Mock
    private StudentRepository studentRepository;

    @InjectMocks
    private StudentService studentService;

    private StudentRequestDTO sampleRequest() {
        return new StudentRequestDTO("Kavir", 22, "Kavir@Example.com", 101, "Computer Science");
    }

    private Student sampleStudent(Long id) {
        Student student = new Student();
        student.setId(id);
        student.setName("Kavir");
        student.setAge(22);
        student.setEmail("kavir@example.com");
        student.setRollNo(101);
        student.setSubject("Computer Science");
        student.setDeleted(false);
        return student;
    }

    @Test
    void createStudent_success_savesNormalizedEmail() {
        when(studentRepository.existsByEmail("kavir@example.com")).thenReturn(false);
        when(studentRepository.existsByRollNo(101)).thenReturn(false);
        when(studentRepository.save(any(Student.class))).thenAnswer(invocation -> {
            Student saved = invocation.getArgument(0);
            saved.setId(1L);
            return saved;
        });

        StudentResponseDTO response = studentService.createStudent(sampleRequest());

        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.email()).isEqualTo("kavir@example.com");
        assertThat(response.name()).isEqualTo("Kavir");
    }

    @Test
    void createStudent_duplicateEmail_throwsConflict() {
        when(studentRepository.existsByEmail("kavir@example.com")).thenReturn(true);

        assertThatThrownBy(() -> studentService.createStudent(sampleRequest()))
                .isInstanceOf(DuplicateResourceException.class);

        verify(studentRepository, never()).save(any(Student.class));
    }

    @Test
    void getStudent_notFound_throwsException() {
        when(studentRepository.findByIdAndDeletedIsFalse(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> studentService.getStudent(99L))
                .isInstanceOf(StudentNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void updateStudent_success_updatesFields() {
        Student existing = sampleStudent(1L);
        when(studentRepository.findByIdAndDeletedIsFalse(1L)).thenReturn(Optional.of(existing));
        when(studentRepository.existsByEmailAndIdNot("kavir@example.com", 1L)).thenReturn(false);
        when(studentRepository.existsByRollNoAndIdNot(101, 1L)).thenReturn(false);
        when(studentRepository.save(any(Student.class))).thenAnswer(invocation -> invocation.getArgument(0));

        StudentRequestDTO request = new StudentRequestDTO("Kavir B", 23, "kavir@example.com", 101, "Data Science");
        StudentResponseDTO response = studentService.updateStudent(1L, request);

        assertThat(response.name()).isEqualTo("Kavir B");
        assertThat(response.age()).isEqualTo(23);
        assertThat(response.subject()).isEqualTo("Data Science");
    }

    @Test
    void deleteStudent_marksStudentAsDeleted() {
        Student existing = sampleStudent(1L);
        when(studentRepository.findByIdAndDeletedIsFalse(1L)).thenReturn(Optional.of(existing));

        studentService.deleteStudent(1L);

        assertThat(existing.isDeleted()).isTrue();
        verify(studentRepository).save(existing);
    }

    @Test
    void deleteStudent_notFound_throwsException() {
        when(studentRepository.findByIdAndDeletedIsFalse(5L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> studentService.deleteStudent(5L))
                .isInstanceOf(StudentNotFoundException.class);
    }
}
