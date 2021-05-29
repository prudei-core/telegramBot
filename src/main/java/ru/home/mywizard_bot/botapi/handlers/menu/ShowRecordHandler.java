package ru.home.mywizard_bot.botapi.handlers.menu;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.home.mywizard_bot.botapi.BotState;
import ru.home.mywizard_bot.botapi.InputMessageHandler;
import ru.home.mywizard_bot.cache.UserDataCache;
import ru.home.mywizard_bot.model.UserProfileData;
import ru.home.mywizard_bot.model.UserRecordData;

/**
 * @author Sergei Viacheslaev
 */
@Component
public class ShowRecordHandler implements InputMessageHandler {
    private UserDataCache userDataCache;

    public ShowRecordHandler(UserDataCache userDataCache) {
        this.userDataCache = userDataCache;
    }

    @Override
    public SendMessage handle(Message message) {
        final int userId = message.getFrom().getId();
        final UserRecordData recordData = userDataCache.getUserRecordData(userId);

        userDataCache.setUsersCurrentBotState(userId, BotState.SHOW_MAIN_MENU);
        return new SendMessage(message.getChatId(),
                String.format("%s%n-------------------%n%s", "Данные по вашей записи:", recordData.toString()));
    }

    @Override
    public BotState getHandlerName() {
        return BotState.SHOW_USER_RECORD;
    }
}
