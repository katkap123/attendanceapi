package com.katta.attendance.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.katta.attendance.model.Attendance;

public interface AttendanceRepository
        extends JpaRepository<Attendance, UUID> {

    List<Attendance> findByStudentIdAndAttendanceDateBetweenOrderByAttendanceDateAsc(
            UUID studentId,
            LocalDate startDate,
            LocalDate endDate
    );
}