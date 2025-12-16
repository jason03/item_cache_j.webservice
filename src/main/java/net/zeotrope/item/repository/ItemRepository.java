package net.zeotrope.item.repository;

import net.zeotrope.item.domain.Item;
import net.zeotrope.item.domain.ItemStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findAllByStatus(ItemStatus status);
}
