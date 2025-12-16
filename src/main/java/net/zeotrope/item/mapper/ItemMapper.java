package net.zeotrope.item.mapper;

import net.zeotrope.item.domain.Item;
import net.zeotrope.item.domain.ItemStatus;
import net.zeotrope.item.model.ItemDto;

import java.time.LocalDateTime;

public class ItemMapper {
    private ItemMapper(){}

    public static Item toNewItem(ItemDto itemDto) {
        var dateTime = LocalDateTime.now();

        return new Item (
                null,
                itemDto.status(),
                itemDto.name(),
                itemDto.description(),
                dateTime,
                dateTime,
                null
        );
    }

    public static Item toUpdateItem(ItemDto item, Item oldItemState){
        var modifiedDateTime = LocalDateTime.now();
        var discontinued = (item.status() == ItemStatus.DISCONTINUED && oldItemState.getStatus() != ItemStatus.DISCONTINUED)
                ? modifiedDateTime : null;

        return new Item(
                oldItemState.getId(),
                item.status(),
                item.name(),
                item.description(),
                oldItemState.getCreatedAt(),
                modifiedDateTime,
                discontinued
        );
    }

    public static Item toUpdateItemStatus(Item item, ItemStatus status){
        var modifiedDateTime = LocalDateTime.now();
        return new Item(
                item.getId(),
                status,
                item.getName(),
                item.getSummary(),
                item.getCreatedAt(),
                modifiedDateTime,
                status == ItemStatus.DISCONTINUED ? modifiedDateTime : null
        );
    }
}
