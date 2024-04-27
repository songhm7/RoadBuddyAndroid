package com.hansung.roadbuddyandroid

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.TextView

class RecentAdapter(context: Context,
                    private val dataSource: MutableList<String>,
                    private val onItemRemoved: (List<String>) -> Unit,
                    private var startPoint: String = "출발지미정",
                    private var endPoint: String = "도착지미정",
                    private var curLat: Double,
                    private var curLon: Double
    ) : ArrayAdapter<String>(context, R.layout.list_item, dataSource) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val inflater = LayoutInflater.from(context)
        val rowView = inflater.inflate(R.layout.list_item, parent, false)

        val textViewItem = rowView.findViewById<TextView>(R.id.textViewItem)
        val buttonRemove = rowView.findViewById<ImageButton>(R.id.buttonRemove)

        val item = getItem(position)
        textViewItem.text = item
        textViewItem.setOnClickListener {
            // 클릭된 아이템의 값을 넘기며 SearchResultActivity를 시작합니다.
            val intent = Intent(context, SearchResultActivity::class.java).apply {
                putExtra("searchText", item)
                Log.d("RecentAdapter startPoint", startPoint)
                Log.d("RecentAdapter endPoint", endPoint)
                putExtra("curLat",curLat)
                putExtra("curLon",curLon)
                if (startPoint != "출발지미정") putExtra("startPoint", startPoint)
                if (endPoint != "도착지미정") putExtra("endPoint", endPoint)
            }
            context.startActivity(intent)
        }

        buttonRemove.setOnClickListener {
            // 아이템 제거
            dataSource.removeAt(position)
            notifyDataSetChanged()

            //콜백함수 호출
            onItemRemoved(dataSource)
        }



        return rowView
    }

    fun updateEndpoints(newStartPoint: String, newEndPoint: String) {
        startPoint = newStartPoint
        endPoint = newEndPoint
    }
}