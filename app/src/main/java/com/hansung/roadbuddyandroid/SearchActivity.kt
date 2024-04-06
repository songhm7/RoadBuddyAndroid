package com.hansung.roadbuddyandroid

import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class SearchActivity : AppCompatActivity(){
    private lateinit var searchingBar : EditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        findViewById<ImageButton>(R.id.backButton).setOnClickListener {
            onBackPressed()
        }

        searchingBar = findViewById(R.id.searchingBar)
        val tvForTest = findViewById<TextView>(R.id.tv_forTest)
        searchingBar.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                // submit 동작을 처리하는 코드
                tvForTest.text = searchingBar.text.toString()
                true // 이벤트 처리 완료
            } else {
                false // 이벤트 미처리
            }
        }
    }
}