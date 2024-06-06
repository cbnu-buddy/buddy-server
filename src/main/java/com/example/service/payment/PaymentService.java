package com.example.service.payment;

import com.example.api.ApiResult;
import com.example.config.jwt.TokenProvider;
import com.example.domain.member.Member;
import com.example.domain.party.Party;
import com.example.domain.party.PartyMember;
import com.example.domain.payment.Payment;
import com.example.dto.request.AddPaymentRequest;
import com.example.dto.response.PaymentInfoResponse;
import com.example.dto.response.PaymentPreviewForLeaderResponse;
import com.example.dto.response.PaymentPreviewForUserResponse;
import com.example.exception.CustomException;
import com.example.exception.ErrorCode;
import com.example.repository.member.MemberRepository;
import com.example.repository.party.PartyMemberRepository;
import com.example.repository.party.PartyRepository;
import com.example.repository.payment.PaymentRepository;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class PaymentService {
    private final MemberRepository memberRepository;
    private final PaymentRepository paymentRepository;
    private final PartyRepository partyRepository;
    private final PartyMemberRepository partyMemberRepository;

    private final TokenProvider tokenProvider;

    public String getUserIdFromToken(HttpServletRequest request) {
        Authentication authentication = tokenProvider.getAuthentication(tokenProvider.resolveToken(request));
        Claims claims = tokenProvider.getTokenClaims(tokenProvider.resolveToken(request));
        return claims.getSubject();
    }

    /*
    결제 정보 추가
     */
    @Transactional
    public ApiResult<?> addPayment(AddPaymentRequest addPaymentRequest, HttpServletRequest request) {
        String userId = getUserIdFromToken(request);
        Member member = memberRepository.findByUserId(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        if (paymentRepository.findByPaymentKey(addPaymentRequest.getPaymentKey()).isPresent()) {
            throw new CustomException(ErrorCode.ALREADY_PAYMENT_KEY);
        }

        Payment payment = addPaymentRequest.toEntity(member);
        paymentRepository.save(payment);

        return ApiResult.success("결제 정보 추가가 성공적으로 처리되었습니다.");
    }

    /*
    결제/이체 내역 정보 목록 조회
    */
    public ApiResult<?> getPayments(HttpServletRequest request) {
        String userId = getUserIdFromToken(request);
        Member member = memberRepository.findByUserId(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        List<Payment> payments = paymentRepository.findByMemberOrderByCreateTimeDesc(member);
        List<PaymentInfoResponse> response = payments.stream().map(payment -> PaymentInfoResponse.builder()
                        .category(payment.getCategory())
                        .item(payment.getItem())
                        .amount(payment.getAmount())
                        .createTime(payment.getCreateTime().format(DateTimeFormatter.ISO_DATE_TIME))
                        .build())
                .collect(Collectors.toList());

        return ApiResult.success(response);
    }

    public ApiResult<?> getPaymentsPreviewForLeader(HttpServletRequest request) {
        String userId = getUserIdFromToken(request);
        Member member = memberRepository.findByUserId(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        List<Party> parties = partyRepository.findByMemberAndProgressStatus(member, true);

        List<PaymentPreviewForLeaderResponse.PlanInfo> planInfos = parties.stream()
                .map(party -> {
                    int recLimit = party.getRecLimit();
                    if (recLimit == 0) {
                        throw new IllegalArgumentException("Recommendation limit cannot be zero.");
                    }
                    int totalMonthlyFee = (int) Math.ceil((double) party.getPlan().getMonthlyFee() / (recLimit + 1) * recLimit);
                    return PaymentPreviewForLeaderResponse.PlanInfo.builder()
                            .name(party.getPlan().getPlanName())
                            .totalMonthlyFee(totalMonthlyFee)
                            .build();
                })
                .collect(Collectors.toList());

        int totalAmount = planInfos.stream()
                .mapToInt(PaymentPreviewForLeaderResponse.PlanInfo::getTotalMonthlyFee)
                .sum();

        PaymentPreviewForLeaderResponse response = PaymentPreviewForLeaderResponse.builder()
                .totalAmount(totalAmount)
                .plans(planInfos)
                .build();

        return ApiResult.success(response);
    }

    public ApiResult<?> getPaymentsPreviewForUser(HttpServletRequest request) {
        String userId = getUserIdFromToken(request);
        Member member = memberRepository.findByUserId(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        List<PartyMember> partyMembers = partyMemberRepository.findByMember(member);

        List<PaymentPreviewForUserResponse.PartyInfo> partyInfos = partyMembers.stream()
                .map(partyMember -> {
                    Party party = partyMember.getParty();
                    int recLimit = party.getRecLimit();
                    if (recLimit == 0) {
                        throw new IllegalArgumentException("Recommendation limit cannot be zero.");
                    }
                    int individualMonthlyFee;
                    if (party.getMember().getMemberId().equals(member.getMemberId())) {

                        int totalMonthlyFee = party.getPlan().getMonthlyFee();
                        int totalIndividualFees = (int) Math.ceil((double) totalMonthlyFee / (recLimit + 1)) * recLimit;
                        individualMonthlyFee = totalMonthlyFee - totalIndividualFees;
                    } else {
                        individualMonthlyFee = (int) Math.ceil((double) party.getPlan().getMonthlyFee() / (recLimit + 1));
                    }
                    return PaymentPreviewForUserResponse.PartyInfo.builder()
                            .name(party.getPlan().getPlanName())
                            .monthlyFee(individualMonthlyFee)
                            .build();
                })
                .collect(Collectors.toList());

        int totalAmount = partyInfos.stream()
                .mapToInt(PaymentPreviewForUserResponse.PartyInfo::getMonthlyFee)
                .sum();

        PaymentPreviewForUserResponse response = PaymentPreviewForUserResponse.builder()
                .totalAmount(totalAmount)
                .parties(partyInfos)
                .build();

        return ApiResult.success(response);
    }
}


