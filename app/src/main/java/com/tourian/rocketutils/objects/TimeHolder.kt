package com.tourian.rocketutils.objects

import kotlin.collections.plusAssign
import kotlin.times


data class TimeHolder(val days: Int, val hours: Int, val minutes: Int, val seconds: Int) {
    fun toFormattedString(): String {
        return "${days}d ${hours}h ${minutes}m ${seconds}s"
    }

    fun toSeconds(): Int {
        var result = days * 6 * 60 * 60
        result += hours * 60 * 60
        result += minutes * 60
        result += seconds
        return result
    }

    companion object {

        fun fromSeconds(seconds: Int): TimeHolder {
            val days = seconds / 60 / 60 / 6
            val hours = (seconds / 60 / 60) % 6
            val minutes = (seconds / 60) % 60
            val seconds = seconds % 60
            return TimeHolder(days, hours, minutes, seconds)
        }


    }
}
