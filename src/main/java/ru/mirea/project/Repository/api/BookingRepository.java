package ru.mirea.project.Repository.api;

import ru.mirea.project.Domain.Enums.BookingStatus;
import ru.mirea.project.Domain.Enums.SessionType;
import ru.mirea.project.Domain.Models.Booking;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository {

    Booking create(Booking booking);
    Optional<Booking> findById(Long id);
    List<Booking> findAll();
    void update(Booking booking);
    void delete(Long id);

//    List<Booking> findByUserId(Long userId);
//    List<Booking> findByDateRange(LocalDate startDate, LocalDate endDate);
//    Optional<Booking> findByLaneAndTime(Integer laneNumber, LocalDateTime startTime);
//    List<Booking> findByStatus(BookingStatus status);
}