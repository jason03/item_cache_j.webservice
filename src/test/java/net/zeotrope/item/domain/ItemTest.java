package net.zeotrope.item.domain;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ItemTest {

    @Test
    @DisplayName("should return a list of item statues in order when sort order is asc")
    public void shouldOrderItemsByItemStatusSortOrderAsc(){
        // given
        var unSortedStatuses = List.of(
                ItemStatus.DISCONTINUED,
                ItemStatus.CURRENT,
                ItemStatus.DISCONTINUED,
                ItemStatus.CURRENT
        );
        var expectedStatuses = List.of(
                ItemStatus.CURRENT,
                ItemStatus.CURRENT,
                ItemStatus.DISCONTINUED,
                ItemStatus.DISCONTINUED
        );

        // when
        var actual = unSortedStatuses.stream().sorted(ItemStatus.statusSort()).toList();

        // then
        assertEquals(expectedStatuses, actual);
    }

    @Test
    @DisplayName("should return a list of items sorted by status and created date order")
    public void shouldReturnItemsSortedByStatusAndCreatedDate(){
        // given
        var items = getItems();

        // when
        var actual = items.stream().sorted(Item.statusCreatedSort).collect(Collectors.toList());

        // then
        assertAll(
                () -> assertEquals(items.size(), actual.size()),
                () -> assertEquals(1234L, actual.get(0).getId()),
                () -> assertEquals(ItemStatus.CURRENT, actual.get(0).getStatus()),
                () -> assertEquals(20001L, actual.get(1).getId()),
                () -> assertEquals(ItemStatus.CURRENT, actual.get(1).getStatus()),
                () -> assertEquals(1236L, actual.get(2).getId()),
                () -> assertEquals(ItemStatus.CURRENT, actual.get(2).getStatus()),
                () -> assertEquals(1237L, actual.get(3).getId()),
                () -> assertEquals(ItemStatus.DISCONTINUED, actual.get(3).getStatus()),
                () -> assertEquals(1235L, actual.get(4).getId()),
                () -> assertEquals(ItemStatus.DISCONTINUED, actual.get(4).getStatus())
        );
    }

    @Test
    @DisplayName("should return a list of items sorted by status")
    public void shouldReturnItemsSortedByStatus(){
        // given
        var items = getItems();

        // when
        var actual = items.stream().sorted(Item.itemStatusSort).collect(Collectors.toList());

        // then
        assertAll(
                () -> assertEquals(items.size(), actual.size()),
                () -> assertEquals(1234L, actual.get(0).getId()),
                () -> assertEquals(ItemStatus.CURRENT, actual.get(0).getStatus()),
                () -> assertEquals(1236L, actual.get(1).getId()),
                () -> assertEquals(ItemStatus.CURRENT, actual.get(1).getStatus()),
                () -> assertEquals(20001L, actual.get(2).getId()),
                () -> assertEquals(ItemStatus.CURRENT, actual.get(2).getStatus()),
                () -> assertEquals(1235L, actual.get(3).getId()),
                () -> assertEquals(ItemStatus.DISCONTINUED, actual.get(3).getStatus()),
                () -> assertEquals(1237L, actual.get(4).getId()),
                () -> assertEquals(ItemStatus.DISCONTINUED, actual.get(4).getStatus())
        );
    }

    private @NotNull List<Item> getItems() {
        var dateTime1 = LocalDateTime.of(2025, 1, 1, 0, 0, 0);
        var dateTime2 = LocalDateTime.of(2025, 1, 14, 0, 0, 0);
        var dateTime3 = LocalDateTime.of(2025, 1, 14, 2, 0, 0);
        var dateTime4 = LocalDateTime.of(2025, 2, 7, 12, 0, 0);
        var dateTime5 = LocalDateTime.of(2025, 2, 14, 15, 0, 0);
        var discontinuedDateTime = LocalDateTime.of(2025, 6, 1, 0, 0, 0, 0);
        var items = List.of(
                new Item(
                        1234L,
                        ItemStatus.CURRENT,
                        "Title",
                        "Summary",
                        dateTime1,
                        dateTime1,
                        null
                ),
                new Item(
                        1235L,
                        ItemStatus.DISCONTINUED,
                        "Title",
                        "Summary",
                        dateTime5,
                        dateTime5,
                        discontinuedDateTime
                ),
                new Item(
                        1236L,
                        ItemStatus.CURRENT,
                        "Title",
                        "Summary",
                        dateTime3,
                        dateTime3,
                        null
                ),
                new Item(
                        1237L,
                        ItemStatus.DISCONTINUED,
                        "Title",
                        "Summary",
                        dateTime4,
                        dateTime4,
                        discontinuedDateTime
                ),
                new Item(
                        20001L,
                        ItemStatus.CURRENT,
                        "Title",
                        "Summary",
                        dateTime2,
                        dateTime2,
                        null
                )
        );
        return items;
    }
}
