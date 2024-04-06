package com.hansung.roadbuddyandroid

data class Place(
    val name: String,        // 장소 이름
    val address: String,     // 장소 주소
    val type: String,        // 시설 종류
    val distance: Double     // 현재 위치로부터의 거리 (미터 단위)
)