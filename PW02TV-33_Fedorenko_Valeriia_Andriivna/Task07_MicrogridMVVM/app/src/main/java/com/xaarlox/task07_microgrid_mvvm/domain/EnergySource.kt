package com.xaarlox.task07_microgrid_mvvm.domain

/**
 * MVVM: Model layer (business logic). There are no dependencies on Android or Compose here
 *
 * Renewable energy source of the microgrid
 *
 * @property capacityKw installed (nominal) capacity of the source, kW
 */
enum class EnergySource(val capacityKw: Int) {
    SOLAR(capacityKw = 100),
    WIND(capacityKw = 60)
}

/** Current power level relative to the nominal capacity */
enum class PowerStatus { LOW, NORMAL, HIGH }