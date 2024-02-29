package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByBookerId(Long id);

    List<Booking> findByItemOwnerId(Long id);

    @Query("select bk from Booking as bk " +
            "join bk.booker as bu " +
            "where bk.end < CURRENT_TIMESTAMP and bu.id = ?1")
    List<Booking> findPastBookingsForBooker(Long id);

    @Query("select bk from Booking as bk " +
            "join bk.item as it " +
            "where bk.end < CURRENT_TIMESTAMP and it.owner.id = ?1")
    List<Booking> findPastBookingsForOwner(Long id);

    @Query("select bk from Booking as bk " +
            "join bk.booker as bu " +
            "where bk.end > CURRENT_TIMESTAMP and bk.start < CURRENT_TIMESTAMP and bu.id = ?1")
    List<Booking> findCurrentBookingsForBooker(Long id);

    @Query("select bk from Booking as bk " +
            "join bk.item as it " +
            "where bk.end > CURRENT_TIMESTAMP and bk.start < CURRENT_TIMESTAMP and it.owner.id = ?1")
    List<Booking> findCurrentBookingsForOwner(Long id);

    @Query("select bk from Booking as bk " +
            "join bk.booker as bu " +
            "where bk.start > CURRENT_TIMESTAMP and bu.id = ?1")
    List<Booking> findFutureBookingsForBooker(Long id);

    @Query("select bk from Booking as bk " +
            "join bk.item as it " +
            "where bk.start > CURRENT_TIMESTAMP and it.owner.id = ?1")
    List<Booking> findFutureBookingsForOwner(Long id);

    @Query("select bk from Booking as bk " +
            "join bk.booker as bu " +
            "where bk.status ='WAITING' and bu.id = ?1")
    List<Booking> findWaitingBookingsForBooker(Long id);

    @Query("select bk from Booking as bk " +
            "join bk.item as it " +
            "where bk.status ='WAITING' and it.owner.id = ?1")
    List<Booking> findWaitingBookingsForOwner(Long id);

    @Query("select bk from Booking as bk " +
            "join bk.booker as bu " +
            "where bk.status ='REJECTED' and bu.id = ?1")
    List<Booking> findRejectedBookingsForBooker(Long id);

    @Query("select bk from Booking as bk " +
            "join bk.item as it " +
            "where bk.status ='REJECTED' and it.owner.id = ?1")
    List<Booking> findRejectedBookingsForOwner(Long id);

    @Query(value = "SELECT * " +
            "FROM BOOKINGS AS qry1 " +
            "INNER JOIN (SELECT ITEM_ID,MAX(END_DATE) as MAXENDDATE " +
            "FROM BOOKINGS " +
            "WHERE ITEM_ID = ?1 AND STATUS <>'REJECTED' AND ((CURRENT_TIMESTAMP > START_DATE " +
            "AND CURRENT_TIMESTAMP < END_DATE) " +
            "OR CURRENT_TIMESTAMP > END_DATE) " +
            "GROUP BY ITEM_ID) AS qry2 ON qry1.ITEM_ID = qry2.ITEM_ID AND qry1.END_DATE= qry2.MAXENDDATE",
            nativeQuery = true)
    Booking findLastBookingIdForItem(Long itemId);

    @Query(value = "SELECT * " +
            "FROM BOOKINGS AS qry1 " +
            "INNER JOIN (SELECT ITEM_ID,MIN(START_DATE) as MINSTARTDATE " +
            "FROM BOOKINGS " +
            "WHERE ITEM_ID = ?1 AND STATUS <>'REJECTED' AND CURRENT_TIMESTAMP <= START_DATE " +
            "GROUP BY ITEM_ID) AS qry2 ON qry1.ITEM_ID = qry2.ITEM_ID AND qry1.START_DATE= qry2.MINSTARTDATE",
            nativeQuery = true)
    Booking findNextBookingForItem(Long itemId);

}
