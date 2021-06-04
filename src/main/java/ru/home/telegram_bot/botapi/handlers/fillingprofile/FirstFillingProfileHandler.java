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
import ru.home.telegram_bot.model.UserRecordData;
import ru.home.telegram_bot.service.PredictionService;
import ru.home.telegram_bot.service.ReplyMessagesService;
import ru.home.telegram_bot.service.UserProfileDataService;
import ru.home.telegram_bot.service.UserRecordDataService;
import ru.home.telegram_bot.utils.Emojis;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class FirstFillingProfileHandler implements InputMessageHandler {
    private UserDataCache userDataCache;
    private UserRecordDataService recordDataService;
    private UserProfileDataService profileDataService;
    private ReplyMessagesService messagesService;
    private PredictionService predictionService;

    public FirstFillingProfileHandler(UserDataCache userDataCache,
                                      UserRecordDataService recordDataService,
                                      UserProfileDataService profileDataService,
                                      ReplyMessagesService messagesService,
                                      PredictionService predictionService) {
        this.userDataCache = userDataCache;
        this.recordDataService = recordDataService;
        this.profileDataService = profileDataService;
        this.messagesService = messagesService;
        this.predictionService = predictionService;
    }

    @Override
    public SendMessage handle(Message message) {
        if (userDataCache.getUsersCurrentBotState(message.getFrom().getId()).equals(BotState.FIRST_FILLING_PROFILE)) {
            userDataCache.setUsersCurrentBotState(message.getFrom().getId(), BotState.ASK_NAME_F);
        }
        return processUsersInput(message);
    }

    @Override
    public BotState getHandlerName() {
        return BotState.FIRST_FILLING_PROFILE;
    }

    private SendMessage processUsersInput(Message inputMsg) {
        String usersAnswer = inputMsg.getText();
        int userId = inputMsg.getFrom().getId();
        long chatId = inputMsg.getChatId();
        SendMessage replyToUser = null;

        UserRecordData recordData = userDataCache.getUserRecordData(userId);
        UserProfileData profileData = userDataCache.getUserProfileData(userId);
        BotState botState = userDataCache.getUsersCurrentBotState(userId);


            if (botState.equals(BotState.ASK_NAME_F)) {
                replyToUser = messagesService.getReplyMessage(chatId, "reply.askName");
                userDataCache.setUsersCurrentBotState(userId, BotState.ASK_AGE_F);
            }

            if (botState.equals(BotState.ASK_AGE_F)) {
                profileData.setName(usersAnswer);
                replyToUser = messagesService.getReplyMessage(chatId, "reply.askAge");
                userDataCache.setUsersCurrentBotState(userId, BotState.ASK_GENDER_F);
            }

            if (botState.equals(BotState.ASK_GENDER_F)) {
                profileData.setAge(usersAnswer);
                replyToUser = messagesService.getReplyMessage(chatId, "reply.askGender");
                replyToUser.setReplyMarkup(getGenderButtonsMarkup());
            }

            if (botState.equals(BotState.ASK_TIME_F)) {
                replyToUser = messagesService.getReplyMessage(chatId, "reply.askTime");
                recordData.setDate(usersAnswer);
                userDataCache.setUsersCurrentBotState(userId, BotState.ASK_SERVICE_F);
            }

            if (botState.equals(BotState.ASK_SERVICE_F)) {
                replyToUser = messagesService.getReplyMessage(chatId, "reply.askService");
                recordData.setTime(usersAnswer);
                userDataCache.setUsersCurrentBotState(userId, BotState.FIRST_PROFILE_FILLED);
            }

            if (botState.equals(BotState.FIRST_PROFILE_FILLED)) {
                recordData.setService(usersAnswer);
                profileData.setChatId(chatId);
                recordData.setChatId(chatId);

                profileDataService.saveUserProfileData(profileData);
                recordDataService.saveUserRecordData(recordData);


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



        private InlineKeyboardMarkup getGenderButtonsMarkup () {
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
    }

