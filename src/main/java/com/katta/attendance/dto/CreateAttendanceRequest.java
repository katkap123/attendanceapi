package com.katta.attendance.dto;

import java.time.LocalDate;
import java.util.UUID;

import com.katta.attendance.model.AttendanceStatus;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateAttendanceRequest(

        @NotNull
        UUID studentId,

        @NotNull
        UUID classId,

        @NotBlank
        String className,

        @NotNull
        LocalDate attendanceDate,

        @NotNull
        AttendanceStatus status

) {}