package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.item.model.Item;

public class dtoMapper {

    public static Item getItemFromDto(ItemDto dto) {
        return Item.builder()
                .id(dto.id)
                .name(dto.name)
                .description(dto.description)
                .available(dto.available)
                .build();
    }

    public static ItemDto getItemDto(Item item) {
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .build();
    }

}
