package com.katta.attendance.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.katta.attendance.model.Attendance;
import com.katta.attendance.security.JwtService;
import com.katta.attendance.service.AttendanceService;

@RestController
@RequestMapping("/api/student/attendance")
public class StudentAttendanceController {

    private final AttendanceService attendanceService;
    private final JwtService jwtService;

    public StudentAttendanceController(
            AttendanceService attendanceService,
            JwtService jwtService) {
        this.attendanceService = attendanceService;
        this.jwtService = jwtService;
    }

    @GetMapping
    public ResponseEntity<List<Attendance>> getAttendance(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate startDate,
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate endDate) {

        String token = authHeader.substring(7);

        UUID studentId = jwtService.extractUserId(token);

        List<Attendance> attendance =
                attendanceService.getStudentAttendance(
                        studentId,
                        startDate,
                        endDate
                );

        return ResponseEntity.ok(attendance);
    }
}