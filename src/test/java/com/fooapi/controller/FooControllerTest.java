package com.fooapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import controller.FooController;
import dto.FooRequestDTO;
import dto.FooResponseDTO;
import exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import service.FooService;

import java.util.Collections; // Pour getAllFoos par exemple
import java.util.List; // Pour getAllFoos par exemple

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.hasSize; // Pour les listes
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing; // Pour les méthodes void comme delete
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FooController.class) // Teste uniquement la couche FooController
// Si vous avez Spring Security, vous pourriez avoir besoin de le configurer ou de le désactiver pour ce test :
// @Import(SecurityConfig.class) // ou utiliser des annotations @WithMockUser etc.
public class FooControllerTest {

    @Autowired
    private MockMvc mockMvc; // Permet de simuler des appels HTTP

    @Mock // Crée un mock de FooService et l'injecte dans le contexte de l'application pour ce test
    private FooService fooService;

    @Autowired
    private ObjectMapper objectMapper; // Pour convertir les objets Java en JSON et vice-versa

    private FooRequestDTO fooRequestDTO;
    private FooResponseDTO fooResponseDTO;

    @BeforeEach
    void setUp() {
        fooRequestDTO = new FooRequestDTO ("Test Foo");
        fooResponseDTO = new FooResponseDTO (1L,"Test Foo");
    }

    @Test
    void createFoo_whenValidInput_shouldReturnCreated() throws Exception {
        // Given
        given(fooService.createFoo(any(FooRequestDTO.class))).willReturn(fooResponseDTO);

        // When
        ResultActions resultActions = mockMvc.perform(post("/api/v1/foos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(fooRequestDTO)));

        // Then
        resultActions.andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(fooResponseDTO.id().intValue())))
                .andExpect(jsonPath("$.name", is(fooResponseDTO.name())));
    }

    @Test
    void createFoo_whenInvalidInputNameBlank_shouldReturnBadRequest() throws Exception {
        // Given
        FooRequestDTO invalidRequest = new FooRequestDTO (""); // Nom vide, devrait échouer la validation @NotBlank

        // When
        ResultActions resultActions = mockMvc.perform(post("/api/v1/foos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)));

        // Then
        resultActions.andExpect(status().isBadRequest());
        // Vous pouvez ajouter des assertions plus précises sur le message d'erreur de validation si votre GlobalExceptionHandler le formate
        // .andExpect(jsonPath("$.errors.name", is("Name cannot be blank")));
    }

    @Test
    void createFoo_whenInvalidInputNameTooShort_shouldReturnBadRequest() throws Exception {
        // Given
        FooRequestDTO invalidRequest = new FooRequestDTO ("A"); // Nom trop court, devrait échouer la validation @Size

        // When
        ResultActions resultActions = mockMvc.perform(post("/api/v1/foos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)));

        // Then
        resultActions.andExpect(status().isBadRequest());
        // .andExpect(jsonPath("$.errors.name", is("Name must be between 2 and 50 characters")));
    }


    @Test
    void getFooById_whenFooExists_shouldReturnFoo() throws Exception {
        // Given
        Long fooId = 1L;
        given(fooService.getFooById(fooId)).willReturn(fooResponseDTO);

        // When
        ResultActions resultActions = mockMvc.perform(get("/api/v1/foos/{id}", fooId)
                .contentType(MediaType.APPLICATION_JSON));

        // Then
        resultActions.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(fooResponseDTO.id().intValue())))
                .andExpect(jsonPath("$.name", is(fooResponseDTO.name())));
    }

    @Test
    void getFooById_whenFooDoesNotExist_shouldReturnNotFound() throws Exception {
        // Given
        Long fooId = 1L;
        // Simuler le service qui lance ResourceNotFoundException
        given(fooService.getFooById(fooId)).willThrow(new ResourceNotFoundException("Foo not found with id: " + fooId));

        // When
        ResultActions resultActions = mockMvc.perform(get("/api/v1/foos/{id}", fooId)
                .contentType(MediaType.APPLICATION_JSON));

        // Then
        resultActions.andExpect(status().isNotFound());
        // Si votre GlobalExceptionHandler renvoie un corps JSON structuré :
        // .andExpect(jsonPath("$.message", is("Foo not found with id: " + fooId)));
    }

    // --- Exemple pour un endpoint getAllFoos (si vous l'avez dans FooController) ---
    /*
    @Test
    void getAllFoos_shouldReturnListOfFoos() throws Exception {
        // Given
        List<FooResponseDTO> fooList = Collections.singletonList(fooResponseDTO);
        given(fooService.getAllFoos()).willReturn(fooList); // suppose que vous avez une méthode getAllFoos

        // When
        ResultActions resultActions = mockMvc.perform(get("/api/v1/foos")
                .contentType(MediaType.APPLICATION_JSON));

        // Then
        resultActions.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(fooResponseDTO.getId().intValue())))
                .andExpect(jsonPath("$[0].name", is(fooResponseDTO.getName())));
    }
    */

    // --- Exemple pour un endpoint updateFoo (si vous l'avez dans FooController) ---
    /*
    @Test
    void updateFoo_whenFooExistsAndValidInput_shouldReturnUpdatedFoo() throws Exception {
        // Given
        Long fooId = 1L;
        FooRequestDTO updateRequest = FooRequestDTO.builder().name("Updated Foo").build();
        FooResponseDTO updatedResponse = FooResponseDTO.builder().id(fooId).name("Updated Foo").build();
        given(fooService.updateFoo(anyLong(), any(FooRequestDTO.class))).willReturn(updatedResponse);

        // When
        ResultActions resultActions = mockMvc.perform(put("/api/v1/foos/{id}", fooId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)));

        // Then
        resultActions.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(updatedResponse.getId().intValue())))
                .andExpect(jsonPath("$.name", is(updatedResponse.getName())));
    }

    @Test
    void updateFoo_whenFooDoesNotExist_shouldReturnNotFound() throws Exception {
        // Given
        Long fooId = 1L;
        FooRequestDTO updateRequest = FooRequestDTO.builder().name("Updated Foo").build();
        given(fooService.updateFoo(anyLong(), any(FooRequestDTO.class))).willThrow(new ResourceNotFoundException("Foo not found with id: " + fooId));

        // When
        ResultActions resultActions = mockMvc.perform(put("/api/v1/foos/{id}", fooId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)));

        // Then
        resultActions.andExpect(status().isNotFound());
    }
    */

    // --- Exemple pour un endpoint deleteFoo (si vous l'avez dans FooController) ---
    /*
    @Test
    void deleteFoo_whenFooExists_shouldReturnNoContent() throws Exception {
        // Given
        Long fooId = 1L;
        doNothing().when(fooService).deleteFoo(fooId); // Simule que la suppression réussit

        // When
        ResultActions resultActions = mockMvc.perform(delete("/api/v1/foos/{id}", fooId));

        // Then
        resultActions.andExpect(status().isNoContent());
    }

    @Test
    void deleteFoo_whenFooDoesNotExist_shouldReturnNotFound() throws Exception {
        // Given
        Long fooId = 1L;
        doThrow(new ResourceNotFoundException("Foo not found with id: " + fooId)).when(fooService).deleteFoo(fooId);

        // When
        ResultActions resultActions = mockMvc.perform(delete("/api/v1/foos/{id}", fooId));

        // Then
        resultActions.andExpect(status().isNotFound());
    }
    */
}
