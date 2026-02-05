package org.example.livecode.service.impl;

import org.example.livecode.dto.request.OrderRequest;
import org.example.livecode.dto.response.OrderResponse;
import org.example.livecode.entity.Item;
import org.example.livecode.entity.Order;
import org.example.livecode.exception.InsufficientStockException;
import org.example.livecode.exception.ResourceNotFoundException;
import org.example.livecode.repository.InventoryRepository;
import org.example.livecode.repository.ItemRepository;
import org.example.livecode.repository.OrderRepository;
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
import static org.mockito.Mockito.*;

public class OrderServiceImplTests {

    private OrderRepository orderRepository;

    private ItemRepository itemRepository;

    private InventoryRepository inventoryRepository;

    private OrderServiceImpl orderService;

    private Item mockItem;

    @BeforeEach
    void setUp() {
        orderRepository = mock(OrderRepository.class);
        itemRepository = mock(ItemRepository.class);
        inventoryRepository = mock(InventoryRepository.class);
        orderService = new OrderServiceImpl(orderRepository, itemRepository, inventoryRepository);

        mockItem = new Item();
        mockItem.setId(1L);
        mockItem.setName("Buku");
        mockItem.setPrice(15000.0);
    }

    @Test
    void save_OrderSuccess() {
        OrderRequest request = new OrderRequest(1L, 2);
        when(itemRepository.findById(1L)).thenReturn(Optional.of(mockItem));
        when(inventoryRepository.getStockByItemId(1L)).thenReturn(10);

        Order savedOrder = new Order();
        savedOrder.setId(1L);
        savedOrder.setItem(mockItem);
        when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);

        OrderResponse response = orderService.save(request);

        assertEquals("O1", response.getOrderNo());
        assertEquals(Status.SUCCESS.name(), response.getStatus());
        verify(inventoryRepository).save(any());
        verify(orderRepository, times(2)).save(any());
    }

    @Test
    void save_OrderFailed() {
        OrderRequest request = new OrderRequest(1L, 100);
        when(itemRepository.findById(1L)).thenReturn(Optional.of(mockItem));
        when(inventoryRepository.getStockByItemId(1L)).thenReturn(10);

        assertThrows(InsufficientStockException.class, () -> orderService.save(request));
        verify(orderRepository, never()).save(any());
    }

    @Test
    void list() {
        Pageable pageable = PageRequest.of(0, 10);
        Order order = new Order();
        order.setItem(mockItem);

        when(orderRepository.findAll(pageable)).thenReturn(new PageImpl<>(List.of(order)));

        Page<OrderResponse> result = orderService.list(pageable);

        assertFalse(result.isEmpty());
        verify(orderRepository).findAll(pageable);
    }

    @Test
    void getById() {
        Order order = new Order();
        order.setId(1L);
        order.setItem(mockItem);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        OrderResponse result = orderService.getById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void getById_NotFound() {
        when(orderRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> orderService.getById(1L));
    }

    @Test
    void update() {
        Order order = new Order();
        order.setItem(mockItem);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        OrderResponse result = orderService.update(1L, new OrderRequest(1L, 5));

        assertNotNull(result);
        verify(orderRepository).findById(1L);
    }

    @Test
    void delete() {
        Order order = new Order();
        order.setItem(mockItem);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        OrderResponse result = orderService.delete(1L);

        assertNotNull(result);
        verify(orderRepository).delete(order);
    }
}
