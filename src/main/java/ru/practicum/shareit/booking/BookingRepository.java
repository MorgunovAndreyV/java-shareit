package ru.practicum.shareit.booking;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    String findPastBookingsForBookerQuery = "select bk from Booking as bk " +
            "join bk.booker as bu " +
            "where bk.end < CURRENT_TIMESTAMP and bu.id = ?1";

    String findPastBookingsForOwnerQuery = "select bk from Booking as bk " +
            "join bk.item as it " +
            "where bk.end < CURRENT_TIMESTAMP and it.owner.id = ?1";

    String findCurrentBookingsForBookerQuery = "select bk from Booking as bk " +
            "join bk.booker as bu " +
            "where bk.end > CURRENT_TIMESTAMP and bk.start < CURRENT_TIMESTAMP and bu.id = ?1";

    String findCurrentBookingsForOwnerQuery = "select bk from Booking as bk " +
            "join bk.item as it " +
            "where bk.end > CURRENT_TIMESTAMP and bk.start < CURRENT_TIMESTAMP and it.owner.id = ?1";

    String findFutureBookingsForBookerQuery = "select bk from Booking as bk " +
            "join bk.booker as bu " +
            "where bk.start > CURRENT_TIMESTAMP and bu.id = ?1";

    String findWaitingBookingsForBookerQuery = "select bk from Booking as bk " +
            "join bk.booker as bu " +
            "where bk.status ='WAITING' and bu.id = ?1";

    String findFutureBookingsForOwnerQuery = "select bk from Booking as bk " +
            "join bk.item as it " +
            "where bk.start > CURRENT_TIMESTAMP and it.owner.id = ?1";

    String findWaitingBookingsForOwnerQuery = "select bk from Booking as bk " +
            "join bk.item as it " +
            "where bk.status ='WAITING' and it.owner.id = ?1";

    String findRejectedBookingsForBookerQuery = "select bk from Booking as bk " +
            "join bk.booker as bu " +
            "where bk.status ='REJECTED' and bu.id = ?1";

    String findRejectedBookingsForOwnerQuery = "select bk from Booking as bk " +
            "join bk.item as it " +
            "where bk.status ='REJECTED' and it.owner.id = ?1";

    List<Booking> findByBookerId(Long id);

    Page<Booking> findByBookerId(Long id, PageRequest pageRequest);

    List<Booking> findByItemOwnerId(Long id);

    Page<Booking> findByItemOwnerId(Long id, PageRequest pageRequest);

    @Query(findPastBookingsForBookerQuery)
    List<Booking> findPastBookingsForBooker(Long id);

    @Query(findPastBookingsForBookerQuery)
    Page<Booking> findPastBookingsForBooker(Long id, PageRequest pageRequest);

    @Query(findPastBookingsForOwnerQuery)
    List<Booking> findPastBookingsForOwner(Long id);

    @Query(findPastBookingsForOwnerQuery)
    Page<Booking> findPastBookingsForOwner(Long id, PageRequest pageRequest);

    @Query(findCurrentBookingsForBookerQuery)
    List<Booking> findCurrentBookingsForBooker(Long id);

    @Query(findCurrentBookingsForBookerQuery)
    Page<Booking> findCurrentBookingsForBooker(Long id, PageRequest pageRequest);

    @Query(findCurrentBookingsForOwnerQuery)
    List<Booking> findCurrentBookingsForOwner(Long id);

    @Query(findCurrentBookingsForOwnerQuery)
    Page<Booking> findCurrentBookingsForOwner(Long id, PageRequest pageRequest);

    @Query(findFutureBookingsForBookerQuery)
    List<Booking> findFutureBookingsForBooker(Long id);

    @Query(findFutureBookingsForOwnerQuery)
    List<Booking> findFutureBookingsForOwner(Long id);

    @Query(findWaitingBookingsForBookerQuery)
    List<Booking> findWaitingBookingsForBooker(Long id);

    @Query(findWaitingBookingsForOwnerQuery)
    List<Booking> findWaitingBookingsForOwner(Long id);

    @Query(findRejectedBookingsForBookerQuery)
    List<Booking> findRejectedBookingsForBooker(Long id);

    @Query(findRejectedBookingsForOwnerQuery)
    List<Booking> findRejectedBookingsForOwner(Long id);

    @Query(findFutureBookingsForBookerQuery)
    Page<Booking> findFutureBookingsForBooker(Long id, PageRequest pageRequest);

    @Query(findFutureBookingsForOwnerQuery)
    Page<Booking> findFutureBookingsForOwner(Long id, PageRequest pageRequest);

    @Query(findWaitingBookingsForBookerQuery)
    Page<Booking> findWaitingBookingsForBooker(Long id, PageRequest pageRequest);

    @Query(findWaitingBookingsForOwnerQuery)
    Page<Booking> findWaitingBookingsForOwner(Long id, PageRequest pageRequest);

    @Query(findRejectedBookingsForBookerQuery)
    Page<Booking> findRejectedBookingsForBooker(Long id, PageRequest pageRequest);

    @Query(findRejectedBookingsForOwnerQuery)
    Page<Booking> findRejectedBookingsForOwner(Long id, PageRequest pageRequest);

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

    @Query(value = "SELECT * " +
            "FROM BOOKINGS AS qry1 " +
            "INNER JOIN (SELECT ITEM_ID,MIN(START_DATE) as MINSTARTDATE " +
            "FROM BOOKINGS " +
            "WHERE ITEM_ID = ?1 AND STATUS <>'REJECTED' AND CURRENT_TIMESTAMP <= START_DATE " +
            "GROUP BY ITEM_ID) AS qry2 ON qry1.ITEM_ID = qry2.ITEM_ID AND qry1.START_DATE= qry2.MINSTARTDATE",
            nativeQuery = true)
    List<Booking> findNextBookingForItem2(Long itemId);
}
