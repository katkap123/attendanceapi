package com.katta.attendance.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.katta.attendance.dto.CreateAttendanceRequest;
import com.katta.attendance.event.AttendanceRecordedEvent;
import com.katta.attendance.kafka.AttendanceEventProducer;
import com.katta.attendance.model.Attendance;
import com.katta.attendance.repository.AttendanceRepository;

@Service
public class AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final AttendanceEventProducer attendanceEventProducer;
    public AttendanceService(
        AttendanceRepository attendanceRepository,
        AttendanceEventProducer attendanceEventProducer) {

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