package org.springframework.samples.petclinic.customers.controller;

import io.micrometer.core.annotation.Timed;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.http.HttpStatus;
import org.springframework.samples.petclinic.customers.dto.PetRequest;
import org.springframework.samples.petclinic.customers.dto.PetResponse;
import org.springframework.samples.petclinic.customers.dto.PetTypeResponse;
import org.springframework.samples.petclinic.customers.exception.ResourceNotFoundException;
import org.springframework.samples.petclinic.customers.mapper.CustomerMapper;
import org.springframework.samples.petclinic.customers.model.Owner;
import org.springframework.samples.petclinic.customers.model.OwnerRepository;
import org.springframework.samples.petclinic.customers.model.Pet;
import org.springframework.samples.petclinic.customers.model.PetRepository;
import org.springframework.samples.petclinic.customers.model.PetType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api")
@Timed("petclinic.pet")
public class PetController {

    private final PetRepository petRepository;
    private final OwnerRepository ownerRepository;

    public PetController(PetRepository petRepository, OwnerRepository ownerRepository) {
        this.petRepository = petRepository;
        this.ownerRepository = ownerRepository;
    }

    @GetMapping("/pet-types")
    public List<PetTypeResponse> findPetTypes() {
        return petRepository.findPetTypes().stream().map(CustomerMapper::toPetType).toList();
    }

    @PostMapping("/owners/{ownerId}/pets")
    @ResponseStatus(HttpStatus.CREATED)
    public PetResponse create(@PathVariable @Min(1) int ownerId, @Valid @RequestBody PetRequest request) {
        Owner owner = loadOwner(ownerId);
        if (hasAnotherPetWithName(owner, request.name(), null)) {
            throw conflict();
        }
        Pet pet = new Pet();
        owner.addPet(pet);
        apply(pet, request);
        return CustomerMapper.toPet(petRepository.save(pet));
    }

    @GetMapping("/owners/{ownerId}/pets/{petId}")
    public PetResponse findById(@PathVariable @Min(1) int ownerId, @PathVariable @Min(1) int petId) {
        loadOwner(ownerId);
        return CustomerMapper.toPet(loadPetOfOwner(ownerId, petId));
    }

    @PutMapping("/owners/{ownerId}/pets/{petId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void update(@PathVariable @Min(1) int ownerId, @PathVariable @Min(1) int petId,
                       @Valid @RequestBody PetRequest request) {
        Owner owner = loadOwner(ownerId);
        Pet pet = loadPetOfOwner(ownerId, petId);
        if (hasAnotherPetWithName(owner, request.name(), petId)) {
            throw conflict();
        }
        apply(pet, request);
        petRepository.save(pet);
    }

    private Owner loadOwner(int ownerId) {
        return ownerRepository.findById(ownerId)
            .orElseThrow(() -> new ResourceNotFoundException("Owner " + ownerId + " not found"));
    }

    private Pet loadPetOfOwner(int ownerId, int petId) {
        Pet pet = petRepository.findById(petId)
            .orElseThrow(() -> new ResourceNotFoundException("Pet " + petId + " not found"));
        if (pet.getOwner() == null || !Objects.equals(pet.getOwner().getId(), ownerId)) {
            throw new ResourceNotFoundException("Pet " + petId + " not found");
        }
        return pet;
    }

    private void apply(Pet pet, PetRequest request) {
        PetType type = petRepository.findPetTypeById(request.typeId())
            .orElseThrow(() -> new ResourceNotFoundException("Pet type not found"));
        pet.setName(request.name());
        pet.setBirthDate(CustomerMapper.toSqlDate(request.birthDate()));
        pet.setType(type);
    }

    private static boolean hasAnotherPetWithName(Owner owner, String name, Integer currentPetId) {
        return owner.getPets().stream()
            .anyMatch(pet -> name.equalsIgnoreCase(pet.getName()) && !Objects.equals(pet.getId(), currentPetId));
    }

    private static ResponseStatusException conflict() {
        return new ResponseStatusException(HttpStatus.CONFLICT, "Pet name already exists for this owner");
    }

}
