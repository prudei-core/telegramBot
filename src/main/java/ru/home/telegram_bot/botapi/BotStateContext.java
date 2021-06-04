package ru.home.telegram_bot.botapi;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Defines message handlers for each state.
 */
@Component
public class BotStateContext {
    private Map<BotState, InputMessageHandler> messageHandlers = new HashMap<>();

    public BotStateContext(List<InputMessageHandler> messageHandlers) {
        messageHandlers.forEach(handler -> this.messageHandlers.put(handler.getHandlerName(), handler));
    }

    public SendMessage processInputMessage(BotState currentState, Message message) {
        InputMessageHandler currentMessageHandler = findMessageHandler(currentState);
        return currentMessageHandler.handle(message);
    }

    private InputMessageHandler findMessageHandler(BotState currentState) {
        if (isFillingProfileState(currentState)) {
            return messageHandlers.get(BotState.FILLING_PROFILE);
        }

        if (isFillingRecordState(currentState)) {
            return messageHandlers.get(BotState.FILLING_RECORD);
        }

        if (isFirstFillingRecordState(currentState)) {
            return messageHandlers.get(BotState.FIRST_FILLING_PROFILE);
        }

        return messageHandlers.get(currentState);
    }

    private boolean isFillingProfileState(BotState currentState) {
        switch (currentState) {
            case ASK_NAME:
            case ASK_AGE:
            case ASK_GENDER:
            case FILLING_PROFILE:
            case PROFILE_FILLED:
                return true;
            default:
                return false;
        }
    }

    private boolean isFillingRecordState(BotState currentState) {
        switch (currentState) {
            case ASK_DATE:
            case ASK_SERVICE:
            case ASK_TIME:
            case FILLING_RECORD:
            case RECORD_FILLED:
                return true;
            default:
                return false;
        }
    }
    private boolean isFirstFillingRecordState(BotState currentState) {
        switch (currentState) {
            case ASK_NAME_F:
            case ASK_AGE_F:
            case ASK_GENDER_F:
            case ASK_DATE_F:
            case ASK_SERVICE_F:
            case ASK_TIME_F:
            case FIRST_FILLING_PROFILE:
            case FIRST_PROFILE_FILLED:
                return true;
            default:
                return false;
        }
    }


}





