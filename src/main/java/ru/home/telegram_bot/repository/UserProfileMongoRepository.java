package ru.home.telegram_bot.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import ru.home.telegram_bot.model.UserProfileData;

@Repository
public interface UserProfileMongoRepository extends MongoRepository<UserProfileData, String> {
    UserProfileData findByChatId(long chatId);
    void deleteByChatId(long chatId);
}
