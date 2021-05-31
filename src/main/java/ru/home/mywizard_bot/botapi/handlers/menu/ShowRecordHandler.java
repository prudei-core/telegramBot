package ru.home.mywizard_bot.botapi.handlers.menu;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.home.mywizard_bot.botapi.BotState;
import ru.home.mywizard_bot.botapi.InputMessageHandler;
import ru.home.mywizard_bot.cache.UserDataCache;
import ru.home.mywizard_bot.model.UserProfileData;
import ru.home.mywizard_bot.model.UserRecordData;
import ru.home.mywizard_bot.service.UserRecordDataService;


@Component
public class ShowRecordHandler implements InputMessageHandler {
    private UserDataCache userDataCache;
    private UserRecordDataService recordDataService;

    public ShowRecordHandler(UserDataCache userDataCache, UserRecordDataService recordDataService) {
        this.userDataCache = userDataCache;
        this.recordDataService = recordDataService;
    }

    @Override
    public SendMessage handle(Message message) {
        SendMessage userReply;
        final int userId = message.getFrom().getId();
        final UserRecordData recordData = recordDataService.getUserRecordData(message.getChatId());

        userDataCache.setUsersCurrentBotState(userId, BotState.SHOW_MAIN_MENU);
        if (recordData != null) {
            userReply = new SendMessage(message.getChatId(),
                    String.format("%s%n-------------------%n%s", "Данные по вашей записи:", recordData.toString()));
        } else {
            userReply = new SendMessage(message.getChatId(), "Такой анкеты в БД не существует");
        }
        return userReply;
    }

    @Override
    public BotState getHandlerName() {
        return BotState.SHOW_USER_RECORD;
    }
}
