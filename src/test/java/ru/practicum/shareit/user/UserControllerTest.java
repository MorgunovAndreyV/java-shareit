package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exception.UserValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
class UserControllerTest {
    private static User testUser1;
    private static User testUser2;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper mapper;

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
                .name("Test user name 2")
                .email("testuser2@mail.ru")
                .build();

    }

    @Test
    void testGetAll() throws Exception {
        Set<User> userList = new HashSet<>();
        userList.add(testUser1);
        userList.add(testUser2);

        when(userService.getAll())
                .thenReturn(userList);

        mvc.perform(get("/users")
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id",
                        is(testUser1.getId()), Long.class))
                .andExpect(jsonPath("$[0].name",
                        is(testUser1.getName())))
                .andExpect(jsonPath("$[0].email",
                        is(testUser1.getEmail())))
                .andExpect(jsonPath("$[1].id",
                        is(testUser2.getId()), Long.class))
                .andExpect(jsonPath("$[1].name",
                        is(testUser2.getName())))
                .andExpect(jsonPath("$[1].email",
                        is(testUser2.getEmail())));

    }

    @Test
    void testGetById() throws Exception {
        when(userService.getUserById(Mockito.anyLong()))
                .thenReturn(testUser1);

        UserDto userDto1 = UserMapper.toDto(testUser1);

        mvc.perform(get("/users/{id}", 1L)
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id",
                        is(userDto1.getId()), Long.class))
                .andExpect(jsonPath("$.name",
                        is(userDto1.getName())))
                .andExpect(jsonPath("$.email",
                        is(userDto1.getEmail())));

    }

    @Test
    void testAddNew() throws Exception {
        when(userService.addNew(any()))
                .thenReturn(testUser1);

        UserDto userDto1 = UserMapper.toDto(testUser1);

        mvc.perform(post("/users")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(userDto1))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id",
                        is(userDto1.getId()), Long.class))
                .andExpect(jsonPath("$.name",
                        is(userDto1.getName())))
                .andExpect(jsonPath("$.email",
                        is(userDto1.getEmail())));

    }

    @Test
    void testChange() throws Exception {
        when(userService.change(any()))
                .thenReturn(testUser1);

        UserDto userDto1 = UserMapper.toDto(testUser1);

        mvc.perform(patch("/users/{id}", 1L)
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(userDto1))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id",
                        is(userDto1.getId()), Long.class))
                .andExpect(jsonPath("$.name",
                        is(userDto1.getName())))
                .andExpect(jsonPath("$.email",
                        is(userDto1.getEmail())));

    }

    @Test
    void testDelete() throws Exception {
        doNothing().when(userService).delete(anyLong());

        mvc.perform(delete("/users/{id}", 1L)
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

    }

    @Test
    void testUserValidationExceptionFailsController() throws Exception {
        UserDto userDto1 = UserMapper.toDto(testUser1);

        when(userService.addNew(Mockito.any())).thenThrow(new UserValidationException("test"));

        mvc.perform(post("/users")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(userDto1))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

    }

}