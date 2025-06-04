package service;

import dto.FooRequestDTO;
import dto.FooResponseDTO;

public interface FooService {
    FooResponseDTO createFoo(FooRequestDTO fooRequestDTO);
    FooResponseDTO getFooById(Long id);
}
