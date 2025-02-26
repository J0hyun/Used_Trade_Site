function handleOrderAndPayment() {
    // CSRF 토큰 가져오기
    var token = $("meta[name='_csrf']").attr("content");
    var header = $("meta[name='_csrf_header']").attr("content");

    // Step 1: 주문 내역 준비
    var orderData = new Object();
    var dataList = [];

    $("input[name=cartChkBox]:checked").each(function () {
        var cartItemId = $(this).val();
        console.log(cartItemId)
        var data = new Object();
        data["cartItemId"] = cartItemId;
        dataList.push(data);
    });

    if (dataList.length === 0) {
        alert("주문할 상품을 선택해주세요.");
        return;
    }

    orderData['cartOrderDtoList'] = dataList;

    // Step 2: 결제 요청
    let merchant_uid = "O_" + new Date().getTime(); // 주문번호 생성
    IMP.init("imp21758336"); // 아임포트 초기화

    IMP.request_pay({
        pg: "html5_inicis",
        pay_method: "card",
        merchant_uid: merchant_uid, // 주문 고유 번호
        name: $('#itemNm').text(),
        amount: $("#orderTotalPrice").attr('data-total'),  // 총 결제 금액
        buyer_email: '',
        buyer_name: $("#name").val(),
        buyer_tel: $("#phone").val(),
        buyer_addr: $("#address").val(),
    }, function (rsp) {
        console.log(rsp);
        if (rsp.success) {
            // 결제 성공 시
            $.ajax({
                type: 'POST',
                url: 'api/payment/validation/' + rsp.imp_uid,
                beforeSend: function (xhr) {
                    xhr.setRequestHeader(header, token); // CSRF 헤더 설정
                }
            }).done(function (data) {
                if ($("#orderTotalPrice").attr('data-total') == data.response.amount) {
                    // Step 3: 주문 내역 저장
                    orderData["impUid"] = rsp.imp_uid;
                    orderData["merchantUid"] = rsp.merchant_uid;

                    $.ajax({
                        url: "cart/orders",
                        type: "POST",
                        contentType: "application/json",
                        data: JSON.stringify(orderData),
                        beforeSend: function (xhr) {
                            xhr.setRequestHeader(header, token); // CSRF 헤더 설정
                        }
                    }).done(function (res) {
                        console.log("res", res);
                        console.log("rsp", rsp);
                        alert("결제가 완료되었습니다.\n주문 번호: " + rsp.merchant_uid);
                        location.href = '/orders'; // 주문 내역 페이지로 이동
                    }).fail(function (error) {
                        alert("주문 정보 저장에 실패했습니다.");
                        console.error(error);
                    });
                } else {
                    alert("결제 금액이 일치하지 않습니다.");
                }
            }).fail(function (error) {
                alert("결제 검증에 실패하였습니다.");
                console.error(error);
            });
        } else {
            alert("결제에 실패하였습니다. " + rsp.error_msg);
        }
    });
}
