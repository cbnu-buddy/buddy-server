package com.example.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // 400 BAD REQUEST
    ID_PASSWORD_NOT_MATCH(HttpStatus.BAD_REQUEST, "아이디 / 비밀번호가 일치하지 않습니다."),
    PASSWORD_NOT_MATCH(HttpStatus.BAD_REQUEST, "기존 비밀번호가 일치하지 않습니다."),
    INVALID_REC_LIMIT(HttpStatus.BAD_REQUEST, "모집 인원은 0보다 커야 합니다."),
    EXCEEDS_MAX_MEMBER_NUM(HttpStatus.BAD_REQUEST, "모집 인원이 요금제의 최대 사용자 수를 초과할 수 없습니다."),
    PARTY_ALREADY_STARTED(HttpStatus.BAD_REQUEST,"시작된 파티의 모집 인원은 변경할 수 없습니다."),


    // 401 UNAUTHORIZED
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "인증 정보가 없습니다. 다시 로그인 하세요"),

    // 403 FORBIDDEN
    MEMBER_NOT_PERMISSION_YET(HttpStatus.FORBIDDEN, "아직 승인되지 않은 회원입니다. 관리자에게 문의하세요"),
    MEMBER_NO_PERMISSION(HttpStatus.FORBIDDEN, "해당 회원에 권한이 없습니다"),

    // 404 NOT FOUND
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 회원이 없습니다."),
    MEMBER_LOGIN_ID_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 이메일에 해당하는 회원이 없습니다."),
    PlAN_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 플랜이 없습니다."),
    PARTY_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 파티가 없습니다."),
    NOT_JOINED_PARTY(HttpStatus.NOT_FOUND, "가입한 파티가 없습니다."),
    PARTY_FULL(HttpStatus.NOT_FOUND, "이미 해당 파티의 정원이 모두 차있습니다."),

    // 409 CONFLICT
    ALREADY_EXIST_USERID(HttpStatus.CONFLICT, "이미 존재하는 아이디입니다."),
    ALREADY_EXIST_EMAIL(HttpStatus.CONFLICT, "이미 존재하는 이메일입니다."),
    ALREADY_EXIST_USERNAME(HttpStatus.CONFLICT, "이미 존재하는 이름입니다."),
    ALREADY_JOINED_PARTY(HttpStatus.CONFLICT, "이미 파티에 가입 되어 있습니다."),
    ALREADY_PAYMENT_KEY(HttpStatus.CONFLICT, "이미 존재하는 결제 키입니다.");


    private final HttpStatus httpStatus;
    private final String msg;
}
