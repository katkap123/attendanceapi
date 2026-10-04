package com.katta.attendance.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.katta.attendance.dto.CreateAttendanceRequest;
import com.katta.attendance.dto.RecordClassAttendanceRequest;
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

    @PostMapping("/class/{classId}")
    public ResponseEntity<List<Attendance>> recordClassAttendance(
            @PathVariable UUID classId,
            @Valid @RequestBody RecordClassAttendanceRequest request,
            @RequestHeader("Authorization") String authHeader) {

        String token = authHeader.substring(7);

        UUID userId = jwtService.extractUserId(token);

        List<String> roles = jwtService.extractRoles(token);

        boolean isAdmin = roles != null && roles.contains("ADMIN");

        List<Attendance> attendance =
                attendanceService.recordClassAttendance(
                        classId,
                        request,
                        userId,
                        isAdmin
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(attendance);
    }

    @GetMapping("/class/{classId}")
        public ResponseEntity<List<Attendance>> getClassAttendance(
                @PathVariable UUID classId,
                @RequestParam LocalDate date,
                @RequestHeader("Authorization") String authHeader) {

        String token = authHeader.substring(7);

        UUID userId = jwtService.extractUserId(token);

        List<String> roles = jwtService.extractRoles(token);

        boolean isAdmin =
                roles != null && roles.contains("ADMIN");

        List<Attendance> attendance =
                attendanceService.getClassAttendance(
                        classId,
                        date,
                        userId,
                        isAdmin
                );

        return ResponseEntity.ok(attendance);
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