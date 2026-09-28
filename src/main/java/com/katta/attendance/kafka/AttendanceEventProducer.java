package com.katta.attendance.kafka;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.katta.attendance.event.AttendanceRecordedEvent;

@Service
public class AttendanceEventProducer {

    private static final String TOPIC = "attendance.recorded";

    private final KafkaTemplate<String, AttendanceRecordedEvent> kafkaTemplate;

    public AttendanceEventProducer(
            KafkaTemplate<String, AttendanceRecordedEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishAttendanceRecorded(
            AttendanceRecordedEvent event) {

        kafkaTemplate.send(
                TOPIC,
                event.studentId().toString(),
                event
        );
    }
}