package com.khanhnam.demo.student;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class StudentRepository {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public StudentRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    List<Student> selectAllStudents() {
        String sql = "" +
                "SELECT " +
                " student_id, " +
                " first_name, " +
                " last_name, " +
                " email, " +
                " gender " +
                "FROM students";

        return jdbcTemplate.query(sql, mapStudentFomDb());
    }

    int insertStudent(UUID studentId, Student student) {
        String sql = "" +
                "INSERT INTO students (" +
                " student_id, " +
                " first_name, " +
                " last_name, " +
                " email, " +
                " gender) " +
                "VALUES (?, ?, ?, ?, ?::gender)";
        return jdbcTemplate.update(
                sql,
                studentId,
                student.getFirstName(),
                student.getLastName(),
                student.getEmail(),
                student.getGender().name().toUpperCase()
        );
    }

    @SuppressWarnings("ConstantConditions")
    boolean isEmailTaken(String email) {
        String sql = "" +
                "SELECT EXISTS ( " +
                " SELECT 1 " +
                " FROM students " +
                " WHERE email = ?" +
                ")";
        return jdbcTemplate.queryForObject(
                sql,
                new Object[]{email},
                (resultSet, i) -> resultSet.getBoolean(1)
        );
    }

    List<StudentRegistration> selectAllStudentCourses(UUID studentId) {
        String sql = "" +
                "SELECT " +
                " s.student_id, " +
                " c.course_id, " +
                " c.name, " +
                " c.description," +
                " c.department," +
                " sc.start_date, " +
                " sc.end_date, " +
                " sc.grade " +
                "FROM students AS s " +
                "JOIN student_course AS sc USING (student_id) " +
                "JOIN course AS c USING (course_id) " +
                "WHERE s.student_id = ?";
        return jdbcTemplate.query(
                sql,
                new Object[]{studentId},
                mapStudentCourseFromDb()
        );
    }

    private RowMapper<StudentRegistration> mapStudentCourseFromDb() {
        return (resultSet, i) ->
                new StudentRegistration(
                        UUID.fromString(resultSet.getString("student_id")),
                        UUID.fromString(resultSet.getString("course_id")),
                        resultSet.getString("name"),
                        resultSet.getString("description"),
                        resultSet.getString("department"),
                        resultSet.getString("teacher_name"),
                        resultSet.getDate("start_date").toLocalDate(),
                        resultSet.getDate("end_date").toLocalDate(),
                        Optional.ofNullable(resultSet.getString("grade"))
                                .map(Integer::parseInt)
                                .orElse(null)
                );
    }

    private RowMapper<Student> mapStudentFomDb() {
        return (resultSet, i) -> {
            String studentIdStr = resultSet.getString("student_id");
            UUID studentId = UUID.fromString(studentIdStr);
            String firstName = resultSet.getString("first_name");
            String lastName = resultSet.getString("last_name");
            String email = resultSet.getString("email");
            String genderStr = resultSet.getString("gender").toUpperCase();
            Student.Gender gender = Student.Gender.valueOf(genderStr);
            return new Student(
                    studentId,
                    firstName,
                    lastName,
                    email,
                    gender
            );
        };
    }

    int updateEmail(UUID studentId, String email) {
        String sql = "" +
                "UPDATE students " +
                "SET email = ? " +
                "WHERE student_id = ?";
        return jdbcTemplate.update(sql, email, studentId);
    }

    int updateFirstName(UUID studentId, String firstName) {
        String sql = "" +
                "UPDATE students " +
                "SET first_name = ? " +
                "WHERE student_id = ?";
        return jdbcTemplate.update(sql, firstName, studentId);
    }

    int updateLastName(UUID studentId, String lastName) {
        String sql = "" +
                "UPDATE students " +
                "SET last_name = ? " +
                "WHERE student_id = ?";
        return jdbcTemplate.update(sql, lastName, studentId);
    }

    @SuppressWarnings("ConstantConditions")
    boolean selectExistsEmail(UUID studentId, String email) {
        String sql = "" +
                "SELECT EXISTS ( " +
                "   SELECT 1 " +
                "   FROM students " +
                "   WHERE student_id <> ? " +
                "    AND email = ? " +
                ")";
        return jdbcTemplate.queryForObject(
                sql,
                new Object[]{studentId, email},
                (resultSet, columnIndex) -> resultSet.getBoolean(1)
        );
    }

    int deleteStudentById(UUID studentId) {
        String sql = "" +
                "DELETE FROM students " +
                "WHERE student_id = ?";
        return jdbcTemplate.update(sql, studentId);
    }
}
