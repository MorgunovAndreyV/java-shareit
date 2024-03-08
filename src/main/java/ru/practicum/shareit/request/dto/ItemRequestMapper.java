package ru.practicum.shareit.request.dto;

import ru.practicum.shareit.request.ItemRequest;

import java.time.temporal.ChronoUnit;

public class ItemRequestMapper {

    public static ItemRequest toEntity(ItemRequestDto dto) {
        return ItemRequest.builder()
                .id(dto.getId())
                .created(dto.getCreated() != null ? dto.getCreated().truncatedTo(ChronoUnit.MILLIS) : null)
                .description(dto.getDescription())
                .build();
    }

    public static ItemRequestDto toDto(ItemRequest request) {
        return ItemRequestDto.builder()
                .id(request.getId())
                .created(request.getCreated() != null ? request.getCreated().truncatedTo(ChronoUnit.MILLIS) : null)
                .description(request.getDescription())
                .build();
    }

    public static void fillFromDto(ItemRequestDto dto, ItemRequest request) {
        if (dto.getCreated() != null) {
            request.setCreated(dto.getCreated().truncatedTo(ChronoUnit.MILLIS));
        }

        if (dto.getDescription() != null) {
            request.setDescription(dto.getDescription());
        }

    }

}