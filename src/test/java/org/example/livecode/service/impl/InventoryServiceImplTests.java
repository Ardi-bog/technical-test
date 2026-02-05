package org.example.livecode.service.impl;

import org.example.livecode.dto.request.InventoryRequest;
import org.example.livecode.dto.response.InventoryResponse;
import org.example.livecode.entity.Inventory;
import org.example.livecode.entity.Item;
import org.example.livecode.exception.InsufficientStockException;
import org.example.livecode.exception.ResourceNotFoundException;
import org.example.livecode.repository.InventoryRepository;
import org.example.livecode.repository.ItemRepository;
import org.example.livecode.utill.InventoryType;
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

public class InventoryServiceImplTests {

    private InventoryRepository inventoryRepository;

    private ItemRepository itemRepository;

    private InventoryServiceImpl inventoryService;

    private Item mockItem;

    private Inventory mockInventory;

    @BeforeEach
    void setUp() {
        inventoryRepository = mock(InventoryRepository.class);
        itemRepository = mock(ItemRepository.class);
        inventoryService = new InventoryServiceImpl(inventoryRepository, itemRepository);

        mockItem = new Item();
        mockItem.setId(1L);
        mockItem.setName("Kopi");

        mockInventory = new Inventory();
        mockInventory.setId(10L);
        mockInventory.setItem(mockItem);
        mockInventory.setQty(10);
        mockInventory.setType(InventoryType.T);
    }

    @Test
    void list() {
        Pageable pageable = PageRequest.of(0, 10);
        when(inventoryRepository.findAll(pageable)).thenReturn(new PageImpl<>(List.of(mockInventory)));

        Page<InventoryResponse> result = inventoryService.list(pageable);

        assertFalse(result.isEmpty());
        assertEquals(1, result.getTotalElements());
    }

    @Test
    void getById() {
        when(inventoryRepository.findById(10L)).thenReturn(Optional.of(mockInventory));

        InventoryResponse result = inventoryService.getById(10L);

        assertNotNull(result);
        assertEquals(10L, result.getId());
    }

    @Test
    void save_TopUp_Success() {
        InventoryRequest req = new InventoryRequest(20, InventoryType.T);
        when(itemRepository.findById(1L)).thenReturn(Optional.of(mockItem));
        when(inventoryRepository.save(any(Inventory.class))).thenReturn(mockInventory);

        InventoryResponse res = inventoryService.save(1L, req);

        assertNotNull(res);
        verify(inventoryRepository).save(any(Inventory.class));
    }

    @Test
    void save_Withdrawal_InsufficientStock() {
        InventoryRequest req = new InventoryRequest(100, InventoryType.W);
        when(itemRepository.findById(1L)).thenReturn(Optional.of(mockItem));
        when(inventoryRepository.getStockByItemId(1L)).thenReturn(10); // Stok cuma 10

        assertThrows(InsufficientStockException.class, () -> inventoryService.save(1L, req));
    }

    @Test
    void update_Withdrawal_Success() {
        InventoryRequest req = new InventoryRequest(5, InventoryType.W);
        when(inventoryRepository.findById(10L)).thenReturn(Optional.of(mockInventory));
        when(inventoryRepository.getStockByItemIdExcluding(1L, 10L)).thenReturn(20);
        when(inventoryRepository.save(any(Inventory.class))).thenReturn(mockInventory);

        InventoryResponse res = inventoryService.update(10L, req);

        assertNotNull(res);
        verify(inventoryRepository).save(any(Inventory.class));
    }

    @Test
    void update_Withdrawal_Insufficient() {
        InventoryRequest req = new InventoryRequest(50, InventoryType.W);
        when(inventoryRepository.findById(10L)).thenReturn(Optional.of(mockInventory));
        when(inventoryRepository.getStockByItemIdExcluding(1L, 10L)).thenReturn(10);

        assertThrows(InsufficientStockException.class, () -> inventoryService.update(10L, req));
    }

    @Test
    void delete_Success() {
        when(inventoryRepository.findById(10L)).thenReturn(Optional.of(mockInventory));

        InventoryResponse res = inventoryService.delete(10L);

        assertNotNull(res);
        verify(inventoryRepository).delete(mockInventory);
    }

    @Test
    void getById_NotFound() {
        when(inventoryRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> inventoryService.getById(1L));
    }
}
