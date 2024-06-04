package com.example.service.point;

import com.example.api.ApiResult;
import com.example.config.jwt.TokenProvider;
import com.example.domain.member.Member;
import com.example.domain.payment.Payment;
import com.example.domain.point.Point;
import com.example.dto.request.AddPointRequest;
import com.example.dto.response.PaymentInfoResponse;
import com.example.dto.response.PointInfoResponse;
import com.example.exception.CustomException;
import com.example.exception.ErrorCode;
import com.example.repository.member.MemberRepository;
import com.example.repository.point.PointRepository;
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
public class PointService {

    private final MemberRepository memberRepository;
    private final PointRepository pointRepository;
    private final TokenProvider tokenProvider;

    public String getUserIdFromToken(HttpServletRequest request) {
        Authentication authentication = tokenProvider.getAuthentication(tokenProvider.resolveToken(request));
        Claims claims = tokenProvider.getTokenClaims(tokenProvider.resolveToken(request));
        return claims.getSubject();
    }

    /*
    포인트 추가
    */
    @Transactional
    public ApiResult<?> addPoint(AddPointRequest addPointRequest,HttpServletRequest request) {
        String userId = getUserIdFromToken(request);
        Member member = memberRepository.findByUserId(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        Integer totalPoint = member.getPoint() + addPointRequest.getPoint();
        member.setPoint(totalPoint);

        Point point = addPointRequest.toEntity(member, totalPoint);
        pointRepository.save(point);

        return ApiResult.success("포인트 수정이 완료되었습니다");
    }

    public ApiResult<?> getPoints(HttpServletRequest request) {
        String userId = getUserIdFromToken(request);
        Member member = memberRepository.findByUserId(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        List<Point> points = pointRepository.findByMemberOrderByCreateTimeDesc(member);
        List<PointInfoResponse> response = points.stream().map(point -> PointInfoResponse.builder()
                        .point(point.getPoint())
                        .totalPoint(point.getTotalPoint())
                        .createTime(point.getCreateTime().format(DateTimeFormatter.ISO_DATE_TIME))
                        .category(point.getCategory())
                        .item(point.getItem())
                        .build())
                .collect(Collectors.toList());

        return ApiResult.success(response);
    }
}
