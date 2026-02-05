package org.example.livecode.service;

import org.example.livecode.dto.request.ItemRequest;
import org.example.livecode.dto.response.ItemResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ItemService {
    Page<ItemResponse>list(Pageable pageable);

    ItemResponse getById(Long id);

    ItemResponse save(ItemRequest item);

    ItemResponse update(Long id, ItemRequest item);

    ItemResponse delete(Long id);
}
