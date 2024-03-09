package ru.practicum.shareit.booking;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hibernate.mapping.Any;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.exception.BookingControllerBadRequestException;
import ru.practicum.shareit.exception.BookingValidationException;
import ru.practicum.shareit.handler.ErrorHandler;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.booking.BookingStatus.WAITING;

@WebMvcTest(controllers = BookingController.class)
class BookingControllerTest {
    @Autowired
    ObjectMapper mapper;

    @MockBean
    BookingService bookingService;

    @MockBean
    ErrorHandler errorHandler;

    @Autowired
    private MockMvc mvc;

    private static BookingDto testBookingDtoIn;
    private static User testUserOwner;
    private static User testUserBooker;
    private static Item testItem;

    @BeforeAll
    static void init() {

        testUserOwner = User.builder()
                .id(2L)
                .name("user_owner1")
                .email("user_owner1@user.com")
                .build();
        testUserBooker = User.builder()
                .id(1L)
                .name("bookerUser")
                .email("booker@user.com")
                .build();

        testItem = Item.builder()
                .id(1L)
                .name("Test item")
                .description("Just test item")
                .available(true)
                .owner(testUserOwner)
                .build();

        testBookingDtoIn = BookingDto.builder()
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .status(WAITING)
                .booker(UserMapper.toDto(testUserBooker))
                .item(ItemMapper.toDto(testItem))
                .build();

    }

    @Test
    void testAddNew() throws Exception {
        BookingDto dto = testBookingDtoIn;
        dto.setId(1L);
        Booking testBooking2 = BookingMapper.toEntity(dto);

        when(bookingService.addNew(Mockito.any(), Mockito.anyLong()))
                .thenReturn(testBooking2);

        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(testBooking2))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.start",
                        is(testBooking2.getStart().truncatedTo(ChronoUnit.MILLIS).toString())))
                .andExpect(jsonPath("$.end",
                        is(testBooking2.getEnd().truncatedTo(ChronoUnit.MILLIS).toString())))
                .andExpect(jsonPath("$.status", is(WAITING.name())))
                .andExpect(jsonPath("$.booker.id", is(testBooking2.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.item.id", is(testBooking2.getItem().getId()), Long.class))
                .andExpect(jsonPath("$.item.name", is(testBooking2.getItem().getName())));
    }

    @Test
    void testChange() throws Exception {
        BookingDto dto = testBookingDtoIn;
        dto.setId(1L);
        Booking testBooking2 = BookingMapper.toEntity(dto);

        when(bookingService.changeBookingApproval(Mockito.anyLong(), Mockito.anyLong(), Mockito.anyBoolean()))
                .thenReturn(testBooking2);

        mvc.perform(patch("/bookings/{id}", 1L)
                        .param("approved", "true")
                        .header("X-Sharer-User-Id", 2L)
                        .content(mapper.writeValueAsString(testBookingDtoIn))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.start",
                        is(testBooking2.getStart().toString())))
                .andExpect(jsonPath("$.end",
                        is(testBooking2.getEnd().toString())))
                .andExpect(jsonPath("$.status", is(WAITING.name())))
                .andExpect(jsonPath("$.booker.id", is(testBooking2.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.item.id", is(testBooking2.getItem().getId()), Long.class))
                .andExpect(jsonPath("$.item.name", is(testBooking2.getItem().getName())));

    }

    @Test
    void testGetById() throws Exception {
        BookingDto dto = testBookingDtoIn;
        dto.setId(1L);
        Booking testBooking2 = BookingMapper.toEntity(dto);

        when(bookingService.getBookingByIdFilteredByUser(Mockito.any(), Mockito.anyLong()))
                .thenReturn(testBooking2);

        mvc.perform(get("/bookings/{id}", 1L)
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.start",
                        is(testBooking2.getStart().truncatedTo(ChronoUnit.MILLIS).toString())))
                .andExpect(jsonPath("$.end",
                        is(testBooking2.getEnd().truncatedTo(ChronoUnit.MILLIS).toString())))
                .andExpect(jsonPath("$.status", is(WAITING.name())))
                .andExpect(jsonPath("$.booker.id", is(testBooking2.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.item.id", is(testBooking2.getItem().getId()), Long.class))
                .andExpect(jsonPath("$.item.name", is(testBooking2.getItem().getName())));

    }

    @Test
    void testGetAllForUsersByState() throws Exception {
        List<Booking> bookingList = new ArrayList<>();
        BookingDto dto = testBookingDtoIn;
        dto.setId(1L);
        Booking testBooking2 = BookingMapper.toEntity(dto);

        bookingList.add(testBooking2);
        when(bookingService.getBookingsFilteredByState(Mockito.anyLong(), Mockito.anyBoolean(), Mockito.any(),
                Mockito.any(), Mockito.any()))
                .thenReturn(bookingList);

        mvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].start",
                        is(testBooking2.getStart().toString())))
                .andExpect(jsonPath("$[0].end",
                        is(testBooking2.getEnd().toString())))
                .andExpect(jsonPath("$[0].booker.id", is(testBooking2.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$[0].item.id", is(testBooking2.getItem().getId()), Long.class))
                .andExpect(jsonPath("$[0].item.name", is(testBooking2.getItem().getName())));

    }

    @Test
    void testGetAllForOwnerByState() throws Exception {
        List<Booking> bookingList = new ArrayList<>();
        BookingDto dto = testBookingDtoIn;
        dto.setId(1L);
        Booking testBooking2 = BookingMapper.toEntity(dto);

        bookingList.add(testBooking2);
        when(bookingService.getBookingsFilteredByState(Mockito.anyLong(), Mockito.anyBoolean(), Mockito.any(),
                Mockito.any(), Mockito.any()))
                .thenReturn(bookingList);

        mvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].start",
                        is(testBooking2.getStart().toString())))
                .andExpect(jsonPath("$[0].end",
                        is(testBooking2.getEnd().toString())))
                .andExpect(jsonPath("$[0].booker.id", is(testBooking2.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$[0].item.id", is(testBooking2.getItem().getId()), Long.class))
                .andExpect(jsonPath("$[0].item.name", is(testBooking2.getItem().getName())));

    }


    @Test
    void testBookingValidationExceptionFailsController() throws Exception {
        BookingDto dto = testBookingDtoIn;
        dto.setId(1L);
        Booking testBooking2 = BookingMapper.toEntity(dto);

        when(bookingService.addNew(Mockito.any(), anyLong())).thenThrow(BookingValidationException.class);

        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(testBooking2))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetAllForUsersByStateFailedByStateUnknown() throws Exception {
        /*when(bookingService.getBookingsFilteredByState(Mockito.anyLong(), Mockito.anyBoolean(), Mockito.any(),
                Mockito.any(), Mockito.any()))
                .thenThrow(BookingControllerBadRequestException.class);*/

        mvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .param("state", "TEST_TEST_TEST")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

    }

}