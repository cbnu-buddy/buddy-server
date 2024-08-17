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
    PARTY_ALREADY_STARTED(HttpStatus.BAD_REQUEST, "시작된 파티의 모집 인원은 변경할 수 없습니다."),
    INVALID_SERVICE_ID(HttpStatus.BAD_REQUEST, "존재하지 않는 서비스입니다."),
    LEADER_CANNOT_LEAVE_PARTY(HttpStatus.BAD_REQUEST, "파티장은 파티를 탈퇴할 수 없습니다."),
    
    // 401 UNAUTHORIZED
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "인증 정보가 없습니다. 다시 로그인 하세요"),

    // 403 FORBIDDEN
    MEMBER_NOT_PERMISSION_YET(HttpStatus.FORBIDDEN, "아직 승인되지 않은 회원입니다. 관리자에게 문의하세요"),
    MEMBER_NO_PERMISSION(HttpStatus.FORBIDDEN, "해당 회원에 권한이 없습니다"),
    UNAUTHORIZED_ACCESS(HttpStatus.FORBIDDEN, "접근 권한이 없습니다."),

    // 404 NOT FOUND
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 회원이 없습니다."),
    MEMBER_LOGIN_ID_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 이메일에 해당하는 회원이 없습니다."),
    PLAN_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 플랜이 없습니다."),
    PARTY_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 파티가 없습니다."),
    TAG_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 태그가 없습니다."),
    NOT_JOINED_PARTY(HttpStatus.NOT_FOUND, "가입한 파티가 없습니다."),
    PARTY_FULL(HttpStatus.NOT_FOUND, "이미 해당 파티의 정원이 모두 차있습니다."),
    POST_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 게시글이 없습니다."),
    COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 댓글이 없습니다."),
    REPLY_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 답글이 없습니다."),
    SUBSCRIPTION_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 태그를 구독하고 있지 않습니다"),
    SEARCH_RESULTS_NOT_FOUND(HttpStatus.NOT_FOUND, "검색 결과가 없습니다."),

    // 409 CONFLICT
    ALREADY_EXIST_USERID(HttpStatus.CONFLICT, "이미 존재하는 아이디입니다."),
    ALREADY_EXIST_EMAIL(HttpStatus.CONFLICT, "이미 존재하는 이메일입니다."),
    ALREADY_EXIST_USERNAME(HttpStatus.CONFLICT, "이미 존재하는 이름입니다."),
    ALREADY_JOINED_PARTY(HttpStatus.CONFLICT, "이미 파티에 가입 되어 있습니다."),
    ALREADY_SUBSCRIBED(HttpStatus.CONFLICT, "이미 해당 태그를 구독 중입니다."),
    ALREADY_ON_NOTIFY(HttpStatus.CONFLICT, "이미 해당 태그에 대한 신규 게시글 알림이 켜져있습니다."),
    ALREADY_OFF_NOTIFY(HttpStatus.CONFLICT, "이미 해당 태그에 대한 신규 게시글 알림이 꺼져있습니다."),
    ALREADY_LIKED(HttpStatus.CONFLICT, "이미 좋아요를 누른 게시글입니다."),
    ALREADY_COMMENT_LIKED(HttpStatus.CONFLICT, "이미 좋아요를 누른 댓글입니다."),
    ALREADY_REPLY_LIKED(HttpStatus.CONFLICT, "이미 좋아요를 누른 답글입니다."),
    ALREADY_PAYMENT_KEY(HttpStatus.CONFLICT, "이미 존재하는 결제 키입니다."),

    // 500 INTERNAL_SERVER_ERROR
    UPLOAD_DIRECTORY_CREATION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "업로드 디렉토리를 생성할 수 없습니다."),
    FILE_UPLOAD_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "파일 업로드에 실패했습니다.");


    private final HttpStatus httpStatus;
    private final String msg;
}