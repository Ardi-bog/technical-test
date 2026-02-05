package org.example.livecode.repository;

import org.example.livecode.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OrderRepository extends JpaRepository<Order, Long> {

    @Query("""
        SELECT COALESCE(SUM(o.qty), 0)
        FROM Order o
        WHERE o.item.id = :itemId
    """)
    Integer getTotal(@Param("itemId") Long itemId);

}
