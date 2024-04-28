package com.hansung.roadbuddyandroid
import android.os.Parcelable
import kotlinx.parcelize.Parcelize
@Parcelize
data class Place(
    var name : String,      // 장소 이름
    var address : String,   //지번 주소
    var category : String,  //시설 카테고리
    var latitude : Double,  //위도
    var longitude : Double, //경도
    var distance : Double   //현위치로부터의 거리
) : Parcelable