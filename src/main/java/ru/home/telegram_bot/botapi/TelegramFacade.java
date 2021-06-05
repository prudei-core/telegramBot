package ru.home.telegram_bot.botapi;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.message.StringFormattedMessage;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.home.telegram_bot.NailTelegramBot;
import ru.home.telegram_bot.cache.UserDataCache;
import ru.home.telegram_bot.model.UserProfileData;
import ru.home.telegram_bot.service.*;
import ru.home.telegram_bot.utils.Emojis;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.regex.Pattern;

/**
 * @author Sergei Viacheslaev
 */
@Component
@Slf4j
public class TelegramFacade {
    private BotStateContext botStateContext;
    private UserDataCache userDataCache;
    private MainMenuService mainMenuService;
    private NailTelegramBot myWizardBot;
    private ReplyMessagesService messagesService;
    UserRecordDataService recordDataService;
    UserProfileDataService profileDataService;

    public TelegramFacade(BotStateContext botStateContext, UserDataCache userDataCache, MainMenuService mainMenuService,
                          @Lazy NailTelegramBot myWizardBot, ReplyMessagesService messagesService, UserRecordDataService recordDataService,
                          UserProfileDataService profileDataService) {
        this.botStateContext = botStateContext;
        this.userDataCache = userDataCache;
        this.mainMenuService = mainMenuService;
        this.myWizardBot = myWizardBot;
        this.messagesService = messagesService;
        this.recordDataService = recordDataService;
        this.profileDataService = profileDataService;
    }

    public BotApiMethod<?> handleUpdate(Update update) {
        SendMessage replyMessage = null;

        if (update.hasCallbackQuery()) {
            CallbackQuery callbackQuery = update.getCallbackQuery();
            log.info("New callbackQuery from User: {}, userId: {}, with data: {}", update.getCallbackQuery().getFrom().getUserName(),
                    callbackQuery.getFrom().getId(), update.getCallbackQuery().getData());
            return processCallbackQuery(callbackQuery);
        }


        Message message = update.getMessage();
        if (message != null && message.hasText()) {
            log.info("New message from User:{}, userId: {}, chatId: {},  with text: {}",
                    message.getFrom().getUserName(), message.getFrom().getId(), message.getChatId(), message.getText());
            replyMessage = handleInputMessage(message);
        }

        return replyMessage;
    }


    private SendMessage handleInputMessage(Message message) {
        String inputMsg = message.getText();
        int userId = message.getFrom().getId();
        long chatId = message.getChatId();
        BotState botState;
        SendMessage replyMessage;

        switch (inputMsg) {
            case "/start":
                boolean profileDataExist = isProfileDataExist(chatId);
                if (profileDataExist){
                    replyMessage = new SendMessage(chatId, "Не верная команда. Воспользуйтесь кнопками меню");
                    return replyMessage;
                }else {
                    botState = BotState.ASK_RECORD;
                    myWizardBot.sendPhoto(chatId, messagesService.getReplyText("reply.hello"), "static/images/nail_logo.jpg");
                    break;
                }
            /*case "/calendar":
                InlineKeyboardMarkup cal = new Calendar(YearMonth.now().getYear(), YearMonth.now().getMonthValue()).getCalendar();
                replyMessage = new SendMessage(chatId, "Please, choose the date");
                replyMessage.setReplyMarkup(cal);
                break;*/
            case "Записаться":
                //botState = BotState.FILLING_PROFILE;
                botState = BotState.FILLING_RECORD;
                break;
            case "Моя запись":
                botState = BotState.SHOW_USER_RECORD;
                break;
            case "Скачать анкету":
                myWizardBot.sendDocument(chatId, "Ваша анкета", getUsersProfile(userId));
                botState = BotState.SHOW_USER_RECORD;
                break;
            case "Мой профиль":
                botState = BotState.SHOW_USER_PROFILE;
                break;
            case "Помощь":
                botState = BotState.SHOW_HELP_MENU;
                break;
            default:
                botState = userDataCache.getUsersCurrentBotState(userId);
                break;
        }

        //botState = BotState.ASK_DESTINY;

        userDataCache.setUsersCurrentBotState(userId, botState);

        replyMessage = botStateContext.processInputMessage(botState, message);

        return replyMessage;
    }


