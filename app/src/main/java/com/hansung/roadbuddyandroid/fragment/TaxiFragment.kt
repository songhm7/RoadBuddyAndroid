package com.hansung.roadbuddyandroid.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
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
    private lateinit var loadingText: TextView
    private lateinit var loadingImage: ImageView

    private lateinit var moveTimeLabel: TextView
    private lateinit var tvMoveTime: TextView

    private lateinit var seoulFeeLabel: TextView
    private lateinit var tvSeoulFee: TextView

    private lateinit var normalFeeLabel: TextView
    private lateinit var tvNormalFee: TextView

    private lateinit var constraintTaxi: ConstraintLayout
    private lateinit var TaxiImage: ImageView
    private lateinit var moveDistance: TextView

    private lateinit var allWaitingLabel: TextView
    private lateinit var tvAllWaiting: TextView

    private lateinit var nearWaitingLabel: TextView
    private lateinit var tvNearWaiting: TextView

    private lateinit var meanWaitingLabel: TextView
    private lateinit var tvMeanWaiting: TextView

    private lateinit var callButton : TextView


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_taxi, container, false)
        arguments?.let {
            val startPoint = it.getParcelable<Place>("startPoint")
            val endPoint = it.getParcelable<Place>("endPoint")

            loadingText = view.findViewById(R.id.textViewFragmentTaxi)
            loadingImage = view.findViewById(R.id.ImageLoadingFT)
            moveTimeLabel = view.findViewById(R.id.moveTimeLabel)
            tvMoveTime = view.findViewById(R.id.tv_moveTime)
            seoulFeeLabel = view.findViewById(R.id.seoulFeeLabel)
            tvSeoulFee = view.findViewById(R.id.tv_seoulFee)
            normalFeeLabel = view.findViewById(R.id.normalFeeLabel)
            tvNormalFee = view.findViewById(R.id.tv_normalFee)
            constraintTaxi = view.findViewById(R.id.constraintTaxi)
            TaxiImage = view.findViewById(R.id.imageTaxi)
            moveDistance = view.findViewById(R.id.moveDistance)
            allWaitingLabel = view.findViewById(R.id.allWaitingLabel)
            tvAllWaiting = view.findViewById(R.id.tv_allWaiting)
            nearWaitingLabel = view.findViewById(R.id.nearWaitingLabel)
            tvNearWaiting = view.findViewById(R.id.tv_nearWaiting)
            meanWaitingLabel = view.findViewById(R.id.meanWaitingLabel)
            tvMeanWaiting = view.findViewById(R.id.tv_meanWaiting)
            callButton = view.findViewById(R.id.button_callNow)

            if (startPoint != null && endPoint != null) {
                goneAllExceptLoading()
                makeNetworkRequest(startPoint, endPoint)
            }
            callButton.setOnClickListener {
                Toast.makeText(requireContext(), "콜 요청을 발송했습니다", Toast.LENGTH_LONG).show()
            }
        }
        return view
    }

    private fun makeNetworkRequest(startPoint: Place, endPoint: Place) {
        val client = OkHttpClient()
        val url =
            "http://3.25.65.146:8080/maps/drive?end.longitude=${endPoint.longitude}&end.latitude=${endPoint.latitude}" +
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
    private fun makeNetworkRequestForWaiting(){
        val client = OkHttpClient()
        val url =
            "http://3.25.65.146:8080/taxi/waiting"
        val request = Request.Builder()
            .url(url)
            .build()
        CoroutineScope(Dispatchers.IO).launch{
            client.newCall(request).execute().use{response ->
                if(response.isSuccessful){
                    val responseData = response.body?.string()
                    if(responseData != null){
                        val jsonResponse = JSONObject(responseData)
                        withContext(Dispatchers.Main){
                            updateUIWithWaiting(jsonResponse)
                        }
                    }
                } else{
                    Log.e("TaxiFragment","Network request failed with code ${response.code}")
                }

            }
        }
    }
    private fun updateUI(jsonResponse: JSONObject) {
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
        val totalTimeDisplay = when {
            totalTime < 60 -> "${totalTime}초"
            totalTime >= 3600 -> {
                val hours = totalTime / 3600
                val minutes = (totalTime % 3600) / 60
                "${hours}시간 ${minutes}분"
            }
            else -> "${totalTime / 60}분"
        }

        val taxiFare = properties.getInt("taxiFare")
        var callFare = if (totalDistance <= 5000) {
            1500
        } else if (totalDistance <= 10000) {
            1500 + (totalDistance - 5000) / 1000 * 280
        } else {
            2900 + (totalDistance - 10000) / 1000 * 70
        }
        if (callFare % 100 != 0) {
            callFare = callFare - callFare % 100
        }
        tvMoveTime.setText(totalTimeDisplay)
        tvSeoulFee.setText("${callFare}원")
        tvNormalFee.setText("${taxiFare}원")
        moveDistance.setText("이동거리 : ${totalDistanceDisplay}")

        makeNetworkRequestForWaiting()
    }
    private fun updateUIWithWaiting(jsonResponse: JSONObject) {
        Log.d("testtesttest",jsonResponse.toString())
        val meanWaiting = jsonResponse.getJSONObject("data").getString("time")
        tvMeanWaiting.setText("${meanWaiting}")
        visibleAllExceptLoading()
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

    fun goneAllExceptLoading(){
        loadingText.visibility = View.VISIBLE
        loadingImage.visibility = View.VISIBLE
        moveTimeLabel.visibility = View.GONE
        tvMoveTime.visibility = View.GONE
        seoulFeeLabel.visibility = View.GONE
        tvSeoulFee.visibility = View.GONE
        normalFeeLabel.visibility = View.GONE
        tvNormalFee.visibility = View.GONE
        constraintTaxi.visibility = View.GONE
        TaxiImage.visibility = View.GONE
        moveDistance.visibility = View.GONE
        allWaitingLabel.visibility = View.GONE
        tvAllWaiting.visibility = View.GONE
        nearWaitingLabel.visibility = View.GONE
        tvNearWaiting.visibility = View.GONE
        meanWaitingLabel.visibility = View.GONE
        tvMeanWaiting.visibility = View.GONE
        callButton.visibility = View.GONE
    }
    fun visibleAllExceptLoading(){
        loadingText.visibility = View.GONE
        loadingImage.visibility = View.GONE
        moveTimeLabel.visibility = View.VISIBLE
        tvMoveTime.visibility = View.VISIBLE
        seoulFeeLabel.visibility = View.VISIBLE
        tvSeoulFee.visibility = View.VISIBLE
        normalFeeLabel.visibility = View.VISIBLE
        tvNormalFee.visibility = View.VISIBLE
        constraintTaxi.visibility = View.VISIBLE
        TaxiImage.visibility = View.VISIBLE
        moveDistance.visibility = View.VISIBLE
        allWaitingLabel.visibility = View.VISIBLE
        tvAllWaiting.visibility = View.VISIBLE
        nearWaitingLabel.visibility = View.VISIBLE
        tvNearWaiting.visibility = View.VISIBLE
        meanWaitingLabel.visibility = View.VISIBLE
        tvMeanWaiting.visibility = View.VISIBLE
        callButton.visibility = View.VISIBLE
    }
}