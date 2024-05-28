package com.hansung.roadbuddyandroid

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.hansung.roadbuddyandroid.fragment.BusFragment
import com.hansung.roadbuddyandroid.fragment.TaxiFragment

class PathMakeActivity : AppCompatActivity() {
    private var startPoint = Place("출발지미정","","",0.0,0.0,0.0)
    private var endPoint = Place("도착지미정","","",0.0,0.0,0.0)
    private lateinit var tvStart: TextView
    private lateinit var tvEnd: TextView
    private lateinit var imgBtnBus : ImageButton
    private lateinit var imgBtnTaxi : ImageButton
    private lateinit var fragment : View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_make_path)

        tvStart = findViewById(R.id.tv_start)
        tvEnd = findViewById(R.id.tv_end)
        imgBtnBus = findViewById(R.id.img_button_bus)
        imgBtnTaxi = findViewById(R.id.img_button_taxi)
        fragment = findViewById(R.id.fragment_mpxml)

        imgBtnBus.visibility = View.GONE  // 처음에는 버튼을 숨김
        imgBtnTaxi.visibility = View.GONE  // 처음에는 버튼을 숨김

        if (intent.hasExtra("startPoint")) startPoint = intent.getParcelableExtra("startPoint")!!
        if (intent.hasExtra("endPoint")) endPoint = intent.getParcelableExtra("endPoint")!!

        tvStart.setText(startPoint.name)
        tvEnd.setText(endPoint.name)
        findViewById<ImageButton>(R.id.button_swap).setOnClickListener {
            val tmp = startPoint
            startPoint = endPoint
            endPoint = tmp
            if(startPoint.name == "도착지미정") startPoint.name = "출발지미정"
            if(endPoint.name == "출발지미정") endPoint.name = "도착지미정"
            tvStart.setText(startPoint.name)
            tvEnd.setText(endPoint.name)
        }
        findViewById<ImageButton>(R.id.button_path_make_begin).setOnClickListener {
            if(startPoint.name=="출발지미정"||endPoint.name=="도착지미정")
                Toast.makeText(this@PathMakeActivity, "출발지와 도착지를 모두 설정해주세요", Toast.LENGTH_LONG).show()
            else if(startPoint.name == endPoint.name){
                Toast.makeText(this@PathMakeActivity, "출발지와 도착지가 같습니다", Toast.LENGTH_LONG).show()
            }
            else {
                showFragment(BusFragment.newInstance(startPoint, endPoint))
                imgBtnBus.visibility = View.VISIBLE
                imgBtnTaxi.visibility = View.VISIBLE
            }
        }

        tvStart.setOnClickListener {
            val intent = Intent(this, SearchActivity::class.java)
            if (endPoint.name != "도착지미정"){
                intent.apply {
                    putExtra("endPoint", endPoint)
                    flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                }
            }
            startActivity(intent)
        }
        tvEnd.setOnClickListener {
            val intent = Intent(this, SearchActivity::class.java)
            if (startPoint.name != "출발지미정"){
                intent.apply {
                    putExtra("startPoint", startPoint)
                    flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                }
            }
            startActivity(intent)
        }
        imgBtnBus.setOnClickListener {
            val fragment = BusFragment.newInstance(startPoint, endPoint)
            showFragment(fragment)
            replaceFragment(fragment)
            imgBtnBus.setBackgroundColor(Color.parseColor("#049EDB"))
            imgBtnTaxi.setBackgroundColor(Color.parseColor("#FFFFFF"))
        }
        imgBtnTaxi.setOnClickListener {
            val fragment = TaxiFragment.newInstance(startPoint, endPoint)
            showFragment(fragment)
            replaceFragment(fragment)
            imgBtnBus.setBackgroundColor(Color.parseColor("#FFFFFF"))
            imgBtnTaxi.setBackgroundColor(Color.parseColor("#049EDB"))
        }
    }

    fun showFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().replace(R.id.fragment_mpxml, fragment).commit()
    }
    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager.beginTransaction()

        // Retrieve the current fragment using the fragment tag (if set) or fragment ID
        val currentFragment = supportFragmentManager.findFragmentById(R.id.fragment_mpxml)

        // If a fragment is currently displayed
        if (currentFragment != null) {
            // Remove the current fragment from the back stack (if it's there)
            fragmentManager.remove(currentFragment)
        }

        // Replace the container with the new fragment
        fragmentManager.replace(R.id.fragment_mpxml, fragment)
        fragmentManager.commit()
    }
}