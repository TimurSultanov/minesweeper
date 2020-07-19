package com.tsultanov.minesweeper.activities

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.tsultanov.minesweeper.R
import com.tsultanov.minesweeper.models.Cell
import com.tsultanov.minesweeper.models.Command
import com.tsultanov.minesweeper.models.MineField

const val PICTURE_MINED = android.R.drawable.ic_menu_close_clear_cancel
const val PICTURE_FREE = android.R.drawable.ic_menu_myplaces
const val PICTURE_BOMB = android.R.drawable.ic_delete
const val PICTURE_EMPTY = android.R.drawable.title_bar

class PlayFieldActivity : AppCompatActivity() {
    private lateinit var field: MineField
    private val pictureNumberMap = intArrayOf(
        R.drawable.ic_number_1_foreground,
        R.drawable.ic_number_2_foreground,
        R.drawable.ic_number_3_foreground,
        R.drawable.ic_number_4_foreground,
        R.drawable.ic_number_5_foreground,
        R.drawable.ic_number_6_foreground,
        R.drawable.ic_number_7_foreground,
        R.drawable.ic_number_8_foreground
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_play_field)

        field = MineField(Pair(intent.getIntExtra(COLUMNS, 0), intent.getIntExtra(ROWS, 0)))
        field.addMines(intent.getIntExtra(MINES, 0))
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)

        createField(field)
        printMessage("Set/unset mines marks or claim a cell as free:")
    }

    private fun clickCell(cell: View) {
        val activeActionId = findViewById<RadioGroup>(R.id.actions).checkedRadioButtonId
        val action = findViewById<RadioButton>(activeActionId).text
        val row = cell.parent
        var x = 0
        var y = 0
        if (row is ViewGroup) {
            x = row.indexOfChild(cell)

            val table = row.parent
            if (table is ViewGroup) {
                y = table.indexOfChild(row)
            }
        }

        try {
            when (action) {
                Command.FREE.commandName -> field.markFree(Pair(y, x))
                Command.MINE.commandName -> field.markMined(Pair(y, x))
                else -> throw Exception("Wrong action")
            }
        } catch (e: Exception) {
            field.revealMines()
            createField(field)
            createDialogEndOfGame(e.message ?: "Unknown exception").show()
        }

        createField(field)

        if (field.isFinish()) {
            createDialogEndOfGame("Congratulations! You found all mines!").show()
        }
    }

    private fun createField(mineField: MineField) {
        Log.d("field:", mineField.toString())

        val playField = findViewById<TableLayout>(R.id.play_field)
        playField.removeAllViews()
        val cellWidth = playField.width / mineField.size.first - 5

        for (rowIndex in 0 until mineField.size.second) {
            val row = TableRow(this)
            row.layoutParams = TableLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT).apply { bottomMargin = 5 }

            for (columnIndex in 0 until mineField.size.first) {
                val cell = ImageButton(this)
                cell.setImageResource(getCellImage(mineField.field[rowIndex][columnIndex]))

                val marginParams = TableRow.LayoutParams(cellWidth, cellWidth).apply { leftMargin = 5 }
                cell.setOnClickListener(::clickCell)
                cell.layoutParams = marginParams

                row.addView(cell, columnIndex)
            }

            playField.addView(row, rowIndex)
        }
    }

    private fun getCellImage(cell: Cell): Int {
        return with(cell) {
            if (isExplored) {
                when {
                    minesAround == 0 && cell.markedAsFree -> PICTURE_FREE
                    minesAround > 0 && cell.markedAsFree -> pictureNumberMap[minesAround - 1]
                    markedAsMined -> PICTURE_MINED
                    isMined -> PICTURE_BOMB
                    else -> throw Exception("Wrong cell!!!")
                }
            } else {
                PICTURE_EMPTY
            }
        }
    }

    private fun printMessage(message: String?) {
        findViewById<TextView>(R.id.message).apply {
            text = message
        }
    }

    private fun createDialogEndOfGame(message: String): AlertDialog {
        val context = this
        val dialogBuilder = AlertDialog.Builder(this).apply {
            setMessage(message)
            setPositiveButton("ok", DialogInterface.OnClickListener { _, _ ->
                val intent = Intent(context, MainActivity::class.java)
                startActivity(intent)
            })
        }

        return dialogBuilder.create()
    }
}
