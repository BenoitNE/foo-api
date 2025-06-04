package service.impl;

import dto.FooRequestDTO;
import dto.FooResponseDTO;
import entity.Foo;
import exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;


import lombok.extern.slf4j.Slf4j;
import mapper.FooMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import repository.FooRepository;
import service.FooService;

@Service
@RequiredArgsConstructor
@Slf4j
public class FooServiceImpl implements FooService {

    private final FooRepository fooRepository;
    private final FooMapper fooMapper;

    @Override
    @Transactional
    public FooResponseDTO createFoo(FooRequestDTO fooRequestDTO) {
        log.info("Creating new Foo with name: {}", fooRequestDTO.name());
        Foo foo = fooMapper.toEntity(fooRequestDTO);
        Foo savedFoo = fooRepository.save(foo);
        log.info("Foo created with ID: {}", savedFoo.getId());
        return fooMapper.toResponseDTO(savedFoo);
    }

    @Override
    @Transactional(readOnly = true)
    public FooResponseDTO getFooById(Long id) {
        log.debug("Fetching Foo with ID: {}", id);
        Foo foo = fooRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Foo not found with ID: {}", id);
                    return new ResourceNotFoundException("Foo not found with id: " + id);
                });
        return fooMapper.toResponseDTO(foo);
    }
    // ... autres méthodes
}
