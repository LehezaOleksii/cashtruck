package com.projects.oleksii.leheza.cashtruck.service.interfaces;

import com.projects.oleksii.leheza.cashtruck.domain.Currency;
import jakarta.validation.constraints.Size;

import java.util.List;
import java.util.Optional;

public interface CurrencyService {

    List<String> getCurrenciesShortNames();
}
