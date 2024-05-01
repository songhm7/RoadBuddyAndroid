package com.hansung.roadbuddyandroid.fragment

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.hansung.roadbuddyandroid.R
import com.hansung.roadbuddyandroid.Route

class BusFragmentAdapter(context: Context, private val routes: List<Route>)
    : ArrayAdapter<Route>(context, 0, routes) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var view = convertView
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.list_bus_item, parent, false)
        }

        val route = getItem(position)!!
        val textViewBusTime = view!!.findViewById<TextView>(R.id.textViewBusTime)
        val textViewBusDuration = view.findViewById<TextView>(R.id.textViewBusDuration)

        textViewBusTime.text = "출발: ${route.legs[0].arrivalTime.text} - ${route.legs[0].departureTime.text}"
        textViewBusDuration.text = route.legs[0].duration.text

        return view
    }
}
