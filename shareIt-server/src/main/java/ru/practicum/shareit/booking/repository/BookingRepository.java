package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.status.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByBookerIdOrderByStartDesc(Long bookerId);

    List<Booking> findByItem_Owner_IdOrderByStartDesc(Long ownerId);

    List<Booking> findByBookerIdAndStatusOrderByStartDesc(Long bookerId, BookingStatus status);

    List<Booking> findByItem_Owner_IdAndStatusOrderByStartDesc(Long ownerId, BookingStatus status);

    List<Booking> findByBookerIdAndEndBeforeOrderByStartDesc(Long bookerId, LocalDateTime now);

    List<Booking> findByBookerIdAndStartAfterOrderByStartDesc(Long bookerId, LocalDateTime now);

    @Query("SELECT b FROM Booking b WHERE b.booker.id = :bookerId AND b.start <= :now AND b.end >= :now ORDER BY b.start DESC")
    List<Booking> findCurrentBookingsForUser(@Param("bookerId") Long bookerId, @Param("now") LocalDateTime now);

    @Query("SELECT b FROM Booking b WHERE b.item.owner.id = :ownerId AND b.start <= :now AND b.end >= :now ORDER BY b.start DESC")
    List<Booking> findCurrentBookingsForOwner(@Param("ownerId") Long ownerId, @Param("now") LocalDateTime now);

    List<Booking> findByItem_Owner_IdAndEndBeforeOrderByStartDesc(Long ownerId, LocalDateTime now);

    List<Booking> findByItem_Owner_IdAndStartAfterOrderByStartDesc(Long ownerId, LocalDateTime now);

    @Query("SELECT COUNT(b) > 0 FROM Booking b " +
            "WHERE b.booker.id = :bookerId " +
            "AND b.item.id = :itemId " +
            "AND b.end < :now " +
            "AND b.status = 'APPROVED'")
    boolean existsByBookerIdAndItemIdAndEndBefore(@Param("bookerId") Long bookerId,
                                                  @Param("itemId") Long itemId,
                                                  @Param("now") LocalDateTime now);

    Optional<Booking> findTopByItem_IdAndStartBeforeAndStatusOrderByEndDesc(Long itemId, LocalDateTime now, BookingStatus status);

    Optional<Booking> findTopByItem_IdAndStartAfterAndStatusOrderByStartAsc(Long itemId, LocalDateTime now, BookingStatus status);
}