package com.hansung.roadbuddyandroid

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.hansung.roadbuddyandroid.SearchHistoryManager.manageHistory
import com.hansung.roadbuddyandroid.SearchHistoryManager.readSearchHistory
import com.hansung.roadbuddyandroid.SearchHistoryManager.writeSearchHistory

class SearchActivity : AppCompatActivity() {
    private lateinit var searchingBar: EditText
    private lateinit var listViewRecent: ListView
    private lateinit var recentSearches: List<String>
    private var startPoint = Place("출발지미정","","",0.0,0.0,0.0)
    private var endPoint = Place("도착지미정","","",0.0,0.0,0.0)
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
        recentSearches = readSearchHistory(this)

        // ArrayAdapter 초기화
        adapterRecent = RecentAdapter(
            context = this,
            dataSource = ArrayList(recentSearches),
            onItemRemoved = { writeSearchHistory(this, it) },
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
                    recentSearches = readSearchHistory(this)
                    val updatedHistory = manageHistory(recentSearches, searchText)
                    searchingBar.setText("")
                    writeSearchHistory(this, updatedHistory)
                    adapterRecent.clear()
                    adapterRecent.addAll(updatedHistory)
                    adapterRecent.notifyDataSetChanged()

                    // SearchResultActivity로 이동
                    val intent = Intent(this, SearchResultActivity::class.java)
                    intent.putExtra("searchText", searchText)
                    intent.putExtra("curLat",curLat)
                    intent.putExtra("curLon",curLon)
                    if(startPoint.name != "출발지미정")
                        intent.putExtra("startPoint",startPoint)
                    if(endPoint.name != "도착지미정")
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

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        setIntent(intent)  // 최신 인텐트로 업데이트

        // startPoint와 endPoint를 업데이트
        if (intent?.hasExtra("startPoint") == true) startPoint = intent.getParcelableExtra("startPoint")!!
        if (intent?.hasExtra("endPoint") == true) endPoint = intent.getParcelableExtra("endPoint")!!
        Log.d("Search startPoint", startPoint.name)
        Log.d("Search endPoint", endPoint.name)
        adapterRecent.updateEndpoints(startPoint, endPoint)
    }
    override fun onResume() {
        super.onResume()

        val newSearches = readSearchHistory(this)
        if (!recentSearches.equals(newSearches)) {
            recentSearches = newSearches
            adapterRecent.clear()
            adapterRecent.addAll(recentSearches)
            adapterRecent.notifyDataSetChanged()
        }
    }


}