package net.zeotrope.item.api.resource;

import net.zeotrope.item.domain.Item;
import net.zeotrope.item.domain.ItemStatus;
import net.zeotrope.item.model.ItemDto;
import net.zeotrope.item.service.ItemService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping(
        value = "/api/v1",
        produces = "application/json"
)
public class ItemController {

    Logger logger = LoggerFactory.getLogger(ItemController.class);

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    private final ItemService itemService;

    @GetMapping("/items")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<Item>> getAllItems(@RequestParam(required = false, name = "status") ItemStatus status) {
        return ResponseEntity.ok(itemService.getAllItems(status));
    }

    @GetMapping("/items/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Item> getItem(@PathVariable Long id, @RequestParam(defaultValue = "0") Integer process) {
        logger.info("Getting item with id {} : virtual {} : id {}", id, Thread.currentThread().isVirtual(), Thread.currentThread().getId());
        var item = itemService.get(id);

        var processedItem = switch (process) {
            case 1 -> itemService.processItemA(item);
            case 2 -> itemService.processItemB(item);
            case 3 -> itemService.processItemC(item);
            default -> item;
        };
        return ResponseEntity.ok(processedItem);
    }

    @PostMapping("/items")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Item> createItem(@RequestBody ItemDto item) {
        return ResponseEntity.status(HttpStatus.CREATED).body(itemService.createItem(item));
    }

    @PutMapping("/items/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Item> updateItem(@PathVariable Long id, @NonNull @RequestBody ItemDto item) {
        var updatedItem = itemService.update(id, item);
        return ResponseEntity.noContent().build();
    }

    @PutMapping(
            value = "/items/{id}",
            params = "status"
    )
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Item> updateItemStatus(@PathVariable Long id, @RequestParam(name = "status") ItemStatus status) {
        var updatedItem = itemService.updateItemStatus(id, status);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/items/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> deleteItem(@PathVariable Long id) {
        itemService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
