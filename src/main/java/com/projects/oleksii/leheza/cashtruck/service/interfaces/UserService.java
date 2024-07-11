package com.projects.oleksii.leheza.cashtruck.service.interfaces;

import com.projects.oleksii.leheza.cashtruck.domain.*;
import com.projects.oleksii.leheza.cashtruck.dto.PageDto;
import com.projects.oleksii.leheza.cashtruck.dto.auth.LoginDto;
import com.projects.oleksii.leheza.cashtruck.dto.create.CreateTransactionDto;
import com.projects.oleksii.leheza.cashtruck.dto.create.CreateUserDto;
import com.projects.oleksii.leheza.cashtruck.dto.filter.UserSearchCriteria;
import com.projects.oleksii.leheza.cashtruck.dto.update.UserUpdateDto;
import com.projects.oleksii.leheza.cashtruck.dto.view.ClientStatisticDto;
import com.projects.oleksii.leheza.cashtruck.dto.view.TransactionDto;
import com.projects.oleksii.leheza.cashtruck.dto.view.UserDto;
import com.projects.oleksii.leheza.cashtruck.dto.view.UserHeaderDto;
import com.projects.oleksii.leheza.cashtruck.enums.ActiveStatus;
import com.projects.oleksii.leheza.cashtruck.enums.Role;
import com.projects.oleksii.leheza.cashtruck.enums.SubscriptionStatus;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface UserService {

//    User save(CreateUserDto createUserDto);

    User save(User user); //TODO paramiters(DTO,id)

    User saveNewUser(LoginDto loginDto);

    User saveNewUserWithActiveStatus(LoginDto loginDto);

    Boolean verifyEmailToken(String token);

    Page<UserDto> findAll(int page, int size);

    PageDto<UserDto> findAll(Integer page, Integer size);

    List<User> findAllManagers();// TODO filter?

    List<User> findAllAdmins();

    User findByEmail(String email);

    UserDto getUserById(Long userId);

    void deleteUserById(Long id);

    UserDto updateUserInfo(Long userId, UserUpdateDto userUpdateDto);

    UserDto updateClient(Long clientId, UserUpdateDto userDto);

    UserUpdateDto getClientUpdateDto(Long clientId);

    void updateToManager(Long userId);

    UserDto getUserDto(Long userId);

    CreateUserDto getCreateUserDto(Long userId);

    //For UI
    ClientStatisticDto getClientStatisticByUserId(Long userId);

    Transaction addTransaction(Long userId, Transaction transaction);

    //TODO use only one method (update) use tranisction
    TransactionDto addTransaction(Long userId, CreateTransactionDto transaction);


    List<BankCard> getBankCardsByUserId(Long userId);

    UserHeaderDto getHeaderClientData(Long userId);

    Image updateAvatar(Long userId, MultipartFile avatar);

    Page<UserDto> findUsersWithFilters(int page, int size, UserSearchCriteria criteria);

    void blockUser(Long userId);

    void unblockUser(Long userId);

    SubscriptionStatus updateUserPlan(Long userId, SubscriptionStatus status);

    List<Long> findUserIdsWithExpiredSubscriptions();

    Role updateUserRole(Long userId, Role role);

    List<UserDto> getUsersByRole(Role role);

    List<UserDto> findAllDtos();

    List<UserDto> getUserListByEmailPattern(String email);

    PageDto<UserDto> getUserPageByEmailPattern(String email, Integer pageNumber, Integer pageSize);

    void sendEmailForAllClients(EmailContext email);

    void setStatus(Long userId, ActiveStatus status);

    Boolean existByEmail(String email);

    void setNewPassword(String email, String newPassword);

    void authenticateUser(String email);
}
