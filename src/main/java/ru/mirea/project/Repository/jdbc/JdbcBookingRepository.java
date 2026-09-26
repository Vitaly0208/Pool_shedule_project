package ru.mirea.project.Repository.jdbc;

import ru.mirea.project.Domain.Enums.BookingStatus;
import ru.mirea.project.Domain.Enums.SessionType;
import ru.mirea.project.Domain.Enums.SubscriptionType;
import ru.mirea.project.Domain.Models.Booking;
import ru.mirea.project.Repository.DatabaseException;
import ru.mirea.project.Repository.api.BookingRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcBookingRepository implements BookingRepository {

    private final DatabaseManager databaseManager;

    public JdbcBookingRepository(DatabaseManager databaseManager){
        this.databaseManager = databaseManager;
    }

    @Override
    public Booking create(Booking booking){

        String sql = "INSERT INTO bookings (user_id, session_type, start_time, duration_minutes, lane_number, booking_status) " +
                "VALUES (?, ?, ?, ?, ?, ?) " +
                "RETURNING id";

        try (
            Connection connection = databaseManager.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)

        ) {
            statement.setLong(1, booking.getUserId());
            statement.setString(2, booking.getSessionType().name());
            statement.setObject(3, booking.getStartTime());
            statement.setObject(4, booking.getDurationMinutes());
            statement.setObject(5, booking.getLaneNumber());
            statement.setString(6, booking.getStatus().name());

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()){
                    Long generatedId = resultSet.getLong("id");

                    return new Booking(
                            generatedId,
                            booking.getUserId(),
                            booking.getSessionType(),
                            booking.getStartTime(),
                            booking.getDurationMinutes(),
                            booking.getLaneNumber(),
                            booking.getStatus()
                    );
                }
                throw new DatabaseException("Не удалось получить сгенерированный ID после вставки бронирования.");
            }

        } catch (SQLException e) {
            throw new DatabaseException("Ошибка при создании букинга" , e);
        }
    }

    @Override
    public List<Booking> findAll(){
        String sql = "SELECT id, user_id, session_type, start_time, duration_minutes, lane_number, booking_status FROM bookings";
        List<Booking> bookings = new ArrayList<>();
        try (
                Connection connection = databaseManager.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet resultSet = statement.executeQuery();
                ) {
            while (resultSet.next()) {
                bookings.add(mapRowToBooking(resultSet));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Ошибка при получении списка бронирований" , e);
        }
        return bookings;
    }

    @Override
    public Optional<Booking> findById(Long id){

        String sql = "SELECT id, user_id, session_type, start_time, duration_minutes, lane_number, booking_status FROM bookings WHERE id = ?";

        try (
                Connection connection = databaseManager.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
                ) {
            statement.setLong(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(mapRowToBooking(resultSet));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Ошибка при поиске бронирования с ID: " + id, e);
        }
        return Optional.empty();
    }

    @Override
    public void update(Booking booking){
        String sql = "UPDATE bookings " +
                "SET user_id = ?, session_type = ?, start_time = ?, duration_minutes = ?, lane_number = ?, booking_status = ? \n" +
                "WHERE id = ?";

        try (
                Connection connection = databaseManager.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
            ) {

            statement.setLong(1, booking.getUserId());
            statement.setString(2, booking.getSessionType().name());
            statement.setObject(3, booking.getStartTime());
            statement.setObject(4, booking.getDurationMinutes());
            statement.setObject(5, booking.getLaneNumber());
            statement.setString(6, booking.getStatus().name());
            statement.setLong(7, booking.getId());

            int updatedRows = statement.executeUpdate();

            if (updatedRows == 0) {
                System.out.println("Бронирование с ID " + booking.getId() + " не найдено для обновления.");
            }

        } catch (SQLException e) {
            throw new DatabaseException("Ошибка при обновлении бронирования", e);
        }
    }

    @Override
    public void delete(Long id) {
        String sql = "DELETE FROM bookings WHERE id = ?";

        try (
                Connection connection = databaseManager.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setLong(1, id);

            int deletedRows = statement.executeUpdate();

            if (deletedRows == 0) {
                System.out.println("Бронирование с ID " + id + " не найдено для удаления.");
            }

        } catch (SQLException e) {
            throw new DatabaseException("Ошибка при удалении бронирования с ID: " + id, e);
        }
    }


    private Booking mapRowToBooking(ResultSet resultSet) throws SQLException {
        return new Booking(
                resultSet.getLong("id"),
                resultSet.getLong("user_id"),
                SessionType.valueOf(resultSet.getString("session_type")),
                resultSet.getObject("start_time", LocalDateTime.class),
                resultSet.getObject("duration_minutes", Integer.class),
                resultSet.getObject("lane_number", Integer.class),
                BookingStatus.valueOf(resultSet.getString("booking_status"))
        );
    }


}
