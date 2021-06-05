package ru.home.telegram_bot.service;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Calendar {
    final String emptyCallbackOrName = " ";
    int year;
    int month;
    LocalDate dayOfMonth;
    boolean isComplete;
    InlineKeyboardMarkup markupKeyboard;

    public Calendar(int year, int month) {
        markupKeyboard = new InlineKeyboardMarkup();
        isComplete = false;
        this.year = year;
        this.month = month;
        dayOfMonth = LocalDate.of(year, month, 1);

        List<List<InlineKeyboardButton>> buttons = new ArrayList<List<InlineKeyboardButton>>();

        //year button
        buttons.add(new ArrayList<InlineKeyboardButton>(
                Arrays.asList(
                        new InlineKeyboardButton().setText(getMonthName(month - 1)+ " "
                                +(Integer.toString(year))).setCallbackData(emptyCallbackOrName)
                )));

        //days of week buttons
        List<InlineKeyboardButton> daysOfWeekCalendar = new ArrayList<InlineKeyboardButton>();
        String[] daysOfWeek = {"Пн", "Вт", "Ср", "Чт", "Пт", "Сб", "Вск"};
        for (String s : daysOfWeek)
            daysOfWeekCalendar.add(new InlineKeyboardButton().setText(s).setCallbackData(emptyCallbackOrName));
        buttons.add(daysOfWeekCalendar);

        //filling days
        for (int i = 0; i < 6; i++) {
            while (!isComplete) {
                buttons.add(getWeekButtons());
            }
        }

        //buttons for change month
        buttons.add(new ArrayList<InlineKeyboardButton>(
                Arrays.asList(
                        new InlineKeyboardButton().setText("<").setCallbackData(dayOfMonth.minusMonths(1).toString()),
                        new InlineKeyboardButton().setText(">").setCallbackData(dayOfMonth.plusMonths(1).toString())
                )));

        markupKeyboard.setKeyboard(buttons);
    }

    private List<InlineKeyboardButton> getWeekButtons() {
        return new ArrayList<InlineKeyboardButton>(
                Arrays.asList(
                        getDayButton(DayOfWeek.MONDAY),
                        getDayButton(DayOfWeek.TUESDAY),
                        getDayButton(DayOfWeek.WEDNESDAY),
                        getDayButton(DayOfWeek.THURSDAY),
                        getDayButton(DayOfWeek.FRIDAY),
                        getDayButton(DayOfWeek.SATURDAY),
                        getDayButton(DayOfWeek.SUNDAY)
                ));
    }

    private InlineKeyboardButton getDayButton(DayOfWeek day) {
        InlineKeyboardButton button = new InlineKeyboardButton();
        if (dayOfMonth.getDayOfWeek() == day && !isComplete) {
            button.setText(Integer.toString(dayOfMonth.getDayOfMonth()));
            button.setCallbackData("You choose " + dayOfMonth.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG)));

            if (dayOfMonth.plusDays(1).getMonthValue() == dayOfMonth.getMonthValue()) {
                dayOfMonth = dayOfMonth.plusDays(1);
            } else {
                isComplete = true;
            }
        } else {
            button.setText(emptyCallbackOrName);
            button.setCallbackData(emptyCallbackOrName);
        }
        return button;
    }

    public InlineKeyboardMarkup getCalendar() {
        return markupKeyboard;
    }

    static String getMonthName(int month) {
        String[] monthNames = {"Январь", "Февраль", "Март", "Апрель", "Май", "Июнь", "Июль", "Август", "Сентябрь", "Октябрь", "Ноябрь", "Декабрь"};
        return monthNames[month];
    }
}