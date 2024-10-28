package com.hhp.ConcertReservation.common.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.NoSuchElementException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<String> handleIllegalArgument(IllegalArgumentException ex) {
		log.warn("잘못된 요청: {}", ex.getMessage(), ex);
		return new ResponseEntity<>("잘못된 요청입니다: " + ex.getMessage(), HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(IllegalStateException.class)
	public ResponseEntity<String> handleIllegalState(IllegalStateException ex) {
		log.error("시스템 상태 오류: {}", ex.getMessage(), ex);
		return new ResponseEntity<>("시스템 상태가 올바르지 않습니다: " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler(NoSuchElementException.class)
	public ResponseEntity<String> handleNoSuchElement(NoSuchElementException ex) {
		log.info("리소스 조회 실패: {}", ex.getMessage(), ex);
		return new ResponseEntity<>("요청하신 리소스를 찾을 수 없습니다: " + ex.getMessage(), HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<String> handleGeneralException(Exception ex) {
		log.error("서버 내부 오류 발생: {}", ex.getMessage(), ex);
		return new ResponseEntity<>("서버 내부 오류가 발생했습니다: " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
	}
}
