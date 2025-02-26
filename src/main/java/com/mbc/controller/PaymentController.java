package com.mbc.controller;

import com.mbc.dto.OrderItemDto;
import com.mbc.service.OrderService;
import com.mbc.service.PaymentService;
import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.exception.IamportResponseException;
import com.siot.IamportRestClient.response.IamportResponse;
import com.siot.IamportRestClient.response.Payment;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
@Log4j2
public class PaymentController {
    private final PaymentService paymentService;

    @PostMapping("/validation/{imp_uid}")
    public IamportResponse<Payment> validateIamport(@PathVariable String imp_uid) throws IamportResponseException, IOException {
        log.info("imp_uid: {}", imp_uid);
        log.info("validateIamport");
        return paymentService.validateIamport(imp_uid);
    }

//    ***/cart/orders에서 요청 처리***
//    @PostMapping("/order")
//    public ResponseEntity<String> processOrder(@RequestBody OrderItemDto orderItemDto) {
//        // 주문 정보를 로그에 출력
//        log.info("Received orders: {}", orderItemDto.toString());
//        // 성공적으로 받아들였다는 응답 반환
//        return ResponseEntity.ok(orderService.orders(orderItemDto));
//    }

//    ***orderService.cancelOrderWithPayment()에서 처리되도록 수정***
//    @PostMapping("/cancel/{imp_uid}")
//    public IamportResponse<Payment> cancelPayment(@PathVariable String imp_uid) throws IamportResponseException, IOException {
//        return paymentService.cancelPayment(imp_uid);
//    }
}
