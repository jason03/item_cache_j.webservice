package net.zeotrope.item.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Comparator;

@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Getter
@Entity
@Table(name = "items")
public class Item implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Enumerated(EnumType.STRING)
    private ItemStatus status;
    private String name;
    private String summary;
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    @Column(name = "last_modified_at")
    private LocalDateTime lastModifiedAt;
    @Column(name = "discontinued_at")
    private LocalDateTime discontinuedAt;

//    public static Comparator<Item> itemStatusSortOld =
//            (Item i1, Item i2) ->
//                    Integer.compare(ItemStatus.sortOrder(i1.getStatus()), ItemStatus.sortOrder(i2.getStatus()));
    // same as above func
    public static Comparator<Item> itemStatusSort =
            Comparator.comparingInt((Item i) -> ItemStatus.sortOrder(i.getStatus()));
    public static Comparator<Item> statusCreatedSort =
            Comparator.comparingInt((Item i) -> ItemStatus.sortOrder(i.getStatus()))
                    .thenComparing(Item::getCreatedAt);
}
