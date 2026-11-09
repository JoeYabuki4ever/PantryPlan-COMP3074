package com.pantryplan.app;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

final class PantryReview {
    enum Group { PASSED, UPCOMING, LATER, UNDATED }
    static Group group(String date, LocalDate today, int window) {
        if (date == null || date.isEmpty()) return Group.UNDATED;
        try {
            LocalDate value = LocalDate.parse(date);
            if (value.isBefore(today)) return Group.PASSED;
            return value.isAfter(today.plusDays(window)) ? Group.LATER : Group.UPCOMING;
        } catch (DateTimeParseException e) { return Group.UNDATED; }
    }
}
