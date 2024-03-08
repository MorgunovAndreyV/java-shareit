package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
class ItemRequestControllerTest {
    private static Item testItem1;
    private static Item testItem2;

    private static User testUser1;
    private static User testUser2;

    private static ItemRequest testItemRequest1;
    private static ItemRequest testItemRequest2;

    @MockBean
    UserService userService;

    @MockBean
    ItemRequestService itemRequestService;

    @MockBean
    ItemService itemService;

    @Autowired
    ObjectMapper mapper;

    @Autowired
    private MockMvc mvc;

    @BeforeAll
    static void init() {
        testUser1 = User.builder()
                .id(1L)
                .name("Test user name")
                .email("testuser@mail.ru")
                .build();

        testUser2 = User.builder()
                .id(2L)
                .name("Test user name #2")
                .email("testuser2@mail.ru")
                .build();

        testItemRequest1 = ItemRequest.builder()
                .id(1L)
                .description("Test description")
                .created(LocalDateTime.now())
                .authorId(testUser2.getId())
                .build();

        testItemRequest2 = ItemRequest.builder()
                .id(2L)
                .description("Test description #2")
                .created(LocalDateTime.now())
                .authorId(testUser2.getId())
                .build();

        testItem1 = Item.builder()
                .id(1L)
                .available(true)
                .name("Test item 1")
                .description("Test Item 1 desc")
                .owner(testUser1)
                .build();

        testItem2 = Item.builder()
                .id(2L)
                .available(true)
                .name("Test item 2")
                .description("Test Item 2 desc")
                .owner(testUser1)
                .build();

    }

    @Test
    void testGetById() throws Exception {
        List<Item> returnItemList = new ArrayList<>();
        returnItemList.add(testItem1);
        returnItemList.add(testItem2);

        when(userService.getUserById(Mockito.anyLong()))
                .thenReturn(testUser1);

        when(itemRequestService.getItemRequestById(Mockito.notNull()
        )).thenReturn(testItemRequest1);

        when(itemService.getItemsByRequestId(Mockito.anyLong()))
                .thenReturn(returnItemList);

        ItemRequestDto testItemRequest1Dto = ItemRequestMapper.toDto(testItemRequest1);

        mvc.perform(get("/requests/{id}", 1L)
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id",
                        is(testItemRequest1Dto.getId()), Long.class))
                .andExpect(jsonPath("$.description",
                        is(testItemRequest1Dto.getDescription())));
    }

    @Test
    void testGetByAuthor() throws Exception {
        List<ItemRequest> returnItemRequestList = new ArrayList<>();
        returnItemRequestList.add(testItemRequest1);
        returnItemRequestList.add(testItemRequest2);

        List<Item> returnItemList = new ArrayList<>();
        returnItemList.add(testItem1);
        returnItemList.add(testItem2);

        when(itemRequestService.getByAuthor(Mockito.anyLong()))
                .thenReturn(returnItemRequestList);

        when(itemService.getItemsByRequestId(Mockito.notNull()
        )).thenReturn(returnItemList);

        ItemRequestDto testItemRequestDto1 = ItemRequestMapper.toDto(testItemRequest1);
        ItemRequestDto testItemRequestDto2 = ItemRequestMapper.toDto(testItemRequest2);

        mvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id",
                        is(testItemRequestDto1.getId()), Long.class))
                .andExpect(jsonPath("$[0].description",
                        is(testItemRequestDto1.getDescription())))
                .andExpect(jsonPath("$[1].id",
                        is(testItemRequestDto2.getId()), Long.class))
                .andExpect(jsonPath("$[1].description",
                        is(testItemRequestDto2.getDescription())));

    }

    @Test
    void testGetAllPaginated() throws Exception {
        List<ItemRequest> returnItemRequestList = new ArrayList<>();
        returnItemRequestList.add(testItemRequest1);
        returnItemRequestList.add(testItemRequest2);

        List<Item> returnItemList = new ArrayList<>();
        returnItemList.add(testItem1);
        returnItemList.add(testItem2);

        when(userService.getUserById(Mockito.anyLong()))
                .thenReturn(testUser1);

        when(itemService.getItemsByRequestId(Mockito.notNull()
        )).thenReturn(returnItemList);

        when(itemRequestService.getAllPaginated(Mockito.any(), Mockito.any()))
                .thenReturn(returnItemRequestList);

        ItemRequestDto testItemRequestDto1 = ItemRequestMapper.toDto(testItemRequest1);
        ItemRequestDto testItemRequestDto2 = ItemRequestMapper.toDto(testItemRequest2);

        mvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id",
                        is(testItemRequestDto1.getId()), Long.class))
                .andExpect(jsonPath("$[0].description",
                        is(testItemRequestDto1.getDescription())))
                .andExpect(jsonPath("$[1].id",
                        is(testItemRequestDto2.getId()), Long.class))
                .andExpect(jsonPath("$[1].description",
                        is(testItemRequestDto2.getDescription())));

    }

    @Test
    void testAddNew() throws Exception {
        when(itemRequestService.addNew(Mockito.any(), Mockito.anyLong()))
                .thenReturn(testItemRequest1);

        ItemRequestDto testItemRequestDto1 = ItemRequestMapper.toDto(testItemRequest1);

        mvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 2L)
                        .content(mapper.writeValueAsString(testItemRequestDto1))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id",
                        is(testItemRequestDto1.getId()), Long.class))
                .andExpect(jsonPath("$.description",
                        is(testItemRequestDto1.getDescription())));
    }
}