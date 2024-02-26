package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.exception.CommentValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final BookingService bookingService;
    private final UserService userService;
    private final ItemService itemService;

    public Comment addNew(Long itemId, Long userId, CommentDto comment) {
        Item item = itemService.getItemById(itemId);
        User user = userService.getUserById(userId);

        validateCommentData(item, user, comment);

        Comment commentFromDto = CommentMapper.toEntity(comment);

        commentFromDto.setAuthor(user);
        commentFromDto.setItem(item);
        commentFromDto.setCreated(LocalDateTime.now());

        Comment newComment = commentRepository.save(commentFromDto);
        log.info("Новый комментарий добавлен успешно. id:" + newComment.getId());

        return newComment;
    }

    public List<Comment> getByItemId(Long itemId) {
        return commentRepository.findByItemId(itemId);
    }

    private void validateCommentData(Item item, User user, CommentDto comment) {
        if (comment.getText() == null || comment.getText().isEmpty()) {
            throw new CommentValidationException("Текст комментария не может быть пустым");
        }

        Set<Booking> itemBookingsByUser = bookingService.getByBooker(user.getId()).stream()
                .filter(booking -> item.getId().equals(booking.getItem().getId())
                        && !BookingStatus.REJECTED.equals(booking.getStatus())
                        && booking.getStart().isBefore(LocalDateTime.now()))
                .collect(Collectors.toSet());

        if (itemBookingsByUser.isEmpty()) {
            throw new CommentValidationException("Для размещения комментария не найдены бронирования пользователем " +
                    user.getId() + " вещи " + item.getId());
        }

    }

}
