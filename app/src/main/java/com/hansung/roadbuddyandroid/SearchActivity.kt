package com.hansung.roadbuddyandroid

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.io.File

class SearchActivity : AppCompatActivity() {
    private lateinit var searchingBar: EditText
    private lateinit var listViewRecent: ListView
    private lateinit var recentSearches: List<String>
    private var startPoint = "출발지미정"
    private var endPoint = "도착지미정"
    private lateinit var adapterRecent : RecentAdapter
    private var curLat : Double = 0.0
    private var curLon : Double = 0.0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        curLat = intent.getDoubleExtra("curLat",0.0)
        curLon = intent.getDoubleExtra("curLon",0.0)

        findViewById<ImageButton>(R.id.backButton).setOnClickListener {
            onBackPressed()
        }
        listViewRecent = findViewById(R.id.listViewRecent)
        searchingBar = findViewById(R.id.searchingBar)
        recentSearches = readSearchHistory()

        // ArrayAdapter 초기화
        adapterRecent = RecentAdapter(
            context = this,
            dataSource = ArrayList(recentSearches),
            onItemRemoved = { writeSearchHistory(it) },
            startPoint = startPoint,
            endPoint = endPoint,
            curLat = curLat,
            curLon = curLon
        )

        // ListView에 ArrayAdapter 설정
        listViewRecent.adapter = adapterRecent

        searchingBar.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                // submit 동작을 처리하는 코드
                val searchText = searchingBar.text.toString()
                if (searchText.isNotEmpty()) {
                    recentSearches = readSearchHistory()
                    val updatedHistory = manageHistory(recentSearches, searchText)
                    searchingBar.setText("")
                    writeSearchHistory(updatedHistory)
                    adapterRecent.clear()
                    adapterRecent.addAll(updatedHistory)
                    adapterRecent.notifyDataSetChanged()

                    // SearchResultActivity로 이동
                    val intent = Intent(this, SearchResultActivity::class.java)
                    intent.putExtra("searchText", searchText)
                    intent.putExtra("curLat",curLat)
                    intent.putExtra("curLon",curLon)
                    if(startPoint != "출발지미정")
                        intent.putExtra("startPoint",startPoint)
                    if(endPoint != "도착지미정")
                        intent.putExtra("endPoint",endPoint)
                    startActivity(intent)
                } else
                    Toast.makeText(this, "검색어를 입력해주세요.", Toast.LENGTH_SHORT).show()
                true // 이벤트 처리 완료
            } else {
                false // 이벤트 미처리
            }
        }

        //리스트뷰의 각 아이템에 대한 클릭 리스너. 테스트를 위해 Toast메시지를 띄운다
        listViewRecent.setOnItemClickListener { parent, view, position, id ->
            val item = adapterRecent.getItem(position)
            Toast.makeText(this, item, Toast.LENGTH_SHORT).show()
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
        return file.readLines().take(10)  // 최근 10개의 검색 기록만 가져옴
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

    //newRecord에 대한 중복처리를 거쳐 리스트를 반환
    private fun manageHistory(history: List<String>, newRecord: String): List<String> {
        val tmpHistory = history.toMutableList() // 기존 목록을 복사하여 임시 MutableList 생성

        if (newRecord.isNotEmpty()) {
            var index = tmpHistory.indexOf(newRecord)
            if (index != -1) {
                // 기존 기록이 있다면 해당 기록 삭제
                tmpHistory.removeAt(index)
            } else if (tmpHistory.size >= 10) {
                // 목록이 10개 이상이면 가장 오래된 기록 삭제
                tmpHistory.removeAt(tmpHistory.size - 1)
            }
            // 새 기록을 맨 앞에 추가
            tmpHistory.add(0, newRecord)
        }
        return tmpHistory.toList() // 임시 MutableList를 다시 List로 변환
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        setIntent(intent)  // 최신 인텐트로 업데이트

        // startPoint와 endPoint를 업데이트
        if (intent?.hasExtra("startPoint") == true) startPoint = intent.getStringExtra("startPoint")!!
        if (intent?.hasExtra("endPoint") == true) endPoint = intent.getStringExtra("endPoint")!!
        Log.d("Search startPoint", startPoint)
        Log.d("Search endPoint", endPoint)
        adapterRecent.updateEndpoints(startPoint, endPoint)
    }
}