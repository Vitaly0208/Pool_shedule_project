package ru.mirea.project.Repository.jdbc;

import ru.mirea.project.Domain.Enums.SubscriptionType;
import ru.mirea.project.Domain.Models.User;
import ru.mirea.project.Repository.Exeptions.DatabaseException;
import ru.mirea.project.Repository.api.UserRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcUserRepository implements UserRepository {

    private final DatabaseManager databaseManager;

    public JdbcUserRepository(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    @Override
    public Optional<User> findById(Long id) {

        String sql = "SELECT id, full_name, age, phone, email, subscription_type, password_hash " +
                "FROM users WHERE id = ?";

        try (
                Connection connection = databaseManager.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setLong(1, id);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(mapRowToUser(resultSet));
                }
            }
            return Optional.empty();

        } catch (SQLException e) {
            throw new DatabaseException("Ошибка при поиске пользователя с ID: " + id, e);
        }
    }

    @Override
    public User create(User user) {

        String sql = "INSERT INTO users (full_name, age, phone, email, subscription_type, password_hash) " +
                "VALUES (?, ?, ?, ?, ?, ?) " +
                "RETURNING id";

        try (
             Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setString(1, user.getName());
            statement.setInt(2, user.getAge());
            statement.setString(3, user.getPhone());
            statement.setString(4, user.getEmail());
            statement.setString(5, user.getSubscriptionType().name());
            statement.setString(6, user.getPasswordHash());

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    Long generatedId = resultSet.getLong("id");

                    User createdUser = new User(
                            generatedId,
                            user.getName(),
                            user.getAge(),
                            user.getPhone(),
                            user.getEmail(),
                            user.getSubscriptionType(),
                            user.getPasswordHash()
                    );
                    return createdUser;
                }
                throw new DatabaseException("Не удалось получить сгенерированный ID после вставки пользователя.");
            }

        } catch (SQLException e) {
            throw new DatabaseException("Ошибка при создание пользователя", e);
        }
    }

    @Override
    public List<User> findAll() {
        String sql = "SELECT id, full_name, age, phone, email, subscription_type, password_hash FROM users";

        List<User> users = new ArrayList<>();

        try (
            Connection connection = databaseManager.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery()
        ) {
            while (resultSet.next()) {
                users.add(mapRowToUser(resultSet));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Ошибка при получении списка пользователей", e);
        }

        return users;
    }

    @Override
    public void update(User user) {

        String sql = "UPDATE users SET full_name = ?, age = ?, phone = ?, email = ?, subscription_type = ?, password_hash = ? WHERE id = ? ";

        try (
            Connection connection = databaseManager.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)
        ) {

            statement.setString(1, user.getName());
            statement.setInt(2, user.getAge());
            statement.setString(3, user.getPhone());
            statement.setString(4, user.getEmail());
            statement.setString(5, user.getSubscriptionType().name());
            statement.setString(6, user.getPasswordHash());
            statement.setLong(7, user.getId());

            statement.executeUpdate();

        } catch (SQLException e) {
            throw new DatabaseException("Ошибка при обновлении пользователя", e);
        }
    }

    @Override
    public void delete(Long id) {

        String sql = "DELETE FROM users WHERE id = ?";

        try (
                Connection connection = databaseManager.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setLong(1, id);

            int deletedRows = statement.executeUpdate();

            if (deletedRows == 0) {
                System.out.println("Пользователь с ID " + id + " не найден для удаления.");
            }
        } catch (SQLException e) {
            throw new DatabaseException("Ошибка при удалении пользователя с ID: " + id, e);
        }
    }

    @Override
    public List<User> findBySubscriptionType(SubscriptionType type) {

        String sql = "SELECT id, full_name, age, phone, email, subscription_type, password_hash " +
                "FROM users WHERE subscription_type = ?";

        List<User> users = new ArrayList<>();

        try (
                Connection connection = databaseManager.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {

            statement.setString(1, type.name());

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()){
                    users.add(mapRowToUser(resultSet));
                }
            }

        } catch (SQLException e) {
            throw new DatabaseException("Ошибка при поиске пользователей", e);
        }

        return users;
    }

    private User mapRowToUser(ResultSet resultSet) throws SQLException {
        return new User(
                resultSet.getLong("id"),
                resultSet.getString("full_name"),
                resultSet.getInt("age"),
                resultSet.getString("phone"),
                resultSet.getString("email"),
                SubscriptionType.valueOf(resultSet.getString("subscription_type")),
                resultSet.getString("password_hash")
        );
    }

}
