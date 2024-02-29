package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.List;

@Data
@Builder
public class ItemDto {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private BookingDto lastBooking;
    private BookingDto nextBooking;
    private List<CommentDto> comments;

    public void setLastBooking(BookingDto bookingDto) {
        this.lastBooking = bookingDto;

        if (bookingDto != null) {
            lastBooking.setBookerId(bookingDto.getBooker().getId());
        }

    }

    public void setNextBooking(BookingDto bookingDto) {
        this.nextBooking = bookingDto;

        if (bookingDto != null) {
            nextBooking.setBookerId(bookingDto.getBooker().getId());
        }

    }

}

