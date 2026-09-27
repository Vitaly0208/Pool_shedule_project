package ru.mirea.project.Application.Services;

import ru.mirea.project.Domain.Models.User;
import ru.mirea.project.Repository.Exeptions.DatabaseException;
import ru.mirea.project.Repository.Exeptions.EntityNotFoundException;
import ru.mirea.project.Repository.api.UserRepository;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User createUser(User user) {
        if (user.getName() == null || user.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Имя пользователя не может быть пустым");
        }

        if (user.getEmail() == null || !user.getEmail().contains("@")) {
            throw new IllegalArgumentException("Некорректный email адрес");
        }

        if (user.getAge() <= 0) {
            throw new IllegalArgumentException("Возраст должен быть больше нуля");
        }

        try {
            return userRepository.create(user);
        } catch (DatabaseException e) {
            throw new RuntimeException("Не удалось создать пользователя: " + e.getMessage(), e);
        }
    }

    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь с ID " + id + " не найден"));
    }

    public List<User> getAllUsers(){
        return userRepository.findAll();
    }

    public User updateUser(User user) {
        getUserById(user.getId());

        if (user.getName() == null || user.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Имя пользователя не может быть пустым");
        }

        if (user.getEmail() == null || !user.getEmail().contains("@")) {
            throw new IllegalArgumentException("Некорректный email адрес");
        }

        if (user.getAge() <= 0) {
            throw new IllegalArgumentException("Возраст должен быть больше нуля");
        }

        try {
            userRepository.update(user);
            return user;
        } catch (DatabaseException e) {
            throw new RuntimeException("Не удалось обновить пользователя: " + e.getMessage(), e);
        }
    }

    public void deleteUser(Long id) {
        getUserById(id);

        try {
            userRepository.delete(id);
        } catch (DatabaseException e) {
            throw new RuntimeException("Не удалось удалить пользователя: " + e.getMessage(), e);
        }
    }

    public List<User> getAllUsersSortedBy(Comparator<User> comparator) {
        return userRepository.findAll().stream()
                .sorted(comparator)
                .collect(Collectors.toList());
    }

    public List<User> getUsersSortedByName() {
        return getAllUsersSortedBy(Comparator.comparing(User::getName));
    }

    public List<User> getUsersSortedByAge() {
        return getAllUsersSortedBy(Comparator.comparing(User::getAge).reversed());
    }

    public List<User> getUsersSortedBySubscriptionType() {
        return getAllUsersSortedBy(Comparator.comparing(User::getSubscriptionType));
    }


}
