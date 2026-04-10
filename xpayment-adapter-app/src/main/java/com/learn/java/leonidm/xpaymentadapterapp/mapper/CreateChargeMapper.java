package com.learn.java.leonidm.xpaymentadapterapp.mapper;

import com.iprody.xpayment.app.api.model.ChargeResponse;
import com.iprody.xpayment.app.api.model.CreateChargeRequest;
import com.learn.java.leonidm.xpaymentadapterapp.model.dto.CreateChargeRequestDto;
import com.learn.java.leonidm.xpaymentadapterapp.model.dto.CreateChargeResponseDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CreateChargeMapper {
    CreateChargeRequest toCreateChargeRequest(CreateChargeRequestDto createChargeRequestDto);

    CreateChargeResponseDto toCreateChargeResponseDto(ChargeResponse chargeResponse);
}
