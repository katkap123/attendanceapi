package com.katta.attendance.dto;

import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateClassRequest(

        @NotBlank
        String name,

        @NotNull
        UUID classTeacherId

) {
}