package org.springframework.samples.petclinic.customers.mapper;

import org.springframework.samples.petclinic.customers.dto.OwnerResponse;
import org.springframework.samples.petclinic.customers.dto.PetResponse;
import org.springframework.samples.petclinic.customers.dto.PetTypeResponse;
import org.springframework.samples.petclinic.customers.model.Owner;
import org.springframework.samples.petclinic.customers.model.Pet;
import org.springframework.samples.petclinic.customers.model.PetType;

import java.sql.Date;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Comparator;

public final class CustomerMapper {

    private CustomerMapper() {
    }

    public static OwnerResponse toOwner(Owner owner) {
        return new OwnerResponse(
            owner.getId(),
            owner.getFirstName(),
            owner.getLastName(),
            owner.getAddress(),
            owner.getCity(),
            owner.getTelephone(),
            owner.getPets().stream()
                .sorted(Comparator.comparing(Pet::getName, Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER)))
                .map(CustomerMapper::toPet)
                .toList()
        );
    }

    public static PetResponse toPet(Pet pet) {
        return new PetResponse(pet.getId(), pet.getName(), toLocalDate(pet.getBirthDate()), toPetType(pet.getType()));
    }

    public static PetTypeResponse toPetType(PetType type) {
        return type == null ? null : new PetTypeResponse(type.getId(), type.getName());
    }

    public static Date toSqlDate(LocalDate date) {
        return Date.valueOf(date);
    }

    private static LocalDate toLocalDate(java.util.Date date) {
        if (date == null) {
            return null;
        }
        if (date instanceof Date sqlDate) {
            return sqlDate.toLocalDate();
        }
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

}
