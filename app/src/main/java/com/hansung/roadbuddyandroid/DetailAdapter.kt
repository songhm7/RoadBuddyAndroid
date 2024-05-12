package com.hansung.roadbuddyandroid

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

class DetailAdapter(context: Context, private val steps: List<Step>) : ArrayAdapter<Step>(context, R.layout.list_item_detail, steps) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.list_item_detail, parent, false)!!
        val step = getItem(position)!!
        val lineStart = view.findViewById<View>(R.id.line_start)
        val lineMiddle = view.findViewById<View>(R.id.line_middle)
        val lineEnd = view.findViewById<View>(R.id.line_end)
        val isWalking = step.travelMode.equals("WALKING")
        val isTransit = !step.travelMode.equals("WALKING")
        if(position==0) {
            if(isWalking)
                lineStart.setBackgroundColor(Color.parseColor("#A9A9A9"))
            else
                lineStart.setBackgroundColor(Color.parseColor(step.transitDetails!!.line.color))
            lineMiddle.visibility = View.INVISIBLE
            lineEnd.visibility = View.INVISIBLE
        }
        else if(position == steps.size-1){
            lineStart.visibility = View.INVISIBLE
            lineMiddle.visibility = View.INVISIBLE
            if(isWalking)
                lineEnd.setBackgroundColor(Color.parseColor("#A9A9A9"))
            else
                lineEnd.setBackgroundColor(Color.parseColor(step.transitDetails!!.line.color))
        }
        else{
            lineStart.visibility = View.INVISIBLE
            if(isWalking)
                lineMiddle.setBackgroundColor(Color.parseColor("#A9A9A9"))
            else
                lineMiddle.setBackgroundColor(Color.parseColor(step.transitDetails!!.line.color))
            lineEnd.visibility = View.INVISIBLE
        }
        view.findViewById<TextView>(R.id.duration).setText(step.duration.text)
        val tvMain = view.findViewById<TextView>(R.id.item_tv_main)
        if (isWalking) {
            if (step.transferPath == null || step.transferPath.isEmpty())
                tvMain.text = "도보로 ${step.distance.text} 이동"
            else
                tvMain.text = "환승 이동"
        } else {
            tvMain.text = "${step.transitDetails!!.line.name} ${step.transitDetails.line.shortName}으로 ${step.distance.text} 이동"
        }

        return view
    }
}
