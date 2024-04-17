package com.hansung.roadbuddyandroid

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import android.content.Intent


class SearchResultAdapter(context: Context,
                          resource: Int,
                          list: List<String>,
                          private val startPoint: String = "출발지미정",
                          private val endPoint: String = "도착지미정")
    : ArrayAdapter<String>(context, resource, list) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = convertView ?: inflater.inflate(R.layout.list_search_result_item, parent, false)
        val textView = view.findViewById<TextView>(R.id.textViewItemSearchResult)
        val itemText = getItem(position) ?: ""
        textView.text = itemText

        // 클릭 리스너 설정
        view.setOnClickListener {
            val intent = Intent(context, PlaceViewActivity::class.java)
            intent.putExtra("selectedPlace", itemText)
            if (startPoint != "출발지미정") intent.putExtra("startPoint", startPoint)
            if (endPoint != "도착지미정") intent.putExtra("endPoint", endPoint)
            context.startActivity(intent)
        }
        return view
    }
}
