package org.springframework.samples.petclinic.customers.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;

public record PetRequest(@NotBlank String name,
                         @NotNull @PastOrPresent @JsonFormat(pattern = "yyyy-MM-dd") LocalDate birthDate,
                         @Positive int typeId) {
}
