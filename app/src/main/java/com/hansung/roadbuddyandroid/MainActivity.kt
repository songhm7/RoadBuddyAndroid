package com.hansung.roadbuddyandroid

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.RelativeLayout
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions


class MainActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var mMap: GoogleMap
    private lateinit var scaleSeekBar: SeekBar
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val hansung = LatLng(37.582701, 127.010274)
    private lateinit var curLocation: LatLng
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback
    private val initialZoomLevel = 17f // 초기 축척 설정

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 현위치,GPS 관련 초기화
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        //위치권한 동의
        checkLocationPermission()

        //자동 현위치 업데이트
        setupLocationUpdates()

        // 지도 fragment 초기화
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        mapFragment.getMapAsync { googleMap ->
            // 사용자 위치 활성화 (내 위치 버튼 포함)
            googleMap.isMyLocationEnabled = true
        }

        // 내 위치 버튼의 참조를 얻기
        val locationButton = (mapFragment.view?.findViewById<View>(Integer.parseInt("1"))?.parent as View).findViewById<View>(Integer.parseInt("2"))

        // 버튼의 위치 속성 조정
        val layoutParams = locationButton.layoutParams as RelativeLayout.LayoutParams

        // 오른쪽 하단에 위치시키기
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0)
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE)
        layoutParams.setMargins(0, 0, 30, 30)

        //개발중 사용할 축척조절바 초기화
        scaleSeekBar = findViewById(R.id.scaleSeekBar)
        scaleSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                // 사용자가 SeekBar를 조작할 때마다 호출할 함수
                // 지도의 줌 레벨을 조절
                mMap.animateCamera(CameraUpdateFactory.zoomTo(progress.toFloat() * 1.5f))
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                // 사용자가 SeekBar를 터치할 경우 호출할 함수
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                // 사용자가 SeekBar 조작을 멈출 때 호출할 함수
            }
        })
        val searchingBar = findViewById<TextView>(R.id.searchingBar)

        //검색창에 커서올리면 액티비티 이동
        searchingBar.setOnClickListener {
            val intent = Intent(this, SearchActivity::class.java)
            startActivity(intent)
        }

        }



    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // 마커 추가
        mMap.addMarker(MarkerOptions().position(hansung).title("한성대학교"))
        // 지도 이동
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(hansung, initialZoomLevel))

    }

    //위치정보권한확인
    private fun checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {

            // 권한이 없는 경우, 사용자에게 권한 요청
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                MY_PERMISSIONS_REQUEST_LOCATION
            )
        } else {
            // 권한이 이미 있으면 위치 정보를 가져옴
            fusedLocationClient?.lastLocation
                ?.addOnSuccessListener(this) { location ->
                    // GPS가 꺼져있거나 최근 위치 정보가 없을 때 location은 null일 수 있음
                    if (location != null) {
                        curLocation = LatLng(location.latitude, location.longitude)
                        // 위치 정보 사용
                        Log.d(
                            "checkLocation 현위치가져오기성공",
                            "${location.latitude}, ${location.longitude}"
                        )
                    } else {
                        Log.d("checkLocationPermission", "location null값 발생")
                    }
                }
        }
    }

    //위치정보권한동의
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            MY_PERMISSIONS_REQUEST_LOCATION -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // 권한이 승인됨. 위치 정보를 가져올 수 있음.
                    if (ContextCompat.checkSelfPermission(
                            this,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        checkLocationPermission()
                    }
                } else {
                    // 권한이 거부됨. 권한 요청에 대한 처리 필요.
                    Log.d("onRequestPermissionsResult", "권한거부")
                }
                return
            }
            // 다른 'case' 라인을 추가하여 다른 권한의 결과를 처리할 수 있음.
        }
    }
    companion object {
        const val MY_PERMISSIONS_REQUEST_LOCATION = 99
    }

    private fun setupLocationUpdates() {
        locationRequest = LocationRequest.create()!!.apply {
            interval = 10000  // 10초 간격으로 업데이트 (필요에 따라 조정)
            fastestInterval = 5000  // 가장 빠른 간격 (필요에 따라 조정)
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult ?: return
                for (location in locationResult.locations){
                    // 여기서 위치 정보를 사용
                    curLocation = LatLng(location.latitude, location.longitude)

                }
            }
        }
        // 위치 업데이트 요청 시작
        startLocationUpdates()
    }

    private fun startLocationUpdates() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.requestLocationUpdates(locationRequest,
                locationCallback,
                Looper.getMainLooper())
        }
    }

    // 위치 업데이트 중지하는 메소드
    private fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }
    override fun onStop() {
        super.onStop()
        stopLocationUpdates()
    }
}
