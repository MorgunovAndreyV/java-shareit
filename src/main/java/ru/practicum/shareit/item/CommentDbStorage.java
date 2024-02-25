package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Comment;

import java.util.List;

@Component("CommentDbStorage")
@RequiredArgsConstructor
public class CommentDbStorage implements CommentStorage {
    private final CommentRepository commentRepository;

    @Override
    public Comment addNew(Comment comment) {
        return commentRepository.save(comment);
    }

    @Override
    public List<Comment> getByItemId(Long itemId) {
        return commentRepository.findByItemId(itemId);
    }


}
