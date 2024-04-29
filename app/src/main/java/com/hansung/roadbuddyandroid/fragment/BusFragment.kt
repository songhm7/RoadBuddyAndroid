package com.hansung.roadbuddyandroid.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.gson.Gson
import com.hansung.roadbuddyandroid.Logr
import com.hansung.roadbuddyandroid.R
import com.hansung.roadbuddyandroid.Response
import com.hansung.roadbuddyandroid.Route


class BusFragment : Fragment() {
    private var data: String? = null
    private lateinit var tv: TextView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_bus, container, false)
        tv = view.findViewById(R.id.textViewFragmentBus)
        tv.text = data
        if (data != null) {
            Logr.d("BusFragment데이터",data!!)
        }
        else
            Log.e("실패","실패")

        //parseAndStore(data!!)
        return view
    }

    fun setData(responseData: String) {
        data = responseData
    }

    companion object {
        fun newInstance(): BusFragment {
            return BusFragment()
        }
    }

    fun parseAndStore(jsonInput: String) {
        val gson = Gson()
        val response = gson.fromJson(jsonInput, Response::class.java)

        // 이제 response 객체를 통해 JSON 데이터에 접근하고 사용할 수 있습니다.
        // 예를 들어 RecyclerView에 데이터를 표시하려면 다음과 같이 할 수 있습니다.
        updateUI(response.data.routes)
    }

    fun updateUI(routes: List<Route>) {
        // 여기서 RecyclerView의 어댑터를 업데이트하거나 필요한 UI 변경을 수행하세요.
        Log.d("RouteList","${routes.size}")
    }
}