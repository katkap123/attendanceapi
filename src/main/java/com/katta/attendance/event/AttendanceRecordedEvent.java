package com.katta.attendance.event;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import com.katta.attendance.model.AttendanceStatus;

public record AttendanceRecordedEvent(
        UUID attendanceId,
        UUID studentId,
        UUID classId,
        String className,
        LocalDate attendanceDate,
        AttendanceStatus status,
        UUID recordedBy,
        LocalDateTime recordedAt
) {
}