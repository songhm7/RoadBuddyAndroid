package com.hansung.roadbuddyandroid

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class PlaceViewActivity : AppCompatActivity(), OnMapReadyCallback {
    // Basic 인증을 위한 사용자 이름과 비밀번호 설정

    private val curLocation = LatLng(37.582701, 127.010274)
    private var startPoint = "출발지미정"
    private var endPoint = "도착지미정"
    private lateinit var locationFromPVA : LatLng
    private lateinit var mMap: GoogleMap
    private lateinit var mapFragment : SupportMapFragment
    private val initialZoomLevel = 17f // 초기 축척 설정
    private lateinit var selectedPlace : Place

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_place_view)
        if (intent.hasExtra("startPoint")) startPoint = intent.getStringExtra("startPoint")!!
        if (intent.hasExtra("endPoint")) endPoint = intent.getStringExtra("endPoint")!!
        Log.d("PlaceView startPoint", startPoint)
        Log.d("PlaceView endPoint", endPoint)
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
        val bottomName = findViewById<TextView>(R.id.bottom_name)
        val bottomAddress = findViewById<TextView>(R.id.bottom_address)

        // 인텐트에서 선택된 장소 데이터 받기
        selectedPlace = intent.getParcelableExtra<Place>("selectedPlace")!!
        findViewById<TextView>(R.id.tv_activity_place_view1).setText(selectedPlace.name)
        bottomName.setText(selectedPlace.name)
        bottomAddress.setText(selectedPlace.address)

        mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        locationFromPVA = LatLng(selectedPlace.latitude,selectedPlace.longitude)


        findViewById<Button>(R.id.button_start_point).setOnClickListener{
            val intent = Intent(this, PathMakeActivity::class.java).apply {
                putExtra("startPoint", selectedPlace.name)
                putExtra("endPoint", endPoint)
            }
            startActivity(intent)
        }
        findViewById<Button>(R.id.button_end_point).setOnClickListener{
            val intent = Intent(this, PathMakeActivity::class.java).apply {
                putExtra("endPoint", selectedPlace.name)
                putExtra("startPoint", startPoint)
            }
            startActivity(intent)
        }

    }
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(locationFromPVA, initialZoomLevel))
        mMap.addMarker(MarkerOptions().position(locationFromPVA).title(selectedPlace.name))
    }
}