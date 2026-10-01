package com.katta.attendance.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.katta.attendance.dto.CreateClassRequest;
import com.katta.attendance.model.SchoolClass;
import com.katta.attendance.model.StudentClass;
import com.katta.attendance.service.SchoolClassService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/admin/classes")
public class AdminClassController {

    private final SchoolClassService schoolClassService;

    public AdminClassController(
            SchoolClassService schoolClassService) {

        this.schoolClassService = schoolClassService;
    }

    @PostMapping
    public ResponseEntity<SchoolClass> createClass(
            @Valid @RequestBody CreateClassRequest request) {

        SchoolClass schoolClass =
                schoolClassService.createClass(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(schoolClass);
    }

    @GetMapping("/{classId}/students")
    public ResponseEntity<List<StudentClass>> getStudentsByClass(
            @PathVariable UUID classId) {

        return ResponseEntity.ok(
                schoolClassService.getStudentsByClass(classId)
        );
    }

    @PostMapping("/{classId}/students/{studentId}")
    public ResponseEntity<StudentClass> assignStudentToClass(
            @PathVariable UUID classId,
            @PathVariable UUID studentId) {

        StudentClass studentClass;
        studentClass = schoolClassService.assignStudentToClass(
                classId,
                studentId
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(studentClass);
    }
}