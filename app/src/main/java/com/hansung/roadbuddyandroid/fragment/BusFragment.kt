package com.hansung.roadbuddyandroid.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.gson.Gson
import com.hansung.roadbuddyandroid.Logr
import com.hansung.roadbuddyandroid.Place
import com.hansung.roadbuddyandroid.R
import com.hansung.roadbuddyandroid.Response
import com.hansung.roadbuddyandroid.Route
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.Credentials
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException


class BusFragment : Fragment() {
    private lateinit var textViewFragmentBus: TextView
    private lateinit var client : OkHttpClient
    private val username = "user"
    private lateinit var password : String
    private lateinit var credentials: String
    private lateinit var responseTMP : String
    private lateinit var BusListView : ListView


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_bus, container, false)
        password = getString(R.string.API_KEY)
        client = OkHttpClient()
        credentials = Credentials.basic(username, password)
        textViewFragmentBus = view.findViewById(R.id.textViewFragmentBus)
        BusListView = view.findViewById(R.id.ListViewBusFragment)
        arguments?.let {
            val startPoint = it.getParcelable<Place>("startPoint")
            val endPoint = it.getParcelable<Place>("endPoint")
            if (startPoint != null && endPoint != null) {
                makeNetworkRequest(startPoint, endPoint)
            }
        }

        return view
    }

    companion object {
        fun newInstance(startPoint: Place, endPoint: Place): BusFragment {
            val fragment = BusFragment()
            val args = Bundle()
            args.putParcelable("startPoint", startPoint)
            args.putParcelable("endPoint", endPoint)
            fragment.arguments = args
            return fragment
        }
    }

    fun parseAndStore(jsonInput: String) {
        try {
            val gson = Gson()
            val response = gson.fromJson(jsonInput, Response::class.java)
            Log.d("parseAndStore", "Parsed response successfully: $response")

            updateUI(response.data.routes)
        } catch (e: Exception) {
            Log.e("parseAndStore", "Error parsing JSON", e)
        }
    }

    fun updateUI(routes: List<Route>) {
        textViewFragmentBus.visibility = View.GONE
        val adapter = BusFragmentAdapter(requireContext(), routes)
        BusListView.adapter = adapter
        adapter.notifyDataSetChanged()
        Logr.d("RouteList","${routes}")
    }
    private fun makeNetworkRequest(startPoint : Place, endPoint : Place) {
        val url = "http://3.25.65.146:8080/maps/directions?origin.latitude=${startPoint.latitude}&origin.longitude=${startPoint.longitude}" +
                "&destination.latitude=${endPoint.latitude}&destination.longitude=${endPoint.longitude}&alternatives=true"

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val request = Request.Builder()
                    .url(url)
                    .header("Authorization", credentials)
                    .build()

                client.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) throw IOException("Unexpected code $response")

                    val responseBody = response.body!!.string()
                    Logr.d("BF응답확인", responseBody)
                    responseTMP = responseBody

                    // UI 업데이트는 메인 스레드에서 수행
                    withContext(Dispatchers.Main) {
                        parseAndStore(responseBody)
                    }
                }
            } catch (e: Exception) {
                Log.e("네트워크 오류", "요청 중 오류 발생: ${e.message}")
            }
        }
    }
}