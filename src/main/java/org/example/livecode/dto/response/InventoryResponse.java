package org.example.livecode.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class InventoryResponse {

    private Long id;

    private Long itemId;

    private String itemName;

    private Integer qty;

    private String type;

    private String status;
}
