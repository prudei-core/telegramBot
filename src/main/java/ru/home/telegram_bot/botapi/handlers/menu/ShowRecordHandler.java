package ru.home.telegram_bot.botapi.handlers.menu;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.home.telegram_bot.botapi.BotState;
import ru.home.telegram_bot.botapi.InputMessageHandler;
import ru.home.telegram_bot.cache.UserDataCache;
import ru.home.telegram_bot.model.UserRecordData;
import ru.home.telegram_bot.service.UserRecordDataService;

import java.util.ArrayList;
import java.util.List;


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
            userReply.setReplyMarkup(getGenderButtonsMarkup());
        } else {
            userReply = new SendMessage(message.getChatId(), "Ваша запись отменена");
        }
        return userReply;
    }

    @Override
    public BotState getHandlerName() {
        return BotState.SHOW_USER_RECORD;
    }

    private InlineKeyboardMarkup getGenderButtonsMarkup() {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        InlineKeyboardButton buttonGenderMan = new InlineKeyboardButton().setText("Редактировать");
        InlineKeyboardButton buttonGenderWoman = new InlineKeyboardButton().setText("Отменить");

        //Every button must have callBackData, or else not work !
        buttonGenderMan.setCallbackData("editRecord");
        buttonGenderWoman.setCallbackData("deleteRecord");

        List<InlineKeyboardButton> keyboardButtonsRow1 = new ArrayList<>();
        keyboardButtonsRow1.add(buttonGenderMan);
        keyboardButtonsRow1.add(buttonGenderWoman);

        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        rowList.add(keyboardButtonsRow1);

        inlineKeyboardMarkup.setKeyboard(rowList);

        return inlineKeyboardMarkup;
    }
}
