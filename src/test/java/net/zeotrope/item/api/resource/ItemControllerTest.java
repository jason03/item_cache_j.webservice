package net.zeotrope.item.api.resource;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.zeotrope.item.domain.Item;
import net.zeotrope.item.domain.ItemStatus;
import net.zeotrope.item.exceptions.ItemNotFoundException;
import net.zeotrope.item.mapper.ItemMapper;
import net.zeotrope.item.model.ItemDto;
import net.zeotrope.item.service.ItemService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ItemController.class)
@ActiveProfiles("test")
public class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ItemService itemService;

    private LocalDateTime createdDate = LocalDateTime.of(2025, 1, 1, 0, 0, 0);
    private LocalDateTime discontinuedDate = LocalDateTime.of(2025, 6, 30, 0, 0, 0);

    @Test
    @DisplayName("should return 200 when get all items")
    public void shouldReturn200GetAllItems() throws Exception {
        // given
        var items = List.of(
                new Item(
                        1234567890L,
                        ItemStatus.CURRENT,
                        "Title One",
                        "Summary One",
                        createdDate,
                        createdDate,
                        null
                ),
                new Item(
                        1234567891L,
                        ItemStatus.DISCONTINUED,
                        "Title Two",
                        "Summary Two",
                        createdDate,
                        createdDate,
                        discontinuedDate
                ),
                new Item(
                        1234567892L,
                        ItemStatus.CURRENT,
                        "Title Three",
                        "Summary Three",
                        createdDate,
                        createdDate,
                        null
                )
        );

        // when
        Mockito.when(itemService.getAllItems(Mockito.any())).thenReturn(items);

        // then
        mockMvc.perform(get("/api/v1/items").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$[0].id").value(1234567890))
                .andExpect(jsonPath("$[1].id").value(1234567891))
                .andExpect(jsonPath("$[2].id").value(1234567892));

        Mockito.verify(itemService, Mockito.times(1)).getAllItems(Mockito.any());
    }

    @Test
    @DisplayName("should return 200 when get all items filtered by status")
    public void shouldReturn200AllItemsFilteredByStatus() throws Exception {
        // given
        var items = List.of(
                new Item(
                        1234567890L,
                        ItemStatus.CURRENT,
                        "Title One",
                        "Summary One",
                        createdDate,
                        createdDate,
                        null
                )
        );
        // when
        Mockito.when(itemService.getAllItems(Mockito.any())).thenReturn(items);

        // then
        mockMvc.perform(
                get("/api/v1/items")
                        .param("status", "current")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpectAll(
                        status().isOk(),
                        header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE),
                        jsonPath("$.length()").value(1),
                        jsonPath("$[0].id").value(1234567890L)
                );

        Mockito.verify(itemService, Mockito.times(1)).getAllItems(Mockito.any());
    }

    @Test
    @DisplayName("should return 200 when get item by id")
    public void shouldReturn200ForItemById() throws Exception {
        // given
        var item = new Item(
                        1234567890L,
                        ItemStatus.CURRENT,
                        "Title One",
                        "Summary One",
                        createdDate,
                        createdDate,
                        null
                );

        // when
        Mockito.when(itemService.get(Mockito.anyLong())).thenReturn(item);

        // then
        mockMvc.perform(
                        get("/api/v1/items/1234567890")
                                .accept(MediaType.APPLICATION_JSON))
                .andExpectAll(
                        status().isOk(),
                        header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE),
                        jsonPath("$.length()").value(7),
                        jsonPath("$.id").value(1234567890L),
                        jsonPath("$.status").value("CURRENT"),
                        jsonPath("$.name").value("Title One"),
                        jsonPath("$.summary").value("Summary One"),
                        jsonPath("$.createdAt").value("2025-01-01T00:00:00"),
                        jsonPath("$.lastModifiedAt").value("2025-01-01T00:00:00"),
                        jsonPath("$.discontinuedAt").isEmpty()
                );

        Mockito.verify(itemService, Mockito.times(1)).get(Mockito.anyLong());
    }

    @Test
    @DisplayName("should throw exception when item not found when getting item by id")
    public void shouldThrowExceptionItemNotFoundById() throws Exception {
        // given
        // when
        Mockito.when(itemService.get(Mockito.anyLong())).thenThrow(new ItemNotFoundException("Test Error"));

        // then
        mockMvc.perform(
                        get("/api/v1/items/1234567890")
                                .accept(MediaType.APPLICATION_JSON))
                .andExpectAll(
                        status().isNotFound(),
                        header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE),
                        jsonPath("$.message").value("Item not found for request: /api/v1/items/1234567890")
                );
    }

    @Test
    @DisplayName("should return 201 when create item")
    public void shouldReturn201CreateItem() throws Exception {
        // given
        var itemDto = new ItemDto(
                "Title",
                ItemStatus.CURRENT,
                "Summary"
        );
        var item = ItemMapper.toNewItem(itemDto);

        // when
        Mockito.when(itemService.createItem(Mockito.any())).thenReturn(item);

        var content = objectMapper.writeValueAsString(item);

        // then
        mockMvc.perform(
                post("/api/v1/items")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))

                .andExpectAll(
                        status().isCreated(),
                        header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                );

        Mockito.verify(itemService, Mockito.times(1)).createItem(Mockito.any());
    }

    @Test
    @DisplayName("should return 204 when update item")
    public void shouldReturn204UpdateItem() throws Exception {
        // given
        var itemDto = new ItemDto(
                "Title",
                ItemStatus.CURRENT,
                "Summary"
        );
        var updatedItem= new Item(
                12345678L,
                ItemStatus.CURRENT,
                "Title",
                "Summary",
                createdDate,
                createdDate,
                null
        );

        // when
        Mockito.when(itemService.update(Mockito.anyLong(), Mockito.any())).thenReturn(updatedItem);
        var content = objectMapper.writeValueAsString(itemDto);

        // then
        var actual = mockMvc.perform(
                put("/api/v1/items/12345678")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpectAll(
                        status().isNoContent()
                )
                .andReturn();

        assertTrue(actual.getResponse().getContentAsString().isEmpty());

        Mockito.verify(itemService, Mockito.times(1)).update(Mockito.anyLong(), Mockito.any());
    }



}
