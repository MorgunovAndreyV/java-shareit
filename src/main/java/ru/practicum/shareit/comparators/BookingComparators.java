package ru.practicum.shareit.comparators;


import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.Comparator;

public class BookingComparators {

    public static final Comparator<BookingDto> compareBookingDtoByStartDateDesc = (booking1, booking2) -> {
        if (booking1.getStart() != null && booking2.getStart() != null) {
            if (booking1.getStart().isBefore(booking2.getStart())) {
                return 1;
            } else if (booking2.getStart().isBefore(booking1.getStart())) {
                return -1;
            } else {
                return 0;
            }

        } else if (booking1.getStart() == null && booking2.getStart() != null) {
            return 1;

        } else if (booking1.getStart() != null) {
            return -1;

        } else {
            return 0;

        }

    };

}
