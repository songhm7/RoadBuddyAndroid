package com.hansung.roadbuddyandroid

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.Credentials
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.io.IOException

class SearchResultActivity : AppCompatActivity() {
    private lateinit var searchText: String
    private lateinit var searchBar2: EditText
    private lateinit var loadingMessage: TextView
    private lateinit var loadingImage: ImageView
    private lateinit var failImage: ImageView
    private lateinit var client: OkHttpClient
    private lateinit var listView : ListView
    private var startPoint = Place("출발지미정","","",0.0,0.0,0.0)
    private var endPoint = Place("도착지미정","","",0.0,0.0,0.0)
    private var curLat : Double = 0.0
    private var curLon : Double = 0.0

    // Basic 인증을 위한 사용자 이름과 비밀번호 설정
    private val username = "user"
    private lateinit var password : String
    private lateinit var credentials: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        password = getString(R.string.API_KEY)
        setContentView(R.layout.activity_search_result)
        listView = findViewById(R.id.listViewSearchResult)

        // SearchActivity에서 전달된 searchText를 변수에 저장
        searchText = intent.getStringExtra("searchText")!!
        curLat = intent.getDoubleExtra("curLat",0.0)
        curLon = intent.getDoubleExtra("curLon",0.0)
        if (intent.hasExtra("startPoint")) startPoint = intent.getParcelableExtra("startPoint")!!
        if (intent.hasExtra("endPoint")) endPoint = intent.getParcelableExtra("endPoint")!!
        Log.d("SearchResult startPoint", startPoint.name)
        Log.d("SearchResult endPoint", endPoint.name)

        searchBar2 = findViewById(R.id.searchingBar2)
        searchBar2.setText(searchText)
        loadingMessage = findViewById(R.id.loadingMessage)
        loadingImage = findViewById(R.id.loadingImage)
        failImage = findViewById(R.id.failImage)

        client = OkHttpClient()
        credentials = Credentials.basic(username, password)

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
        val url = "http://3.25.65.146:8080/maps/locations?query=$searchQuery&coordinate.latitude=$curLat&coordinate.longitude=$curLon"

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val request = Request.Builder()
                    .url(url)
                    .header("Authorization", credentials)
                    .build()

                client.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) throw IOException("Unexpected code $response")

                    val responseBody = response.body!!.string()
                    Logr.d("SR응답확인", responseBody)

                    // UI 업데이트는 메인 스레드에서 수행
                    withContext(Dispatchers.Main) {
                        updateUI(responseBody)
                        loadingMessage.visibility = View.INVISIBLE  // 응답 처리 후 사라짐
                        loadingImage.visibility = View.INVISIBLE
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@SearchResultActivity, "네트워크 오류가 발생했습니다. 다시 시도해주세요.", Toast.LENGTH_LONG).show()
                }
                Log.e("SR네트워크 에러", "요청 중 오류 발생: ${e.message}", e)
            }
        }
    }

    private fun updateUI(responseData: String) {
        val jsonResponse = JSONObject(responseData)
        val dataObject = jsonResponse.optJSONObject("data")

        if(dataObject?.optJSONArray("items") == null || dataObject.getJSONArray("items").length() == 0 ){
            //검색 결과가 없거나, items 배열이 비었을 경우
            Log.e("검색결과가 없거나 items배열이 비었음","검색결과가 없거나 items배열이 비었음")
            runOnUiThread {
                Toast.makeText(this, "검색 결과가 없습니다.", Toast.LENGTH_LONG).show()
                loadingImage.visibility = View.GONE
                listView.visibility = View.GONE
                failImage.visibility = View.VISIBLE
            }
        } else{
            val items = dataObject.getJSONArray("items")
            val placesList = mutableListOf<Place>()
            for(i in 0 until items.length()){
                val item = items.getJSONObject(i)
                val geocoding = item.getJSONObject("geocoding")
                val addresses = geocoding.getJSONArray("addresses").getJSONObject(0)

                val name = item.getString("title").replace("<b>","").replace("</b>","") //HTML태그제거
                val address = item.getString("address")
                val category = item.getString("category")
                val latitude = addresses.getDouble("y")
                val longitude = addresses.getDouble("x")
                val distance = addresses.getDouble("distance")

                //Place 객체 생성
                val place = Place(name, address, category, latitude, longitude, distance)
                placesList.add(place)
            }
            // UI 스레드에서 리스트뷰에 데이터 설정
            runOnUiThread {
                val adapter = SearchResultAdapter(this, R.layout.list_search_result_item, placesList, startPoint, endPoint)
                listView.adapter = adapter
            }
        }
    }

}