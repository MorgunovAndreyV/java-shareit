package ru.practicum.shareit.comparators;

import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.Comparator;

public class ItemRequestDtoComparators {
    public static final Comparator<ItemRequestDto> compareItemRequestsById = (request1, request2) -> {
        if (request1.getId() != null && request2.getId() != null) {
            return request1.getId().compareTo(request2.getId());

        } else if (request1.getId() == null && request2.getId() != null) {
            return -1;

        } else if (request1.getId() != null) {
            return 1;

        } else {
            return 0;

        }

    };
}
