package com.katta.attendance.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.katta.attendance.model.SchoolClass;
import com.katta.attendance.model.StudentClass;
import com.katta.attendance.security.JwtService;
import com.katta.attendance.service.SchoolClassService;

@RestController
@RequestMapping("/api/teacher/classes")
public class TeacherClassController {

    private final SchoolClassService schoolClassService;
    private final JwtService jwtService;

    public TeacherClassController(
            SchoolClassService schoolClassService,
            JwtService jwtService) {

        this.schoolClassService = schoolClassService;
        this.jwtService = jwtService;
    }

    @GetMapping
    public ResponseEntity<List<SchoolClass>> getMyClasses(
            @RequestHeader("Authorization") String authHeader) {

        String token = authHeader.substring(7);
        UUID teacherId = jwtService.extractUserId(token);

        return ResponseEntity.ok(
                schoolClassService.getClassesForTeacher(teacherId)
        );
    }

    @GetMapping("/{classId}/students")
    public ResponseEntity<List<StudentClass>> getStudents(
            @PathVariable UUID classId,
            @RequestHeader("Authorization") String authHeader) {

        String token = authHeader.substring(7);
        UUID teacherId = jwtService.extractUserId(token);

        return ResponseEntity.ok(
                schoolClassService.getStudentsForTeacherClass(
                        classId,
                        teacherId
                )
        );
    }
}