package com.katta.attendance.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.katta.attendance.model.StudentClass;

public interface StudentClassRepository
        extends JpaRepository<StudentClass, UUID> {

    List<StudentClass> findByClassId(UUID classId);

    Optional<StudentClass> findByStudentId(UUID studentId);

    boolean existsByStudentId(UUID studentId);
}