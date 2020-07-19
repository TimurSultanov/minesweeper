package com.tsultanov.minesweeper.models

import com.tsultanov.minesweeper.exceptions.ExplodeException
import kotlin.math.max
import kotlin.math.min

class MineField(val size: Pair<Int, Int>) {
    val field = List(size.first) { List(size.second) { Cell() } }

    override fun toString(): String {
        var string = " | "

        for (colNum in 1..size.first) {
            string += "$colNum "
        }

        string += "|" + '\n' + "-|" + "-".repeat(size.first) + "|" + '\n'
        string += getFieldString()
        string += "-|" + "-".repeat(size.first) + "|" + '\n'

        return string
    }

    fun markFree(coordinate: Pair<Int, Int>) {
        val cell = this.field[coordinate.first][coordinate.second]
        if (cell.isMined) throw ExplodeException("You stepped on a mine and failed!")

        if (cell.markedAsFree) return

        cell.isExplored = true
        cell.markedAsFree = true

        if (cell.minesAround == 0) {
            val neighbourCells = this.getNeighbourCells(coordinate)

            for (rowIndex in neighbourCells.first.first..neighbourCells.second.first) {
                for (columnIndex in neighbourCells.first.second..neighbourCells.second.second) {
                    if (!this.field[rowIndex][columnIndex].isMined) this.markFree(Pair(rowIndex, columnIndex))
                }
            }
        }
    }

    fun markMined(coordinate: Pair<Int, Int>) {
        val cell = this.field[coordinate.first][coordinate.second]
        cell.markedAsMined = !cell.markedAsMined
        cell.isExplored = !cell.isExplored
    }

    fun isFinish(): Boolean {
        var isNotDefused = false;
        var isNotSafe = false;
        for (row in this.field) {
            for (cell in row) {
                if (cell.isMined && !cell.markedAsMined) isNotDefused = true
                if (!cell.isMined && !cell.markedAsFree) isNotSafe = true
                if (isNotDefused && isNotSafe) return false
            }
        }

        return true
    }

    fun addMines(quantity: Int) {
        val rangeX = 0 until this.size.first
        val rangeY = 0 until this.size.second

        repeat(quantity) {
            while (true) {
                val coordinate = Pair(rangeX.random(), rangeY.random())

                if (!this.hasMine(coordinate)) {
                    this.addMine(coordinate)
                    break
                }
            }
        }
    }

    fun revealMines() {
        for (row in this.field) {
            for (cell in row) {
                if (cell.isMined) cell.isExplored = true
            }
        }
    }

    private fun getFieldString(): String {
        var string = ""

        for (columnIndex in this.field.indices) {
            string += (columnIndex + 1).toString() + "| "
            val column = this.field[columnIndex]
            string += column.joinToString("") { cell ->
                with(cell) {
                    if (isExplored) {
                        when {
                            minesAround == 0 && cell.markedAsFree -> "/"
                            minesAround > 0 && cell.markedAsFree -> minesAround.toString()
                            markedAsMined -> "*"
                            isMined -> "X"
                            else -> throw Exception("Wrong cell!!!")
                        }
                    } else {
                        "."
                    }
                }
            } + " |" + '\n'
        }

        return string
    }

    private fun addMine(coordinate: Pair<Int, Int>) {
        this.field[coordinate.first][coordinate.second].isMined = true
        val neighbourCells = this.getNeighbourCells(coordinate)

        for (rowIndex in neighbourCells.first.first..neighbourCells.second.first) {
            for (columnIndex in neighbourCells.first.second..neighbourCells.second.second) {
                ++this.field[rowIndex][columnIndex].minesAround
            }
        }
    }

    private fun getNeighbourCells(coordinate: Pair<Int, Int>): Pair<Pair<Int, Int>, Pair<Int, Int>> {
        val leftBorder = Pair(max(0, coordinate.first - 1), max(0, coordinate.second - 1))
        val rightBorder = Pair(min(this.size.first - 1, coordinate.first + 1), min(this.size.second - 1, coordinate.second + 1))

        return Pair(leftBorder, rightBorder)
    }

    private fun hasMine(coordinate: Pair<Int, Int>): Boolean {
        return this.field[coordinate.first][coordinate.second].isMined
    }
}
