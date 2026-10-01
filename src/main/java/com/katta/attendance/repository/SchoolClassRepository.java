package com.katta.attendance.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.katta.attendance.model.SchoolClass;

public interface SchoolClassRepository
        extends JpaRepository<SchoolClass, UUID> {

    List<SchoolClass> findByClassTeacherId(UUID classTeacherId);
    
}