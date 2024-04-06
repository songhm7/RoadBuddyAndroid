package com.hansung.roadbuddyandroid

import android.content.Context
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.io.File

class SearchActivity : AppCompatActivity(){
    private lateinit var searchingBar : EditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        findViewById<ImageButton>(R.id.backButton).setOnClickListener {
            onBackPressed()
        }

        searchingBar = findViewById(R.id.searchingBar)

        val tvForTest1 = findViewById<TextView>(R.id.tv_forTest1)
        val tvForTest2 = findViewById<TextView>(R.id.tv_forTest2)
        val tvForTest3 = findViewById<TextView>(R.id.tv_forTest3)

        val recentSearches = readSearchHistory()
        if (recentSearches.isNotEmpty()) {
            tvForTest1.text = recentSearches.getOrElse(0) { "" }
            tvForTest2.text = recentSearches.getOrElse(1) { "" }
            tvForTest3.text = recentSearches.getOrElse(2) { "" }
        }

        searchingBar.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                // submit 동작을 처리하는 코드
                val searchText = searchingBar.text.toString()
                if (searchText.isNotEmpty()) {
                    if(searchText == tvForTest1.text.toString()) {
                        tvForTest1.text = searchText
                    }
                    else if(searchText == tvForTest2.text.toString()){
                        tvForTest2.text = tvForTest1.text
                        tvForTest1.text = searchText
                    }
                    else {
                        tvForTest3.text = tvForTest2.text
                        tvForTest2.text = tvForTest1.text
                        tvForTest1.text = searchText
                    }
                    var newRecent : List<String> = listOf(tvForTest1.text.toString(),tvForTest2.text.toString(),tvForTest3.text.toString())
                    writeSearchHistory(newRecent)
                    searchingBar.setText("")
                }
                true // 이벤트 처리 완료
            } else {
                false // 이벤트 미처리
            }
        }
    }

    //액티비티가 전환되면 자동으로 검색바에 커서가 올라가고 키보드가 올라오게끔 하는 메서드
    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            // searchingBar에 포커스를 요청합니다.
            searchingBar.requestFocus()

            // InputMethodManager를 통해 키보드를 표시합니다.
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(searchingBar, InputMethodManager.SHOW_IMPLICIT)
        }
    }


    private fun readSearchHistory(): List<String> {
        val file = File(filesDir, "recent.txt")
        if (!file.exists()) {
            file.createNewFile()
        }
        return file.readLines().take(3)  // 최근 3개의 검색 기록만 가져옴
    }

    private fun writeSearchHistory(searches: List<String>) {
        val file = File(filesDir, "recent.txt")
        file.writeText(searches.joinToString("\n"))
    }
    private fun deleteSearchHistory() {
        val file = File(filesDir, "recent.txt")
        if (file.exists()) {
            file.delete()
        }
    }
}