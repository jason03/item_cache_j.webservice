package net.zeotrope.item.api.resource;

import com.fasterxml.jackson.core.util.ByteArrayBuilder;
import net.zeotrope.item.exceptions.InvalidStatusException;
import net.zeotrope.item.service.ItemService;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest( {ItemController.class, DefaultExceptionHandler.class, CustomExceptionHandler.class} )
@ActiveProfiles("test")
public class ExceptionsHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ItemService itemService;

    @Test
    @DisplayName("should handle an internal server error exception")
    public void shouldHandleInternalServerErrorException() throws Exception {
        // given
        var uri = "/api/v1/items/1234567890";

        // when
        Mockito.when(itemService.get(Mockito.anyLong())).thenThrow(
                HttpServerErrorException.InternalServerError.create(
                        HttpStatusCode.valueOf(500),
                        "Test Error",
                        HttpHeaders.EMPTY,
                        ByteArrayBuilder.NO_BYTES,
                        null
                ));

        // then
        mockMvc.perform(get(uri))
                .andExpectAll(
                        status().is5xxServerError(),
                        jsonPath("$.message").value(String.format("Internal Server Error for request: %s", uri))
                );
    }

    @Test
    @DisplayName("should handle a bad gateway exception")
    public void shouldHandleBadGatewayException() throws Exception {
        // given
        var uri = "/api/v1/items/1234567890";

        // when
        Mockito.when(itemService.get(Mockito.anyLong())).thenThrow(
                HttpServerErrorException.InternalServerError.create(
                        HttpStatusCode.valueOf(502),
                        "Test Error",
                        HttpHeaders.EMPTY,
                        ByteArrayBuilder.NO_BYTES,
                        null
                ));

        // then
        mockMvc.perform(get(uri))
                .andExpectAll(
                        status().isBadGateway(),
                        jsonPath("$.message").value(String.format("Bad Gateway for request: %s", uri))
                );
    }

    @Test
    @DisplayName("should handle a no resource found exception")
    public void shouldHandleNoResourceFoundException() throws Exception {
        // given
        var uri = "/api/v1/items/111";

        // when
        // Checked exception
        Mockito.when(itemService.get(Mockito.anyLong())).thenAnswer( invocation -> { throw  new NoResourceFoundException(HttpMethod.GET, uri); } );

        // then
        mockMvc.perform(get(uri))
                .andExpectAll(
                        status().isNotFound(),
                        jsonPath("$.message").value(String.format("Resource Not Found for request: %s", uri))
                );
    }

    @Test
    @DisplayName("should handle an invalid status exception")
    public void shouldHandleInvalidStatusException() throws Exception {
        // given
        var invalidStatus = "invalid";
        var uri = "/api/v1/items/123456?status=" + invalidStatus;
        // when
        Mockito.when(itemService.get(Mockito.anyLong())).thenAnswer(invocation -> { throw new InvalidStatusException(invalidStatus); });
        // then
        mockMvc.perform(get(uri)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpectAll(
                        status().isBadRequest(),
                        jsonPath("$.message").value(String.format("Item request is invalid: /api/v1/items/123456?status=%s", invalidStatus))
                );
    }

    @Test
    @DisplayName("should handle a generic exception")
    public void shouldHandleGenericException() throws Exception {
        // given
        var uri = "/api/v1/items/123456";

        // when
        Mockito.when(itemService.get(Mockito.anyLong())).thenAnswer(invocation -> { throw new Exception(uri); });

        // then
        mockMvc.perform(get(uri)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpectAll(
                        status().is5xxServerError(),
                        jsonPath("$.message").value(String.format("Internal server error for request: %s", uri))
                );
    }
}
