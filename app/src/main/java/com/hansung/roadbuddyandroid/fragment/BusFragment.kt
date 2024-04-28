package com.hansung.roadbuddyandroid.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.hansung.roadbuddyandroid.R

class BusFragment : Fragment() {
//    private var data : JSONObject? =null
    private var data : String? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_bus, container, false)
        val tv = view.findViewById<TextView>(R.id.textViewFragmentBus)
        tv.text = data
        return view
    }
    // 데이터 설정 메소드 추가
    fun setData(responseData: String) {
        //data = JSONObject(responseData)
        data = responseData
    }
    companion object {
        fun newInstance(): BusFragment {
            return BusFragment()
        }
    }
}