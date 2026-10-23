package org.springframework.samples.petclinic.customers.mapper;

public interface Mapper<R, E> {
    E map(E entity, R request);
}
