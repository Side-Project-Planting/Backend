package com.example.planservice.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public enum ErrorCode {
    // 공통
    SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버에서 에러가 발생했습니다. 관리자에게 문의하세요"),
    REQUEST_CONFLICT(HttpStatus.CONFLICT, "요청들간 충돌이 발생했습니다. 다시 시도해 주세요"),
    AUTHORIZATION_FAIL(HttpStatus.FORBIDDEN, "해당되는 권한이 없습니다"),

    // 멤버
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "멤버가 존재하지 않습니다"),
    MEMBER_ALREADY_REGISTERED(HttpStatus.BAD_REQUEST, "이미 가입된 회원입니다"),

    // 플랜
    PLAN_NOT_FOUND(HttpStatus.NOT_FOUND, "플랜이 존재하지 않습니다"),
    PLAN_TAB_MISMATCH(HttpStatus.BAD_REQUEST, "플랜과 탭 사이 관계가 없습니다"),
    MEMBER_NOT_FOUND_IN_PLAN(HttpStatus.FORBIDDEN, "플랜에 소속되지 않은 멤버입니다"),
    MEMBER_ALREADY_IN_PLAN(HttpStatus.BAD_REQUEST, "이미 플랜에 소속된 멤버입니다"),

    // 탭
    TAB_NOT_FOUND(HttpStatus.NOT_FOUND, "탭을 찾을 수 없습니다"),
    TAB_SIZE_INVALID(HttpStatus.BAD_REQUEST, "플랜에 속할 수 있는 탭의 개수가 잘못되었습니다"),
    TAB_NAME_DUPLICATE(HttpStatus.BAD_REQUEST, "탭 이름이 중복되었습니다"),
    TAB_NOT_FOUND_IN_PLAN(HttpStatus.NOT_FOUND, "현재 플랜에서 해당되는 탭을 찾을 수 없습니다"),
    TAB_ORDER_FIXED(HttpStatus.FORBIDDEN, "해당 탭은 순서를 변경할 수 없습니다"),
    TAB_CANNOT_DELETE(HttpStatus.FORBIDDEN, "해당 탭은 삭제할 수 없습니다"),
    TARGET_TAB_SAME_AS_NEW_PREV(HttpStatus.BAD_REQUEST, "target과 newPrev는 동일할 수 없습니다"),

    // 태스크
    TASK_NOT_FOUND(HttpStatus.NOT_FOUND, "태스크가 존재하지 않습니다"),
    TASK_DATE_INVALID(HttpStatus.BAD_REQUEST, "태스크의 시작은 끝보다 빨라야 합니다"),

    // 라벨
    LABEL_NAME_DUPLICATE(HttpStatus.BAD_REQUEST, "라벨 이름이 중복되었습니다"),
    LABEL_NOT_FOUND(HttpStatus.NOT_FOUND, "라벨이 존재하지 않습니다");

    private final HttpStatus status;
    private final String message;

    ErrorCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }
}
