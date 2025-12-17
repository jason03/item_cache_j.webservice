package net.zeotrope.item.repository;

import net.zeotrope.item.domain.ItemStatus;
import net.zeotrope.item.mapper.ItemMapper;
import net.zeotrope.item.model.ItemDto;
import net.zeotrope.item.util.TestServiceContainers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.utility.TestcontainersConfiguration;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Import(TestcontainersConfiguration.class)
@DirtiesContext
@SpringBootTest
@ActiveProfiles("test")
public class ItemRepositoryTest extends TestServiceContainers {

    @Autowired
    private ItemRepository itemRepository;

    public static final Logger LOGGER = LoggerFactory.getLogger(ItemRepositoryTest.class);

    @Test
    @DisplayName("should add an item to the database")
    public void shouldAddItemToDatabase(){
        // given
        var itemDto = new ItemDto(
                "Title",
                ItemStatus.CURRENT,
                "Summary"
        );
        var item = ItemMapper.toNewItem(itemDto);

        // when
        var actual = itemRepository.save(item);
        var a2 = itemRepository.findAll();

        // then
        assertEquals(7, a2.size());
        assertAll(
                () -> assertEquals(item.getStatus(), actual.getStatus()),
                () -> assertEquals(item.getName(), actual.getName()),
                () -> assertEquals(item.getSummary(), actual.getSummary()),
                () -> assertEquals(item.getCreatedAt(), actual.getCreatedAt())
        );
    }
}
