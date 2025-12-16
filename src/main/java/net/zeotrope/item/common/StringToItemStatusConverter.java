package net.zeotrope.item.common;

import lombok.NonNull;
import net.zeotrope.item.domain.ItemStatus;
import net.zeotrope.item.exceptions.InvalidStatusException;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class StringToItemStatusConverter implements Converter<String, ItemStatus> {
    @Override
    public ItemStatus convert(String source) {
        if (source == null || source.isBlank()) {
            throw new InvalidStatusException("item status cannot be null");
        }
        try {
            return ItemStatus.valueOf(source.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new InvalidStatusException(source);
        }
    }
}
