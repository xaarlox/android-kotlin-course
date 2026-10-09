package com.xaarlox.task08_microgrid_room.ui

import androidx.annotation.StringRes
import com.xaarlox.task08_microgrid_room.R
import com.xaarlox.task08_microgrid_room.domain.InputError
import com.xaarlox.task08_microgrid_room.domain.PowerFilter
import com.xaarlox.task08_microgrid_room.domain.SortOrder

@StringRes
fun PowerFilter.labelRes(): Int = when (this) {
    PowerFilter.ALL -> R.string.filter_all
    PowerFilter.LOW -> R.string.filter_low
    PowerFilter.HIGH -> R.string.filter_high
}

@StringRes
fun SortOrder.labelRes(): Int = when (this) {
    SortOrder.NAME -> R.string.sort_name
    SortOrder.POWER_DESC -> R.string.sort_power
    SortOrder.VOLTAGE_DESC -> R.string.sort_voltage
    SortOrder.NEWEST -> R.string.sort_newest
}

@StringRes
fun InputError.messageRes(): Int = when (this) {
    InputError.NAME_EMPTY -> R.string.error_name_empty
    InputError.NUMBERS_INVALID -> R.string.error_numbers
    InputError.DUPLICATE_NAME -> R.string.error_duplicate
}