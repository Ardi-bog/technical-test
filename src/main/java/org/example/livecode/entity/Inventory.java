package org.example.livecode.entity;


import jakarta.persistence.*;
import lombok.Data;
import org.example.livecode.utill.InventoryType;

@Entity
@Table(name = "inventory")
@Data
public class Inventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "item_id")
    private Item item;

    private Integer qty;

    @Enumerated(EnumType.STRING)
    private InventoryType type;
}
