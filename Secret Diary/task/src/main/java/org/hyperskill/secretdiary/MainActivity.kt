package org.hyperskill.secretdiary

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import kotlinx.datetime.Clock
import java.text.SimpleDateFormat
import java.util.*

const val PREF_NAME = "PREF_DIARY"
const val DIARY_KEY = "KEY_DIARY_TEXT"

class MainActivity : AppCompatActivity() {

    private lateinit var textDiary: TextView
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var prefEdit: SharedPreferences.Editor
    private var notes: MutableList<String> = mutableListOf()


    @SuppressLint("CommitPrefEdits")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sharedPreferences = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefEdit = sharedPreferences.edit()

        val editDiary = findViewById<EditText>(R.id.etNewWriting)
        val buttonSave = findViewById<Button>(R.id.btnSave)
        val buttonUndo = findViewById<Button>(R.id.btnUndo)
        textDiary = findViewById(R.id.tvDiary)

        setDiary()

        buttonSave.setOnClickListener {
            val newText = editDiary.text
            if (newText.isBlank()) {
                Toast.makeText(applicationContext, R.string.empty_error, Toast.LENGTH_LONG).show()
            } else {
                saveNote(newText)
                editDiary.setText("")
            }
        }

        buttonUndo.setOnClickListener {
            undoNote()
        }

    }

    private fun setDiary() {
        val diaryText = sharedPreferences.getString("KEY_DIARY_TEXT", null)
        if (diaryText != null) {
            notes = diaryText.split("\n\n").reversed().toMutableList()
            textDiary.text = diaryText
        }
    }

    private fun saveDiary(text: String) {
        prefEdit.putString(DIARY_KEY, text).apply()
    }

    @SuppressLint("SetTextI18n")
    private fun saveNote(text: Editable) {
        val nowTime = Clock.System.now().toEpochMilliseconds()
        val formattedTime = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            .format(nowTime)
        notes.add("$formattedTime\n$text")
        val diaries = notes.reversed().joinToString("\n\n")
        textDiary.text = diaries
        saveDiary(diaries)
    }

    private fun undoNote() {
        AlertDialog.Builder(this)
            .setTitle("Remove last note")
            .setMessage("Do you really want to remove the last writing? This operation cannot be undone!")
            .setPositiveButton(R.string.yes) { _, _ ->
                if (notes.isNotEmpty()) {
                    notes.removeLast()
                    val diaries = notes.reversed().joinToString("\n\n")
                    textDiary.text = diaries
                    saveDiary(diaries)
            }
        }
            .setNegativeButton(R.string.no, null).show()
    }

}