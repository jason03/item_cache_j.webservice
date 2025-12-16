package net.zeotrope.item.domain;

import java.util.Comparator;

public enum ItemStatus {
    CURRENT,
    DISCONTINUED;

    public static int sortOrder(ItemStatus status) {
        return switch(status) {
            case CURRENT -> 0;
            case DISCONTINUED -> 1;
        };
    }

    public static final Comparator<ItemStatus> statusSort(){
        return Comparator.comparing(ItemStatus::sortOrder);
    }
}
