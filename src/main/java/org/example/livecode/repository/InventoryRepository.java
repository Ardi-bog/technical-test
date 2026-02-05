package org.example.livecode.repository;

import org.example.livecode.entity.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {


    @Query("SELECT " +
            "COALESCE(SUM(CASE WHEN inv.type = 'T' THEN inv.qty ELSE -inv.qty END), 0) " +
            "FROM Inventory inv WHERE inv.item.id = :itemId")
    Integer getStockByItemId(@Param("itemId") Long itemId);

    @Query("SELECT COALESCE(SUM(CASE WHEN i.type = 'T' THEN i.qty ELSE -i.qty END), 0) " +
            "FROM Inventory i WHERE i.item.id = :itemId AND i.id <> :excludeInventoryId")
    Integer getStockByItemIdExcluding(@Param("itemId") Long itemId, @Param("excludeInventoryId") Long excludeInventoryId);
}
