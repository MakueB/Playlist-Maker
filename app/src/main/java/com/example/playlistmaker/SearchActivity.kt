package com.example.playlistmaker

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import android.text.Editable
import android.text.Layout
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.content.ContextCompat.getSystemService
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class SearchActivity : AppCompatActivity() {
    private var text = TEXT_DEF
    lateinit var editText: EditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        val trackList = TrackRepository.trackList


        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val trackAdapter = TrackAdapter(trackList)
        recyclerView.adapter = trackAdapter

        val linearLayout = findViewById<LinearLayout>(R.id.searchContainer)
        editText = findViewById<EditText>(R.id.editText)
        val clearButton = findViewById<ImageView>(R.id.imageViewClear)
        val backButton = findViewById<ImageView>(R.id.backArrow)

/*        if (savedInstanceState != null){
            text = savedInstanceState.getString(TEXT, TEXT_DEF)
            editText.setText(text)
            Log.d("my", "onRestoreInstanceState: Restored text - $text")
        }*/

        editText.requestFocus()
        showKeyboard(editText)

        clearButton.setOnClickListener {
            editText.setText("")
        }

        backButton.setOnClickListener {
            super.onBackPressed()
        }

        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                clearButton.visibility = clearButtonVisibility(s)
                checkAndHideKeyboard(editText)
                text = s.toString()
            }

            override fun afterTextChanged(s: Editable?) {
            }
        }
        editText.addTextChangedListener(textWatcher)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(TEXT, text)
        Log.d("my", "onSaveInstanceState: Saved text - $text")
    }

    override fun onRestoreInstanceState(
        savedInstanceState: Bundle,
    ) {
        super.onRestoreInstanceState(savedInstanceState)
        text = savedInstanceState.getString(TEXT, TEXT_DEF)
        editText.setText(text)
        Log.d("my", "onRestoreInstanceState: Restored text - $text")
    }

    companion object {
        const val TEXT = "TEXT_DEF"
        const val TEXT_DEF = ""
    }

    private fun showKeyboard(editText: EditText) {
        val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as? InputMethodManager
        inputMethodManager?.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT)
    }
    private fun hideKeyboard (editText: EditText) {
        val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        inputMethodManager?.hideSoftInputFromWindow(editText.windowToken, 0)
    }
    private fun checkAndHideKeyboard(editText: EditText) {
        val text = editText.text.toString().trim()
        // Если текст пуст, скрываем клавиатуру
        if (text.isNullOrEmpty()) {
            hideKeyboard(editText)
        }
        else {
            showKeyboard(editText)
        }
    }
}
private fun clearButtonVisibility(s: CharSequence?): Int {
    return if (s.isNullOrEmpty()) {
        View.GONE
    } else {
        View.VISIBLE
    }
}






