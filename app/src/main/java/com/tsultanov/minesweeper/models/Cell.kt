package com.tsultanov.minesweeper.models

data class Cell(
    var isMined: Boolean = false,
    var minesAround: Int = 0,
    var markedAsFree: Boolean = false,
    var markedAsMined: Boolean = false,
    var isExplored: Boolean = false
)
