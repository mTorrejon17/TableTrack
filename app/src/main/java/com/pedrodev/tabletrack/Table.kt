package com.pedrodev.tabletrack

data class Table(
    val number: String = "",
    val isAvailable: Boolean = true
)

enum class Status {
    AVAILABLE, UNAVAILABLE
}

