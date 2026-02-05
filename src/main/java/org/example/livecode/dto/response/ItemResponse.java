package org.example.livecode.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ItemResponse {

    private Long id;

    private String name;

    private Double price;

    private Integer stock;

    private String status;

}
