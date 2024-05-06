package com.hansung.roadbuddyandroid.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

class TaxiFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        arguments?.let {
            val startPoint = it.getParcelable<Place>("startPoint")
            val endPoint = it.getParcelable<Place>("endPoint")
            if (startPoint != null && endPoint != null) {
                makeNetworkRequest(startPoint, endPoint)
            }
        }
        return inflater.inflate(R.layout.fragment_taxi, container, false)
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

        val totalDistance = properties.getInt("totalDistance")
        val totalTime = properties.getInt("totalTime")
        val taxiFare = properties.getInt("taxiFare")

        // 추출한 정보를 로그로 출력
        Log.d("TF응답확인", "Total Distance: $totalDistance, Total Time: $totalTime, Taxi Fare: $taxiFare")    }

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
}