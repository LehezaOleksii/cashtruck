package com.projects.oleksii.leheza.cashtruck.repository;

import com.projects.oleksii.leheza.cashtruck.domain.Currency;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CurrencyRepository extends JpaRepository<Currency, Long> {

    @Query("SELECT c.shortName FROM Currency c")
    List<String> findAllShortNames();

    Optional<Currency> findByShortName(String currencyShortName);

    @Query("SELECT c FROM Currency c WHERE c.code = :currencyCode")
    Optional<Currency> findByCode(int currencyCode);
}
