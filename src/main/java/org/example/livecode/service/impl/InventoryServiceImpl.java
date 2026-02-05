package org.example.livecode.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.livecode.dto.request.InventoryRequest;
import org.example.livecode.dto.response.InventoryResponse;
import org.example.livecode.entity.Inventory;
import org.example.livecode.entity.Item;
import org.example.livecode.exception.InsufficientStockException;
import org.example.livecode.exception.ResourceNotFoundException;
import org.example.livecode.repository.InventoryRepository;
import org.example.livecode.repository.ItemRepository;
import org.example.livecode.service.InventoryService;
import org.example.livecode.utill.InventoryType;
import org.example.livecode.utill.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService {

    private final InventoryRepository inventoryRepository;
    private final ItemRepository itemRepository;

    @Override
    public Page<InventoryResponse> list(Pageable pageable) {
        return inventoryRepository.findAll(pageable)
                .map(this::mapToResponse);
    }

    @Override
    public InventoryResponse getById(Long id) {
        return inventoryRepository.findById(id)
                .map(this::mapToResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory record not found"));
    }

    @Override
    @Transactional
    public InventoryResponse save(Long itemId, InventoryRequest request) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Item not found with id: " + itemId));

        if (request.getType() == InventoryType.W) {
            Integer currentStock = inventoryRepository.getStockByItemId(itemId);
            if (currentStock < request.getQty()) {
                throw new InsufficientStockException("Insufficient stock for withdrawal. Current stock: " + currentStock);
            }
        }

        Inventory inventory = new Inventory();
        inventory.setItem(item);
        inventory.setQty(request.getQty());
        inventory.setType(request.getType());

        Inventory saved = inventoryRepository.save(inventory);

        return mapToResponse(saved);
    }

    @Override
    @Transactional
    public InventoryResponse update(Long id, InventoryRequest request) {
        Inventory existing = inventoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory record not found"));

        if (request.getType() == InventoryType.W) {
            Integer currentStockWithoutThis = inventoryRepository.getStockByItemIdExcluding(existing.getItem().getId(), id);
            if (currentStockWithoutThis < request.getQty()) {
                throw new InsufficientStockException("Insufficient stock for this update");
            }
        }

        existing.setQty(request.getQty());
        existing.setType(request.getType());

        return mapToResponse(inventoryRepository.save(existing));
    }

    @Override
    @Transactional
    public InventoryResponse delete(Long id) {
        Inventory inventory = inventoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory record not found"));

        InventoryResponse response = mapToResponse(inventory);
        inventoryRepository.delete(inventory);
        return response;
    }

    private InventoryResponse mapToResponse(Inventory inventory) {
        return new InventoryResponse(
                inventory.getId(),
                inventory.getItem().getId(),
                inventory.getItem().getName(),
                inventory.getQty(),
                inventory.getType().name(),
                Status.SUCCESS.name()
        );
    }
}
