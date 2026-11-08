package in.strikes.studentmanagement.service;

import in.strikes.studentmanagement.dto.PageResponse;
import in.strikes.studentmanagement.dto.StudentRequestDTO;
import in.strikes.studentmanagement.dto.StudentResponseDTO;
import in.strikes.studentmanagement.entity.Student;
import in.strikes.studentmanagement.exception.DuplicateResourceException;
import in.strikes.studentmanagement.exception.StudentNotFoundException;
import in.strikes.studentmanagement.mapper.StudentMapper;
import in.strikes.studentmanagement.repository.StudentRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class StudentService {

    private final StudentRepository studentRepository;

    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    @Transactional
    public StudentResponseDTO createStudent(StudentRequestDTO request) {
        String email = normalizeEmail(request.email());

        if (studentRepository.existsByEmail(email)) {
            throw new DuplicateResourceException("Student already exists with email: " + email);
        }
        if (studentRepository.existsByRollNo(request.rollNo())) {
            throw new DuplicateResourceException("Student already exists with roll number: " + request.rollNo());
        }

        Student student = StudentMapper.toEntity(request);
        student.setDeleted(false);

        return StudentMapper.toResponse(studentRepository.save(student));
    }

    public StudentResponseDTO getStudent(Long id) {
        return StudentMapper.toResponse(findActiveStudent(id));
    }

    public PageResponse<StudentResponseDTO> getAllStudents(Pageable pageable) {
        Page<StudentResponseDTO> page = studentRepository
                .findByDeletedIsFalse(pageable)
                .map(StudentMapper::toResponse);
        return PageResponse.from(page);
    }

    @Transactional
    public StudentResponseDTO updateStudent(Long id, StudentRequestDTO request) {
        Student student = findActiveStudent(id);
        String email = normalizeEmail(request.email());

        if (studentRepository.existsByEmailAndIdNot(email, id)) {
            throw new DuplicateResourceException("Student already exists with email: " + email);
        }
        if (studentRepository.existsByRollNoAndIdNot(request.rollNo(), id)) {
            throw new DuplicateResourceException("Student already exists with roll number: " + request.rollNo());
        }

        StudentMapper.applyToEntity(request, student);

        return StudentMapper.toResponse(studentRepository.save(student));
    }

    // Soft delete: record DB mein rehta hai, bas deleted = true ho jata hai
    @Transactional
    public void deleteStudent(Long id) {
        Student student = findActiveStudent(id);
        student.setDeleted(true);
        studentRepository.save(student);
    }

    private Student findActiveStudent(Long id) {
        return studentRepository.findByIdAndDeletedIsFalse(id)
                .orElseThrow(() -> new StudentNotFoundException(id));
    }

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase();
    }
}
