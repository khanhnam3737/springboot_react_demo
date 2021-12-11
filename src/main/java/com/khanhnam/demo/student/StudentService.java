package com.khanhnam.demo.student;

import com.khanhnam.demo.EmailValidator;
import com.khanhnam.demo.exception.ApiRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class StudentService {

    private final StudentRepository studentRepository;
    private final EmailValidator emailValidator;

    @Autowired
    public StudentService(
            StudentRepository studentRepository,
            EmailValidator emailValidator) {
        this.studentRepository = studentRepository;
        this.emailValidator = emailValidator;
    }

    List<Student> getAllStudents() {
        return studentRepository.selectAllStudents();
    }

    void addNewStudent(Student student) {
        addNewStudent(null, student);
    }

    void addNewStudent(UUID studentId, Student student) {
        UUID newStudentId = Optional.ofNullable(studentId)
                .orElse(UUID.randomUUID());

        if (!emailValidator.test(student.getEmail())) {
            throw new ApiRequestException(student.getEmail() + " is not valid!!!");
        }

        if (studentRepository.isEmailTaken(student.getEmail())) {
            throw new ApiRequestException(student.getEmail() + " is taken!!!");
        }

        studentRepository.insertStudent(newStudentId, student);
    }

    List<StudentRegistration> getAllCoursesForStudent(UUID studentId) {
        return studentRepository.selectAllStudentCourses(studentId);
    }

    public void updateStudent(UUID studentId, Student student) {
        Optional.ofNullable(student.getEmail())
                .ifPresent(email -> {
                    boolean taken = studentRepository.selectExistsEmail(studentId, email);
                    if (!taken) {
                        studentRepository.updateEmail(studentId, email);
                    } else {
                        throw new IllegalStateException("Email already in use: " + student.getEmail());
                    }
                });

        Optional.ofNullable(student.getFirstName())
                .filter(fistName -> !StringUtils.isEmpty(fistName))
                .map(StringUtils::capitalize)
                .ifPresent(firstName -> studentRepository.updateFirstName(studentId, firstName));

        Optional.ofNullable(student.getLastName())
                .filter(lastName -> !StringUtils.isEmpty(lastName))
                .map(StringUtils::capitalize)
                .ifPresent(lastName -> studentRepository.updateLastName(studentId, lastName));
    }

    void deleteStudent(UUID studentId) {
        studentRepository.deleteStudentById(studentId);
    }
}