    private BotApiMethod<?> processCallbackQuery(CallbackQuery buttonQuery) {

        final long chatId = buttonQuery.getMessage().getChatId();
        final int userId = buttonQuery.getFrom().getId();
        BotApiMethod<?> callBackAnswer = null;
        //BotApiMethod<?> callBackAnswer = mainMenuService.getMainMenuMessage(chatId, "Воспользуйтесь главным меню");


        //From Destiny choose buttons
        if (buttonQuery.getData().equals("buttonYes")) {
            callBackAnswer = new SendMessage(chatId, "Как тебя зовут ?");
            userDataCache.setUsersCurrentBotState(userId, BotState.ASK_AGE_F);
        } else if (buttonQuery.getData().equals("buttonNo")) {
            callBackAnswer = sendAnswerCallbackQuery("Возвращайтесь скорее! Мы всегда рады вас видеть", false, buttonQuery);
        } else if (buttonQuery.getData().equals("buttonIwillThink")) {
            callBackAnswer = sendAnswerCallbackQuery("Данная кнопка не поддерживается", true, buttonQuery);
        }

        //From Gender choose buttons
        else if (buttonQuery.getData().equals("buttonMan")) {
            UserProfileData userProfileData = userDataCache.getUserProfileData(userId);
            userProfileData.setGender("М");
            userDataCache.saveUserProfileData(userId, userProfileData);
            BotState botState = userDataCache.getUsersCurrentBotState(userId);
            if (botState.equals(BotState.ASK_GENDER_F)){
                userDataCache.setUsersCurrentBotState(userId, BotState.ASK_TIME_F);
            } else {
                userDataCache.setUsersCurrentBotState(userId, BotState.ASK_TIME);
            }
            userDataCache.saveUserProfileData(userId, userProfileData);
            callBackAnswer = new SendMessage(chatId, "Выберите свободную дату:");
        } else if (buttonQuery.getData().equals("buttonWoman")) {
            UserProfileData userProfileData = userDataCache.getUserProfileData(userId);
            userProfileData.setGender("Ж");
            userDataCache.saveUserProfileData(userId, userProfileData);
            BotState botState = userDataCache.getUsersCurrentBotState(userId);
            if (botState.equals(BotState.ASK_GENDER_F)){
                userDataCache.setUsersCurrentBotState(userId, BotState.ASK_TIME_F);
            } else {
                userDataCache.setUsersCurrentBotState(userId, BotState.ASK_TIME);
            }
            userDataCache.saveUserProfileData(userId, userProfileData);
            callBackAnswer = new SendMessage(chatId, "Выберите свободную дату:");
        } else if (buttonQuery.getData().equals("editRecord")) {
            recordDataService.deleteRecord(chatId);
            userDataCache.setUsersCurrentBotState(userId, BotState.ASK_TIME);
            callBackAnswer = new SendMessage(chatId, "Выберите свободную дату:");
            
        } else if (buttonQuery.getData().equals("deleteRecord")) {

            recordDataService.deleteRecord(chatId);
            userDataCache.setUsersCurrentBotState(userId, BotState.SHOW_MAIN_MENU);
            callBackAnswer = new SendMessage(chatId, "Ваша запись отменена");

        } else {
            //userDataCache.setUsersCurrentBotState(userId, BotState.SHOW_MAIN_MENU);
            int msgId = buttonQuery.getMessage().getMessageId();
            String callback = buttonQuery.getData();

            if (Pattern.matches("^[0-9]{4}-[0-9]{2}-[0-9]{2}$", callback) && !buttonQuery.getData().isEmpty()) {
                //delete old calendar
                DeleteMessage delMsg = new DeleteMessage();
                delMsg.setChatId(chatId);
                delMsg.setMessageId(msgId);
                EditMessageReplyMarkup editMessageReplyMarkup = new EditMessageReplyMarkup();
                editMessageReplyMarkup.setChatId(chatId);
                editMessageReplyMarkup.setMessageId(msgId);


                //set new calendar
                LocalDate date = LocalDate.parse(buttonQuery.getData());
                InlineKeyboardMarkup cal = new Calendar(date.getYear(), date.getMonthValue()).getCalendar();
                SendMessage message = new SendMessage(chatId, "Выберите свободную дату:");
                message.setReplyMarkup(cal);
                /*try {
                    myWizardBot.execute(delMsg);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }*/
                callBackAnswer = editMessageReplyMarkup.setReplyMarkup(cal);
                //callBackAnswer = message;
            }
            userDataCache.setUsersCurrentBotState(userId, BotState.ASK_TIME);
        }


        return callBackAnswer;


    }


    private AnswerCallbackQuery sendAnswerCallbackQuery(String text, boolean alert, CallbackQuery callbackquery) {
        AnswerCallbackQuery answerCallbackQuery = new AnswerCallbackQuery();
        answerCallbackQuery.setCallbackQueryId(callbackquery.getId());
        answerCallbackQuery.setShowAlert(alert);
        answerCallbackQuery.setText(text);
        return answerCallbackQuery;
    }

    @SneakyThrows
    public File getUsersProfile(int userId) {
        UserProfileData userProfileData = userDataCache.getUserProfileData(userId);
        File profileFile = ResourceUtils.getFile("classpath:static/docs/users_profile.txt");

        try (FileWriter fw = new FileWriter(profileFile.getAbsoluteFile());
             BufferedWriter bw = new BufferedWriter(fw)) {
            bw.write(userProfileData.toString());
        }


        return profileFile;

    }

    private boolean isProfileDataExist ( long chatId){
        UserProfileData userProfileData = profileDataService.getUserProfileData(chatId);
        if (userProfileData != null) {
            return true;
        } else {
            return false;
        }
    }


}
