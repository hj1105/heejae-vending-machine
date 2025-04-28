package org.example.service

import org.example.model.payment.PaymentMethod
import org.example.model.sales.SaleHistory
import java.time.LocalDateTime

class SaleHistoryService {
    private val salesHistory = mutableListOf<SaleHistory>()

    fun addSale(saleHistory: SaleHistory) {
        salesHistory.add(saleHistory)
    }

    fun getAllSales(): List<SaleHistory> {
        return salesHistory.toList()
    }

    fun getSalesInPeriod(startDate: LocalDateTime, endDate: LocalDateTime): List<SaleHistory> {
        return salesHistory.filter { it.timestamp in startDate..endDate }
    }

    fun getSalesByBeverage(beverageId: String): List<SaleHistory> {
        return salesHistory.filter { it.beverage.id == beverageId }
    }

    fun getSalesByPaymentMethod(paymentMethod: PaymentMethod): List<SaleHistory> {
        return salesHistory.filter { it.paymentMethod == paymentMethod }
    }

    fun getTotalRevenue(): Int {
        return salesHistory.sumOf { it.amount }
    }

    fun getRevenueByBeverage(): Map<String, Pair<Int, Int>> {
        val result = mutableMapOf<String, Pair<Int, Int>>()

        for (sale in salesHistory) {
            val beverageId = sale.beverage.id
            val (count, revenue) = result.getOrDefault(beverageId, Pair(0, 0))
            result[beverageId] = Pair(count + 1, revenue + sale.amount)
        }

        return result
    }

    fun getRevenueByPaymentMethod(): Map<PaymentMethod, Int> {
        return salesHistory.groupBy { it.paymentMethod }
            .mapValues { (_, sales) -> sales.sumOf { it.amount } }
    }

    fun generateSalesReport(): String {
        if (salesHistory.isEmpty()) {
            return "판매 내역이 없습니다."
        }

        val totalRevenue = getTotalRevenue()
        val beverageStats = getRevenueByBeverage()
        val paymentStats = getRevenueByPaymentMethod()

        val beverageReportBuilder = StringBuilder()
        beverageStats.forEach { (id, stats) ->
            val beverage = salesHistory.find { it.beverage.id == id }?.beverage
            if (beverage != null) {
                val (count, revenue) = stats
                beverageReportBuilder.append("  ${beverage.name}: ${count}개 (${revenue}원)\n")
            }
        }

        val paymentReportBuilder = StringBuilder()
        paymentStats.forEach { (method, revenue) ->
            paymentReportBuilder.append("  ${method.description}: ${revenue}원\n")
        }

        return """
        |===== 매출 통계 =====
        |총 판매액: ${totalRevenue}원
        |음료별 판매:
        |${beverageReportBuilder}
        |결제 방법별 판매:
        |${paymentReportBuilder}
        |====================
        """.trimMargin()
    }

    fun clearSalesHistory() {
        salesHistory.clear()
    }
}