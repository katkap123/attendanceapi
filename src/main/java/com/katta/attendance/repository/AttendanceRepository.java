package com.katta.attendance.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
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
        
        Optional<Attendance> findByStudentIdAndClassIdAndAttendanceDate(
                        UUID studentId,
                        UUID classId,
                        LocalDate attendanceDate
                );

        List<Attendance> findByClassIdAndAttendanceDate(
        UUID classId,
        LocalDate attendanceDate);

}