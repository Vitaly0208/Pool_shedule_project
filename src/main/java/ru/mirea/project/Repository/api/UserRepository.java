package ru.mirea.project.Repository.api;

import ru.mirea.project.Domain.Enums.SubscriptionType;
import ru.mirea.project.Domain.Models.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    User create(User user);
    Optional<User> findById(Long id);
    List<User> findAll();
    void update(User user);
    void delete(Long id);
    List<User> findBySubscriptionType(SubscriptionType type);
}
