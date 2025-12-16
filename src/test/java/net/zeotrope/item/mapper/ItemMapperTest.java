package net.zeotrope.item.mapper;

import net.zeotrope.item.domain.Item;
import net.zeotrope.item.domain.ItemStatus;
import net.zeotrope.item.model.ItemDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Named;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalDateTime;
import java.util.stream.Stream;

import static java.lang.Boolean.TRUE;
import static org.junit.jupiter.api.Assertions.*;

public class ItemMapperTest {

    private static Stream<Arguments> itemDtoToDomain(){
        return Stream.of(
                Arguments.of(
                        Named.of("Article Title",
                                new ItemDto(
                                        "Article Title",
                                        ItemStatus.CURRENT,
                                        "Article Summary"
                                )
                        )
                ),
                Arguments.of(
                        Named.of("Article Title Two",
                                new ItemDto(
                                        "Article Title Two",
                                        ItemStatus.CURRENT,
                                        "Article Summary Two"
                                )
                        )
                )
        );
    }

    @ParameterizedTest(name = "should map item created DTO to item domain object with title: {0}")
    @MethodSource("itemDtoToDomain")
    public void shouldMapItemDtoToDomain(ItemDto itemDto){
        // given
        var baseDateTime = LocalDateTime.now();

        // when
        var actual = ItemMapper.toNewItem(itemDto);

        // then
        assertAll(
                () -> assertNull(actual.getId()),
                () -> assertEquals(itemDto.status(), actual.getStatus()),
                () -> assertEquals(itemDto.name(), actual.getName()),
                () -> assertEquals(itemDto.description(), actual.getSummary()),
                () -> assertTrue(actual.getCreatedAt().isAfter(baseDateTime)),
                () -> assertTrue(actual.getLastModifiedAt().isAfter(baseDateTime))
        );
    }

    @Test
    @DisplayName("should map item DTO to domain when updating existing item")
    public void shouldMapItemDtoToDomainWhenUpdatingExistingItem(){
        // given
        var baseDateTime = LocalDateTime.of(2025, 1, 1, 0, 0, 0);
        var existingItem = new Item (
                1234567890L,
                ItemStatus.CURRENT,
                "Article Title One",
                "Article Summary One",
                baseDateTime,
                baseDateTime,
                null
        );
        var item = new ItemDto(
                "Article Title One",
                ItemStatus.CURRENT,
                "Article Summary"
        );

        // when
        var actual = ItemMapper.toUpdateItem(item, existingItem);

        // then
        assertAll(
                () -> assertEquals(existingItem.getId(), actual.getId()),
                () -> assertEquals(item.status(), actual.getStatus()),
                () -> assertEquals(item.name(), actual.getName()),
                () -> assertEquals(item.description(), actual.getSummary()),
                () -> assertTrue(actual.getCreatedAt() == existingItem.getCreatedAt()),
                () -> assertTrue(actual.getLastModifiedAt().isAfter(existingItem.getLastModifiedAt()))
        );
    }

    @CsvSource(
            value = {
                "DISCONTINUED, true",
                "CURRENT, false"
            }
    )
    @ParameterizedTest(name = "should update item status to {0} and discontinuedAt populated {1}")
    public void shouldUpdateItemToNewStatusAndSetDiscontinuedAt(ItemStatus status, Boolean hasDiscontinuedAt){
        // given
        var createdDate = LocalDateTime.of(2025, 1, 1, 0, 0, 0);
        var item = new Item(
                1234567890L,
                ItemStatus.CURRENT,
                "Article Title One",
                "Article Summary One",
                createdDate,
                createdDate,
                null
        );
        // when
        var actual = ItemMapper.toUpdateItemStatus(item, status);

        // then
        assertAll(
                () -> assertEquals(status, actual.getStatus()),
                () -> {
                    if (TRUE.equals(hasDiscontinuedAt)) {
                        assertTrue(actual.getDiscontinuedAt() != null && actual.getDiscontinuedAt().isAfter(createdDate));
                    } else {
                        assertTrue(actual.getDiscontinuedAt() == null);
                    }
                }
        );
    }
}
