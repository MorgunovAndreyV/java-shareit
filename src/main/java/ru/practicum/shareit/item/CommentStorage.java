package ru.practicum.shareit.item;

import ru.practicum.shareit.item.model.Comment;

import java.util.List;

public interface CommentStorage {

    Comment addNew(Comment comment);

    List<Comment> getByItemId(Long itemId);
}
