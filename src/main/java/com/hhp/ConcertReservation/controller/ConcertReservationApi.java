package com.hhp.ConcertReservation.controller;

import com.hhp.ConcertReservation.controller.dto.RequestDto;
import com.hhp.ConcertReservation.controller.dto.ResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

public interface ConcertReservationApi {

	@Operation(summary = "대기열 추가 및 순번 조회", description = "사용자를 콘서트 예약 대기열에 추가하고 대기열 내 순번을 반환합니다.")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "성공적으로 대기열에 추가됨"),
			@ApiResponse(responseCode = "400", description = "잘못된 회원 ID")
	})
	ResponseEntity<ResponseDto.QueueResponse> addToQueue(RequestDto.PostQueue requestDto);

	@Operation(summary = "대기열 순번 확인", description = "주어진 토큰과 연관된 사용자의 대기열 순번을 확인합니다.")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "성공적으로 대기열 순번을 조회함"),
			@ApiResponse(responseCode = "400", description = "잘못된 토큰")
	})
	ResponseEntity<ResponseDto.QueueResponse> getQueuePosition(@RequestHeader String token);

	@Operation(summary = "좌석 예약", description = "사용자가 특정 콘서트 좌석을 예약합니다.")
	@ApiResponses({
			@ApiResponse(responseCode = "201", description = "좌석이 성공적으로 예약됨"),
			@ApiResponse(responseCode = "400", description = "잘못된 회원 ID 또는 좌석 ID")
	})
	ResponseEntity<ResponseDto.ReserveSeat> reserveSeat(RequestDto.reserveSeat requestDto);

	@Operation(summary = "예약 결제 처리", description = "예약된 좌석에 대한 결제를 처리합니다.")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "결제가 성공적으로 처리됨"),
			@ApiResponse(responseCode = "404", description = "예약을 찾을 수 없음")
	})
	ResponseEntity<ResponseDto.ProcessPayment> processPayment(RequestDto.processPayment requestDto);

	@Operation(summary = "계좌 잔액 충전", description = "사용자의 계좌 잔액을 충전합니다.")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "계좌가 성공적으로 충전됨"),
			@ApiResponse(responseCode = "400", description = "잘못된 금액 또는 계좌 ID")
	})
	ResponseEntity<ResponseDto.ChargeBalance> chargeBalance(RequestDto.ChargeBalance requestDto);

	@Operation(summary = "계좌 잔액 조회", description = "사용자의 계좌 잔액을 조회합니다.")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "성공적으로 계좌 잔액을 조회함"),
			@ApiResponse(responseCode = "404", description = "계좌를 찾을 수 없음")
	})
	ResponseEntity<ResponseDto.GetBalance> getBalance(@Parameter(description = "잔액을 조회할 계좌의 ID") @PathVariable Long accountId);

	@Operation(summary = "예약 가능한 콘서트 일정 조회", description = "특정 날짜 이후의 예약 가능한 콘서트 일정을 조회합니다.")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "성공적으로 예약 가능한 콘서트 일정을 조회함")
	})
	ResponseEntity<ResponseDto.GetAvailableConcertSchedules> getAvailableConcertSchedules(@Parameter(description = "해당 날짜 이후의 예약 가능한 일정을 조회할 날짜와 시간") @PathVariable LocalDateTime dateTime);

	@Operation(summary = "콘서트 일정의 예약 가능한 좌석 조회", description = "특정 콘서트 일정에 대한 예약 가능한 좌석을 조회합니다.")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "성공적으로 예약 가능한 좌석을 조회함"),
			@ApiResponse(responseCode = "404", description = "콘서트 일정을 찾을 수 없음")
	})
	ResponseEntity<ResponseDto.GetAvailableSeat> getAvailableSeat(@Parameter(description = "좌석을 조회할 콘서트 일정의 ID") @PathVariable Long concertScheduleId);
}
