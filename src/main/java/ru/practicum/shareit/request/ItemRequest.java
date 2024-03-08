package ru.practicum.shareit.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;


@Entity
@Table(name = "ITEMREQUESTS")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemRequest {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "created_date")
    private LocalDateTime created;
    @Column(name = "description")
    private String description;
    @Column(name = "author_id")
    private Long authorId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ItemRequest request = (ItemRequest) o;
        return Objects.equals(id, request.id)
                && Objects.equals(created.withNano(0), request.created.withNano(0))
                && Objects.equals(description, request.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, created, description);
    }
}
