package org.example.service

import org.example.model.beverage.Beverage
import org.example.model.beverage.BeverageFactory
import org.example.model.inventory.Inventory
import org.example.model.payment.Money
import org.example.model.payment.PaymentMethod
import org.example.model.payment.PaymentResult
import org.example.model.sales.SaleHistory
import org.example.model.state.VendingMachineState

class VendingMachineService {

    private val beverageInventory = Inventory<Beverage>()
    private val cashProcessor = CashPaymentService()
    private val cardProcessor = CardPaymentService()
    private val salesRecordManager = SaleHistoryService()

    private var currentPaymentMethod: PaymentMethod? = null

    private var state = VendingMachineState.IDLE

    init {
        BeverageFactory.getAllBeverages().forEach { beverage ->
            beverageInventory.addItem(beverage, 10)
        }
    }

    fun displayStatus() {
        println("\n===== 자판기 상태 =====")
        println("현재 상태: ${state.name}")

        println("\n음료 목록:")
        val beverages = BeverageFactory.getAllBeverages()
        beverages.forEach { beverage ->
            val stock = beverageInventory.getQuantity(beverage)
            println("  ${beverage.name}: ${beverage.price}원 (재고: ${stock}개)")
        }

        if (currentPaymentMethod == PaymentMethod.CASH) {
            println("\n현재 투입 금액: ${cashProcessor.getInsertedAmount()}원")
        }

        println("=======================\n")
    }

    fun selectPaymentMethod(method: PaymentMethod): Boolean {
        currentPaymentMethod = method
        state = when (method) {
            PaymentMethod.CASH -> VendingMachineState.ACCEPTING_MONEY
            PaymentMethod.CARD -> VendingMachineState.SELECTING_BEVERAGE
        }

        println("${method.description} 결제가 선택되었습니다.")
        return true
    }

    fun insertMoney(money: Money): Boolean {
        if (currentPaymentMethod != PaymentMethod.CASH) {
            println("현금 결제가 아닙니다.")
            return false
        }

        state = VendingMachineState.ACCEPTING_MONEY
        val totalAmount = cashProcessor.insertMoney(money)
        println("${money.description}원이 투입되었습니다. 현재 총 투입 금액: ${totalAmount}원")

        return true
    }

    fun selectBeverage(beverageId: String): Boolean {
        val beverage = BeverageFactory.createBeverage(beverageId)
            ?: run {
                println("존재하지 않는 음료입니다.")
                return false
            }

        if (!beverageInventory.hasItem(beverage)) {
            println("${beverage.name}의 재고가 부족합니다.")
            return false
        }

        state = VendingMachineState.PROCESSING_PAYMENT

        val paymentResult = when (currentPaymentMethod) {
            PaymentMethod.CASH -> processCashPayment(beverage)
            PaymentMethod.CARD -> {
                println("카드를 투입해주세요.")
                return true
            }
            null -> {
                println("결제 방식을 먼저 선택해주세요.")
                state = VendingMachineState.IDLE
                return false
            }
        }

        return if (paymentResult.success) {
            dispenseBeverage(beverage)
            offerReceipt(beverage)
            state = VendingMachineState.IDLE
            true
        } else {
            println(paymentResult.message)
            state = when (currentPaymentMethod) {
                PaymentMethod.CASH -> VendingMachineState.ACCEPTING_MONEY
                PaymentMethod.CARD -> VendingMachineState.SELECTING_BEVERAGE
                null -> VendingMachineState.IDLE
            }
            false
        }
    }

    fun insertCard(cardNumber: String, beverageId: String): Boolean {
        if (currentPaymentMethod != PaymentMethod.CARD) {
            println("카드 결제가 아닙니다.")
            return false
        }

        // 음료 존재 여부 확인
        val beverage = BeverageFactory.createBeverage(beverageId)
            ?: run {
                println("존재하지 않는 음료입니다.")
                return false
            }

        if (!beverageInventory.hasItem(beverage)) {
            println("${beverage.name}의 재고가 부족합니다.")
            return false
        }

        state = VendingMachineState.PROCESSING_PAYMENT
        println("카드 승인 요청 중...")

        val paymentResult = cardProcessor.processPayment(cardNumber, beverage.price)

        return if (paymentResult.success) {
            println(paymentResult.message)
            dispenseBeverage(beverage)
            offerReceipt(beverage)
            state = VendingMachineState.IDLE
            true
        } else {
            println(paymentResult.message)
            state = VendingMachineState.SELECTING_BEVERAGE
            false
        }
    }

    private fun processCashPayment(beverage: Beverage): PaymentResult {
        // 금액 확인
        val insertedAmount = cashProcessor.getInsertedAmount()
        if (insertedAmount < beverage.price) {
            return PaymentResult(
                success = false,
                message = "금액이 부족합니다. ${beverage.price - insertedAmount}원이 더 필요합니다."
            )
        }

        val paymentResult = cashProcessor.processPayment(beverage.price)

        if (paymentResult.success && paymentResult.change.isNotEmpty()) {
            println("거스름돈 정보:")
            for ((money, count) in paymentResult.change) {
                println("  ${money.description} x ${count}개")
            }
        }

        return paymentResult
    }

    private fun dispenseBeverage(beverage: Beverage): Boolean {
        state = VendingMachineState.DISPENSING_PRODUCT

        beverageInventory.removeItem(beverage)

        val sale = SaleHistory(
            beverage = beverage,
            paymentMethod = currentPaymentMethod!!,
            amount = beverage.price
        )
        salesRecordManager.addSale(sale)

        println("${beverage.name}이(가) 나왔습니다. 맛있게 드세요!")
        return true
    }

    private fun offerReceipt(beverage: Beverage): Boolean {
        val lastSale = salesRecordManager.getAllSales().lastOrNull() ?: return false

        println(lastSale.generateReceipt())
        return true
    }

    fun cancelAndReturnCash(): Boolean {
        if (currentPaymentMethod != PaymentMethod.CASH || cashProcessor.getInsertedAmount() <= 0) {
            println("반환할 금액이 없습니다.")
            return false
        }

        val result = cashProcessor.cancelAndReturnMoney()
        println(result.message)
        state = VendingMachineState.IDLE

        return result.success
    }

    fun refillBeverage(beverageId: String, quantity: Int): Boolean {
        val beverage = BeverageFactory.createBeverage(beverageId) ?: run {
            println("존재하지 않는 음료입니다.")
            return false
        }

        beverageInventory.addItem(beverage, quantity)
        println("${beverage.name}의 재고가 ${quantity}개 추가되었습니다. 현재 재고: ${beverageInventory.getQuantity(beverage)}개")
        return true
    }

    fun refillCash(money: Money, quantity: Int): Boolean {
        cashProcessor.refillCash(money, quantity)
        println("${money.description} 화폐가 ${quantity}개 추가되었습니다.")
        return true
    }

    fun getSalesReport(): String {
        val report = salesRecordManager.generateSalesReport()
        println(report)
        return report
    }

    fun getInventoryStatus(): String {
        return beverageInventory.toString()
    }

    fun getCashInventoryStatus(): String {
        val cashInventory = cashProcessor.getCashInventory()
        return cashInventory.entries.joinToString("\n") { (money, quantity) ->
            "${money.description}: ${quantity}개"
        }
    }
}