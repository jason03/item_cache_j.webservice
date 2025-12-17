package net.zeotrope.item.service;

import net.zeotrope.item.domain.Item;
import net.zeotrope.item.domain.ItemStatus;
import net.zeotrope.item.exceptions.ItemNotFoundException;
import net.zeotrope.item.model.ItemDto;
import net.zeotrope.item.repository.ItemRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@SpringBootTest(classes = {ItemService.class, ItemRepository.class})
public class ItemServiceTest {

    @Autowired
    private ItemService itemService;

    @MockitoBean
    private ItemRepository itemRepository;

    private LocalDateTime createdDate = LocalDateTime.of(2025, 1, 1, 0, 0, 0, 0);
    private LocalDateTime modifiedDate = LocalDateTime.of(2025, 1, 1, 0, 0, 0, 0);

    private List<Item> items = List.of(
            new Item(
                    1L,
                    ItemStatus.CURRENT,
                    "Title One",
                    "Summary One",
                    createdDate,
                    modifiedDate,
                    null
            ),
            new Item(
                    2L,
                    ItemStatus.CURRENT,
                    "Title Two",
                    "Summary Two",
                    createdDate,
                    modifiedDate,
                    null
            ),
            new Item(
                    3L,
                    ItemStatus.CURRENT,
                    "Title Three",
                    "Summary Three",
                    createdDate,
                    modifiedDate,
                    null
            )
    );

    private ItemDto itemDto = new ItemDto(
            "Title Ten",
            ItemStatus.CURRENT,
            "Summary Ten"
    );

    @Test
    @DisplayName("should return an item from the repository")
    public void shouldReturnItemFromRepository() {
        // given
        var item = new Item(
                1L,
                ItemStatus.CURRENT,
                "Title Ten",
                "Summary Ten",
                createdDate,
                modifiedDate,
                null
        );

        // when
        Mockito.when(itemRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(item));

        var actual = itemService.get(1L);

        // then
        assertEquals(item, actual);
        Mockito.verify(itemRepository, Mockito.times(1)).findById(Mockito.anyLong());
    }

