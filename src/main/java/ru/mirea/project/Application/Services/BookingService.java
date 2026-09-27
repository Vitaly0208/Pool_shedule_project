package ru.mirea.project.Application.Services;

import ru.mirea.project.Domain.Enums.BookingStatus;
import ru.mirea.project.Domain.Models.Booking;
import ru.mirea.project.Repository.Exeptions.DatabaseException;
import ru.mirea.project.Repository.Exeptions.EntityNotFoundException;
import ru.mirea.project.Repository.api.BookingRepository;
import ru.mirea.project.Repository.api.UserRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;

    public BookingService(BookingRepository bookingRepository, UserRepository userRepository) {
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
    }

    public Booking createBooking(Booking booking) {
        userRepository.findById(booking.getUserId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Пользователь с ID " + booking.getUserId() + " не найден. Невозможно создать бронирование."
                ));

        if (booking.getStartTime().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Нельзя создать бронирование в прошлом времени");
        }

        if (booking.getDurationMinutes() == null || booking.getDurationMinutes() < 30 || booking.getDurationMinutes() > 120) {
            throw new IllegalArgumentException("Длительность бронирования должна быть от 30 до 120 минут");
        }

        if (booking.getLaneNumber() == null || booking.getLaneNumber() <= 0) {
            throw new IllegalArgumentException("Номер дорожки должен быть положительным числом");
        }

        LocalDateTime endTime = booking.getStartTime().plusMinutes(booking.getDurationMinutes());
        Optional<Booking> conflict = bookingRepository.findByLaneAndTime(
                booking.getLaneNumber(),
                booking.getStartTime(),
                endTime
        );

        if (conflict.isPresent()) {
            throw new RuntimeException(
                    "Дорожка " + booking.getLaneNumber() + " уже занята в выбранное время. " +
                            "Конфликт с бронированием ID: " + conflict.get().getId()
            );
        }
        booking.setStatus(BookingStatus.SCHEDULED);

        try {
            return bookingRepository.create(booking);
        } catch (DatabaseException e) {
            throw new RuntimeException("Не удалось создать бронирование: " + e.getMessage(), e);
        }
    }

    public Booking getBookingById(Long id) {
        return bookingRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Бронирование с ID " + id + " не найдено"));
    }

    public List<Booking> getBookingsByUserId(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь с ID " + userId + " не найден"));

        return bookingRepository.findByUserId(userId);
    }

    public List<Booking> getBookingsByDateRange(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("Даты начала и конца диапазона не могут быть null");
        }
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Дата начала не может быть позже даты конца");
        }

        return bookingRepository.findByDateRange(startDate, endDate);
    }

    public Booking cancelBooking(Long id) {
        Booking booking = getBookingById(id);

        if (booking.getStatus() == BookingStatus.CANCELLED) {
            throw new IllegalStateException("Бронирование с ID " + id + " уже отменено");
        }
        if (booking.getStatus() == BookingStatus.COMPLETED) {
            throw new IllegalStateException("Нельзя отменить завершённое бронирование");
        }
        booking.setStatus(BookingStatus.CANCELLED);

        try {
            bookingRepository.update(booking);
            return booking;
        } catch (DatabaseException e) {
            throw new RuntimeException("Не удалось отменить бронирование: " + e.getMessage(), e);
        }
    }

    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }

    public List<Booking> getAllBookingsSortedBy(Comparator<Booking> comparator) {
        return bookingRepository.findAll().stream()
                .sorted(comparator)
                .collect(Collectors.toList());
    }

    public List<Booking> getBookingsSortedByStartTime() {
        return getAllBookingsSortedBy(Comparator.comparing(Booking::getStartTime));
    }

    public List<Booking> getBookingsSortedByLaneNumber() {
        return getAllBookingsSortedBy(Comparator.comparing(Booking::getLaneNumber));
    }

    public List<Booking> getBookingsSortedByStatus() {
        return getAllBookingsSortedBy(Comparator.comparing(Booking::getStatus));
    }
}