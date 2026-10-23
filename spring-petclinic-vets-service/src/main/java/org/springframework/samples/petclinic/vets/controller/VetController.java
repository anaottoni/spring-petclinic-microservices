package org.springframework.samples.petclinic.vets.controller;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.samples.petclinic.vets.dto.SpecialtyResponse;
import org.springframework.samples.petclinic.vets.dto.VetResponse;
import org.springframework.samples.petclinic.vets.model.Vet;
import org.springframework.samples.petclinic.vets.model.VetRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/vets")
public class VetController {

    private final VetRepository vetRepository;

    public VetController(VetRepository vetRepository) {
        this.vetRepository = vetRepository;
    }

    @GetMapping
    @Cacheable("vets")
    public List<VetResponse> findAll() {
        return vetRepository.findAll().stream().map(VetController::toResponse).toList();
    }

    private static VetResponse toResponse(Vet vet) {
        List<SpecialtyResponse> specialties = vet.getSpecialties().stream()
            .map(specialty -> new SpecialtyResponse(specialty.getId(), specialty.getName()))
            .toList();
        return new VetResponse(vet.getId(), vet.getFirstName(), vet.getLastName(), specialties);
    }

}
