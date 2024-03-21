package com.projects.oleksii.leheza.cashtruck.repository;

import com.projects.oleksii.leheza.cashtruck.domain.BankCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BankCardRepository extends JpaRepository<BankCard,Long> {

    @Query("SELECT bc FROM BankCard bc WHERE bc.cardNumber=?1")
    Optional<BankCard> findCardByNumber(String bankCard);

    @Query("SELECT u.saving.bankCards FROM User u WHERE u.id = :userId")
    List<Optional<BankCard>> getBankCardsByUserId(@Param("userId") Long userId);


}
