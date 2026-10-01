package com.katta.attendance.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.katta.attendance.dto.CreateAttendanceRequest;
import com.katta.attendance.dto.RecordClassAttendanceRequest;
import com.katta.attendance.event.AttendanceRecordedEvent;
import com.katta.attendance.kafka.AttendanceEventProducer;
import com.katta.attendance.model.Attendance;
import com.katta.attendance.model.AttendanceStatus;
import com.katta.attendance.model.SchoolClass;
import com.katta.attendance.model.StudentClass;
import com.katta.attendance.repository.AttendanceRepository;
import com.katta.attendance.repository.SchoolClassRepository;
import com.katta.attendance.repository.StudentClassRepository;

import jakarta.transaction.Transactional;

@Service
public class AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final AttendanceEventProducer attendanceEventProducer;
    private final SchoolClassRepository schoolClassRepository;
    private final StudentClassRepository studentClassRepository;
    public AttendanceService(
        AttendanceRepository attendanceRepository,
        AttendanceEventProducer attendanceEventProducer,
        SchoolClassRepository schoolClassRepository,
        StudentClassRepository studentClassRepository) {
        this.schoolClassRepository = schoolClassRepository;
        this.studentClassRepository = studentClassRepository;
        this.attendanceRepository = attendanceRepository;
        this.attendanceEventProducer = attendanceEventProducer;
    }

    public Attendance recordAttendance(
        CreateAttendanceRequest request,
        UUID recordedBy) {

    Attendance attendance = new Attendance();

    attendance.setStudentId(request.studentId());
    attendance.setClassId(request.classId());
    attendance.setClassName(request.className());
    attendance.setAttendanceDate(request.attendanceDate());
    attendance.setStatus(request.status());
    attendance.setRecordedBy(recordedBy);
    attendance.setCreatedAt(LocalDateTime.now());

    // 1. Save attendance to PostgreSQL
    Attendance savedAttendance =
            attendanceRepository.save(attendance);

    // 2. Create Kafka event
    AttendanceRecordedEvent event =
            new AttendanceRecordedEvent(
                    savedAttendance.getId(),
                    savedAttendance.getStudentId(),
                    savedAttendance.getClassId(),
                    savedAttendance.getClassName(),
                    savedAttendance.getAttendanceDate(),
                    savedAttendance.getStatus(),
                    savedAttendance.getRecordedBy(),
                    savedAttendance.getCreatedAt()
            );

    // 3. Publish event
    attendanceEventProducer.publishAttendanceRecorded(event);

    // 4. Return saved attendance
    return savedAttendance;
}

    @Transactional
    public List<Attendance> recordClassAttendance(
            UUID classId,
            RecordClassAttendanceRequest request,
            UUID recordedBy,
            boolean isAdmin) {

        SchoolClass schoolClass = schoolClassRepository
                .findById(classId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Class not found"));

        if (!isAdmin &&
                !schoolClass.getClassTeacherId().equals(recordedBy)) {

            throw new SecurityException(
                    "You are not authorized to record attendance for this class"
            );
        }

        List<StudentClass> enrollments =
                studentClassRepository.findByClassId(classId);

        Set<UUID> enrolledStudentIds = enrollments.stream()
                .map(studentClass -> studentClass.getStudentId())
                .collect(Collectors.toSet());

        // Make sure teacher didn't submit a student
        // who doesn't belong to this class.
        if (!enrolledStudentIds.containsAll(request.absentStudentIds())) {
            throw new IllegalArgumentException(
                    "One or more absent students do not belong to this class"
            );
        }

        List<Attendance> results = new ArrayList<>();

        for (UUID studentId : enrolledStudentIds) {

            AttendanceStatus status;
            status = request.absentStudentIds().contains(studentId)
                    ? AttendanceStatus.ABSENT
                    : AttendanceStatus.PRESENT;

            Attendance attendance = attendanceRepository
                    .findByStudentIdAndClassIdAndAttendanceDate(
                            studentId,
                            classId,
                            request.attendanceDate()
                    )
                    .orElseGet(Attendance::new);

            attendance.setStudentId(studentId);
            attendance.setClassId(classId);
            attendance.setClassName(schoolClass.getName());
            attendance.setAttendanceDate(request.attendanceDate());
            attendance.setStatus(status);
            attendance.setRecordedBy(recordedBy);

            if (attendance.getCreatedAt() == null) {
                attendance.setCreatedAt(LocalDateTime.now());
            }

            Attendance savedAttendance =
        attendanceRepository.save(attendance);

        results.add(savedAttendance);

        AttendanceRecordedEvent event =
                new AttendanceRecordedEvent(
                        savedAttendance.getId(),
                        savedAttendance.getStudentId(),
                        savedAttendance.getClassId(),
                        savedAttendance.getClassName(),
                        savedAttendance.getAttendanceDate(),
                        savedAttendance.getStatus(),
                        savedAttendance.getRecordedBy(),
                        savedAttendance.getCreatedAt()
                );

        attendanceEventProducer.publishAttendanceRecorded(event);
        }

        return results;
    }

    public List<Attendance> getStudentAttendance(
        UUID studentId,
        LocalDate startDate,
        LocalDate endDate) {

    return attendanceRepository
            .findByStudentIdAndAttendanceDateBetweenOrderByAttendanceDateAsc(
                    studentId,
                    startDate,
                    endDate
            );
}
}