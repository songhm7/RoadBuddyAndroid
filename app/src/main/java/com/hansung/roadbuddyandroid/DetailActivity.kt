package com.hansung.roadbuddyandroid

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.Credentials
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException

class DetailActivity : AppCompatActivity() {
    private lateinit var route: Route
    // Basic 인증을 위한 사용자 이름과 비밀번호 설정
    private val username = "user"
    private lateinit var password : String
    private lateinit var credentials: String
    private lateinit var client: OkHttpClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        route = intent.getParcelableExtra("route")!!
        val testLeg = route.legs[0]
        password = getString(R.string.API_KEY)
        client = OkHttpClient()
        credentials = Credentials.basic(username, password)

        makeNetworkRequest(testLeg)
    }

    private fun makeNetworkRequest(leg: Leg){
        val url = "http://3.25.65.146:8080/subway/transfer"
        val gson = Gson()
        val legJson = gson.toJson(leg)  // Leg 객체를 JSON 문자열로 변환
        val mediaType = "application/json; charset=utf-8".toMediaType()
        val body = legJson.toRequestBody(mediaType)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val request = Request.Builder()
                    .url(url)
                    .post(body)
                    .header("Authorization", credentials)
                    .build()

                client.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) throw IOException("Unexpected code $response")
                    val responseBody = response.body!!.string()

                    // UI 업데이트는 메인 스레드에서 수행
                    withContext(Dispatchers.Main) {
                        Logr.d("디테일 액티비티 체크", responseBody)
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@DetailActivity, "네트워크 오류가 발생했습니다. 다시 시도해주세요.", Toast.LENGTH_LONG).show()
                }
                Log.e("디테일 네트워크 에러", "요청 중 오류 발생: ${e.message}", e)
            }
        }
    }
}