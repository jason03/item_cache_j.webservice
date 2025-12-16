package net.zeotrope.item.service;

import lombok.val;
import net.zeotrope.item.domain.Item;
import net.zeotrope.item.domain.ItemStatus;
import net.zeotrope.item.exceptions.ItemNotFoundException;
import net.zeotrope.item.mapper.ItemMapper;
import net.zeotrope.item.model.ItemDto;
import net.zeotrope.item.repository.ItemRepository;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@CacheConfig(cacheNames = "items")
@Service
public class ItemService {

    public ItemService(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    private final ItemRepository itemRepository;

    public List<Item> getAllItems(ItemStatus status) {
        if (status != null) {
            return itemRepository.findAllByStatus(status);
        }
        return itemRepository.findAll();
    }

    @Cacheable(value = "items", key = "#id")
    public Item get(Long id) {
        return itemRepository.findById(id)
                .map( item -> new Item(
                        item.getId(),
                        item.getStatus(),
                        item.getName(),
                        item.getSummary(),
                        item.getCreatedAt(),
                        item.getLastModifiedAt(),
                        item.getDiscontinuedAt()
                ))
                .orElseThrow(() -> new ItemNotFoundException(String.format("Item with id %d not found", id)));
    }

    @Transactional
    @CachePut(value = "items", key = "#result.id")
    public Item update(Long id, ItemDto item) {
        var updatedItem = itemRepository.findById(id)
                .map(oldItem -> ItemMapper.toUpdateItem(item, oldItem))
                .map(itemRepository::save)
                .orElseThrow(() -> new ItemNotFoundException(String.format("Item with id %d not found", id)));

        return itemRepository.save(updatedItem);
    }

    @Transactional
    @CachePut(value = "items", key = "#result.id")
    public Item updateItemStatus(Long id, ItemStatus status){
        var updatedItem = itemRepository.findById(id)
                .map(oldItem -> ItemMapper.toUpdateItemStatus(oldItem, status))
                .map(itemRepository::save)
                .orElseThrow(() -> new ItemNotFoundException(String.format("Item with id %d not found", id)));

        return itemRepository.save(updatedItem);
    }

    @Transactional
    @CacheEvict(value = "items", key = "#id")
    public void delete(Long id) {
        if (!itemRepository.existsById(id))
            return;
        itemRepository.deleteById(id);
    }

    @CachePut(value = "items", key = "#result.id")
    public Item createItem(ItemDto item) {
        Item saved = itemRepository.save(ItemMapper.toNewItem(item));
        return saved;
    }
}
