package org.example.model.state

enum class VendingMachineState {
    IDLE,                // 대기 상태
    ACCEPTING_MONEY,     // 현금 투입 중
    SELECTING_BEVERAGE,  // 음료 선택 중
    PROCESSING_PAYMENT,  // 결제 처리 중
    DISPENSING_PRODUCT,  // 상품 배출 중
    ERROR                // 오류 상태
}