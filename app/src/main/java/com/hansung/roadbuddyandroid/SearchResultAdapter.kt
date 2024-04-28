package com.hansung.roadbuddyandroid

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView


class SearchResultAdapter(context: Context,
                          resource: Int,
                          list: List<Place>,
                          private val startPoint: Place,
                          private val endPoint: Place)
    : ArrayAdapter<Place>(context, resource, list) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = convertView ?: inflater.inflate(R.layout.list_search_result_item, parent, false)

        val textViewItem = view.findViewById<TextView>(R.id.textViewItemSearchResult)
        val textViewAddress = view.findViewById<TextView>(R.id.textViewAddressSearchResult)
        val textViewCategoryDistance = view.findViewById<TextView>(R.id.textViewCategoryDistance)

        val item = getItem(position) ?: return view
        val distanceDisplay = if (item.distance < 1000) {
            "${item.distance.toInt()}m"
        } else {
            String.format("%.3fkm", item.distance / 1000)
        }
        textViewItem.text = item.name
        textViewAddress.text = item.address
        textViewCategoryDistance.text = "${item.category}\n${distanceDisplay}"


        // 클릭 리스너 설정
        view.setOnClickListener {
            val intent = Intent(context, PlaceViewActivity::class.java)
            intent.putExtra("selectedPlace",item)
            if (startPoint.name != "출발지미정") intent.putExtra("startPoint", startPoint)
            if (endPoint.name != "도착지미정") intent.putExtra("endPoint", endPoint)
            context.startActivity(intent)
        }
        return view
    }
}
