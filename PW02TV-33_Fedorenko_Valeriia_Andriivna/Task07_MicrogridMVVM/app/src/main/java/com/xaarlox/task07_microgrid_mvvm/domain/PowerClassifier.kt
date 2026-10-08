package com.xaarlox.task07_microgrid_mvvm.domain

/**
 * Business logic: determines the power status based on the ratio to the nominal capacity
 * Pure function with no side effects, making it easy to test
 */
object PowerClassifier {
    const val LOW_THRESHOLD = 0.2
    const val HIGH_THRESHOLD = 0.8

    /**
     * @param powerKw current power, kW
     * @param capacityKw nominal capacity, kW
     */
    fun classify(powerKw: Int, capacityKw: Int): PowerStatus {
        val ratio = powerKw.toDouble() / capacityKw
        return when {
            ratio < LOW_THRESHOLD -> PowerStatus.LOW
            ratio > HIGH_THRESHOLD -> PowerStatus.HIGH
            else -> PowerStatus.NORMAL
        }
    }
}