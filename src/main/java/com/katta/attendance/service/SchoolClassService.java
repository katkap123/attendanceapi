package com.katta.attendance.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.katta.attendance.dto.CreateClassRequest;
import com.katta.attendance.model.SchoolClass;
import com.katta.attendance.model.StudentClass;
import com.katta.attendance.repository.SchoolClassRepository;
import com.katta.attendance.repository.StudentClassRepository;

@Service
public class SchoolClassService {

    private final SchoolClassRepository schoolClassRepository;
    private final StudentClassRepository studentClassRepository;

    public SchoolClassService(
        SchoolClassRepository schoolClassRepository,
        StudentClassRepository studentClassRepository) {

    this.schoolClassRepository = schoolClassRepository;
    this.studentClassRepository = studentClassRepository;
}

    public SchoolClass createClass(CreateClassRequest request) {

        SchoolClass schoolClass = new SchoolClass();

        schoolClass.setName(request.name());
        schoolClass.setClassTeacherId(
                request.classTeacherId()
        );

        return schoolClassRepository.save(schoolClass);
    }

    public List<StudentClass> getStudentsByClass(UUID classId) {

        if (!schoolClassRepository.existsById(classId)) {
            throw new IllegalArgumentException("Class not found");
        }

        return studentClassRepository.findByClassId(classId);
    }

    public List<SchoolClass> getClassesForTeacher(UUID teacherId) {
        return schoolClassRepository.findByClassTeacherId(teacherId);
    }

    public List<StudentClass> getStudentsForTeacherClass(
        UUID classId,
        UUID teacherId) {

        SchoolClass schoolClass = schoolClassRepository
                .findById(classId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Class not found"));

        if (!schoolClass.getClassTeacherId().equals(teacherId)) {
            throw new SecurityException(
                    "Teacher is not assigned to this class"
            );
        }

        return studentClassRepository.findByClassId(classId);
    }

    public StudentClass assignStudentToClass(
        UUID classId,
        UUID studentId) {

        // Make sure the class exists
        if (!schoolClassRepository.existsById(classId)) {
            throw new IllegalArgumentException("Class not found");
        }

        // A student can belong to only one class
        if (studentClassRepository.existsByStudentId(studentId)) {
            throw new IllegalArgumentException(
                    "Student is already assigned to a class"
            );
        }

        StudentClass studentClass = new StudentClass();
        studentClass.setStudentId(studentId);
        studentClass.setClassId(classId);

        return studentClassRepository.save(studentClass);
    }
}