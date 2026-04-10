package com.learn.java.leonidm.xpaymentadapterapp.api;

import com.learn.java.leonidm.xpaymentadapterapp.model.dto.CreateChargeRequestDto;
import com.learn.java.leonidm.xpaymentadapterapp.model.dto.CreateChargeResponseDto;
import org.springframework.web.client.RestClientException;

import java.util.UUID;

public interface XPaymentProviderGateway {

    CreateChargeResponseDto createCharge(CreateChargeRequestDto createChargeRequest) throws RestClientException;

    CreateChargeResponseDto retrieveCharge(UUID id) throws RestClientException;
}
