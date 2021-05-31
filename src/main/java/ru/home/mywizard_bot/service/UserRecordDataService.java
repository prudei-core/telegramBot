package ru.home.mywizard_bot.service;

import org.springframework.stereotype.Service;
import ru.home.mywizard_bot.model.UserProfileData;
import ru.home.mywizard_bot.model.UserRecordData;
import ru.home.mywizard_bot.repository.UserRecordMongoRepository;

import java.util.List;

@Service
public class UserRecordDataService {
    UserRecordMongoRepository recordMongoRepository;

    public UserRecordDataService(UserRecordMongoRepository recordMongoRepository) {
        this.recordMongoRepository = recordMongoRepository;
    }

    public List<UserRecordData> getAllProfiles() {
        return recordMongoRepository.findAll();
    }

    public void saveUserRecordData(UserRecordData userRecordData) {
        recordMongoRepository.save(userRecordData);
    }

    public void deleteUsersProfileData(String profileDataId) {
        recordMongoRepository.deleteById(profileDataId);
    }

    public UserRecordData getUserRecordData(long chatId) {
        return recordMongoRepository.findByChatId(chatId);
    }
}
