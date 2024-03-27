package com.projects.oleksii.leheza.cashtruck.repository;

import com.projects.oleksii.leheza.cashtruck.domain.CustomUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<CustomUser, Long> {

    CustomUser findByEmailIgnoreCase(String email); // TODO Optional<User>

    Boolean existsByEmailIgnoreCase(String email);

//    User findUserByEmail(String email);   // TODO Optional<User>+ Client + Manager + Admin


    @Query("SELECT u.avatar.imageBytes FROM CustomUser u WHERE u.id = :userId")
    byte[] findAvatarByUserId(@Param("userId") Long userId);

}
