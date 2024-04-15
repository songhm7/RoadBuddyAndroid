package com.hansung.roadbuddyandroid

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.Credentials
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.io.IOException

class PlaceViewActivity : AppCompatActivity(), OnMapReadyCallback {
    // Basic 인증을 위한 사용자 이름과 비밀번호 설정
    private val username = "user"
    private val password = "4fd3fcbb-4825-4fd0-a28b-f31b1d4ed718"
    private val hansung = LatLng(37.582701, 127.010274)

    private lateinit var credentials: String
    private lateinit var client: OkHttpClient
    private lateinit var placeName : String
    private lateinit var locationFromPVA : LatLng
    private lateinit var bottomName : TextView
    private lateinit var bottomAddress : TextView
    private lateinit var mMap: GoogleMap
    private lateinit var mapFragment : SupportMapFragment
    private val initialZoomLevel = 17f // 초기 축척 설정

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_place_view)
        findViewById<ImageButton>(R.id.backButton).setOnClickListener {
            onBackPressed()
        }
        findViewById<ImageButton>(R.id.buttonToHome).setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            intent.putExtra("latitude", locationFromPVA.latitude)
            intent.putExtra("longitude", locationFromPVA.longitude)
            startActivity(intent)
            finish() // 현재 액티비티 종료
        }
        bottomName = findViewById(R.id.bottom_name)
        bottomAddress = findViewById(R.id.bottom_address)

        // 인텐트에서 선택된 장소 데이터 받기
        val selectedPlace = intent.getStringExtra("selectedPlace")
        placeName = selectedPlace!!
        findViewById<TextView>(R.id.tv_activity_place_view1).setText(placeName)
        bottomName.setText(placeName)

        mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        client = OkHttpClient()
        credentials = Credentials.basic(username, password)
        makeNetworkRequest(placeName)

    }
    private fun makeNetworkRequest(searchQuery: String) {
        val url = "http://3.25.65.146:8080/maps/geocoding?address=$searchQuery"

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
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@PlaceViewActivity, "네트워크 오류가 발생했습니다. 다시 시도해주세요.", Toast.LENGTH_LONG).show()
                }
                Log.e("네트워크 오류", "요청 중 오류 발생: ${e.message}")
            }
        }
    }
    private fun updateUI(responseData: String) {
        val jsonResponse = JSONObject(responseData)
        val results = jsonResponse.getJSONObject("data").getJSONArray("results")
        if (results.length() > 0) {
            val firstResult = results.getJSONObject(0)
            val formattedAddress = firstResult.getString("formatted_address")
            val location = firstResult.getJSONObject("geometry").getJSONObject("location")
            val lat = location.getDouble("lat")
            val lng = location.getDouble("lng")

            // Location 객체 생성
            locationFromPVA = LatLng(lat, lng)

            // UI 업데이트
            runOnUiThread {
                bottomAddress.text = formattedAddress
                Log.d("Location", "Latitude: $lat, Longitude: $lng")
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(locationFromPVA, initialZoomLevel))
                mMap.addMarker(MarkerOptions().position(locationFromPVA).title(placeName))
            }
        } else {
            runOnUiThread {
                Toast.makeText(this@PlaceViewActivity, "결과가 없습니다.", Toast.LENGTH_LONG).show()
            }
        }
    }
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(hansung, 100f))
    }
}