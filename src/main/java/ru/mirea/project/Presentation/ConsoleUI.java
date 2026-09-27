package ru.mirea.project.Presentation;

import ru.mirea.project.Application.Services.BookingService;
import ru.mirea.project.Application.Services.UserService;
import ru.mirea.project.Domain.Enums.SessionType;
import ru.mirea.project.Domain.Enums.SubscriptionType;
import ru.mirea.project.Domain.Models.Booking;
import ru.mirea.project.Domain.Models.User;
import ru.mirea.project.Repository.Exeptions.EntityNotFoundException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

public class ConsoleUI {

    private final UserService userService;
    private final BookingService bookingService;
    private final Scanner scanner;

    public ConsoleUI(UserService userService, BookingService bookingService) {
        this.userService = userService;
        this.bookingService = bookingService;
        this.scanner = new Scanner(System.in);
    }

    private int getValidIntInput(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine();
            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Ошибка: введено не число. Попробуйте снова.");
            }
        }
    }

    private long getValidLongInput(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine();
            try {
                return Long.parseLong(input);
            } catch (NumberFormatException e) {
                System.out.println("Ошибка: введено не число. Попробуйте снова.");
            }
        }
    }

    private LocalDate getValidLocalDateInput(String prompt, String format) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine();
            try {
                return LocalDate.parse(input, formatter);
            } catch (DateTimeParseException e) {
                System.out.println("Ошибка: неверный формат даты. Используйте формат: " + format);
            }
        }
    }

    private LocalDateTime getValidLocalDateTimeInput(String prompt, String format) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine();
            try {
                return LocalDateTime.parse(input, formatter);
            } catch (DateTimeParseException e) {
                System.out.println("Ошибка: неверный формат даты и времени. Используйте формат: " + format);
            }
        }
    }

    private SubscriptionType getValidSubscriptionTypeInput(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().toUpperCase();
            try {
                return SubscriptionType.valueOf(input);
            } catch (IllegalArgumentException e) {
                System.out.println("Ошибка: неверный тип подписки. Допустимые значения: SINGLE, MONTHLY, YEARLY");
            }
        }
    }

    private SessionType getValidSessionTypeInput(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().toUpperCase();
            try {
                return SessionType.valueOf(input);
            } catch (IllegalArgumentException e) {
                System.out.println("Ошибка: неверный тип сессии. Допустимые значения: FREE_SWIMMING, CHILDREN_GROUP, AEROBICS, THERAPEUTIC");
            }
        }
    }

    public void start() {
        while (true) {
            System.out.println("\n=== Система управления бассейном ===");
            System.out.println("1. Управление пользователями");
            System.out.println("2. Управление бронированиями");
            System.out.println("3. Просмотр расписания");
            System.out.println("0. Выход");
            System.out.print("Выберите действие: ");

            String choice = scanner.nextLine();

            try {
                switch (choice) {
                    case "1" -> showUserMenu();
                    case "2" -> showBookingMenu();
                    case "3" -> showScheduleMenu();
                    case "0" -> {
                        System.out.println("До свидания!");
                        return;
                    }
                    default -> System.out.println("Неверный выбор, попробуйте снова");
                }
            } catch (Exception e) {
                System.out.println("Ошибка: " + e.getMessage());
            }
        }
    }

    private void showUserMenu() {
        System.out.println("\n--- Управление пользователями ---");
        System.out.println("1. Создать пользователя");
        System.out.println("2. Показать всех пользователей");
        System.out.println("3. Найти пользователя по ID");
        System.out.println("4. Обновить пользователя");
        System.out.println("5. Удалить пользователя");
        System.out.println("6. Показать пользователей, отсортированных по имени");
        System.out.println("7. Показать пользователей, отсортированных по возрасту");
        System.out.println("0. Назад");
        System.out.print("Выберите действие: ");

        String choice = scanner.nextLine();

        switch (choice) {
            case "1" -> createUser();
            case "2" -> showAllUsers();
            case "3" -> findUserById();
            case "4" -> updateUser();
            case "5" -> deleteUser();
            case "6" -> showUsersSortedByName();
            case "7" -> showUsersSortedByAge();
            case "0" -> { }
            default -> System.out.println("Неверный выбор");
        }
    }

    private void createUser() {
        System.out.println("\n--- Создание нового пользователя ---");

        System.out.print("Введите имя: ");
        String name = scanner.nextLine();

        int age = getValidIntInput("Введите возраст: ");

        System.out.print("Введите телефон: ");
        String phone = scanner.nextLine();

        System.out.print("Введите email: ");
        String email = scanner.nextLine();

        SubscriptionType subscriptionType = getValidSubscriptionTypeInput("Введите тип подписки (SINGLE, MONTHLY, YEARLY): ");

        System.out.print("Введите пароль: ");
        String password = scanner.nextLine();

        String passwordHash = password;

        User user = new User(null, name, age, phone, email, subscriptionType, passwordHash);

        try {
            User createdUser = userService.createUser(user);
            System.out.println("Пользователь успешно создан с ID: " + createdUser.getId());
        } catch (IllegalArgumentException e) {
            System.out.println("Ошибка валидации: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Ошибка при создании: " + e.getMessage());
        }
    }

    private void showAllUsers() {
        System.out.println("\n--- Все пользователи ---");
        List<User> users = userService.getAllUsers();

        if (users.isEmpty()) {
            System.out.println("Пользователи не найдены");
            return;
        }

        for (User user : users) {
            System.out.println("ID: " + user.getId() +
                    ", Имя: " + user.getName() +
                    ", Email: " + user.getEmail() +
                    ", Подписка: " + user.getSubscriptionType());
        }
    }

    private void findUserById() {
        long id = getValidLongInput("Введите ID пользователя: ");

        try {
            User user = userService.getUserById(id);
            System.out.println("Найден пользователь: " + user.getName() + " (" + user.getEmail() + ")");
        } catch (EntityNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }

    private void updateUser() {
        long id = getValidLongInput("Введите ID пользователя для обновления: ");

        try {
            User existingUser = userService.getUserById(id);

            System.out.print("Новое имя (или Enter для пропуска): ");
            String name = scanner.nextLine();
            if (!name.isEmpty()) existingUser.setName(name);

            System.out.print("Новый возраст (или Enter для пропуска): ");
            String ageStr = scanner.nextLine();
            if (!ageStr.isEmpty()) {
                try {
                    existingUser.setAge(Integer.parseInt(ageStr));
                } catch (NumberFormatException e) {
                    System.out.println("Ошибка: введено не число. Возраст не изменён.");
                }
            }

            System.out.print("Новый email (или Enter для пропуска): ");
            String email = scanner.nextLine();
            if (!email.isEmpty()) existingUser.setEmail(email);

            userService.updateUser(existingUser);
            System.out.println("Пользователь успешно обновлён");
        } catch (EntityNotFoundException e) {
            System.out.println(e.getMessage());
        } catch (Exception e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }

    private void deleteUser() {
        long id = getValidLongInput("Введите ID пользователя для удаления: ");

        try {
            userService.deleteUser(id);
            System.out.println("Пользователь успешно удалён");
        } catch (EntityNotFoundException e) {
            System.out.println(e.getMessage());
        } catch (Exception e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }

    private void showUsersSortedByName() {
        System.out.println("\n--- Пользователи, отсортированные по имени ---");
        List<User> users = userService.getUsersSortedByName();

        if (users.isEmpty()) {
            System.out.println("Пользователи не найдены");
            return;
        }

        for (User user : users) {
            System.out.println("ID: " + user.getId() +
                    ", Имя: " + user.getName() +
                    ", Возраст: " + user.getAge() +
                    ", Email: " + user.getEmail());
        }
    }

    private void showUsersSortedByAge() {
        System.out.println("\n--- Пользователи, отсортированные по возрасту (от старшего к младшему) ---");
        List<User> users = userService.getUsersSortedByAge();

        if (users.isEmpty()) {
            System.out.println("Пользователи не найдены");
            return;
        }

        for (User user : users) {
            System.out.println("ID: " + user.getId() +
                    ", Имя: " + user.getName() +
                    ", Возраст: " + user.getAge() +
                    ", Email: " + user.getEmail());
        }
    }

    private void showBookingMenu() {
        System.out.println("\n--- Управление бронированиями ---");
        System.out.println("1. Создать бронирование");
        System.out.println("2. Показать все бронирования");
        System.out.println("3. Найти бронирование по ID");
        System.out.println("4. Показать бронирования пользователя");
        System.out.println("5. Отменить бронирование");
        System.out.println("6. Показать бронирования, отсортированные по времени");
        System.out.println("7. Показать бронирования, отсортированные по дорожке");
        System.out.println("8. Показать бронирования, отсортированные по статусу");
        System.out.println("0. Назад");
        System.out.print("Выберите действие: ");

        String choice = scanner.nextLine();

        switch (choice) {
            case "1" -> createBooking();
            case "2" -> showAllBookings();
            case "3" -> findBookingById();
            case "4" -> showBookingsByUser();
            case "5" -> cancelBooking();
            case "6" -> showBookingsSortedByStartTime();
            case "7" -> showBookingsSortedByLaneNumber();
            case "8" -> showBookingsSortedByStatus();
            case "0" -> { }
            default -> System.out.println("Неверный выбор");
        }
    }

    private void createBooking() {
        System.out.println("\n--- Создание бронирования ---");

        long userId = getValidLongInput("Введите ID пользователя: ");

        int laneNumber = getValidIntInput("Введите номер дорожки: ");

        LocalDateTime startTime = getValidLocalDateTimeInput("Введите дату и время начала (формат: yyyy-MM-dd HH:mm): ", "yyyy-MM-dd HH:mm");

        int durationMinutes = getValidIntInput("Введите длительность в минутах (30-120): ");

        SessionType sessionType = getValidSessionTypeInput("Введите тип сессии (FREE_SWIMMING, CHILDREN_GROUP, AEROBICS, THERAPEUTIC): ");

        Booking booking = new Booking(null, userId, sessionType, startTime, durationMinutes, laneNumber, null);

        try {
            Booking createdBooking = bookingService.createBooking(booking);
            System.out.println("Бронирование успешно создано с ID: " + createdBooking.getId());
        } catch (IllegalArgumentException e) {
            System.out.println("Ошибка валидации: " + e.getMessage());
        } catch (EntityNotFoundException e) {
            System.out.println(e.getMessage());
        } catch (RuntimeException e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }

    private void showAllBookings() {
        System.out.println("\n--- Все бронирования ---");
        List<Booking> bookings = bookingService.getAllBookings();

        if (bookings.isEmpty()) {
            System.out.println("Бронирования не найдены");
            return;
        }

        for (Booking booking : bookings) {
            System.out.println("ID: " + booking.getId() +
                    ", Пользователь: " + booking.getUserId() +
                    ", Дорожка: " + booking.getLaneNumber() +
                    ", Время: " + booking.getStartTime() +
                    ", Длительность: " + booking.getDurationMinutes() + " мин" +
                    ", Статус: " + booking.getStatus());
        }
    }

    private void findBookingById() {
        long id = getValidLongInput("Введите ID бронирования: ");

        try {
            Booking booking = bookingService.getBookingById(id);
            System.out.println("Найдено бронирование: Дорожка " + booking.getLaneNumber() +
                    " на " + booking.getStartTime());
        } catch (EntityNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }

    private void showBookingsByUser() {
        long userId = getValidLongInput("Введите ID пользователя: ");

        try {
            List<Booking> bookings = bookingService.getBookingsByUserId(userId);

            if (bookings.isEmpty()) {
                System.out.println("У пользователя нет бронирований");
                return;
            }

            for (Booking booking : bookings) {
                System.out.println("ID: " + booking.getId() +
                        ", Дорожка: " + booking.getLaneNumber() +
                        ", Время: " + booking.getStartTime() +
                        ", Статус: " + booking.getStatus());
            }
        } catch (EntityNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }

    private void cancelBooking() {
        long id = getValidLongInput("Введите ID бронирования для отмены: ");

        try {
            Booking cancelledBooking = bookingService.cancelBooking(id);
            System.out.println("Бронирование отменено. Новый статус: " + cancelledBooking.getStatus());
        } catch (EntityNotFoundException e) {
            System.out.println(e.getMessage());
        } catch (IllegalStateException e) {
            System.out.println(e.getMessage());
        } catch (Exception e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }

    private void showBookingsSortedByStartTime() {
        System.out.println("\n--- Бронирования, отсортированные по времени начала ---");
        List<Booking> bookings = bookingService.getBookingsSortedByStartTime();

        if (bookings.isEmpty()) {
            System.out.println("Бронирования не найдены");
            return;
        }

        for (Booking booking : bookings) {
            System.out.println("ID: " + booking.getId() +
                    ", Время: " + booking.getStartTime() +
                    ", Дорожка: " + booking.getLaneNumber() +
                    ", Пользователь: " + booking.getUserId() +
                    ", Статус: " + booking.getStatus());
        }
    }

    private void showBookingsSortedByLaneNumber() {
        System.out.println("\n--- Бронирования, отсортированные по номеру дорожки ---");
        List<Booking> bookings = bookingService.getBookingsSortedByLaneNumber();

        if (bookings.isEmpty()) {
            System.out.println("Бронирования не найдены");
            return;
        }

        for (Booking booking : bookings) {
            System.out.println("ID: " + booking.getId() +
                    ", Дорожка: " + booking.getLaneNumber() +
                    ", Время: " + booking.getStartTime() +
                    ", Пользователь: " + booking.getUserId() +
                    ", Статус: " + booking.getStatus());
        }
    }

    private void showBookingsSortedByStatus() {
        System.out.println("\n--- Бронирования, отсортированные по статусу ---");
        List<Booking> bookings = bookingService.getBookingsSortedByStatus();

        if (bookings.isEmpty()) {
            System.out.println("Бронирования не найдены");
            return;
        }

        for (Booking booking : bookings) {
            System.out.println("ID: " + booking.getId() +
                    ", Статус: " + booking.getStatus() +
                    ", Время: " + booking.getStartTime() +
                    ", Дорожка: " + booking.getLaneNumber() +
                    ", Пользователь: " + booking.getUserId());
        }
    }

    private void showScheduleMenu() {
        System.out.println("\n--- Просмотр расписания ---");
        System.out.println("1. Показать расписание на дату");
        System.out.println("2. Показать расписание за период");
        System.out.println("0. Назад");
        System.out.print("Выберите действие: ");

        String choice = scanner.nextLine();

        switch (choice) {
            case "1" -> showScheduleByDate();
            case "2" -> showScheduleByRange();
            case "0" -> { }
            default -> System.out.println("Неверный выбор");
        }
    }

    private void showScheduleByDate() {
        LocalDate date = getValidLocalDateInput("Введите дату (формат: yyyy-MM-dd): ", "yyyy-MM-dd");

        List<Booking> bookings = bookingService.getBookingsByDateRange(date, date);

        if (bookings.isEmpty()) {
            System.out.println("На эту дату нет бронирований");
            return;
        }

        System.out.println("\nРасписание на " + date + ":");
        for (Booking booking : bookings) {
            System.out.println("Дорожка " + booking.getLaneNumber() +
                    ": " + booking.getStartTime() +
                    " - " + booking.getStartTime().plusMinutes(booking.getDurationMinutes()) +
                    " (Пользователь ID: " + booking.getUserId() + ")");
        }
    }

    private void showScheduleByRange() {
        LocalDate startDate = getValidLocalDateInput("Введите дату начала (формат: yyyy-MM-dd): ", "yyyy-MM-dd");

        LocalDate endDate = getValidLocalDateInput("Введите дату конца (формат: yyyy-MM-dd): ", "yyyy-MM-dd");

        List<Booking> bookings = bookingService.getBookingsByDateRange(startDate, endDate);

        if (bookings.isEmpty()) {
            System.out.println("В этот период нет бронирований");
            return;
        }

        System.out.println("\nРасписание с " + startDate + " по " + endDate + ":");
        for (Booking booking : bookings) {
            System.out.println(booking.getStartTime() +
                    " | Дорожка " + booking.getLaneNumber() +
                    " | Пользователь " + booking.getUserId() +
                    " | " + booking.getStatus());
        }
    }
}