package ru.home.telegram_bot.botapi;

/**Возможные состояния бота
 */

public enum BotState {
    ASK_DESTINY,
    ASK_NAME,
    ASK_AGE,
    ASK_GENDER,
    ASK_TIME,
    ASK_DATE,
    ASK_SERVICE,
    ASK_SONG,
    FILLING_PROFILE,
    PROFILE_FILLED,
    SHOW_USER_RECORD,
    SHOW_USER_PROFILE,
    SHOW_MAIN_MENU,
    SHOW_HELP_MENU;
}
