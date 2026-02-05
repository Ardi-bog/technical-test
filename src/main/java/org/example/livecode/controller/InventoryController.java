package org.example.livecode.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.livecode.dto.request.InventoryRequest;
import org.example.livecode.dto.response.InventoryResponse;
import org.example.livecode.service.InventoryService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    @GetMapping
    public ResponseEntity<Page<InventoryResponse>> list(Pageable pageable) {
        return ResponseEntity.ok(inventoryService.list(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<InventoryResponse> get(@PathVariable Long id) {
        return ResponseEntity.ok(inventoryService.getById(id));
    }

    @PostMapping("/{itemId}")
    public ResponseEntity<InventoryResponse> save(
            @PathVariable Long itemId,
            @RequestBody @Valid InventoryRequest inventory) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(inventoryService.save(itemId, inventory));
    }

    @PutMapping("/{id}")
    public ResponseEntity<InventoryResponse> update(
            @PathVariable Long id,
            @RequestBody @Valid InventoryRequest inventory) {
        return ResponseEntity.ok(inventoryService.update(id, inventory));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<InventoryResponse> delete(@PathVariable Long id) {
        return ResponseEntity.ok(inventoryService.delete(id));
    }

}

