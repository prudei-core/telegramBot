package ru.home.mywizard_bot.model;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Document(collection = "userRecordData")
public class UserRecordData implements Serializable {
    @Id
    String id;
    String time;
    String service;
    String date;
    long chatId;

    @Override
    public String toString() {
        return String.format("Дата: %s%nВремя: %s%nУслуга: %s%n", getDate(), getTime(), getService());
    }
}
