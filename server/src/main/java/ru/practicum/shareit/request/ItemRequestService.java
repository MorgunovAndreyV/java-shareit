package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.RecordNotFoundException;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemRequestService {
    private final UserService userService;
    private final ItemRequestRepository itemRequestRepository;

    public List<ItemRequest> getAllPaginated(Integer numberFrom, Integer pageSize) {
        if (pageSize == null && numberFrom == null) {
            return itemRequestRepository.findAll();
        }

        return itemRequestRepository
                .findAll(PageRequest.of(numberFrom > 0 ? numberFrom / pageSize : 0, pageSize))
                .stream().collect(Collectors.toList());
    }

    public ItemRequest addNew(ItemRequest request, Long userId) {
        userService.getUserById(userId);
        request.setAuthorId(userId);
        request.setCreated(LocalDateTime.now());

        return itemRequestRepository.save(request);
    }

    public ItemRequest getItemRequestById(Long id) {
        ItemRequest request = itemRequestRepository.findById(id).orElse(null);

        if (request == null) {
            throw new RecordNotFoundException("Заявка с id " + id + " не найдена");
        }

        return request;
    }

    public List<ItemRequest> getByAuthor(Long id) {
        userService.getUserById(id);

        List<ItemRequest> requestList = itemRequestRepository.findByAuthorId(id);

        return requestList;
    }

}
