package org.example.livecode.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.livecode.dto.request.ItemRequest;
import org.example.livecode.dto.response.ItemResponse;
import org.example.livecode.entity.Item;
import org.example.livecode.service.ItemService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @GetMapping
    public ResponseEntity<Page<ItemResponse>> list(Pageable pageable) {
        return ResponseEntity.ok(itemService.list(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ItemResponse> get(@PathVariable Long id) {
        return ResponseEntity.ok(itemService.getById(id));
    }

    @PostMapping
    public ResponseEntity<ItemResponse> save(
            @RequestBody @Valid ItemRequest item) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(itemService.save(item));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ItemResponse> update(
            @PathVariable Long id,
            @RequestBody @Valid ItemRequest item) {
        return ResponseEntity.ok(itemService.update(id, item));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ItemResponse> delete(@PathVariable Long id) {
        ItemResponse deletedItem = itemService.delete(id);
        return ResponseEntity.ok(deletedItem);
    }

}
