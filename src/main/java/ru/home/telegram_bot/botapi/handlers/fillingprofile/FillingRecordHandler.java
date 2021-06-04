package ru.home.telegram_bot.botapi.handlers.fillingprofile;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.home.telegram_bot.botapi.BotState;
import ru.home.telegram_bot.botapi.InputMessageHandler;
import ru.home.telegram_bot.cache.UserDataCache;
import ru.home.telegram_bot.model.UserRecordData;
import ru.home.telegram_bot.service.ReplyMessagesService;
import ru.home.telegram_bot.service.UserProfileDataService;
import ru.home.telegram_bot.service.UserRecordDataService;

@Slf4j
@Component
public class FillingRecordHandler implements InputMessageHandler {
    private UserDataCache userDataCache;
    private UserRecordDataService recordDataService;
    private ReplyMessagesService messagesService;

    public FillingRecordHandler(UserDataCache userDataCache, UserRecordDataService recordDataService,
                                ReplyMessagesService messagesService) {
        this.userDataCache = userDataCache;
        this.recordDataService = recordDataService;
        this.messagesService = messagesService;
    }

    @Override
    public SendMessage handle(Message message) {
        if (userDataCache.getUsersCurrentBotState(message.getFrom().getId()).equals(BotState.FILLING_RECORD)){
            userDataCache.setUsersCurrentBotState(message.getFrom().getId(), BotState.ASK_DATE);
        }
        return processUsersInput(message);
    }

    @Override
    public BotState getHandlerName() {
        return BotState.FILLING_RECORD;
    }

    private SendMessage processUsersInput(Message inputMsg) {
        String usersAnswer = inputMsg.getText();
        int userId = inputMsg.getFrom().getId();
        long chatId = inputMsg.getChatId();
        SendMessage replyToUser = null;

        UserRecordData recordData = userDataCache.getUserRecordData(userId);
        BotState botState = userDataCache.getUsersCurrentBotState(userId);

        boolean recordDataExist = isRecordDataExist(chatId);
        if (!recordDataExist) {
            if (botState.equals(BotState.ASK_DATE)) {

                replyToUser = messagesService.getReplyMessage(chatId, "reply.askDate");
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
                userDataCache.setUsersCurrentBotState(userId, BotState.RECORD_FILLED);
            }

            if (botState.equals(BotState.RECORD_FILLED)) {
                recordData.setService(usersAnswer);
                recordData.setChatId(chatId);
                recordDataService.saveUserRecordData(recordData);
                replyToUser = new SendMessage(chatId, "Запись успешно выполнена!");
            }
        } else {
            userDataCache.setUsersCurrentBotState(userId, BotState.SHOW_MAIN_MENU);
            replyToUser = new SendMessage(chatId, "Вы уже записаны");
        }

        userDataCache.saveUserRecordData(userId, recordData);

        return  replyToUser;
    }

    private boolean isRecordDataExist(long chatId) {
        UserRecordData userRecordData = recordDataService.getUserRecordData(chatId);
        if (userRecordData != null) {
            return true;
        } else {
            return false;
        }
    }
}
