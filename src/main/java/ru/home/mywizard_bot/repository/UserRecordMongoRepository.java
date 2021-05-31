package ru.home.mywizard_bot.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import ru.home.mywizard_bot.model.UserRecordData;

@Repository
public interface UserRecordMongoRepository extends MongoRepository<UserRecordData, String> {
    UserRecordData findByChatId(long chatId);
    void deleteByChatId(long chatId);
}
