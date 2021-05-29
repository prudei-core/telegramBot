package ru.home.mywizard_bot.model;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserRecordData implements Serializable {
    String time;
    String service;
    String date;

    @Override
    public String toString() {
        return String.format("Дата: %s%nВремя: %s%nУслуга: %s%n", getDate(), getTime(), getService());
    }
}
