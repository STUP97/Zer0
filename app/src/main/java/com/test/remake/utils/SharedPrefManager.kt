package com.test.remake.utils

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.test.remake.App
import com.test.remake.model.SearchData
import com.test.remake.utils.Constants.TAG

object SharedPrefManager {
    private const val SHARED_SEARCH_HISTORY = "shared_search_history"
    private const val KEY_SEARCH_HISTORY = "key_search_history"

    private const val SHARED_SEARCH_HISTORY_MODE = "shared_search_history_mode"
    private const val KEY_SEARCH_HISTORY_MODE = "key_search_history_mode"

    // 검색어 저장 모드 설정
    fun setSearchHistoryMode(isActivated:Boolean){
        Log.d(TAG, "SharedPrefManager - setSearchHistoryMode() called")
        val shared = App.instance.getSharedPreferences(SHARED_SEARCH_HISTORY_MODE, Context.MODE_PRIVATE)

        val editor = shared.edit()

        editor.putBoolean(KEY_SEARCH_HISTORY_MODE, isActivated)

        editor.apply()
    }

    // 검색어 저장 모드 확인하기
    fun checkSearchHistoryMode() : Boolean{
        val shared = App.instance.getSharedPreferences(SHARED_SEARCH_HISTORY_MODE, Context.MODE_PRIVATE)

        return shared.getBoolean(KEY_SEARCH_HISTORY_MODE, false)
    }


    // 검색 목록 저장
    fun storeSearchHistoryList(searchHistoryList: MutableList<SearchData>){
        Log.d(TAG, "SharedPrefManager - storeSearchHistoryList() called")

        val searchHistoryListString : String = Gson().toJson(searchHistoryList)
        Log.d(TAG, "SharedPrefManager - searchHistoryListString : $searchHistoryListString")

        val shared = App.instance.getSharedPreferences(SHARED_SEARCH_HISTORY, Context.MODE_PRIVATE)

        val editor = shared.edit()

        editor.putString(KEY_SEARCH_HISTORY, searchHistoryListString)

        editor.apply()
    }

    // 검색 목록 가져오기

    fun getSearchHistoryList() : MutableList<SearchData>{
        // 가져오기
        val shared = App.instance.getSharedPreferences(SHARED_SEARCH_HISTORY, Context.MODE_PRIVATE)

        val storedSearchHistoryListString = shared.getString(KEY_SEARCH_HISTORY, "")!!

        var storedSearchHistoryList = ArrayList<SearchData>()
        // 값이 있으면
        if(storedSearchHistoryListString.isNotEmpty()){
            // 저장된 문자열을 -> 객체 배열로 컨버팅(변경)
            storedSearchHistoryList = Gson().fromJson(storedSearchHistoryListString, Array<SearchData>::class.java).toMutableList() as ArrayList<SearchData>

        }

        return storedSearchHistoryList
    }

    // 검색목록 지우기

    fun clearSearchHistoryList(){
        Log.d(TAG, "SharedPrefManager - clearSearchHistoryList() called ")
        val shared = App.instance.getSharedPreferences(SHARED_SEARCH_HISTORY, Context.MODE_PRIVATE)
        val editor = shared.edit()

        editor.clear()

        editor.apply()
    }
}