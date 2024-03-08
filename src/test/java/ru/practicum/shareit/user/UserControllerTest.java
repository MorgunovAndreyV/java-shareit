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
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;

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

    @MockBean
    UserService userService;

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
}