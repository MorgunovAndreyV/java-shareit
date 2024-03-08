package ru.practicum.shareit.booking.dto;

import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.user.dto.UserMapper;

import java.time.temporal.ChronoUnit;

public class BookingMapper {

    public static Booking toEntity(BookingDto dto) {
        return Booking.builder()
                .id(dto.getId())
                .start(dto.getStart() != null ? dto.getStart().truncatedTo(ChronoUnit.MILLIS) : null)
                .end(dto.getEnd().truncatedTo(ChronoUnit.MILLIS))
                .status(dto.getStatus())
                .booker(UserMapper.toEntity(dto.getBooker()))
                .item(ItemMapper.toEntity(dto.getItem()))
                .build();
    }

    public static BookingDto toDto(Booking booking) {
        return BookingDto.builder()
                .id(booking.getId())
                .start(booking.getStart() != null ? booking.getStart().truncatedTo(ChronoUnit.MILLIS) : null)
                .end(booking.getEnd().truncatedTo(ChronoUnit.MILLIS))
                .status(booking.getStatus())
                .booker(UserMapper.toDto(booking.getBooker()))
                .item(ItemMapper.toDto(booking.getItem()))
                .build();
    }

    public static void fillFromDto(BookingDto bookingDto, Booking booking) {
        if (bookingDto.getStart() != null) {
            booking.setStart(bookingDto.getStart().truncatedTo(ChronoUnit.MILLIS));
        }

        if (bookingDto.getEnd() != null) {
            booking.setEnd(bookingDto.getEnd().truncatedTo(ChronoUnit.MILLIS));
        }

        if (bookingDto.getStatus() != null) {
            booking.setStatus(bookingDto.getStatus());
        }

        if (bookingDto.getBooker() != null) {
            booking.setBooker(UserMapper.toEntity(bookingDto.getBooker()));
        }

        if (bookingDto.getItem() != null) {
            booking.setItem(ItemMapper.toEntity(bookingDto.getItem()));
        }

    }

}
