package com.hansung.roadbuddyandroid

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomsheet.BottomSheetBehavior

class PlaceViewActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_place_view)

        // 인텐트에서 선택된 장소 데이터 받기
        val selectedPlace = intent.getStringExtra("selectedPlace")

        // 받은 텍스트를 TextView에 설정
        val textView = findViewById<TextView>(R.id.tv_activity_place_view1)
        textView.text = selectedPlace ?: "장소 정보 없음"
    }
}