package com.mbc.validation;

import com.mbc.dto.ItemFormDto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class TradeLocationValidator implements ConstraintValidator<TradeLocationValid, ItemFormDto> {

    @Override
    public boolean isValid(ItemFormDto itemFormDto, ConstraintValidatorContext context) {
        // "possible"일 때만 tradeLocation 필수 검사
        if ("possible".equals(itemFormDto.getTradeAvailable())) {
            if (itemFormDto.getTradeLocation() == null || itemFormDto.getTradeLocation().trim().isEmpty()) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate("직거래 위치정보는 필수 입력 값입니다.")
                        .addPropertyNode("tradeLocation")
                        .addConstraintViolation();
                return false;
            }
        }
        return true;
    }
}
