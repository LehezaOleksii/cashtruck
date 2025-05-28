package com.projects.oleksii.leheza.cashtruck.repository;

import com.projects.oleksii.leheza.cashtruck.domain.BankCard;
import com.projects.oleksii.leheza.cashtruck.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BankCardRepository extends JpaRepository<BankCard, Long> {

    @Query("SELECT bc FROM BankCard bc JOIN bc.user u WHERE u.id = :userId")
    List<BankCard> getBankCardsByUserId(@Param("userId") Long userId);

    @Query("SELECT bc FROM BankCard bc JOIN bc.user u WHERE bc.cardNumber=:cardNumber AND u.id=:userId")
    Optional<BankCard> findCardByNumberAndUserId(String cardNumber, Long userId);

    @Query("SELECT  bc FROM BankCard bc JOIN bc.user u WHERE u.id =:userId AND bc.cardNumber=:cardNumber")
    Optional<BankCard> findByCardNumber(String cardNumber, Long userId);

    List<BankCard> user(User user);
}
