package net.zeotrope.item.service;

import net.zeotrope.item.domain.Item;
import net.zeotrope.item.domain.ItemStatus;
import net.zeotrope.item.exceptions.ItemNotFoundException;
import net.zeotrope.item.mapper.ItemMapper;
import net.zeotrope.item.model.ItemDto;
import net.zeotrope.item.repository.ItemRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.*;

@CacheConfig(cacheNames = "items")
@Service
public class ItemService {

    Logger logger = LoggerFactory.getLogger(ItemService.class);

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
                .map(item -> new Item(
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

        return updatedItem;
    }

    @Transactional
    @CachePut(value = "items", key = "#result.id")
    public Item updateItemStatus(Long id, ItemStatus status) {
        var updatedItem = itemRepository.findById(id)
                .map(oldItem -> ItemMapper.toUpdateItemStatus(oldItem, status))
                .map(itemRepository::save)
                .orElseThrow(() -> new ItemNotFoundException(String.format("Item with id %d not found", id)));

        return updatedItem;
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

    @Async("virtualThreadExecutor")
    @CachePut(value = "items", key = "#result.id")
    public Item processItemA(Item item) {
        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            Callable<Long> process = () -> {
                logger.info("Process A  is virtual {} with id {}", Thread.currentThread().isVirtual(), Thread.currentThread().getId());
                // Mock processing duration
                Thread.sleep(Duration.ofMillis(3000));
                logger.info("Process A processed");
                return ThreadLocalRandom.current().nextLong(100L);
            };

            List<Callable<Long>> callables = List.of(
                    () -> {
                        logger.info("Process B is virtual {} with id {}", Thread.currentThread().isVirtual(), Thread.currentThread().getId());
                        // Mock processing duration
                        Thread.sleep(Duration.ofMillis(3000));
                        logger.info("Process B processed");
                        return ThreadLocalRandom.current().nextLong(100L);
                    },
                    process
            );

            var result = executor.invokeAll(callables, 5000, TimeUnit.MILLISECONDS);

            return new Item(
                            item.getId(),
                            item.getStatus(),
                            item.getName(),
                            String.format("Test Summary processed with A. Random value %d", result.get(0).get()),
                            item.getCreatedAt(),
                            LocalDateTime.now(),
                            item.getLastModifiedAt()
                    );
        } catch (ExecutionException | InterruptedException | CancellationException e) {
            throw new RuntimeException(e);
        }
    }

    @Async("virtualThreadExecutor")
    @CachePut(value = "items", key = "#result.id")
    public Item processItemB(Item item) {
        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            Future<Item> itemFuture = executor.submit(() -> {
                try {
                    logger.info("Process item with id {} : virtual {} : id {}", item.getId(), Thread.currentThread().isVirtual(), Thread.currentThread().getId());
                    // Mock processing duration
                    Thread.sleep(Duration.ofMillis(1000));
                    var itemName = item.getName().startsWith("Processed") ? String.format("Ultra %s", item.getName()) : String.format("Processed %s", item.getName()) ;
                    return new Item(
                            item.getId(),
                            item.getStatus(),
                            itemName,
                            "Test Summary processed with B",
                            item.getCreatedAt(),
                            LocalDateTime.now(),
                            item.getLastModifiedAt()
                    );
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            });
            return itemFuture.get(5000, TimeUnit.MILLISECONDS);
        } catch (ExecutionException | InterruptedException | TimeoutException e) {
            throw new RuntimeException(e);
        }
    }

    @Async("virtualThreadExecutor")
    @CachePut(value = "items", key = "#result.id")
    public Item processItemC(Item item) {
        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            Future<Long> processFuture = executor.submit(() -> {
                try {
                    logger.info("Process virtual {} : id {}", item.getId(), Thread.currentThread().isVirtual(), Thread.currentThread().getId());
                    // fake processing duration
                    Thread.sleep(Duration.ofMillis(1000));
                    return ThreadLocalRandom.current().nextLong(1000L);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            });

            var id = processFuture.get(5000, TimeUnit.MILLISECONDS);

            return new Item(
                    id,
                    item.getStatus(),
                    item.getName(),
                    String.format("Test Summary processed with C. Random value %d", id),
                    item.getCreatedAt(),
                    LocalDateTime.now(),
                    item.getLastModifiedAt()
            );
        } catch (ExecutionException | InterruptedException | TimeoutException e) {
            throw new RuntimeException(e);
        }
    }
}
