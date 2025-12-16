package net.zeotrope.item.api.resource;

import net.zeotrope.item.domain.Item;
import net.zeotrope.item.domain.ItemStatus;
import net.zeotrope.item.model.ItemDto;
import net.zeotrope.item.service.ItemService;
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

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    private final ItemService itemService;

    @GetMapping("/items")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<Item>> getAllItems(@RequestParam(required = false, name = "status") ItemStatus status) {
        var timestamp = LocalDateTime.now();
        var item = new Item(
                100L,
                ItemStatus.CURRENT,
                "Item 100",
                "Item 100 Summary",
                timestamp,
                timestamp,
                null
        );
        return ResponseEntity.ok(itemService.getAllItems(status));
    }

    @GetMapping("/items/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Item> getItem(@PathVariable Long id) {
        var timestamp = LocalDateTime.now();
        var item = new Item(
                id,
                ItemStatus.CURRENT,
                "Item 100",
                "Item 100 Summary",
                timestamp,
                timestamp,
                null
        );
        return ResponseEntity.ok(itemService.get(id));
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
