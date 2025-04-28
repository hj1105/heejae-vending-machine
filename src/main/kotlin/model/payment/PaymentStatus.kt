package org.example.model.payment

enum class PaymentStatus {
    IDLE,       // 대기 중
    PROCESSING, // 처리 중
    COMPLETED,  // 완료됨
    FAILED      // 실패
}