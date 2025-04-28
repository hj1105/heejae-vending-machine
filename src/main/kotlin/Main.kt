package org.example

import org.example.service.VendingMachineService
import org.example.model.payment.PaymentMethod
import org.example.model.payment.Money

fun main() {
    println("===== 자판기 시스템 시작 =====")

    val vendingMachine = VendingMachineService()

    vendingMachine.displayStatus()

    println("1. 현금 결제로 콜라 구매")
    vendingMachine.selectPaymentMethod(PaymentMethod.CASH)
    vendingMachine.insertMoney(Money.BILL_1000)
    vendingMachine.insertMoney(Money.COIN_500)
    vendingMachine.selectBeverage("cola")

    vendingMachine.displayStatus()

    println("\n2. 카드 결제로 물 구매")
    vendingMachine.selectPaymentMethod(PaymentMethod.CARD)
    vendingMachine.insertCard("1234567890123456", "water")

    vendingMachine.displayStatus()

    println("\n3. 재고 부족 상황 테스트")
    val coffeeStockStatus = vendingMachine.refillBeverage("coffee", -10)
    println("재고 감소 작업 성공: $coffeeStockStatus")

    vendingMachine.selectPaymentMethod(PaymentMethod.CASH)
    vendingMachine.insertMoney(Money.BILL_1000)
    vendingMachine.selectBeverage("coffee")

    vendingMachine.refillBeverage("coffee", 5)
    vendingMachine.selectBeverage("coffee")

    vendingMachine.displayStatus()

    println("\n4. 금액 부족 상황 테스트")
    vendingMachine.selectPaymentMethod(PaymentMethod.CASH)
    vendingMachine.insertMoney(Money.COIN_500)
    vendingMachine.selectBeverage("cola")

    vendingMachine.insertMoney(Money.COIN_500)
    vendingMachine.insertMoney(Money.COIN_100)
    vendingMachine.selectBeverage("cola")

    vendingMachine.displayStatus()

    println("\n5. 거스름돈 계산 테스트")
    vendingMachine.selectPaymentMethod(PaymentMethod.CASH)
    vendingMachine.insertMoney(Money.BILL_5000)
    vendingMachine.selectBeverage("water")

    vendingMachine.displayStatus()

    println("\n6. 현금 결제 취소 테스트")
    vendingMachine.selectPaymentMethod(PaymentMethod.CASH)
    vendingMachine.insertMoney(Money.BILL_1000)
    vendingMachine.cancelAndReturnCash()

    vendingMachine.displayStatus()

    println("\n7. 카드 결제 실패 테스트")
    vendingMachine.selectPaymentMethod(PaymentMethod.CARD)
    vendingMachine.insertCard("1234567890123457", "cola")

    vendingMachine.displayStatus()

    println("\n8. 매출 통계 확인")
    vendingMachine.getSalesReport()

    println("\n===== 자판기 시스템 종료 =====")
}