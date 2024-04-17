package com.projects.oleksii.leheza.cashtruck.service.interfaces;

import com.projects.oleksii.leheza.cashtruck.domain.BankCard;
import com.projects.oleksii.leheza.cashtruck.domain.Transaction;
import com.projects.oleksii.leheza.cashtruck.domain.User;
import com.projects.oleksii.leheza.cashtruck.dto.create.CreateUserDto;
import com.projects.oleksii.leheza.cashtruck.dto.filter.UserSearchCriteria;
import com.projects.oleksii.leheza.cashtruck.dto.update.UserUpdateDto;
import com.projects.oleksii.leheza.cashtruck.dto.view.ClientStatisticDto;
import com.projects.oleksii.leheza.cashtruck.dto.view.UserDto;
import com.projects.oleksii.leheza.cashtruck.dto.view.UserHeaderDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface UserService {

    User saveClient(CreateUserDto createUserDto);

    User saveUser(User user); //TODO paramiters(DTO,id)

    Boolean verifyEmailToken(String token);

    List<UserDto> findAll();

    List<User> findAllManagers();// TODO filter?

    List<User> findAllAdmins();

    User findByEmail(String email);

    UserDto getUserById(Long userId);

    void deleteUserById(Long id);

    void updateUserInfo(Long userId, UserUpdateDto userUpdateDto);

    void updateClient(Long clientId, UserUpdateDto userDto);

    UserUpdateDto getClientUpdateDto(Long clientId);

    void updateToManager(Long userId);

    UserDto getUserDto(Long userId);

    CreateUserDto getCreateUserDto(Long userId);

    //For UI
    ClientStatisticDto getClientStatisticByUserId(Long userId);

    void addTransaction(Long userId, Transaction transaction);
    //TODO use only one method (update) use tranisction

    List<BankCard> getBankCardsByUserId(Long userId);

    UserHeaderDto getHeaderClientData(Long userId);

    void updateAvatar(Long userId, MultipartFile avatar);

    List<User> findUsersWithFilters(UserSearchCriteria criteria);

    void blockUser(Long userId);

    void unblockUser(Long userId);
}
