package com.hansung.roadbuddyandroid

import android.content.Context
import java.io.File

object SearchHistoryManager{
    private const val FILENAME = "recent.txt"
    fun readSearchHistory(context: Context): List<String> {
        val file = File(context.filesDir, FILENAME)
        if (!file.exists()) {
            file.createNewFile()
        }
        return file.readLines().take(10)  // 최근 10개의 검색 기록만 가져옴
    }
     fun writeSearchHistory(context: Context, searches: List<String>) {
        val file = File(context.filesDir, FILENAME)
        file.writeText(searches.joinToString("\n"))
    }
    //newRecord에 대한 중복처리를 거쳐 리스트를 반환
     fun manageHistory(history: List<String>, newRecord: String): List<String> {
        val tmpHistory = history.toMutableList() // 기존 목록을 복사하여 임시 MutableList 생성

        if (newRecord.isNotEmpty()) {
            var index = tmpHistory.indexOf(newRecord)
            if (index != -1) {
                // 기존 기록이 있다면 해당 기록 삭제
                tmpHistory.removeAt(index)
            } else if (tmpHistory.size >= 10) {
                // 목록이 10개 이상이면 가장 오래된 기록 삭제
                tmpHistory.removeAt(tmpHistory.size - 1)
            }
            // 새 기록을 맨 앞에 추가
            tmpHistory.add(0, newRecord)
        }
        return tmpHistory.toList() // 임시 MutableList를 다시 List로 변환
    }
     fun deleteSearchHistory(context: Context) {
        val file = File(context.filesDir, FILENAME)
        if (file.exists()) {
            file.delete()
        }
    }
}