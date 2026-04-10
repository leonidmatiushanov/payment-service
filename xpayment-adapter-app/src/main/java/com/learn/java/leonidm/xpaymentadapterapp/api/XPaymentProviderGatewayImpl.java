package com.learn.java.leonidm.xpaymentadapterapp.api;

import com.iprody.xpayment.app.api.client.DefaultApi;
import com.iprody.xpayment.app.api.model.ChargeResponse;
import com.iprody.xpayment.app.api.model.CreateChargeRequest;
import com.learn.java.leonidm.xpaymentadapterapp.mapper.CreateChargeMapper;
import com.learn.java.leonidm.xpaymentadapterapp.model.dto.CreateChargeRequestDto;
import com.learn.java.leonidm.xpaymentadapterapp.model.dto.CreateChargeResponseDto;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;

import java.util.UUID;

@Service
class XPaymentProviderGatewayImpl implements XPaymentProviderGateway {
    private final DefaultApi defaultApi;
    private final CreateChargeMapper createChargeMapper;

    public XPaymentProviderGatewayImpl(DefaultApi defaultApi, CreateChargeMapper createChargeMapper) {
        this.defaultApi = defaultApi;
        this.createChargeMapper = createChargeMapper;
    }

    @Override
    public CreateChargeResponseDto createCharge(CreateChargeRequestDto createChargeRequest) throws RestClientException {
        CreateChargeRequest createChargeRq = createChargeMapper.toCreateChargeRequest(createChargeRequest);
        ChargeResponse charge = defaultApi.createCharge(createChargeRq);
        return createChargeMapper.toCreateChargeResponseDto(charge);
    }

    @Override
    public CreateChargeResponseDto retrieveCharge(UUID id) throws RestClientException {
        ChargeResponse chargeResponse = defaultApi.retrieveCharge(id);
        return createChargeMapper.toCreateChargeResponseDto(chargeResponse);
    }
}
