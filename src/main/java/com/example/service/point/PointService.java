package com.example.service.point;

import com.example.domain.member.Member;
import com.example.dto.request.PointModifyRequest;
import com.example.exception.CustomException;
import com.example.exception.ErrorCode;
import com.example.repository.member.MemberRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PointService {

    private final MemberRepository memberRepository;

    @Autowired
    public PointService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Transactional
    public void modifyPoint(PointModifyRequest pointModifyRequest){
        Member member = memberRepository.findById(pointModifyRequest.getId())
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        // 회원의 기존 포인트와 SignUpRequest에서 가져온 정보를 더하여 계산
        int totalPoint = member.getPoint() + pointModifyRequest.getPoint();
        member.setPoint(totalPoint);

    }
}
