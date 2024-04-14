package com.hansung.roadbuddyandroid

import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Credentials
import java.io.IOException

class SearchResultActivity : AppCompatActivity() {
    private lateinit var searchText: String
    private lateinit var searchBar2: EditText
    private lateinit var client: OkHttpClient

    // Basic 인증을 위한 사용자 이름과 비밀번호 설정
    private val username = "user"
    private val password = "4fd3fcbb-4825-4fd0-a28b-f31b1d4ed718"
    private lateinit var credentials: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_result)

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
                    Log.d("응답확인", responseBody)

                    // UI 업데이트는 메인 스레드에서 수행
                    withContext(Dispatchers.Main) {
                        updateUI(responseBody)
                    }
                }
            } catch (e: Exception) {
                Log.e("네트워크 오류", "요청 중 오류 발생: ${e.message}")
            }
        }
    }

    private fun updateUI(responseData: String) {
        // 여기에 UI 업데이트 로직 구현
    }
}