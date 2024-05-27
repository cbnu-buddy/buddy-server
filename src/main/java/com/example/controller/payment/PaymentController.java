package com.example.controller.payment;

import com.example.api.ApiResult;
import com.example.dto.request.AddPaymentRequest;
import com.example.service.payment.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Tag(name = "멤버 결제 API")
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/private/member")
public class PaymentController {
    private final PaymentService paymentService;

    /*
    결제 정보 추가
    */
    @PostMapping("/payment")
    @Operation(summary = "결제 정보 추가", description = "")
    public ApiResult<?> addPayment(@RequestBody AddPaymentRequest addPaymentRequest,
                                    HttpServletRequest request) {
        return paymentService.addPayment(addPaymentRequest, request);
    }

    /*
    결제 내역 정보 목록 조회
    */
    @GetMapping("/payments")
    @Operation(summary = "결제 내역 정보 목록 조회", description = "")
    public ApiResult<?> getPayments(HttpServletRequest request) {

        return paymentService.getPayments(request);
    }
}
