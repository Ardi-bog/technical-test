package org.example.livecode.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OrderResponse {

    private Long id;

    private String orderNo;

    private Long itemId;

    private String itemName;

    private Integer qty;

    private Double price;

    private String status;
}

