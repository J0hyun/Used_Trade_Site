package com.mbc.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = TradeLocationValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface TradeLocationValid {
    String message() default "직거래 위치정보는 필수 입력 값입니다.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
