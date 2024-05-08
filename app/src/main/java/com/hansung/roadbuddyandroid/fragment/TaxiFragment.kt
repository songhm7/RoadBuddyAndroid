package com.hansung.roadbuddyandroid.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.hansung.roadbuddyandroid.Place
import com.hansung.roadbuddyandroid.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.time.LocalDateTime
import java.util.Random

class TaxiFragment : Fragment() {
    private lateinit var taxiBody : TextView
    private lateinit var textViewFragmentTaxi: TextView
    private lateinit var image : ImageView
    private lateinit var expect : TextView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_taxi, container, false)
        arguments?.let {
            val startPoint = it.getParcelable<Place>("startPoint")
            val endPoint = it.getParcelable<Place>("endPoint")
            taxiBody = view.findViewById(R.id.textViewBodyTaxi)
            textViewFragmentTaxi = view.findViewById(R.id.textViewFragmentTaxi)
            image = view.findViewById(R.id.ImageLoadingFT)
            expect = view.findViewById(R.id.expect)
            if (startPoint != null && endPoint != null) {
                makeNetworkRequest(startPoint, endPoint)
            }
        }
        return view
    }

    private fun makeNetworkRequest(startPoint: Place, endPoint: Place) {
        val client = OkHttpClient()
        val url = "http://3.25.65.146:8080/maps/drive?end.longitude=${endPoint.longitude}&end.latitude=${endPoint.latitude}" +
                "&start.longitude=${startPoint.longitude}&start.latitude=${startPoint.latitude}"

        val request = Request.Builder()
            .url(url)
            .build()
        CoroutineScope(Dispatchers.IO).launch {
                client.newCall(request).execute().use { response ->
                    if (response.isSuccessful) {
                        val responseData = response.body?.string()
                        if (responseData != null) {
                            val jsonResponse = JSONObject(responseData)
                            withContext(Dispatchers.Main) {
                                updateUI(jsonResponse)
                            }
                        }
                    } else {
                        Log.e("TaxiFragment", "Network request failed with code ${response.code}")
                    }
                }
            }
        }

    private fun updateUI(jsonResponse: JSONObject){
        // JSON에서 필요한 정보 추출
        val features = jsonResponse.getJSONObject("data").getJSONArray("features")
        val properties = features.getJSONObject(0).getJSONObject("properties")

        var totalDistance = properties.getInt("totalDistance")
        var totalDistanceDisplay = if (totalDistance < 1000) {
            "${totalDistance}m"
        } else {
            String.format("%.2fkm", totalDistance / 1000.0)
        }

        var totalTime = properties.getInt("totalTime")
        var totalTimeDisplay = if(totalTime < 60){
            "${totalTime}분"
        } else{
            "${totalTime/60}시간 ${totalTime%60}분"
        }

        val taxiFare = properties.getInt("taxiFare")
        val callTime = generateRandomWithTimeSeed()
        var callFare = if(totalDistance <=5000) {
            1500
        } else if(totalDistance <=10000){
            1500 + (totalDistance - 5000)/1000 * 280
        } else{
            2900 + (totalDistance - 10000) / 1000 * 70
        }
        if (callFare%100 != 0){
            callFare = callFare - callFare%100
        }

        taxiBody.setText("총 이동거리 : ${totalDistanceDisplay}\n\n" +
                "이동 소요시간 : ${totalTimeDisplay}\n\n" +
                "일반택시 요금 : ${taxiFare}원\n\n" +
                "장애인 콜택시 대기시간 : ${callTime}분\n\n" +
                "장애인 콜택시 요금 : ${callFare}원\n\n")

        textViewFragmentTaxi.visibility = View.GONE
        image.visibility = View.GONE
        taxiBody.visibility = View.VISIBLE
        expect.visibility = View.VISIBLE
        // 추출한 정보를 로그로 출력
        Log.d("TF응답확인", "Total Distance: $totalDistanceDisplay, Total Time: $totalDistanceDisplay, Taxi Fare: $taxiFare," +
                "콜택시 대기시간 : $callTime, 콜택시 요금 : $callFare 원")
    }

    companion object {
        fun newInstance(startPoint: Place, endPoint: Place): TaxiFragment {
            val fragment = TaxiFragment()
            val args = Bundle()
            args.putParcelable("startPoint", startPoint)
            args.putParcelable("endPoint", endPoint)
            fragment.arguments = args
            return fragment
        }
    }
    fun generateRandomWithTimeSeed(): Int {
        val currentTime = LocalDateTime.now()
        // 10분 단위로 같은 seed 값을 사용하기 위해 시간과 분을 이용해 seed 계산
        val seed = currentTime.hour * 60 + (currentTime.minute / 10)
        val random = Random(seed.toLong())

        // 20에서 60 사이의 난수 생성
        return random.nextInt(41) + 20  // 41은 (60-20+1)의 결과
    }
}