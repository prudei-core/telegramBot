package ru.home.mywizard_bot.model;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

/**
 * Данные анкеты пользователя
 */

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserProfileData implements Serializable {
    String name;
    String gender;
    String time;
    String service;
    String age;
    String date;


    @Override
    public String toString() {
        return String.format("Имя: %s%nВозраст: %s%n", getName(), getAge(), getGender());
    }
}
