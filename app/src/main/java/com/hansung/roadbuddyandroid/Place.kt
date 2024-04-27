package com.hansung.roadbuddyandroid
import android.os.Parcelable
import kotlinx.parcelize.Parcelize
@Parcelize
data class Place(
    val name : String,      // 장소 이름
    val address : String,   //지번 주소
    val category : String,  //시설 카테고리
    val latitude : Double,  //위도
    val longitude : Double, //경도
    val distance : Double   //현위치로부터의 거리
) : Parcelable