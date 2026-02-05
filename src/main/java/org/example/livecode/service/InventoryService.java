package org.example.livecode.service;

import org.example.livecode.dto.request.InventoryRequest;
import org.example.livecode.dto.response.InventoryResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface InventoryService {

    Page<InventoryResponse> list(Pageable pageable);

    InventoryResponse getById(Long id);

    InventoryResponse save(Long itemId, InventoryRequest inventory);

    InventoryResponse update(Long id, InventoryRequest inventory);

    InventoryResponse delete(Long id);
}
