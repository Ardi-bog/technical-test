package org.example.livecode.service.impl;

import org.example.livecode.dto.request.ItemRequest;
import org.example.livecode.dto.response.ItemResponse;
import org.example.livecode.entity.Item;
import org.example.livecode.exception.ResourceNotFoundException;
import org.example.livecode.repository.InventoryRepository;
import org.example.livecode.repository.ItemRepository;
import org.example.livecode.utill.Status;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class ItemServiceImplTests {

    private ItemRepository itemRepository;

    private InventoryRepository inventoryRepository;

    private ItemServiceImpl itemService;

    private Item mockItem;

    @BeforeEach
    void setUp() {
        itemRepository = mock(ItemRepository.class);
        inventoryRepository = mock(InventoryRepository.class);
        itemService = new ItemServiceImpl(itemRepository, inventoryRepository);

        mockItem = new Item();
        mockItem.setId(1L);
        mockItem.setName("Laptop");
        mockItem.setPrice(15000000.0);
    }

    @Test
    void list() {
        Pageable pageable = PageRequest.of(0, 10);
        when(itemRepository.findAll(pageable)).thenReturn(new PageImpl<>(List.of(mockItem)));
        when(inventoryRepository.getStockByItemId(1L)).thenReturn(50);

        Page<ItemResponse> result = itemService.list(pageable);

        assertFalse(result.isEmpty());
        assertEquals(1, result.getTotalElements());
        assertEquals(50, result.getContent().get(0).getStock());
    }

    @Test
    void getById() {
        when(itemRepository.findById(1L)).thenReturn(Optional.of(mockItem));
        when(inventoryRepository.getStockByItemId(1L)).thenReturn(20);

        ItemResponse result = itemService.getById(1L);

        assertNotNull(result);
        assertEquals("Laptop", result.getName());
        assertEquals(20, result.getStock());
    }

    @Test
    void getById_NotFound() {
        when(itemRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> itemService.getById(1L));
    }

    @Test
    void save_Success() {
        ItemRequest request = new ItemRequest();
        request.setName("Mouse");
        request.setPrice(200000.0);

        when(itemRepository.save(any(Item.class))).thenReturn(mockItem);

        ItemResponse result = itemService.save(request);

        assertNotNull(result);
        assertEquals(Status.SUCCESS.name(), result.getStatus());
        assertEquals(0, result.getStock());
    }

    @Test
    void update_Success() {
        ItemRequest request = new ItemRequest();
        request.setName("Laptop Gaming");
        request.setPrice(20000000.0);

        when(itemRepository.findById(1L)).thenReturn(Optional.of(mockItem));
        when(itemRepository.save(any(Item.class))).thenReturn(mockItem);
        when(inventoryRepository.getStockByItemId(1L)).thenReturn(10);

        ItemResponse result = itemService.update(1L, request);

        assertNotNull(result);
        verify(itemRepository).save(mockItem);
    }

    @Test
    void update_NotFound() {
        when(itemRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
                itemService.update(1L, new ItemRequest()));
    }

    @Test
    void delete_Success() {
        when(itemRepository.findById(1L)).thenReturn(Optional.of(mockItem));
        when(inventoryRepository.getStockByItemId(1L)).thenReturn(5);

        ItemResponse result = itemService.delete(1L);

        assertEquals(Status.DELETED.name(), result.getStatus());
        assertEquals(5, result.getStock());
        verify(itemRepository).delete(mockItem);
    }

    @Test
    void delete_NotFound_ThrowsException() {
        when(itemRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> itemService.delete(1L));
    }
}
