package ru.home.telegram_bot.botapi.handlers.fillingprofile;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.home.telegram_bot.botapi.BotState;
import ru.home.telegram_bot.botapi.InputMessageHandler;
import ru.home.telegram_bot.cache.UserDataCache;
import ru.home.telegram_bot.model.UserProfileData;
import ru.home.telegram_bot.service.ReplyMessagesService;
import ru.home.telegram_bot.service.UserProfileDataService;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class FillingProfileHandler implements InputMessageHandler {
    private UserDataCache userDataCache;
    private ReplyMessagesService messagesService;
    private UserProfileDataService profileDataService;

    public FillingProfileHandler(UserDataCache userDataCache, ReplyMessagesService messagesService, UserProfileDataService profileDataService) {
        this.userDataCache = userDataCache;
        this.messagesService = messagesService;
        this.profileDataService = profileDataService;
    }

    @Override
    public SendMessage handle(Message message) {
        if (userDataCache.getUsersCurrentBotState(message.getFrom().getId()).equals(BotState.FILLING_PROFILE)){
            userDataCache.setUsersCurrentBotState(message.getFrom().getId(), BotState.ASK_AGE);
        }
        return processUsersInput(message);
    }

    @Override
    public BotState getHandlerName() {
        return BotState.FILLING_PROFILE;
    }

    private SendMessage processUsersInput(Message inputMsg) {
        String usersAnswer = inputMsg.getText();
        int userId = inputMsg.getFrom().getId();
        long chatId = inputMsg.getChatId();
        SendMessage replyToUser = null;

        UserProfileData profileData = userDataCache.getUserProfileData((userId));
        BotState botState = userDataCache.getUsersCurrentBotState(userId);

        boolean profileDataExist = isProfileDataExist(chatId);

            if (!profileDataExist) {

                if (botState.equals(BotState.ASK_NAME)) {
                    replyToUser = messagesService.getReplyMessage(chatId, "reply.askName");
                    userDataCache.setUsersCurrentBotState(userId, BotState.ASK_AGE);
                }

                if (botState.equals(BotState.ASK_AGE)) {
                    profileData.setName(usersAnswer);
                    replyToUser = messagesService.getReplyMessage(chatId, "reply.askAge");
                    userDataCache.setUsersCurrentBotState(userId, BotState.ASK_GENDER);
                }

                if (botState.equals(BotState.ASK_GENDER)) {
                    profileData.setAge(usersAnswer);
                    replyToUser = messagesService.getReplyMessage(chatId, "reply.askGender");
                    replyToUser.setReplyMarkup(getGenderButtonsMarkup());
                }
                if (botState.equals(BotState.PROFILE_FILLED)) {
                    profileData.setGender(usersAnswer);
                    profileData.setChatId(chatId);
                    profileDataService.saveUserProfileData(profileData);
                    userDataCache.setUsersCurrentBotState(userId, BotState.ASK_TIME);
                    replyToUser = messagesService.getReplyMessage(chatId, "reply.askTime");
                }
                userDataCache.saveUserProfileData(userId, profileData);
            } else {
                userDataCache.setUsersCurrentBotState(userId, BotState.FILLING_RECORD);
            }



        return  replyToUser;
    }

    private InlineKeyboardMarkup getGenderButtonsMarkup() {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        InlineKeyboardButton buttonGenderMan = new InlineKeyboardButton().setText("М");
        InlineKeyboardButton buttonGenderWoman = new InlineKeyboardButton().setText("Ж");

        //Every button must have callBackData, or else not work !
        buttonGenderMan.setCallbackData("buttonMan");
        buttonGenderWoman.setCallbackData("buttonWoman");

        List<InlineKeyboardButton> keyboardButtonsRow1 = new ArrayList<>();
        keyboardButtonsRow1.add(buttonGenderMan);
        keyboardButtonsRow1.add(buttonGenderWoman);

        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        rowList.add(keyboardButtonsRow1);

        inlineKeyboardMarkup.setKeyboard(rowList);

        return inlineKeyboardMarkup;
    }

    private boolean isProfileDataExist(long chatId) {
        UserProfileData userProfileData = profileDataService.getUserProfileData(chatId);
        if (userProfileData != null) {
            return true;
        } else {
            return false;
        }
    }
}
