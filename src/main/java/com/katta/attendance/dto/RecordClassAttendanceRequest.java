package com.katta.attendance.dto;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

import jakarta.validation.constraints.NotNull;

public record RecordClassAttendanceRequest(

        @NotNull
        LocalDate attendanceDate,

        @NotNull
        Set<UUID> absentStudentIds

) {
}