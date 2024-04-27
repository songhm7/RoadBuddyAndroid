package com.hansung.roadbuddyandroid

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class PathMakeActivity : AppCompatActivity() {
    private var startPoint = "출발지미정"
    private var endPoint = "도착지미정"
    private lateinit var tvStart: TextView
    private lateinit var tvEnd: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_make_path)
        tvStart = findViewById(R.id.tv_start)
        tvEnd = findViewById(R.id.tv_end)

        if (intent.hasExtra("startPoint")) startPoint = intent.getStringExtra("startPoint")!!
        if (intent.hasExtra("endPoint")) endPoint = intent.getStringExtra("endPoint")!!

        tvStart.setText(startPoint)
        tvEnd.setText(endPoint)
        findViewById<Button>(R.id.button_exchange).setOnClickListener {
            val tmp = startPoint
            startPoint = endPoint
            endPoint = tmp
            if(startPoint == "도착지미정") startPoint = "출발지미정"
            if(endPoint == "출발지미정") endPoint = "도착지미정"
            tvStart.setText(startPoint)
            tvEnd.setText(endPoint)
        }
        findViewById<Button>(R.id.button_path_make_begin).setOnClickListener {
            Toast.makeText(this, "출발지 : ${startPoint}\n도착지 : ${endPoint}", Toast.LENGTH_SHORT).show()
        }

        tvStart.setOnClickListener {
            val intent = Intent(this, SearchActivity::class.java)
            if (endPoint != "도착지미정"){
                intent.apply {
                    putExtra("endPoint", endPoint)
                    flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                }
            }
            startActivity(intent)
        }
        tvEnd.setOnClickListener {
            val intent = Intent(this, SearchActivity::class.java)
            if (startPoint != "출발지미정"){
                intent.apply {
                    putExtra("startPoint", startPoint)
                    flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                }
            }
            startActivity(intent)
        }
    }
}