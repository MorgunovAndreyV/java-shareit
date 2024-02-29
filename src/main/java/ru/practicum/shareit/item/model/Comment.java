package ru.practicum.shareit.item.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "COMMENTS")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "text")
    private String text;

    @ManyToOne
    @JoinColumn(name = "author_id")
    private User author;

    @Column(name = "created_date")
    private LocalDateTime created;

    @ManyToOne
    @JoinColumn(name = "item_id")
    private Item item;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Comment comment = (Comment) o;
        return Objects.equals(text, comment.text) && Objects.equals(author, comment.author)
                && Objects.equals(created, comment.created);
    }

    @Override
    public int hashCode() {
        return Objects.hash(text, author, created);
    }
}
