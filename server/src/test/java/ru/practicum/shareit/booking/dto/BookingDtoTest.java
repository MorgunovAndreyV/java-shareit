package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.BookingStatus;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class BookingDtoTest {

    @Autowired
    private JacksonTester<BookingDto> json;

    @Test
    void testBookingDto() throws Exception {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

        BookingDto bookingDto = new BookingDto(
                1L,
                LocalDateTime.now().plusDays(1).truncatedTo(ChronoUnit.SECONDS),
                LocalDateTime.now().plusDays(1).truncatedTo(ChronoUnit.SECONDS),
                1L,
                1L,
                null,
                null,
                BookingStatus.WAITING);

        JsonContent<BookingDto> result = json.write(bookingDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").asString()
                .isEqualTo(bookingDto.getId().toString());
        assertThat(result).extractingJsonPathStringValue("$.start")
                .isEqualTo(bookingDto.getStart().format(dtf));
        assertThat(result).extractingJsonPathStringValue("$.end")
                .isEqualTo(bookingDto.getEnd().format(dtf));
        assertThat(result).extractingJsonPathNumberValue("$.itemId").asString()
                .isEqualTo(bookingDto.getItemId().toString());
        assertThat(result).extractingJsonPathNumberValue("$.bookerId").asString()
                .isEqualTo(bookingDto.getBookerId().toString());
        assertThat(result).extractingJsonPathStringValue("$.status").isEqualTo("WAITING");
    }

}