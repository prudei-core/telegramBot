package ru.home.telegram_bot.service;

import org.springframework.stereotype.Service;
import ru.home.telegram_bot.model.UserProfileData;
import ru.home.telegram_bot.repository.UserProfileMongoRepository;

import java.util.List;

@Service
public class UserProfileDataService {
    private UserProfileMongoRepository profileMongoRepository;

    public UserProfileDataService(UserProfileMongoRepository profileMongoRepository) {
        this.profileMongoRepository = profileMongoRepository;
    }

    public List<UserProfileData> getAllProfiles() {
        return profileMongoRepository.findAll();
    }

    public void saveUserProfileData(UserProfileData userProfileData) {
        profileMongoRepository.save(userProfileData);
    }

    public void deleteUsersProfileData(String profileDataId) {
        profileMongoRepository.deleteById(profileDataId);
    }

    public UserProfileData getUserProfileData(long chatId) {
        return profileMongoRepository.findByChatId(chatId);
    }
}
