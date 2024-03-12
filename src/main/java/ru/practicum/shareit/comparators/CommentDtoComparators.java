package ru.practicum.shareit.comparators;

import ru.practicum.shareit.item.dto.CommentDto;

import java.util.Comparator;

public class CommentDtoComparators {

    public static final Comparator<CommentDto> compareCommentDtosById = (comment1, comment2) -> {
        if (comment1.getId() != null && comment2.getId() != null) {
            return comment1.getId().compareTo(comment2.getId());

        } else if (comment1.getId() == null && comment2.getId() != null) {
            return -1;

        } else if (comment1.getId() != null) {
            return 1;

        } else {
            return 0;

        }

    };
}
