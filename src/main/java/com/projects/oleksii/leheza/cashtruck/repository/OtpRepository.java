package com.projects.oleksii.leheza.cashtruck.repository;

import com.projects.oleksii.leheza.cashtruck.domain.OtpToken;
import com.projects.oleksii.leheza.cashtruck.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OtpRepository extends JpaRepository<OtpToken, Long> {

    Boolean existsByPassword(int password);

    @Query("SELECT u FROM OtpToken o JOIN o.user u  WHERE o.password = ?1")
    Optional<User> findByPassword(int password);

    void deleteByPassword(int password);

    Boolean existsByUserEmail(String email);

    void deleteByUserEmail(String email);
}
