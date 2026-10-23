package org.springframework.samples.petclinic.customers.dto;

import java.util.List;

public record OwnerResponse(Integer id,
                            String firstName,
                            String lastName,
                            String address,
                            String city,
                            String telephone,
                            List<PetResponse> pets) {
}
