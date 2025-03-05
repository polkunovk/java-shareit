package ru.practicum.shareit.booking.status;

public enum BookingState {
    ALL,        // Все бронирования
    CURRENT,    // Текущие бронирования
    PAST,       // Завершённые бронирования
    FUTURE,     // Будущие бронирования
    WAITING,    // Ожидают подтверждения
    REJECTED    // Отклонённые бронирования
}
