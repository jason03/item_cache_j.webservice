package net.zeotrope.item.common;

import net.zeotrope.item.domain.ItemStatus;
import net.zeotrope.item.exceptions.InvalidStatusException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ActiveProfiles("test")
@SpringBootTest(
        classes = { StringToItemStatusConverter.class }
)
public class StringToItemStatusConverterTest {

    @Autowired
    private StringToItemStatusConverter converterUnderTest;

    @CsvSource(
            value = {
                    "current, CURRENT",
                    "Current, CURRENT",
                    "disContinued, DISCONTINUED"
            }
    )
    @ParameterizedTest(name = "should convert {0} to item status {1}")
    public void shouldConvertStringToItemStatus(String status, ItemStatus expected) {
        // given
        // when
        var actual = converterUnderTest.convert(status);
        // then
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName( "should throw an InvalidStatusException for an invalid item status")
    public void shouldThrowAnInvalidStatusExceptionForInvalidItemStatus(){
        // given
        var invalidStatus = "invalid";
        // when
        // then
        var exception = assertThrows(InvalidStatusException.class,
                () -> {
                    converterUnderTest.convert(invalidStatus);
                }
        );
        assertEquals(invalidStatus, exception.getMessage());
    }
}
