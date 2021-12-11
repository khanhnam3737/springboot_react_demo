ALTER TABLE courses
    ALTER department SET NOT NULL;

ALTER TABLE students
    DROP CONSTRAINT IF EXISTS student_gender_check;

CREATE TYPE gender AS ENUM ('MALE', 'FEMALE');
ALTER TABLE students
    ALTER COLUMN gender TYPE gender USING (gender::gender);