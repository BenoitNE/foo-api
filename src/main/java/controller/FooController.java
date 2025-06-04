package controller;

import dto.FooRequestDTO;
import dto.FooResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import service.FooService;

@RestController
@RequestMapping("/api/v1/foos")
@RequiredArgsConstructor
@Tag(name = "Foo Management", description = "APIs for managing Foo resources")
public class FooController {

    private final FooService fooService;

    @Operation(summary = "Create a new Foo")
    @PostMapping
    public ResponseEntity<FooResponseDTO> createFoo(@Valid @RequestBody FooRequestDTO fooRequestDTO) {
        FooResponseDTO createdFoo = fooService.createFoo(fooRequestDTO);
        return new ResponseEntity<>(createdFoo, HttpStatus.CREATED);
    }

    @Operation(summary = "Get a Foo by its ID")
    @GetMapping("/{id}")
    public ResponseEntity<FooResponseDTO> getFooById(@PathVariable Long id) {
        FooResponseDTO foo = fooService.getFooById(id);
        return ResponseEntity.ok(foo);
    }

    // ... autres endpoints (getAll, update, delete)
}
