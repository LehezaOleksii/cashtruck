package com.projects.oleksii.leheza.cashtruck.service.interfaces;

import com.projects.oleksii.leheza.cashtruck.domain.BankCard;
import com.projects.oleksii.leheza.cashtruck.domain.CustomUser;
import com.projects.oleksii.leheza.cashtruck.domain.Transaction;
import com.projects.oleksii.leheza.cashtruck.dto.auth.SignUpRequest;
import com.projects.oleksii.leheza.cashtruck.dto.create.CreateUserDto;
import com.projects.oleksii.leheza.cashtruck.dto.update.UserUpdateDto;
import com.projects.oleksii.leheza.cashtruck.dto.view.ClientStatisticDto;
import com.projects.oleksii.leheza.cashtruck.dto.view.UserDto;
import com.projects.oleksii.leheza.cashtruck.dto.view.UserHeaderDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface UserService {

    CustomUser saveClient(CreateUserDto createUserDto);

    boolean registerUser(SignUpRequest signUpRequest);

    CustomUser saveUser(CustomUser customUser); //TODO paramiters(DTO,id)


    List<CustomUser> findAll();

    List<CustomUser> findAllManagers();// TODO filter?

    List<CustomUser> findAllAdmins();

    CustomUser findByEmail(String email);

    CustomUser getUserById(Long userId);

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
}
