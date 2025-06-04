package com.fooapi.service;

import dto.FooResponseDTO;
import entity.Foo;
import exception.ResourceNotFoundException;
import mapper.FooMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import repository.FooRepository;
import service.impl.FooServiceImpl;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FooServiceTest {

    @Mock
    private FooRepository fooRepository;

    @Mock
    private FooMapper fooMapper;

    @InjectMocks
    private FooServiceImpl fooService;

    @Test
    void getFooById_whenFooExists_shouldReturnFoo() {
        Long fooId = 1L;
        Foo fooEntity = new Foo(fooId, "Test Foo");
        FooResponseDTO expectedResponse = new FooResponseDTO(fooId, "Test Foo");

        when(fooRepository.findById(fooId)).thenReturn(Optional.of(fooEntity));
        when(fooMapper.toResponseDTO(fooEntity)).thenReturn(expectedResponse);

        FooResponseDTO actualResponse = fooService.getFooById(fooId);

        assertNotNull(actualResponse);
        assertEquals(expectedResponse.id(), actualResponse.id());
        assertEquals(expectedResponse.name(), actualResponse.name());
        verify(fooRepository).findById(fooId);
        verify(fooMapper).toResponseDTO(fooEntity);
    }

    @Test
    void getFooById_whenFooDoesNotExist_shouldThrowResourceNotFoundException() {
        Long fooId = 1L;
        when(fooRepository.findById(fooId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> fooService.getFooById(fooId));
        verify(fooRepository).findById(fooId);
        verifyNoInteractions(fooMapper); // Ou verify(fooMapper, never()).toResponseDTO(any());
    }
}
