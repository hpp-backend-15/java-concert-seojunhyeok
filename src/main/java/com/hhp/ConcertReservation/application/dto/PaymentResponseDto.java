package com.hhp.ConcertReservation.application.dto;

public record PaymentResponseDto(String paymentId, String reservationId, String userId, double amount) {}