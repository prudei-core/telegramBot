package ru.home.telegram_bot.cache;

import ru.home.telegram_bot.botapi.BotState;
import ru.home.telegram_bot.model.UserProfileData;
import ru.home.telegram_bot.model.UserRecordData;


public interface DataCache {
    void setUsersCurrentBotState(int userId, BotState botState);

    BotState getUsersCurrentBotState(int userId);

    UserProfileData getUserProfileData(int userId);

    UserRecordData getUserRecordData(int userId);

    void saveUserProfileData(int userId, UserProfileData userProfileData);

    void saveUserRecordData(int userId, UserRecordData userRecordData);
}
