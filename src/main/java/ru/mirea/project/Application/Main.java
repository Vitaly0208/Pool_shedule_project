package ru.mirea.project.Application;

import ru.mirea.project.Application.Services.BookingService;
import ru.mirea.project.Application.Services.UserService;
import ru.mirea.project.Presentation.ConsoleUI;
import ru.mirea.project.Repository.jdbc.DatabaseManager;
import ru.mirea.project.Repository.jdbc.JdbcBookingRepository;
import ru.mirea.project.Repository.jdbc.JdbcUserRepository;

public class Main {
    public static void main(String[] args) {
        DatabaseManager databaseManager = new DatabaseManager();

        JdbcUserRepository userRepository = new JdbcUserRepository(databaseManager);
        JdbcBookingRepository bookingRepository = new JdbcBookingRepository(databaseManager);

        UserService userService = new UserService(userRepository);
        BookingService bookingService = new BookingService(bookingRepository, userRepository);

        ConsoleUI consoleUI = new ConsoleUI(userService, bookingService);
        consoleUI.start();
    }
}