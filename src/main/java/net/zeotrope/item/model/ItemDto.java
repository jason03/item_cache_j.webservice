package net.zeotrope.item.model;

import net.zeotrope.item.domain.ItemStatus;

public record ItemDto(
        String name,
        ItemStatus status,
        String description
) {}
