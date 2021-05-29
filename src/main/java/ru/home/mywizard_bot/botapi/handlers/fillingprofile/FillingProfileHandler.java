package ru.home.mywizard_bot.botapi.handlers.fillingprofile;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.home.mywizard_bot.botapi.BotState;
import ru.home.mywizard_bot.botapi.InputMessageHandler;
import ru.home.mywizard_bot.cache.UserDataCache;
import ru.home.mywizard_bot.model.UserProfileData;
import ru.home.mywizard_bot.model.UserRecordData;
import ru.home.mywizard_bot.service.PredictionService;
import ru.home.mywizard_bot.service.ReplyMessagesService;
import ru.home.mywizard_bot.utils.Emojis;

import java.util.ArrayList;
import java.util.List;


/**
 * Формирует анкету пользователя.
 */

@Slf4j
@Component
public class FillingProfileHandler implements InputMessageHandler {
    private UserDataCache userDataCache;
    private ReplyMessagesService messagesService;
    private PredictionService predictionService;

    public FillingProfileHandler(UserDataCache userDataCache, ReplyMessagesService messagesService,
                                 PredictionService predictionService) {
        this.userDataCache = userDataCache;
        this.messagesService = messagesService;
        this.predictionService = predictionService;
    }

    @Override
    public SendMessage handle(Message message) {
        if (userDataCache.getUsersCurrentBotState(message.getFrom().getId()).equals(BotState.FILLING_PROFILE)) {
            userDataCache.setUsersCurrentBotState(message.getFrom().getId(), BotState.ASK_NAME);
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

        UserProfileData profileData = userDataCache.getUserProfileData(userId);
        UserRecordData recordData = userDataCache.getUserRecordData(userId);
        BotState botState = userDataCache.getUsersCurrentBotState(userId);

        SendMessage replyToUser = null;

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

        if (botState.equals(BotState.ASK_DATE)) {
            replyToUser = messagesService.getReplyMessage(chatId, "reply.askDate");
            profileData.setGender(usersAnswer);
            userDataCache.setUsersCurrentBotState(userId, BotState.ASK_TIME);
        }

        if (botState.equals(BotState.ASK_TIME)) {
            replyToUser = messagesService.getReplyMessage(chatId, "reply.askTime");
            recordData.setDate(usersAnswer);
            userDataCache.setUsersCurrentBotState(userId, BotState.ASK_SERVICE);
        }

        if (botState.equals(BotState.ASK_SERVICE)) {
            replyToUser = messagesService.getReplyMessage(chatId, "reply.askService");
            recordData.setTime(usersAnswer);
            userDataCache.setUsersCurrentBotState(userId, BotState.PROFILE_FILLED);
        }

        if (botState.equals(BotState.PROFILE_FILLED)) {
            recordData.setService(usersAnswer);
            userDataCache.setUsersCurrentBotState(userId, BotState.SHOW_MAIN_MENU);

            String profileFilledMessage = messagesService.getReplyText("reply.profileFilled",
                    profileData.getName(), Emojis.SPARKLES);
            String predictionMessage = predictionService.getPrediction();

            replyToUser = new SendMessage(chatId, String.format("%s%n%n%s %s", profileFilledMessage, Emojis.SCROLL, predictionMessage));
            replyToUser.setParseMode("HTML");
        }

        userDataCache.saveUserProfileData(userId, profileData);
        userDataCache.saveUserRecordData(userId, recordData);

        return replyToUser;
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

    private InlineKeyboardMarkup getCalendar() {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

        InlineKeyboardButton month = new InlineKeyboardButton().setText("Май 2020");
        InlineKeyboardButton monday = new InlineKeyboardButton().setText("Пн");
        InlineKeyboardButton tuesday = new InlineKeyboardButton().setText("Вт");
        InlineKeyboardButton wednesday = new InlineKeyboardButton().setText("Ср");
        InlineKeyboardButton thursday = new InlineKeyboardButton().setText("Чт");
        InlineKeyboardButton friday = new InlineKeyboardButton().setText("Пт");
        InlineKeyboardButton sunday = new InlineKeyboardButton().setText("Сб");
        InlineKeyboardButton saturday = new InlineKeyboardButton().setText("Вск");
        //InlineKeyboardButton buttonIdontKnow = new InlineKeyboardButton().setText("Еще не определился");

        //Every button must have callBackData, or else not work !
        month.setCallbackData("buttonYes");
        monday.setCallbackData("buttonNo");
        tuesday.setCallbackData("buttonIwillThink");
        wednesday.setCallbackData("buttonIwillThink");
        thursday.setCallbackData("buttonIwillThink");
        friday.setCallbackData("buttonIwillThink");
        sunday.setCallbackData("buttonIwillThink");
        saturday.setCallbackData("buttonIwillThink");
        //buttonIdontKnow.setCallbackData("-");

        List<InlineKeyboardButton> keyboardButtonsRow1 = new ArrayList<>();
        keyboardButtonsRow1.add(month);
        //keyboardButtonsRow1.add(buttonNo);

        List<InlineKeyboardButton> keyboardButtonsRow2 = new ArrayList<>();
        keyboardButtonsRow2.add(monday);
        keyboardButtonsRow2.add(tuesday);
        keyboardButtonsRow2.add(wednesday);
        keyboardButtonsRow2.add(thursday);
        keyboardButtonsRow2.add(friday);
        keyboardButtonsRow2.add(sunday);
        keyboardButtonsRow2.add(saturday);


        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        rowList.add(keyboardButtonsRow1);
        rowList.add(keyboardButtonsRow2);

        inlineKeyboardMarkup.setKeyboard(rowList);

        return inlineKeyboardMarkup;
    }


}



