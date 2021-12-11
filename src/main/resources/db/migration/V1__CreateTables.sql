CREATE TABLE IF NOT EXISTS students (
    student_id UUID PRIMARY KEY NOT NULL,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    gender VARCHAR(6) NOT NULL
        CHECK ( gender in('MALE', 'male', 'FEMALE', 'female'))
);
CREATE TABLE IF NOT EXISTS courses (
    course_id UUID NOT NULL PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    description TEXT NOT NULL,
    department VARCHAR(255)
);
CREATE TABLE IF NOT EXISTS resgistration (
    student_id UUID NOT NULL REFERENCES students (student_id),
    course_id UUID NOT NULL REFERENCES courses (course_id),
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    grade INTEGER CHECK (grade >= 0 AND grade <= 100),
    UNIQUE (student_id, course_id)
);
