package com.example.playlistmaker

import android.content.Context
import android.graphics.drawable.Drawable
import android.graphics.drawable.Icon
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SearchActivity : AppCompatActivity() {
    private var text = TEXT_DEF
    lateinit var editText: EditText
    lateinit var placeHolderImage: ImageView
    lateinit var smthWrongMessage: TextView
    lateinit var refreshButton: Button

    private val iTunesBaseUrl = "https://itunes.apple.com"

    private val retrofit = Retrofit.Builder()
        .baseUrl(iTunesBaseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val iTunesService = retrofit.create(ITunesApi::class.java)

    private val trackList = ArrayList<Track>()
    private val adapter = TrackAdapter()

    private fun search() {
        val call = iTunesService.search(editText.text.toString()).enqueue(object : Callback<ITunesResponse> {
            override fun onResponse(
                call: Call<ITunesResponse>,
                response: retrofit2.Response<ITunesResponse>
            ) {
                if (response.code() == 200){
                    Log.d("y", response.body()?.results.toString())
                    if (response.body()?.results?.isNotEmpty() == true){
                        trackList.clear()
                        trackList.addAll(response.body()?.results!!)
                        adapter.notifyDataSetChanged()
                        showPlaceholder(null)
                        showMessage("", "")
                        showButton(false)
                    } else {
                        showPlaceholder(PlaceholderIcon.nothingFound)
                        showMessage("Ничего не нашлось","")
                        showButton(false)
                        Log.d("y", response.body()?.results.toString())
                    }
                } else {
                    showPlaceholder(PlaceholderIcon.noInternet)
                    showMessage("Проблемы со связью", "Загрузка не удалась. Проверьте подключение к интернету")
                    showButton(true)
                }
            }

            override fun onFailure(call: Call<ITunesResponse>, t: Throwable) {
                showPlaceholder(PlaceholderIcon.noInternet)
                showMessage("Проблемы со связью", "Загрузка не удалась. Проверьте подключение к интернету")
                showButton(true)
            }
        })
    }

    private fun showButton(show: Boolean) {
        if (show) {
            refreshButton.visibility = View.VISIBLE
        } else
            refreshButton.visibility = View.GONE
    }

    private fun showPlaceholder(icon: PlaceholderIcon?) {
        if (icon != null) {
            placeHolderImage.setImageResource(icon.resourceId)
            placeHolderImage.visibility = View.VISIBLE
        } else
            placeHolderImage.visibility = View.GONE
    }

    private fun showMessage(text: String, additionalText: String) {
        if (text.isNotEmpty()) {
            smthWrongMessage.visibility = View.VISIBLE
            trackList.clear()
            adapter.notifyDataSetChanged()
            smthWrongMessage.text = text
            if (additionalText.isNotEmpty()){
                smthWrongMessage.text = "$text\n\n$additionalText"
            }
        } else
            smthWrongMessage.visibility = View.GONE
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        //val trackList = TrackRepository.trackList

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter.tracks = trackList
        recyclerView.adapter = adapter

        val linearLayout = findViewById<LinearLayout>(R.id.searchContainer)
        editText = findViewById<EditText>(R.id.editText)
        val clearButton = findViewById<ImageView>(R.id.imageViewClear)
        val backButton = findViewById<ImageView>(R.id.backArrow)
        placeHolderImage = findViewById<ImageView>(R.id.searchPlaceholder)
        smthWrongMessage = findViewById(R.id.somethingWrongTexView)
        refreshButton = findViewById(R.id.refreshButton)

        editText.requestFocus()
        showKeyboard(editText)

        editText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                search()
                true
            }
            false
        }

        clearButton.setOnClickListener {
            editText.setText("")
        }

        backButton.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        refreshButton.setOnClickListener {
            search()
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