    @Test
    @DisplayName("should throw an item not found exception when failing to retrieve item by id")
    public void shouldThrowExceptionItemNotFoundItemById(){
        // given
        // when
        Mockito.when(itemRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

        // then
        var actual = assertThrows(ItemNotFoundException.class,
                () -> itemService.get(1L)
        );
        assertEquals("Item with id 1 not found", actual.getMessage());
    }

    @Test
    @DisplayName("should create a new item")
    public void shouldCreateAnItem() {
        // given
        var itemDto = new ItemDto(
                "Title Twenty",
                ItemStatus.CURRENT,
                "Summary Twenty"
        );
        var expectedItem = new Item(
                1L,
                ItemStatus.CURRENT,
                "Title Twenty",
                "Summary Twenty",
                createdDate,
                modifiedDate,
                null
        );

        // when
        Mockito.when(itemRepository.save(Mockito.any())).thenReturn(expectedItem);
        var actual = itemService.createItem(itemDto);

        // then
        assertEquals(expectedItem, actual);
        Mockito.verify(itemRepository, Mockito.times(1)).save(Mockito.any());
    }

    @Test
    @DisplayName("should return a list of items")
    public void shouldReturnListItems() {
        // given
        // when
        Mockito.when(itemRepository.findAll()).thenReturn(items);
        // then
        var actual = itemService.getAllItems(null);

        assertEquals(items, actual);
        Mockito.verify(itemRepository, Mockito.times(1)).findAll();
    }

    @Test
    @DisplayName("should return a list of items filtered by status")
    public void shouldReturnListItemsFilteredByStatus(){
        // given
        // when
        Mockito.when(itemRepository.findAllByStatus(ItemStatus.CURRENT)).thenReturn(items);

        var actual = itemService.getAllItems(ItemStatus.CURRENT);

        // then
        assertEquals(items, actual);
        Mockito.verify(itemRepository, Mockito.times(1)).findAllByStatus(ItemStatus.CURRENT);
    }

    @Test
    @DisplayName("should delete an existing item")
    public void shouldDeleteExistingItem(){
        // given
        // when
        Mockito.when(itemRepository.existsById(Mockito.anyLong())).thenReturn(true);
        Mockito.doNothing().when(itemRepository).deleteById(Mockito.anyLong());

        itemService.delete(1L);

        // then
        Mockito.verify(itemRepository, Mockito.times(1)).existsById(Mockito.anyLong());
        Mockito.verify(itemRepository, Mockito.times(1)).deleteById(Mockito.anyLong());
    }

    @Test
    @DisplayName("should not error or call repository and cache delete when deleting item with invalid Id")
    public void shouldNotErrorWhenDeletingItemWithInvalidId(){
        // given
        // when
        Mockito.when(itemRepository.existsById(Mockito.anyLong())).thenReturn(false);
        itemService.delete(1L);
        // then
        Mockito.verify(itemRepository, Mockito.times(1)).existsById(Mockito.anyLong());
        Mockito.verify(itemRepository, Mockito.never()).deleteById(Mockito.anyLong());
    }

    @Test
    @DisplayName("should throw an item not found exception when updating item with invalid Id")
    public void shouldThrowItemNotFoundExceptionWhenUpdatingItemWithInvalidId(){
        // given
        // when
        Mockito.when(itemRepository.existsById(Mockito.anyLong())).thenReturn(false);

        // then
        var actual = assertThrows(ItemNotFoundException.class,
                () -> itemService.update(1L, itemDto)
        );
        assertEquals("Item with id 1 not found", actual.getMessage());

        Mockito.verify(itemRepository, Mockito.times(1)).findById(Mockito.anyLong());
        Mockito.verify(itemRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    @DisplayName("should update an existing item")
    public void shouldUpdateItem(){
        // given
        var item = new Item(
                1L,
                ItemStatus.CURRENT,
                "Title Twenty",
                "Summary Twenty",
                createdDate,
                modifiedDate,
                null
        );

        // when
        Mockito.when(itemRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(item));
        Mockito.when(itemRepository.save(Mockito.any())).thenReturn(item);

        itemService.update(1L, itemDto);

        // then
        Mockito.verify(itemRepository, Mockito.times(1)).findById(Mockito.anyLong());
        Mockito.verify(itemRepository, Mockito.times(1)).save(Mockito.any());
    }

    @Test
    @DisplayName("should throw a item not found exception when updating item status with invalid id")
    public void shouldThrowItemNotFoundExceptionWhenUpdatingItemStatusWithInvalidId() {
        // given
        // when
        Mockito.when(itemRepository.existsById(Mockito.anyLong())).thenReturn(false);

        // then
        var actual = assertThrows(ItemNotFoundException.class,
                () -> itemService.updateItemStatus(1L, ItemStatus.DISCONTINUED)
        );
        assertEquals("Item with id 1 not found", actual.getMessage());

        Mockito.verify(itemRepository, Mockito.times(1)).findById(Mockito.anyLong());
        Mockito.verify(itemRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    @DisplayName("should update an existing item's status")
    public void shouldUpdateExistingItemStatus() {
        // given
        var newItemStatus = ItemStatus.DISCONTINUED;
        var item = new Item(
                1L,
                ItemStatus.CURRENT,
                "Title",
                "Summary",
                createdDate,
                modifiedDate,
                null
        );

        // when
        Mockito.when(itemRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(item));
        Mockito.when(itemRepository.save(Mockito.any())).thenReturn(new Item(
                1L,
                ItemStatus.DISCONTINUED,
                "Title",
                "Summary",
                createdDate,
                modifiedDate,
                null
        ));

        var actual = itemService.updateItemStatus(1L, newItemStatus);

        // then
        assertEquals(newItemStatus, actual.getStatus());
        Mockito.verify(itemRepository, Mockito.times(1)).findById(Mockito.anyLong());
        Mockito.verify(itemRepository, Mockito.times(1)).save(Mockito.any());
    }
}
