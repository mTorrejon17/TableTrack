package com.pedrodev.tabletrack

data class Table(
    val id: String = "",
    val coordinates: Coordinates = Coordinates(),
    val status: Status = Status.AVAILABLE,
    val capacity: Int = 4
)

enum class Status {
    AVAILABLE, UNAVAILABLE
}


data class Coordinates(
    val x: Int = 0,
    val y: Int = 0
)
