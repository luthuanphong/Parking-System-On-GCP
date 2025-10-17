package com.gcp.practise.parking.utils;

import java.time.LocalDate;
import java.time.LocalTime;

public final class DateUtils {
    public static LocalDate getTargetDate() {
        // Check if current time is after 8 PM (20:00)
        if (LocalTime.now().isAfter(LocalTime.of(20, 0))) {
            return LocalDate.now().plusDays(1); // Use tomorrow's date
        }
        return LocalDate.now(); // Use today's date
    }
}
