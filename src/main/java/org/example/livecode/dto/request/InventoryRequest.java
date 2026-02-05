package org.example.livecode.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.livecode.utill.InventoryType;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InventoryRequest {

    @NotNull(message = "Quantity is required")
    private Integer qty;

    @NotBlank(message = "Type is required")
    private InventoryType type;
}
