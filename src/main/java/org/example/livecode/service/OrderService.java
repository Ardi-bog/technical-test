package org.example.livecode.service;

import org.example.livecode.dto.request.OrderRequest;
import org.example.livecode.dto.response.OrderResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderService {

    Page<OrderResponse> list(Pageable pageable);

    OrderResponse getById(Long id);

    OrderResponse save(OrderRequest request);

    OrderResponse update(Long id, OrderRequest request);

    OrderResponse delete(Long id);
}
