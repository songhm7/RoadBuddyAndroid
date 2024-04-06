package com.hansung.roadbuddyandroid

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast

class CustomAdapter(context: Context, private val dataSource: MutableList<String>, private val onItemRemoved: (List<String>) -> Unit) : ArrayAdapter<String>(context, R.layout.list_item, dataSource) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val inflater = LayoutInflater.from(context)
        val rowView = inflater.inflate(R.layout.list_item, parent, false)

        val textViewItem = rowView.findViewById<TextView>(R.id.textViewItem)
        val buttonRemove = rowView.findViewById<ImageButton>(R.id.buttonRemove)

        val item = getItem(position)
        textViewItem.text = item
        textViewItem.setOnClickListener {
            Toast.makeText(context, item, Toast.LENGTH_SHORT).show()
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
}