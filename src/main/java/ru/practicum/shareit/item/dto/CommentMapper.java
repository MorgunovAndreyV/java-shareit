package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.item.model.Comment;

public class CommentMapper {

    public static Comment toEntity(CommentDto dto) {
        return Comment.builder()
                .id(dto.getId())
                .text(dto.getText())
                .created(dto.getCreated())
                .build();
    }

    public static CommentDto toDto(Comment comment) {
        return CommentDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .authorName(comment.getAuthor().getName())
                .created(comment.getCreated())
                .build();
    }

    public static void fillFromDto(CommentDto dto, Comment comment) {
        if (dto.getText() != null) {
            comment.setText(dto.getText());
        }

        if (dto.getCreated() != null) {
            comment.setCreated(dto.getCreated());
        }

    }

}
