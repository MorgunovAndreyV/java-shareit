package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
class ItemControllerTest {

    private static Item testItem1;
    private static Item testItem2;

    private static Comment testComment1;
    private static Comment testComment2;

    private static User testUser1;

    @Autowired
    ObjectMapper mapper;

    @MockBean
    ItemService itemService;

    @MockBean
    BookingService bookingService;

    @MockBean
    CommentService commentService;

    @Autowired
    private MockMvc mvc;

    @BeforeAll
    static void init() {
        testUser1 = User.builder()
                .id(1L)
                .name("Test user name")
                .email("testuser@mail.ru")
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

        testComment1 = Comment.builder()
                .id(1L)
                .text("test comment 1")
                .item(testItem1)
                .author(testUser1)
                .build();

        testComment2 = Comment.builder()
                .id(2L)
                .text("test comment 2")
                .item(testItem1)
                .author(testUser1)
                .build();
    }

    @Test
    void testGetByOwner() throws Exception {
        List<Item> returnItemList = new ArrayList<>();
        returnItemList.add(testItem1);
        returnItemList.add(testItem2);

        ItemDto testItem1Dto = ItemMapper.toDto(testItem1);
        ItemDto testItem2Dto = ItemMapper.toDto(testItem2);

        List<Comment> returnCommentList = new ArrayList<>();
        returnCommentList.add(testComment1);
        returnCommentList.add(testComment2);

        when(commentService.getByItemId(Mockito.notNull()
        )).thenReturn(returnCommentList);

        when(itemService.getByOwner(Mockito.anyLong()))
                .thenReturn(returnItemList);

        mvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id",
                        is(testItem1Dto.getId()), Long.class))
                .andExpect(jsonPath("$[1].id",
                        is(testItem2Dto.getId()), Long.class));
    }

    @Test
    void testGetById() throws Exception {
        List<Comment> returnCommentList = new ArrayList<>();
        returnCommentList.add(testComment1);
        returnCommentList.add(testComment2);

        ItemDto testItem1Dto = ItemMapper.toDto(testItem1);

        when(commentService.getByItemId(Mockito.notNull()
        )).thenReturn(returnCommentList);

        when(itemService.getItemById(Mockito.anyLong()))
                .thenReturn(testItem1);

        mvc.perform(get("/items/{id}", 1L)
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id",
                        is(testItem1Dto.getId()), Long.class));
    }

    @Test
    void testSearchByText() throws Exception {
        List<Item> returnItemList = new ArrayList<>();
        returnItemList.add(testItem1);
        returnItemList.add(testItem2);

        ItemDto testItem1Dto = ItemMapper.toDto(testItem1);
        ItemDto testItem2Dto = ItemMapper.toDto(testItem2);


        when(itemService.getAvailableItemsByText(Mockito.anyString()))
                .thenReturn(returnItemList);

        mvc.perform(get("/items/search")
                        .header("X-Sharer-User-Id", 1L)
                        .param("text", "test test")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id",
                        is(testItem1Dto.getId()), Long.class))
                .andExpect(jsonPath("$[1].id",
                        is(testItem2Dto.getId()), Long.class));


    }

    @Test
    void testCreateComment() throws Exception {
        CommentDto testComment1Dto = CommentMapper.toDto(testComment1);

        when(commentService.addNew(Mockito.anyLong(), Mockito.anyLong(), Mockito.any()))
                .thenReturn(testComment1);

        mvc.perform(post("/items/{id}/comment", 1L)
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(testComment1Dto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id",
                        is(testComment1.getId()), Long.class))
                .andExpect(jsonPath("$.text",
                        is(testComment1.getText())));
    }

    @Test
    void testAddNew() throws Exception {
        ItemDto testItem2Dto = ItemMapper.toDto(testItem2);

        when(itemService.addNew(Mockito.any(), Mockito.anyLong()))
                .thenReturn(testItem2);

        mvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(testItem2Dto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id",
                        is(testComment2.getId()), Long.class));

    }

    @Test
    void testChange() throws Exception {
        ItemDto testItem2Dto = ItemMapper.toDto(testItem2);

        when(itemService.change(Mockito.any(), Mockito.anyLong()))
                .thenReturn(testItem2);


        mvc.perform(patch("/items/{id}", 1L)
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(testItem2Dto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id",
                        is(testComment2.getId()), Long.class));
    }
}