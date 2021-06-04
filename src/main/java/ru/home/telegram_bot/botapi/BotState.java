package ru.home.telegram_bot.botapi;

/**Возможные состояния бота
 */

public enum BotState {
    ASK_RECORD,
    ASK_NAME,
    ASK_NAME_F,
    ASK_AGE,
    ASK_AGE_F,
    ASK_GENDER,
    ASK_GENDER_F,
    ASK_TIME,
    ASK_TIME_F,
    ASK_DATE,
    ASK_DATE_F,
    ASK_SERVICE,
    ASK_SERVICE_F,
    FIRST_FILLING_PROFILE,
    FILLING_PROFILE,
    FILLING_RECORD,
    PROFILE_FILLED,
    FIRST_PROFILE_FILLED,
    RECORD_FILLED,
    SHOW_USER_RECORD,
    SHOW_USER_PROFILE,
    SHOW_MAIN_MENU,
    SHOW_HELP_MENU;
}
