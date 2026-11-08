package in.strikes.studentmanagement.dto;

public record StudentResponseDTO(
        Long id,
        String name,
        int age,
        String email,
        int rollNo,
        String subject
) {
}
