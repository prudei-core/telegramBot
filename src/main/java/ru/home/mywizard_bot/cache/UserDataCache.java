package ru.home.mywizard_bot.cache;

import org.springframework.stereotype.Component;
import ru.home.mywizard_bot.botapi.BotState;
import ru.home.mywizard_bot.model.UserProfileData;
import ru.home.mywizard_bot.model.UserRecordData;

import java.util.HashMap;
import java.util.Map;

/**
 * In-memory cache.
 * usersBotStates: user_id and user's bot state
 * usersProfileData: user_id  and user's profile data.
 */

@Component
public class UserDataCache implements DataCache {
    private Map<Integer, BotState> usersBotStates = new HashMap<>();
    private Map<Integer, UserProfileData> usersProfileData = new HashMap<>();
    private Map<Integer, UserRecordData> usersRecordData = new HashMap<>();

    @Override
    public void setUsersCurrentBotState(int userId, BotState botState) {
        usersBotStates.put(userId, botState);
    }

    @Override
    public BotState getUsersCurrentBotState(int userId) {
        BotState botState = usersBotStates.get(userId);
        if (botState == null) {
            botState = BotState.ASK_DESTINY;
        }

        return botState;
    }

    @Override
    public UserProfileData getUserProfileData(int userId) {
        UserProfileData userProfileData = usersProfileData.get(userId);
        if (userProfileData == null) {
            userProfileData = new UserProfileData();
        }
        return userProfileData;
    }

    @Override
    public UserRecordData getUserRecordData(int userId) {
        UserRecordData userRecordData = usersRecordData.get(userId);
        if (userRecordData == null) {
            userRecordData = new UserRecordData();
        }
        return userRecordData;
    }

    @Override
    public void saveUserProfileData(int userId, UserProfileData userProfileData) {
        usersProfileData.put(userId, userProfileData);
    }

    @Override
    public void saveUserRecordData(int userId, UserRecordData userRecordData) {
        usersRecordData.put(userId, userRecordData);
    }
}
