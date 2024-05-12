package com.hansung.roadbuddyandroid

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.Dash
import com.google.android.gms.maps.model.Gap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.google.gson.Gson
import com.google.maps.android.PolyUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.Credentials
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException


class DetailActivity : AppCompatActivity() , OnMapReadyCallback {
    private lateinit var routeFromBFA: Route
    private val username = "user"
    private lateinit var password : String
    private lateinit var credentials : String
    private lateinit var client : OkHttpClient
    private lateinit var mMap : GoogleMap
    private lateinit var mapFragment: SupportMapFragment
    private lateinit var listViewDetail : ListView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        listViewDetail = findViewById(R.id.listViewDetail)
        routeFromBFA = intent.getParcelableExtra("route")!!
        val testLeg = routeFromBFA.legs[0]
        password = getString(R.string.API_KEY)
        client = OkHttpClient()
        credentials = Credentials.basic(username, password)
        mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        Logr.d("디테일 요청Leg 확인", testLeg.toString())
        makeNetworkRequest(testLeg)
    }
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(37.582701, 127.010274), 17f))
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
                    Logr.d("디테일 응답확인", responseBody)

                    val jsonResponse = JSONObject(responseBody)
                    val dataString = jsonResponse.getJSONObject("data").toString()

                    val leg = Gson().fromJson(dataString, Leg::class.java)
                    Logr.d("레그 변환 확인",leg.toString())
                    withContext(Dispatchers.Main) {
                        updateMapWithData(leg)
                        updateListViewWithData(leg)
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
    private fun updateMapWithData(leg: Leg) {
        // Draw polylines for each step
        leg.steps.forEach { step ->
            val polylineOptions = PolylineOptions()
            val pattern = listOf(Dash(20f), Gap(20f)) // 점선 패턴 설정: 선 20px, 공간 20px
            val color = if (step.travelMode == "TRANSIT" && step.transitDetails != null) {
                step.transitDetails.line.color
            } else {
                "#A9A9A9"  // Default color for walking mode
            }

            val decodedPath = PolyUtil.decode(step.polyline.points)
            polylineOptions.addAll(decodedPath)
            polylineOptions.color(Color.parseColor(color))
            polylineOptions.width(15f)
            if (step.travelMode == "WALKING") {
                polylineOptions.pattern(pattern)  // 걷기 모드일 때만 점선 적용
            }
            mMap.addPolyline(polylineOptions)
        }

        // 급경사지를 위한 노란마커
        leg.steps.flatMap { step -> step.steepSlopes ?: emptyList() }.forEach { slope ->
            mMap.addMarker(
                MarkerOptions()
                    .position(LatLng(slope.latitude, slope.longitude))
                    .title("급경사")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW))
            )
        }

        // Marker for the starting location (red marker)
        mMap.addMarker(
            MarkerOptions()
                .position(LatLng(leg.startLocation.lat, leg.startLocation.lng))
                .title("출발지")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
        )

        // Marker for the ending location (red marker)
        mMap.addMarker(
            MarkerOptions()
                .position(LatLng(leg.endLocation.lat, leg.endLocation.lng))
                .title("도착지")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
        )

        // Use provided bounds to initialize camera view
        val bounds = LatLngBounds(
            LatLng(routeFromBFA.bounds.southwest.lat, routeFromBFA.bounds.southwest.lng), // Southwest corner
            LatLng(routeFromBFA.bounds.northeast.lat, routeFromBFA.bounds.northeast.lng)  // Northeast corner
        )

        // Adjust the camera to the bounds with some padding
        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100)) // 100px padding
    }
    private fun updateListViewWithData(leg: Leg){
        listViewDetail.adapter = DetailAdapter(context = this, steps = leg.steps)
        listViewDetail.setOnItemClickListener { adapterView, view, position, id ->
            val step = adapterView.getItemAtPosition(position) as Step
            val latLng = LatLng(step.startLocation.lat, step.startLocation.lng)
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17f)) // 15f는 확대 수준입니다.
        }
    }
}