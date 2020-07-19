package com.tsultanov.minesweeper.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.tsultanov.minesweeper.R

const val COLUMNS = "com.tsultanov.minesweepe.COLUMNS_COUNT"
const val ROWS = "com.tsultanov.minesweepe.ROWS_COUNT"
const val MINES = "com.tsultanov.minesweepe.MINES_COUNT"

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun startGame(view: View) {
        val columns = findViewById<EditText>(R.id.column_count)
        val rows = findViewById<EditText>(R.id.row_count)
        val mines = findViewById<EditText>(R.id.mines_count)

        if (checkField(columns) && checkField(rows) && checkField(mines)) {
            val intent = Intent(this, PlayFieldActivity::class.java).apply {
                putExtra(COLUMNS, columns.text.toString().toInt())
                putExtra(ROWS, rows.text.toString().toInt())
                putExtra(MINES, mines.text.toString().toInt())
            }
            startActivity(intent)
        }
    }

    private fun checkField(field: EditText): Boolean {
        if (field.text.toString().isEmpty()) {
            field.error = "The field can not be empty"

            return false
        }

        return true
    }
}
