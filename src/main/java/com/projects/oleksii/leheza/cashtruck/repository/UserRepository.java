package com.projects.oleksii.leheza.cashtruck.repository;

import com.projects.oleksii.leheza.cashtruck.domain.User;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {

    User findByEmailIgnoreCase(String email); // TODO Optional<User>

    Boolean existsByEmailIgnoreCase(String email);

//    User findUserByEmail(String email);   // TODO Optional<User>+ Client + Manager + Admin


    @Query("SELECT u.avatar.imageBytes FROM User u WHERE u.id = :userId")
    byte[] findAvatarByUserId(@Param("userId") Long userId);

    List<User> findAll(Specification<User> specification);
}
