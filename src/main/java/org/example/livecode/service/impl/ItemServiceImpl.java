package org.example.livecode.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.livecode.dto.request.ItemRequest;
import org.example.livecode.dto.response.ItemResponse;
import org.example.livecode.entity.Item;
import org.example.livecode.exception.ResourceNotFoundException;
import org.example.livecode.repository.InventoryRepository;
import org.example.livecode.repository.ItemRepository;
import org.example.livecode.service.ItemService;
import org.example.livecode.utill.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final InventoryRepository inventoryRepository;

    @Override
    public Page<ItemResponse> list(Pageable pageable) {
        return itemRepository.findAll(pageable)
                .map(this::mapToResponse);
    }

    @Override
    public ItemResponse getById(Long id) {
        return itemRepository.findById(id)
                .map(this::mapToResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Item with ID " + id + " not found"));
    }

    @Override
    @Transactional
    public ItemResponse save(ItemRequest itemRequest) {
        Item item = new Item();
        item.setName(itemRequest.getName());
        item.setPrice(itemRequest.getPrice());

        Item saved = itemRepository.save(item);
        return new ItemResponse(saved.getId(), saved.getName(), saved.getPrice(), 0, Status.SUCCESS.name());
    }

    @Override
    @Transactional
    public ItemResponse update(Long id, ItemRequest itemRequest) {
        return itemRepository.findById(id)
                .map(item -> {
                    item.setName(itemRequest.getName());
                    item.setPrice(itemRequest.getPrice());
                    Item updated = itemRepository.save(item);
                    return mapToResponse(updated);
                })
                .orElseThrow(() -> new ResourceNotFoundException("Item with ID " + id + " not found"));
    }

    @Override
    @Transactional
    public ItemResponse delete(Long id) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Item with ID " + id + " not found"));

        Integer stock = inventoryRepository.getStockByItemId(id);
        ItemResponse response = new ItemResponse(
                item.getId(),
                item.getName(),
                item.getPrice(),
                stock,
                Status.DELETED.name()
        );

        itemRepository.delete(item);
        return response;
    }

    private ItemResponse mapToResponse(Item item) {
        Integer stock = inventoryRepository.getStockByItemId(item.getId());
        return new ItemResponse(
                item.getId(),
                item.getName(),
                item.getPrice(),
                stock,
                Status.SUCCESS.name()
        );
    }
}
