package com.hansung.roadbuddyandroid

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Credentials
import org.json.JSONObject
import java.io.IOException

class SearchResultActivity : AppCompatActivity() {
    private lateinit var searchText: String
    private lateinit var searchBar2: EditText
    private lateinit var client: OkHttpClient
    private lateinit var listView : ListView

    // Basic 인증을 위한 사용자 이름과 비밀번호 설정
    private val username = "user"
    private val password = "4fd3fcbb-4825-4fd0-a28b-f31b1d4ed718"
    private lateinit var credentials: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_result)
        listView = findViewById(R.id.listViewSearchResult)

        // SearchActivity에서 전달된 searchText를 변수에 저장
        searchText = intent.getStringExtra("searchText")!!

        searchBar2 = findViewById(R.id.searchingBar2)
        searchBar2.setText(searchText)

        client = OkHttpClient()
        credentials = Credentials.basic(username, password)

        // 네트워크 요청 실행
        makeNetworkRequest(searchText)

        findViewById<ImageButton>(R.id.backButton).setOnClickListener {
            onBackPressed()
        }
        searchBar2.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                // submit 동작을 처리하는 코드
                val searchText = searchBar2.text.toString()
                if (searchText.isNotEmpty())
                    makeNetworkRequest(searchText)
                else
                    Toast.makeText(this, "검색어를 입력해주세요.", Toast.LENGTH_SHORT).show()
                true // 이벤트 처리 완료
            } else {
                false // 이벤트 미처리
            }
        }
    }

    private fun makeNetworkRequest(searchQuery: String) {
        val url = "http://3.25.65.146:8080/maps/locations?input=$searchQuery"

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val request = Request.Builder()
                    .url(url)
                    .header("Authorization", credentials)
                    .build()

                client.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) throw IOException("Unexpected code $response")

                    val responseBody = response.body!!.string()
                    //Log.d("응답확인", responseBody)

                    // UI 업데이트는 메인 스레드에서 수행
                    withContext(Dispatchers.Main) {
                        updateUI(responseBody)
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@SearchResultActivity, "네트워크 오류가 발생했습니다. 다시 시도해주세요.", Toast.LENGTH_LONG).show()
                }
                //Log.e("네트워크 오류", "요청 중 오류 발생: ${e.message}")
            }
        }
    }

    private fun updateUI(responseData: String) {
        val jsonResponse = JSONObject(responseData)
        val status = jsonResponse.getJSONObject("data").getString("status")

        if (status == "ZERO_RESULTS") {
            // 검색 결과가 없을 경우
            Toast.makeText(this, "일치하는 결과가 없습니다.", Toast.LENGTH_LONG).show()
        } else {
            val predictions = jsonResponse.getJSONObject("data").getJSONArray("predictions")
            val firstTermsList = mutableListOf<String>()

            // predictions 배열에서 각 요소의 'terms' 배열의 첫 번째 요소 추출
            for (i in 0 until predictions.length()) {
                val prediction = predictions.getJSONObject(i)
                val firstTermValue = prediction.getJSONArray("terms").getJSONObject(0).getString("value")
                firstTermsList.add(firstTermValue)
            }

            // UI 스레드에서 리스트뷰에 데이터 설정
            runOnUiThread {
                val adapter = SearchResultAdapter(this, R.layout.list_search_result_item, firstTermsList)
                listView.adapter = adapter
            }
        }
    }

}