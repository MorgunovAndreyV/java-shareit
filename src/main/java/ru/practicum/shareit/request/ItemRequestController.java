package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.comparators.ItemRequestDtoComparators;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.user.UserService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
public class ItemRequestController {
    private final ItemRequestService itemRequestService;
    private final ItemService itemService;
    private final UserService userService;

    @GetMapping("/{id}")
    public ItemRequestDto getById(@PathVariable("id") Long requestId, @RequestHeader("X-Sharer-User-Id") Long userId) {
        userService.getUserById(userId);

        ItemRequest request = itemRequestService.getItemRequestById(requestId);
        ItemRequestDto dto = ItemRequestMapper.toDto(request);

        dto.setItems(itemService.getItemsByRequestId(request.getId())
                .stream().map(ItemMapper::toDto).collect(Collectors.toList()));

        return dto;
    }

    @GetMapping
    public List<ItemRequestDto> getByAuthor(@RequestHeader("X-Sharer-User-Id") Long userId) {
        List<ItemRequest> requestList = itemRequestService.getByAuthor(userId);

        return requestList.stream()
                .map(itemRequest -> {
                    ItemRequestDto dto = ItemRequestMapper.toDto(itemRequest);
                    dto.setItems(itemService.getItemsByRequestId(itemRequest.getId())
                            .stream().map(ItemMapper::toDto).collect(Collectors.toList()));
                    return dto;
                })
                .sorted(ItemRequestDtoComparators.compareItemRequestsById)
                .collect(Collectors.toList());
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getAllPaginated(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                @Nullable @RequestParam("from") Integer numberFrom,
                                                @Nullable @RequestParam("size") Integer pageSize) {

        userService.getUserById(userId);
        List<ItemRequest> requestList =
                itemRequestService.getAllPaginated(numberFrom, pageSize);

        return requestList.stream()
                .filter(request -> !userId.equals(request.getAuthorId()))
                .map(itemRequest -> {
                    ItemRequestDto dto = ItemRequestMapper.toDto(itemRequest);
                    dto.setItems(itemService.getItemsByRequestId(itemRequest.getId())
                            .stream().map(ItemMapper::toDto).collect(Collectors.toList()));
                    return dto;
                })
                .sorted(ItemRequestDtoComparators.compareItemRequestsById)
                .collect(Collectors.toList());
    }


    @PostMapping
    public ItemRequestDto addNew(@RequestHeader("X-Sharer-User-Id") Long userId, @RequestBody ItemRequestDto itemRequestDto) {
        return ItemRequestMapper.toDto(itemRequestService.addNew(ItemRequestMapper.toEntity(itemRequestDto), userId));
    }


}
