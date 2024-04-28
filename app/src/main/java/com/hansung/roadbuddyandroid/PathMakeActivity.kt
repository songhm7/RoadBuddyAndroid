package com.hansung.roadbuddyandroid

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.hansung.roadbuddyandroid.fragment.BusFragment
import com.hansung.roadbuddyandroid.fragment.TaxiFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.Credentials
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException

class PathMakeActivity : AppCompatActivity() {
    private var startPoint = Place("출발지미정","","",0.0,0.0,0.0)
    private var endPoint = Place("도착지미정","","",0.0,0.0,0.0)
    private lateinit var tvStart: TextView
    private lateinit var tvEnd: TextView
    private lateinit var client: OkHttpClient
    private lateinit var tvbtnbus : TextView
    private lateinit var tvbtntexi : TextView
    // Basic 인증을 위한 사용자 이름과 비밀번호 설정
    private val username = "user"
    private lateinit var password : String
    private lateinit var credentials: String
    private lateinit var fragment : View
    private lateinit var responseTMP : String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_make_path)
        password = getString(R.string.API_KEY)
        client = OkHttpClient()
        credentials = Credentials.basic(username, password)

        tvStart = findViewById(R.id.tv_start)
        tvEnd = findViewById(R.id.tv_end)
        tvbtnbus = findViewById(R.id.tvbutton_bus)
        tvbtntexi = findViewById(R.id.tvbutton_texi)
        fragment = findViewById(R.id.fragment_mpxml)

        tvbtnbus.visibility = View.GONE  // 처음에는 버튼을 숨김
        tvbtntexi.visibility = View.GONE  // 처음에는 버튼을 숨김

        if (intent.hasExtra("startPoint")) startPoint = intent.getParcelableExtra("startPoint")!!
        if (intent.hasExtra("endPoint")) endPoint = intent.getParcelableExtra("endPoint")!!

        tvStart.setText(startPoint.name)
        tvEnd.setText(endPoint.name)
        findViewById<Button>(R.id.button_exchange).setOnClickListener {
            val tmp = startPoint
            startPoint = endPoint
            endPoint = tmp
            if(startPoint.name == "도착지미정") startPoint.name = "출발지미정"
            if(endPoint.name == "출발지미정") endPoint.name = "도착지미정"
            tvStart.setText(startPoint.name)
            tvEnd.setText(endPoint.name)
        }
        findViewById<Button>(R.id.button_path_make_begin).setOnClickListener {
            if(startPoint.name=="출발지미정"||endPoint.name=="도착지미정")
                Toast.makeText(this@PathMakeActivity, "출발지와 도착지를 모두 설정해주세요", Toast.LENGTH_LONG).show()
            else if(startPoint.name == endPoint.name){
                Toast.makeText(this@PathMakeActivity, "출발지와 도착지가 같습니다", Toast.LENGTH_LONG).show()
            }
            else {
                makeNetworkRequest(startPoint, endPoint)
                showFragment(BusFragment.newInstance())
                tvbtnbus.visibility = View.VISIBLE
                tvbtntexi.visibility = View.VISIBLE
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
        tvbtnbus.setOnClickListener {
            val fragment = BusFragment.newInstance()
            fragment.setData(responseTMP)  // 데이터 설정
            replaceFragment(fragment)
        }
        tvbtntexi.setOnClickListener {
            val fragment = TaxiFragment.newInstance()
            //fragment.setData(responseTaxiTMP)  // 데이터 설정
            replaceFragment(fragment)
        }
    }
    private fun makeNetworkRequest(startPoint : Place, endPoint : Place) {
        val url = "http://3.25.65.146:8080/maps/directions?origin.latitude=${startPoint.latitude}&origin.longitude=${startPoint.longitude}" +
                "&destination.latitude=${endPoint.latitude}&destination.longitude=${endPoint.longitude}&alternatives=false"

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val request = Request.Builder()
                    .url(url)
                    .header("Authorization", credentials)
                    .build()

                client.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) throw IOException("Unexpected code $response")

                    val responseBody = response.body!!.string()
                    Logr.d("PM응답확인", responseBody)
                    responseTMP = responseBody

                    // UI 업데이트는 메인 스레드에서 수행
                    withContext(Dispatchers.Main) {
                        val fragment = BusFragment.newInstance()
                        fragment.setData(responseBody)  // 데이터 설정
                        replaceFragment(fragment)
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@PathMakeActivity, "네트워크 오류가 발생했습니다. 다시 시도해주세요.", Toast.LENGTH_LONG).show()
                }
                Log.e("네트워크 오류", "요청 중 오류 발생: ${e.message}")
            }
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