package ru.home.telegram_bot.model;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

/**
 * Данные анкеты пользователя
 */

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Document(collection = "userProfileData")
public class UserProfileData implements Serializable {
    @Id
    String id;
    String name;
    String gender;
    String age;
    long chatId;


    @Override
    public String toString() {
        return String.format("Имя: %s%nВозраст: %s%nПол: %s%n", getName(), getAge(), getGender());
    }
}
