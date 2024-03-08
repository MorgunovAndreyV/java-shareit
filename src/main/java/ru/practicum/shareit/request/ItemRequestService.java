package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.RecordNotFoundException;
import ru.practicum.shareit.exception.RequestValidationException;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemRequestService {
    private final UserService userService;
    private final ItemRequestRepository itemRequestRepository;

    public Set<ItemRequest> getAll() {
        return new HashSet<>(itemRequestRepository.findAll());
    }

    public List<ItemRequest> getAllPaginated(Integer numberFrom, Integer pageSize) {
        if (pageSize == null && numberFrom == null) {
            return itemRequestRepository.findAll();
        }

        if (pageSize < 1 || numberFrom < 0) {
            throw new RequestValidationException("Некорректные параметры запроса с постраничным выводом");
        }

        return itemRequestRepository
                .findAll(PageRequest.of(numberFrom > 0 ? numberFrom / pageSize : 0, pageSize))
                .stream().collect(Collectors.toList());
    }

    public ItemRequest addNew(ItemRequest request, Long userId) {
        validateRequestData(request);
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


    public void delete(Long id) throws RecordNotFoundException {
        getItemRequestById(id);

        itemRequestRepository.deleteById(id);

    }

    public List<ItemRequest> getByAuthor(Long id) {
        userService.getUserById(id);

        List<ItemRequest> requestList = itemRequestRepository.findByAuthorId(id);

        return requestList;
    }

    private void validateRequestData(ItemRequest request) {
        if (request.getDescription() == null || request.getDescription().isEmpty()) {
            throw new RequestValidationException("Текст заявки не может быть пустым");
        }

    }
}
