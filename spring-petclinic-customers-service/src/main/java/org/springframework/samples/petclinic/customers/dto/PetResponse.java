package org.springframework.samples.petclinic.customers.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;

public record PetResponse(Integer id,
                          String name,
                          @JsonFormat(pattern = "yyyy-MM-dd") LocalDate birthDate,
                          PetTypeResponse type) {
}
