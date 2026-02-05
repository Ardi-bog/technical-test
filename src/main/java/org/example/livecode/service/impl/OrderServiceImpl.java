package org.example.livecode.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.livecode.dto.request.OrderRequest;
import org.example.livecode.dto.response.OrderResponse;
import org.example.livecode.entity.Inventory;
import org.example.livecode.entity.Item;
import org.example.livecode.entity.Order;
import org.example.livecode.exception.InsufficientStockException;
import org.example.livecode.exception.ResourceNotFoundException;
import org.example.livecode.repository.InventoryRepository;
import org.example.livecode.repository.ItemRepository;
import org.example.livecode.repository.OrderRepository;
import org.example.livecode.service.OrderService;
import org.example.livecode.utill.InventoryType;
import org.example.livecode.utill.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;

    private final ItemRepository itemRepository;

    private final InventoryRepository inventoryRepository;

    @Override
    @Transactional
    public OrderResponse save(OrderRequest request) {
        Item item = itemRepository.findById(request.getItemId())
                .orElseThrow(() -> new ResourceNotFoundException("Item not found"));

        Integer currentStock = inventoryRepository.getStockByItemId(item.getId());
        if (currentStock < request.getQty()) {
            throw new InsufficientStockException("Insufficient stock message");
        }

        Order order = new Order();
        order.setItem(item);
        order.setQty(request.getQty());
        order.setPrice(item.getPrice());
        Order savedOrder = orderRepository.save(order);

        savedOrder.setOrderNo("O" + savedOrder.getId());
        orderRepository.save(savedOrder);

        Inventory withdrawal = new Inventory();
        withdrawal.setItem(item);
        withdrawal.setQty(request.getQty());
        withdrawal.setType(InventoryType.W);
        inventoryRepository.save(withdrawal);

        return mapToResponse(savedOrder);
    }

    @Override
    public Page<OrderResponse> list(Pageable pageable) {
        return orderRepository.findAll(pageable).map(this::mapToResponse);
    }

    @Override
    public OrderResponse getById(Long id) {
        return orderRepository.findById(id)
                .map(this::mapToResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
    }

    @Override
    @Transactional
    public OrderResponse update(Long id, OrderRequest request) {
        Order existing = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
        return mapToResponse(existing);
    }

    @Override
    @Transactional
    public OrderResponse delete(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
        orderRepository.delete(order);
        return mapToResponse(order);
    }

    private OrderResponse mapToResponse(Order order) {
        return new OrderResponse(
                order.getId(),
                order.getOrderNo(),
                order.getItem().getId(),
                order.getItem().getName(),
                order.getQty(),
                order.getPrice(),
                Status.SUCCESS.name()
        );
    }
}
