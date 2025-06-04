package com.fooapi.controller;

import dto.ErrorResponseDTO;
import dto.FooRequestDTO;
import dto.FooResponseDTO;
import entity.Foo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import repository.FooRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
// import org.testcontainers.containers.PostgreSQLContainer; // Si utilisation de Testcontainers
// import org.testcontainers.junit.jupiter.Container;
// import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
// @Testcontainers // Décommenter si Testcontainers est utilisé
class FooControllerIT {

    /* // Décommenter et configurer si Testcontainers est utilisé
    @Container
    public static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("testdb")
            .withUsername("testuser")
            .withPassword("testpass");

    static {
        // S'assurer que le conteneur démarre avant le contexte Spring
        // et configurer dynamiquement les propriétés de la datasource
        System.setProperty("spring.datasource.url", postgreSQLContainer.getJdbcUrl());
        System.setProperty("spring.datasource.username", postgreSQLContainer.getUsername());
        System.setProperty("spring.datasource.password", postgreSQLContainer.getPassword());
    }
    */

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private FooRepository fooRepository;

    @BeforeEach
    void setUp() {
        fooRepository.deleteAll(); // Nettoyer la base de données avant chaque test
    }

    private String getBaseUrl() {
        return "http://localhost:" + port + "/api/v1/foos";
    }

    @Test
    void createFoo_shouldReturnCreatedFoo() {
        FooRequestDTO request = new FooRequestDTO ("Integration Test Foo");
        ResponseEntity<FooResponseDTO> response = restTemplate.postForEntity(getBaseUrl(), request, FooResponseDTO.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().id());
        assertEquals("Integration Test Foo", response.getBody().name());
    }

    @Test
    void getFooById_whenFooExists_shouldReturnFoo() {
        Foo savedFoo = fooRepository.save(new Foo(null, "Existing Foo"));
        ResponseEntity<FooResponseDTO> response = restTemplate.getForEntity(getBaseUrl() + "/" + savedFoo.getId(), FooResponseDTO.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(savedFoo.getId(), response.getBody().id());
        assertEquals("Existing Foo", response.getBody().name());
    }

    @Test
    void getFooById_whenFooDoesNotExist_shouldReturnNotFound() {
        ResponseEntity<ErrorResponseDTO> response = restTemplate.getForEntity(getBaseUrl() + "/999", ErrorResponseDTO.class);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}
