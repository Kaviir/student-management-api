package in.strikes.studentmanagement.mapper;

import in.strikes.studentmanagement.dto.StudentRequestDTO;
import in.strikes.studentmanagement.dto.StudentResponseDTO;
import in.strikes.studentmanagement.entity.Student;

public final class StudentMapper {

    private StudentMapper() {
    }

    public static Student toEntity(StudentRequestDTO request) {
        Student student = new Student();
        applyToEntity(request, student);
        return student;
    }

    public static void applyToEntity(StudentRequestDTO request, Student student) {
        student.setName(request.name().trim());
        student.setAge(request.age());
        student.setEmail(request.email().trim().toLowerCase());
        student.setRollNo(request.rollNo());
        student.setSubject(request.subject().trim());
    }

    public static StudentResponseDTO toResponse(Student student) {
        return new StudentResponseDTO(
                student.getId(),
                student.getName(),
                student.getAge(),
                student.getEmail(),
                student.getRollNo(),
                student.getSubject()
        );
    }
}
