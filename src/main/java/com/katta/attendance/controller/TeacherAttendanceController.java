package com.katta.attendance.controller;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.katta.attendance.dto.CreateAttendanceRequest;
import com.katta.attendance.model.Attendance;
import com.katta.attendance.security.JwtService;
import com.katta.attendance.service.AttendanceService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/teacher/attendance")
public class TeacherAttendanceController {

    private final AttendanceService attendanceService;
    private final JwtService jwtService;

    public TeacherAttendanceController(
            AttendanceService attendanceService,
            JwtService jwtService) {

        this.attendanceService = attendanceService;
        this.jwtService = jwtService;
    }

    @PostMapping
    public ResponseEntity<Attendance> recordAttendance(
            @Valid @RequestBody CreateAttendanceRequest request,
            @RequestHeader("Authorization") String authHeader) {

        String token = authHeader.substring(7);

        UUID teacherId = jwtService.extractUserId(token);

        Attendance attendance =
                attendanceService.recordAttendance(
                        request,
                        teacherId
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(attendance);
    }
}