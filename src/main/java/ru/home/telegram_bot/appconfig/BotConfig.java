package ru.home.telegram_bot.appconfig;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import ru.home.telegram_bot.NailTelegramBot;
import ru.home.telegram_bot.botapi.TelegramFacade;


@Setter
@Getter
@Configuration
@ConfigurationProperties(prefix = "telegrambot")
public class BotConfig {
    private String webHookPath;
    private String botUserName;
    private String botToken;

    @Bean
    public NailTelegramBot myWizardTelegramBot(TelegramFacade telegramFacade) {


        NailTelegramBot nailTelegramBot = new NailTelegramBot(telegramFacade);
        nailTelegramBot.setBotUserName(botUserName);
        nailTelegramBot.setBotToken(botToken);
        nailTelegramBot.setWebHookPath(webHookPath);

        return nailTelegramBot;
    }

    @Bean
    public MessageSource messageSource() {
        ReloadableResourceBundleMessageSource messageSource
                = new ReloadableResourceBundleMessageSource();

        messageSource.setBasename("classpath:messages");
        messageSource.setDefaultEncoding("UTF-8");
        return messageSource;
    }
}
