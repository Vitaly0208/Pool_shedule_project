package ru.mirea.project.Domain.Models;

import java.time.LocalDateTime;

import ru.mirea.project.Domain.Enums.BookingStatus;
import ru.mirea.project.Domain.Enums.SessionType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Booking {
    private Long id;
    private Long userId;
    private SessionType sessionType;
    private LocalDateTime startTime;
    private Integer durationMinutes;
    private Integer laneNumber;
    private BookingStatus status;

    public Booking(Long id, Long userId, SessionType sessionType, LocalDateTime startTime, Integer durationMinutes, Integer laneNumber, BookingStatus status){
        this.id = id;
        this.userId = userId;
        this.sessionType = sessionType;
        this.durationMinutes = durationMinutes;
        this.startTime = startTime;
        this.laneNumber = laneNumber;
        this.status = status;
    }

    public Booking(Long userId, SessionType sessionType, LocalDateTime startTime, Integer durationMinutes, Integer laneNumber, BookingStatus status){
        this.userId = userId;
        this.sessionType = sessionType;
        this.durationMinutes = durationMinutes;
        this.startTime = startTime;
        this.laneNumber = laneNumber;
        this.status = status;
    }

    @Override
    public String toString() {
        return "Booking{" +
                "id=" + id +
                ", userId=" + userId +
                ", sessionType=" + sessionType +
                ", startTime=" + startTime +
                ", durationMinutes=" + durationMinutes +
                ", laneNumber=" + laneNumber +
                ", status=" + status +
                '}';
    }

}
