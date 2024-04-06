package com.hansung.roadbuddyandroid

import android.os.Bundle
import android.widget.EditText
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity

class SearchResultActivity : AppCompatActivity() {
    private lateinit var searchText : String
    private lateinit var searchBar2 : EditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_result)

        // SearchActivity에서 전달된 searchText를 변수에 저장
        searchText = intent.getStringExtra("searchText")!!

        searchBar2 = findViewById(R.id.searchingBar2)
        searchBar2.setText(searchText)

        findViewById<ImageButton>(R.id.backButton).setOnClickListener {
            onBackPressed()
        }
    }
}