package net.zeotrope.item.repository;

import net.zeotrope.item.configurer.RedisCacheConfig;
import net.zeotrope.item.domain.Item;
import net.zeotrope.item.domain.ItemStatus;
import net.zeotrope.item.util.TestServiceContainers;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.utility.TestcontainersConfiguration;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;


@Import(TestcontainersConfiguration.class)
@DirtiesContext
@SpringBootTest
@ActiveProfiles("test")
public class ItemCacheRepositoryTest extends TestServiceContainers {

    private static final String CACHE_NAME = "items";
    private static final String CACHE_KEY_FORMAT = "%s:%d";
    private static final Long INVALID_ITEM_ID = 12345678L;

    @Autowired
    private CacheManager cacheManager;

    public static final Logger LOGGER = LoggerFactory.getLogger(ItemCacheRepositoryTest.class);

    @BeforeEach
    public void setUp() {
        cacheManager.getCache(CACHE_NAME).clear();
    }

    @Test
    @DisplayName("should add and then get an item from the cache")
    public void shouldAddAndThenGetItemFromCache() {
        // given
        var dateTime = LocalDateTime.of(2025, 1, 1, 0, 0, 0);
        var item =  new Item(1L, ItemStatus.CURRENT, "test", "test", dateTime, dateTime, null);
        var cache = cacheManager.getCache(CACHE_NAME);
        assertNotNull(cache);

        var cacheKey = String.format(CACHE_KEY_FORMAT, CACHE_NAME, item.getId());
        cache.put(cacheKey, item);

        // when
        Item actual = cache.get(cacheKey, Item.class);

        // then
        assertAll (
                () -> assertNotNull(actual),
                () -> assertEquals(item.getId(), actual.getId())
        );
    }

    @Test
    @DisplayName("should check that an invalid cache key returns null")
    public void shouldReturnNullForInvalidCacheKey() {
        // given
        var dateTime = LocalDateTime.of(2025, 2, 1, 0, 0, 0);
        var item =  new Item(10L, ItemStatus.CURRENT, "test", "test", dateTime, dateTime, null);

        var cache = cacheManager.getCache(CACHE_NAME);
        var cacheKey = String.format(CACHE_KEY_FORMAT, CACHE_NAME, item.getId());
        var invalidCacheKey = String.format(CACHE_KEY_FORMAT, CACHE_NAME, INVALID_ITEM_ID);
        assertNotNull(cache);

        cache.put(cacheKey, item);

        // when
        Item actual = cache.get(invalidCacheKey, Item.class);

        // then
        assertNull(actual);
    }

    @CsvSource(
            value = {
                    "100, true",
                    "101, false"
            }
    )
    @ParameterizedTest
    @DisplayName("should remove an item from the cache if present")
    public void shouldRemoveItemFromCacheIfPresent(Long idToEvict, Boolean expectedResult) {
        // given
        var dateTime = LocalDateTime.of(2025, 3, 1, 0, 0, 0);
        var item =  new Item(100L, ItemStatus.CURRENT, "test", "test", dateTime, dateTime, null);
        var cache = cacheManager.getCache(CACHE_NAME);
        var cacheKeyInsert= String.format(CACHE_KEY_FORMAT, CACHE_NAME, item.getId());
        var cacheKeyEvict = String.format(CACHE_KEY_FORMAT, CACHE_NAME, idToEvict);

        assertNotNull(cache);
        cache.put(cacheKeyInsert, item);

        // when
        cache.evictIfPresent(cacheKeyEvict);
        var actual = cache.get(cacheKeyInsert, Item.class);

        // then
        assertEquals(expectedResult, actual == null);
    }


}
