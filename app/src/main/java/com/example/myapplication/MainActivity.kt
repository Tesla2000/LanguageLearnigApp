package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TableLayout
import android.widget.TableRow


class MainActivity : LoggedClass() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        createButtons(intent.getStringArrayListExtra("languages")!!)
    }

    private fun createButtons(buttonNames: List<String>){

        val tableLayout = findViewById<TableLayout>(R.id.tableLayout)

        for (i in buttonNames.indices) {
            val tableRow = TableRow(this)
            val button = Button(this)
            button.text = buttonNames[i]
            button.textSize = 24f

            val rowParams = TableRow.LayoutParams(
                TableRow.LayoutParams.WRAP_CONTENT,
                TableRow.LayoutParams.WRAP_CONTENT
            )

            button.layoutParams = rowParams
            tableRow.addView(button)
            tableLayout.addView(tableRow)

            button.setOnClickListener {
                for (row in 0 until tableLayout.childCount) {
                    val currentRow = tableLayout.getChildAt(row) as TableRow
                    for (index in 0 until currentRow.childCount) {
                        val currentButton = currentRow.getChildAt(index) as Button
                        currentButton.isEnabled = false
                    }
                }

                val intent = Intent(this, LearnActivity::class.java)
                intent.putExtra("username", username)
                intent.putExtra("token", token)
                intent.putExtra("language", button.text)
                startActivity(intent)
                finish()
            }
        }
    }
}
