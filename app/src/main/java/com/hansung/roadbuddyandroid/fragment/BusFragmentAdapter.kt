package com.hansung.roadbuddyandroid.fragment

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.hansung.roadbuddyandroid.DetailActivity
import com.hansung.roadbuddyandroid.Logr
import com.hansung.roadbuddyandroid.R
import com.hansung.roadbuddyandroid.Route
class BusFragmentAdapter(context: Context, private val routes: List<Route>)
    : ArrayAdapter<Route>(context, 0, routes) {
    private lateinit var firstImage : ImageView
    private lateinit var firstTime : TextView
    private lateinit var firstText : TextView
    private lateinit var secondImage : ImageView
    private lateinit var secondTime : TextView
    private lateinit var secondText : TextView
    private lateinit var thirdImage : ImageView
    private lateinit var thirdTime : TextView
    private lateinit var thirdText : TextView
    private lateinit var fourthImage : ImageView
    private lateinit var fourthTime : TextView
    private lateinit var fourthText : TextView
    private lateinit var firstToward : ImageView
    private lateinit var secondToward : ImageView
    private lateinit var thirdToward : ImageView


    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var view = convertView
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.list_bus_item, parent, false)
        }
        firstImage = view!!.findViewById(R.id.firstImage)
        firstTime = view.findViewById(R.id.firstTime)
        firstText = view.findViewById(R.id.firstText)
        secondImage = view.findViewById(R.id.secondImage)
        secondTime = view.findViewById(R.id.secondTime)
        secondText = view.findViewById(R.id.secondText)
        thirdImage = view.findViewById(R.id.thirdImage)
        thirdTime = view.findViewById(R.id.thirdTime)
        thirdText = view.findViewById(R.id.thirdText)
        fourthImage = view.findViewById(R.id.fourthImage)
        fourthTime = view.findViewById(R.id.fourthTime)
        fourthText = view.findViewById(R.id.fourthText)
        firstToward = view.findViewById(R.id.firstToward)
        secondToward = view.findViewById(R.id.secondToward)
        thirdToward = view.findViewById(R.id.thirdToward)

        val route = getItem(position)!!
        Logr.d("BFA 루트${position}",route.toString());
        val textViewBusTime = view.findViewById<TextView>(R.id.textViewBusTime)
        val textViewBusDuration = view.findViewById<TextView>(R.id.textViewBusDuration)

        var arrivalTime = route.legs[0].arrivalTime.text
        var departureTime = route.legs[0].departureTime.text

        if(arrivalTime.contains("AM"))
            arrivalTime = "오전 " + arrivalTime.replace("AM","").replace(" ","")
        if(arrivalTime.contains("PM"))
            arrivalTime = "오후 " + arrivalTime.replace("PM","").replace(" ","")
        if(departureTime.contains("AM"))
            departureTime = "오전 " + departureTime.replace("AM","").replace(" ","")
        if(departureTime.contains("PM"))
            departureTime = "오후 " + departureTime.replace("PM","").replace(" ","")

        textViewBusTime.text = "${departureTime} - ${arrivalTime}"
        textViewBusDuration.text = route.legs[0].duration.text

        updateUI(route)
        view.setOnClickListener {
            val intent = Intent(context, DetailActivity::class.java)
            intent.putExtra("route",route)
            context.startActivity(intent)
        }
        return view
    }

    private fun updateUI(route: Route) {
        val steps = route.legs[0].steps
        when (steps.size) {
            1 -> {
                // steps.size가 1일 때 실행할 코드
                if (steps[0].travelMode == "WALKING"){
                    firstText.visibility = View.INVISIBLE
                    firstTime.text = steps[0].duration.text.replace("분","")
                    //TODO("시간 단위 응답 오는지 체크")
                } else if(steps[0].travelMode == "TRANSIT"){
                    firstImage.visibility = View.INVISIBLE
                    firstTime.visibility = View.INVISIBLE
                    firstText.text = steps[0].transitDetails!!.line.shortName
                    firstText.setTextColor(Color.parseColor(steps[0].transitDetails!!.line.textColor))
                    firstText.setBackgroundColor(Color.parseColor(steps[0].transitDetails!!.line.color))
                } else{
                    Log.e("BusFragmentAdapter error", "travelMode에러발생")
                }
                invisibleView(2); invisibleView(3); invisibleView(4)
                firstToward.visibility = View.INVISIBLE
                secondToward.visibility = View.INVISIBLE
                thirdToward.visibility = View.INVISIBLE
            }
            2 -> {
                // steps.size가 2일 때 실행할 코드
                invisibleView(3); invisibleView(4)
                secondToward.visibility = View.INVISIBLE
                thirdToward.visibility = View.INVISIBLE
                if (steps[0].travelMode == "WALKING"){
                    firstText.visibility = View.INVISIBLE
                    firstTime.text = steps[0].duration.text.replace("분","")
                } else if(steps[0].travelMode == "TRANSIT"){
                    firstImage.visibility = View.INVISIBLE
                    firstTime.visibility = View.INVISIBLE
                    firstText.text =  steps[0].transitDetails!!.line.shortName
                    firstText.setTextColor(Color.parseColor(steps[0].transitDetails!!.line.textColor))
                    firstText.setBackgroundColor(Color.parseColor(steps[0].transitDetails!!.line.color))
                } else{
                    Log.e("BusFragmentAdapter error", "travelMode에러발생")
                }
                if (steps[1].travelMode == "WALKING"){
                    secondText.visibility = View.INVISIBLE
                    secondTime.text = steps[1].duration.text.replace("분","")
                } else if(steps[1].travelMode == "TRANSIT"){
                    secondImage.visibility = View.INVISIBLE
                    secondTime.visibility = View.INVISIBLE
                    secondText.text = steps[1].transitDetails!!.line.shortName
                    secondText.setTextColor(Color.parseColor(steps[1].transitDetails!!.line.textColor))
                    secondText.setBackgroundColor(Color.parseColor(steps[1].transitDetails!!.line.color))
                } else{
                    Log.e("BusFragmentAdapter error", "travelMode에러발생")
                }
            }
            3 -> {
                // steps.size가 3일 때 실행할 코드
                invisibleView(4)
                thirdToward.visibility = View.INVISIBLE
                if (steps[0].travelMode == "WALKING"){
                    firstText.visibility = View.INVISIBLE
                    firstTime.text = steps[0].duration.text.replace("분","")
                } else if(steps[0].travelMode == "TRANSIT"){
                    firstImage.visibility = View.INVISIBLE
                    firstTime.visibility = View.INVISIBLE
                    firstText.text = steps[0].transitDetails!!.line.shortName
                    firstText.setTextColor(Color.parseColor(steps[0].transitDetails!!.line.textColor))
                    firstText.setBackgroundColor(Color.parseColor(steps[0].transitDetails!!.line.color))
                } else{
                    Log.e("BusFragmentAdapter error", "travelMode에러발생")
                }
                if (steps[1].travelMode == "WALKING"){
                    secondText.visibility = View.INVISIBLE
                    secondTime.text = steps[1].duration.text.replace("분","")
                } else if(steps[1].travelMode == "TRANSIT"){
                    secondImage.visibility = View.INVISIBLE
                    secondTime.visibility = View.INVISIBLE
                    secondText.text = steps[1].transitDetails!!.line.shortName
                    secondText.setTextColor(Color.parseColor(steps[1].transitDetails!!.line.textColor))
                    secondText.setBackgroundColor(Color.parseColor(steps[1].transitDetails!!.line.color))
                } else{
                    Log.e("BusFragmentAdapter error", "travelMode에러발생")
                }
                if (steps[2].travelMode == "WALKING"){
                    thirdText.visibility = View.INVISIBLE
                    thirdTime.text = steps[2].duration.text.replace("분","")
                } else if(steps[2].travelMode == "TRANSIT"){
                    thirdImage.visibility = View.INVISIBLE
                    thirdTime.visibility = View.INVISIBLE
                    thirdText.text = steps[2].transitDetails!!.line.shortName
                    thirdText.setTextColor(Color.parseColor(steps[2].transitDetails!!.line.textColor))
                    thirdText.setBackgroundColor(Color.parseColor(steps[2].transitDetails!!.line.color))
                } else{
                    Log.e("BusFragmentAdapter error", "travelMode에러발생")
                }
            }
            4 -> {
                // steps.size가 4일 때 실행할 코드
                if (steps[0].travelMode == "WALKING"){
                    firstText.visibility = View.INVISIBLE
                    firstTime.text = steps[0].duration.text.replace("분","")
                } else if(steps[0].travelMode == "TRANSIT"){
                    firstImage.visibility = View.INVISIBLE
                    firstTime.visibility = View.INVISIBLE
                    firstText.text = steps[0].transitDetails!!.line.shortName
                    firstText.setTextColor(Color.parseColor(steps[0].transitDetails!!.line.textColor))
                    firstText.setBackgroundColor(Color.parseColor(steps[0].transitDetails!!.line.color))
                } else{
                    Log.e("BusFragmentAdapter error", "travelMode에러발생")
                }
                if (steps[1].travelMode == "WALKING"){
                    secondText.visibility = View.INVISIBLE
                    secondTime.text = steps[1].duration.text.replace("분","")
                } else if(steps[1].travelMode == "TRANSIT"){
                    secondImage.visibility = View.INVISIBLE
                    secondTime.visibility = View.INVISIBLE
                    secondText.text = steps[1].transitDetails!!.line.shortName
                    secondText.setTextColor(Color.parseColor(steps[1].transitDetails!!.line.textColor))
                    secondText.setBackgroundColor(Color.parseColor(steps[1].transitDetails!!.line.color))
                } else{
                    Log.e("BusFragmentAdapter error", "travelMode에러발생")
                }
                if (steps[2].travelMode == "WALKING"){
                    thirdText.visibility = View.INVISIBLE
                    thirdTime.text = steps[2].duration.text.replace("분","")
                } else if(steps[2].travelMode == "TRANSIT"){
                    thirdImage.visibility = View.INVISIBLE
                    thirdTime.visibility = View.INVISIBLE
                    thirdText.text = steps[2].transitDetails!!.line.shortName
                    thirdText.setTextColor(Color.parseColor(steps[2].transitDetails!!.line.textColor))
                    thirdText.setBackgroundColor(Color.parseColor(steps[2].transitDetails!!.line.color))
                } else{
                    Log.e("BusFragmentAdapter error", "travelMode에러발생")
                }
                if (steps[3].travelMode == "WALKING"){
                    fourthText.visibility = View.INVISIBLE
                    fourthTime.text = steps[3].duration.text.replace("분","")
                } else if(steps[3].travelMode == "TRANSIT"){
                    fourthImage.visibility = View.INVISIBLE
                    fourthTime.visibility = View.INVISIBLE
                    fourthText.text = steps[3].transitDetails!!.line.shortName
                    fourthText.setTextColor(Color.parseColor(steps[3].transitDetails!!.line.textColor))
                    fourthText.setBackgroundColor(Color.parseColor(steps[3].transitDetails!!.line.color))
                } else{
                    Log.e("BusFragmentAdapter error", "travelMode에러발생")
                }
            }
            else -> {
                // steps.size가 4 초과일 때 실행할 코드
                if (steps[0].travelMode == "WALKING"){
                    firstText.visibility = View.INVISIBLE
                    firstTime.text = steps[0].duration.text.replace("분","")
                } else if(steps[0].travelMode == "TRANSIT"){
                    firstImage.visibility = View.INVISIBLE
                    firstTime.visibility = View.INVISIBLE
                    firstText.text = steps[0].transitDetails!!.line.shortName
                    firstText.setTextColor(Color.parseColor(steps[0].transitDetails!!.line.textColor))
                    firstText.setBackgroundColor(Color.parseColor(steps[0].transitDetails!!.line.color))
                } else{
                    Log.e("BusFragmentAdapter error", "travelMode에러발생")
                }
                if (steps[1].travelMode == "WALKING"){
                    secondText.visibility = View.INVISIBLE
                    secondTime.text = steps[1].duration.text.replace("분","")
                } else if(steps[1].travelMode == "TRANSIT"){
                    secondImage.visibility = View.INVISIBLE
                    secondTime.visibility = View.INVISIBLE
                    secondText.text = steps[1].transitDetails!!.line.shortName
                    secondText.setTextColor(Color.parseColor(steps[1].transitDetails!!.line.textColor))
                    secondText.setBackgroundColor(Color.parseColor(steps[1].transitDetails!!.line.color))
                } else{
                    Log.e("BusFragmentAdapter error", "travelMode에러발생")
                }
                //생략하기
                thirdImage.visibility = View.INVISIBLE
                thirdTime.visibility = View.INVISIBLE
                thirdText.text = " . . . "
                thirdText.setTextColor(Color.parseColor("#FFFFFF"))
                thirdText.setBackgroundColor(Color.parseColor("#626466"))

                if (steps[steps.size-1].travelMode == "WALKING"){
                    fourthText.visibility = View.INVISIBLE
                    fourthTime.text = steps[steps.size-1].duration.text.replace("분","")
                } else if(steps[steps.size-1].travelMode == "TRANSIT"){
                    fourthImage.visibility = View.INVISIBLE
                    fourthTime.visibility = View.INVISIBLE
                    fourthText.text = steps[steps.size-1].transitDetails!!.line.shortName
                    fourthText.setTextColor(Color.parseColor(steps[steps.size-1].transitDetails!!.line.textColor))
                    fourthText.setBackgroundColor(Color.parseColor(steps[steps.size-1].transitDetails!!.line.color))
                } else{
                    Log.e("BusFragmentAdapter error", "travelMode에러발생")
                }
            }
        }
    }
    private fun invisibleView(num : Int){
        when (num){
            1 -> {
                firstImage.visibility = View.INVISIBLE
                firstTime.visibility = View.INVISIBLE
                firstText.visibility = View.INVISIBLE
            }
            2 -> {
                secondImage.visibility = View.INVISIBLE
                secondTime.visibility = View.INVISIBLE
                secondText.visibility = View.INVISIBLE
            }
            3 -> {
                thirdImage.visibility = View.INVISIBLE
                thirdTime.visibility = View.INVISIBLE
                thirdText.visibility = View.INVISIBLE
            }
            4 -> {
                fourthImage.visibility = View.INVISIBLE
                fourthTime.visibility = View.INVISIBLE
                fourthText.visibility = View.INVISIBLE
            }
            else -> {
                Log.e("BusFragmentAdapter error", "잘못된 invisibleView 입력")
            }
        }
    }
}
