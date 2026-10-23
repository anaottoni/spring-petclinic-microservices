package org.springframework.samples.petclinic.customers.controller;

import io.micrometer.core.annotation.Timed;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.http.HttpStatus;
import org.springframework.samples.petclinic.customers.dto.OwnerRequest;
import org.springframework.samples.petclinic.customers.dto.OwnerResponse;
import org.springframework.samples.petclinic.customers.exception.ResourceNotFoundException;
import org.springframework.samples.petclinic.customers.mapper.CustomerMapper;
import org.springframework.samples.petclinic.customers.mapper.OwnerEntityMapper;
import org.springframework.samples.petclinic.customers.model.Owner;
import org.springframework.samples.petclinic.customers.model.OwnerRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/owners")
@Timed("petclinic.owner")
public class OwnerController {

    private final OwnerRepository ownerRepository;
    private final OwnerEntityMapper ownerEntityMapper;

    public OwnerController(OwnerRepository ownerRepository, OwnerEntityMapper ownerEntityMapper) {
        this.ownerRepository = ownerRepository;
        this.ownerEntityMapper = ownerEntityMapper;
    }

    @GetMapping
    public List<OwnerResponse> findAll() {
        return ownerRepository.findAll().stream().map(CustomerMapper::toOwner).toList();
    }

    @GetMapping("/{ownerId}")
    public OwnerResponse findById(@PathVariable @Min(1) int ownerId) {
        return CustomerMapper.toOwner(loadOwner(ownerId));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OwnerResponse create(@Valid @RequestBody OwnerRequest request) {
        Owner owner = ownerEntityMapper.map(new Owner(), request);
        return CustomerMapper.toOwner(ownerRepository.save(owner));
    }

    @PutMapping("/{ownerId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void update(@PathVariable @Min(1) int ownerId, @Valid @RequestBody OwnerRequest request) {
        Owner owner = loadOwner(ownerId);
        ownerEntityMapper.map(owner, request);
        ownerRepository.save(owner);
    }

    private Owner loadOwner(int ownerId) {
        return ownerRepository.findById(ownerId)
            .orElseThrow(() -> new ResourceNotFoundException("Owner " + ownerId + " not found"));
    }

}
