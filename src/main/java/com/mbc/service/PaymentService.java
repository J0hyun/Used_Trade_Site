package com.mbc.service;

import com.mbc.dto.OrderItemDto;
import com.mbc.entity.Order;
import com.mbc.entity.OrderItem;
import com.mbc.repository.OrderRepository;
import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.request.CancelData;
import com.siot.IamportRestClient.response.IamportResponse;
import com.siot.IamportRestClient.response.Payment;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Log4j2
public class PaymentService {
    private final IamportClient iamportClient;
    private final OrderRepository orderRepository;

    /**
     * 아임포트 서버로부터 결제 정보를 검증
     * @param imp_uid
     */
    public IamportResponse<Payment> validateIamport(String imp_uid) {
        try {
            IamportResponse<Payment> payment = iamportClient.paymentByImpUid(imp_uid);
            log.info("결제 요청 응답. 결제 내역 - 주문 번호: {}", payment.getResponse());
            return payment;
        } catch (Exception e) {
            log.info(e.getMessage());
            return null;
        }
    }

//    /**
//     * 주문 정보 저장
//     * @param orderDto
//     * @return
//     */
//    public String saveOrder(OrderDto orderDto){
//        try {
//            orderRepository.save(orderDto.toEntity());
//            return "주문 정보가 성공적으로 저장되었습니다.";
//        } catch (Exception e) {
//            log.info(e.getMessage());
//            cancelPayment(orderDto.getImpUid());
//            return "주문 정보 저장에 실패했습니다.";
//        }
//    }

    /**
     * 아임포트 서버로부터 결제 취소 요청
     *
     * @param imp_uid
     * @return
     */
    public IamportResponse<Payment> cancelPayment(String imp_uid) {
        try {
            CancelData cancelData = new CancelData(imp_uid, true);
            IamportResponse<Payment> payment = iamportClient.cancelPaymentByImpUid(cancelData);
            return payment;
        } catch (Exception e) {
            log.info(e.getMessage());
            return null;
        }
    }
}
