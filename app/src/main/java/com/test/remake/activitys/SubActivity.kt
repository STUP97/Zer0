package com.test.remake.activitys

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.test.remake.model.drink
import com.test.remake.recyclerview.RecyclerViewAdapter
import com.test.remake.utils.Constants
import kotlinx.android.synthetic.main.activity_sub.*
import android.graphics.Color
import android.text.InputFilter
import android.view.Menu
import android.view.View
import android.widget.CompoundButton
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.room.*
import com.test.remake.R
import com.test.remake.model.SearchData
import com.test.remake.recyclerview.ISearchHistoryRecyclerView
import com.test.remake.recyclerview.SearchHistoryRecyclerViewAdapter
import com.test.remake.utils.Constants.TAG
import com.test.remake.utils.SharedPrefManager
import com.test.remake.utils.SqliteHelper
import com.test.remake.utils.toSimpleString
import java.util.*
import kotlin.collections.ArrayList


class SubActivity : AppCompatActivity(),
                    SearchView.OnQueryTextListener,
                    CompoundButton.OnCheckedChangeListener,
                    View.OnClickListener,
                    ISearchHistoryRecyclerView
{
    // DB
    val DB_NAME = "DrinkDB.db"
    val DB_VERSION = 1
    // 데이터
    var drinkList = mutableListOf<drink>()
    // 필터된 데이터
    var drinkListFiltered = mutableListOf<drink>()
    // 검색 기록 배열
    var searchHistoryList = ArrayList<SearchData>()
    // 서치뷰
    private lateinit var mySearchView: SearchView
    // 서치뷰 에딧 텍스트
    private lateinit var mySearchViewEditText: EditText

    private lateinit var mySearchHistoryRecyclerViewAdapter: SearchHistoryRecyclerViewAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sub)


        // DB
        val helper = SqliteHelper(this, DB_NAME, DB_VERSION)
        // var input_data = drink(id,name, maker, image_url)

        //select Data
        val drinks = helper.selectDrink()

        drinkList = drinks
        //
        val searchTerm = intent.getStringExtra("search_key")
        Log.d(Constants.TAG, "SubActivity - 리사이클러뷰 진입 / SearchKey : $searchTerm")
        Log.d(Constants.TAG, "SubActivity - drinkDB 확인 / DrinkDB : ${drinkList.size}")


        search_history_mode_switch.setOnCheckedChangeListener(this)
        clear_search_history_button.setOnClickListener(this)

        search_history_mode_switch.isChecked = SharedPrefManager.checkSearchHistoryMode()

        top_app_bar.title = searchTerm
        setSupportActionBar(top_app_bar)
        for(arr in drinkList){
            if(arr.name!!.contains(searchTerm.toString())){
                drinkListFiltered.add(arr)
            }
        }

        recyclerView.adapter = RecyclerViewAdapter(this, drinkListFiltered)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)

        // 저장된 검색 기록 불러오기
        this.searchHistoryList = SharedPrefManager.getSearchHistoryList() as ArrayList<SearchData>

        this.searchHistoryList.forEach{
            Log.d(TAG, "저장된 검색 기록 - it.term : ${it.term}, it.timestamp : ${it.timestamp}")
        }

        //  검색 기록 리사이클러뷰 준비
        handleSearchViewUi()
        this.searchHistoryRecyclerViewSetting((this.searchHistoryList))
        if(searchTerm!!.isNotEmpty()){
            val term = searchTerm?.let{
                it
            }?: ""
            insertSearchTermHistory(term)
        }

    }// OnCreate

    private fun searchHistoryRecyclerViewSetting(searchHistoryList: ArrayList<SearchData>){
        Log.d(TAG, "SubActivity - searchHistoryRecyclerViewSetting: ")
        this.mySearchHistoryRecyclerViewAdapter = SearchHistoryRecyclerViewAdapter(this)
        this.mySearchHistoryRecyclerViewAdapter.submitList(searchHistoryList)

        val myLinearLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, true)
        myLinearLayoutManager.stackFromEnd = true

        search_history_recycler_view.apply{
            layoutManager = myLinearLayoutManager
            this.scrollToPosition(mySearchHistoryRecyclerViewAdapter.itemCount - 1)
            adapter = mySearchHistoryRecyclerViewAdapter
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        Log.d(TAG, "Sub Activity - onCreateOptionMenu() called")

        val inflater = menuInflater

        inflater.inflate(R.menu.top_app_bar_menu, menu)

        this.mySearchView = menu?.findItem(R.id.search_menu_item)?.actionView as SearchView
        this.mySearchView.apply{
            this.queryHint = "검색어를 입력해주세요."

            this.setOnQueryTextListener(this@SubActivity)
            this.setOnQueryTextFocusChangeListener { _, hasExpaned ->
                when(hasExpaned) {
                    true -> {
                        Log.d(TAG, "서치뷰 열림")
                        linear_search_history_view.visibility = View.VISIBLE
                        handleSearchViewUi()
                    }
                    false -> {
                        Log.d(TAG, "서치뷰 닫힘")
                        linear_search_history_view.visibility = View.INVISIBLE
                    }
                }

            }

            mySearchViewEditText = this.findViewById(androidx.appcompat.R.id.search_src_text)
        }

        this.mySearchViewEditText.apply{
            this.filters = arrayOf(InputFilter.LengthFilter(12))
            this.setTextColor(Color.WHITE)
            this.setHintTextColor(Color.WHITE)
        }

        return true
    }

    // 서치뷰 검색어 이벤트
    // 검색 했을 때.
    override fun onQueryTextSubmit(query: String?): Boolean {
        Log.d(TAG, "SubActivity - onQueryTextSubmit() called / query: $query")
        if(!query.isNullOrEmpty()){
            this.top_app_bar.title = query
            // 검색어 저장
            this.insertSearchTermHistory(query)
            this.searchDrinkApiCall(query)
        }
        this.top_app_bar.collapseActionView()
        return true
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        val userInputText = newText.let{
            it
        }?: ""
        if(userInputText.count() == 12){
            Toast.makeText(this, "검색어는 12자 까지만 입력 가능합니다.", Toast.LENGTH_SHORT).show()
        }
        return true
    }

    override fun onCheckedChanged(switch: CompoundButton?, isChecked: Boolean) {
        when(switch){
            search_history_mode_switch -> {
                if(isChecked == true){
                    Log.d(TAG, "검색어 저장기능 온")
                    SharedPrefManager.setSearchHistoryMode(isActivated = true)
                }
                else{
                    Log.d(TAG, "검색어 저장기능 오프")
                    SharedPrefManager.setSearchHistoryMode(isActivated = false)
                }
            }
        }
    }

    override fun onClick(view: View?) {
        when(view){
            clear_search_history_button -> {
                Log.d(TAG,"검색어 삭제 버튼 클릭")
                SharedPrefManager.clearSearchHistoryList()
                this.searchHistoryList.clear()
                handleSearchViewUi()
            }
        }
    }
    // 아이템 삭제 버튼 이벤트
    override fun onSearchItemDeleteClicked(position: Int) {
        Log.d(TAG, "SubActivity - onSearchItemDeleteClicked: ")
        this.searchHistoryList.removeAt(position)
        // 데이터 덮어쓰기
        SharedPrefManager.storeSearchHistoryList(this.searchHistoryList)
        // 데이터 변경 되었다고 알려줌.
        this.mySearchHistoryRecyclerViewAdapter.notifyDataSetChanged()
        handleSearchViewUi()
    }
    // 아이템 클릭 이벤트
    override fun onSearchItemClicked(position: Int) {
        Log.d(TAG, "SubActivity - onSearchItemClicked: ")

        val queryString = this.searchHistoryList[position].term

        searchDrinkApiCall(queryString)

        top_app_bar.title = queryString

        this.insertSearchTermHistory(searchTerm = queryString)
        this.top_app_bar.collapseActionView()
    }

    private fun searchDrinkApiCall(query: String){
    //
        drinkListFiltered.clear()
        for(arr in drinkList){
            if(arr.name!!.contains(query.toString())){
                drinkListFiltered.add(arr)
            }
        }
        recyclerView.adapter = RecyclerViewAdapter(this, drinkListFiltered)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)
    }


    private fun handleSearchViewUi(){
        Log.d(TAG, "SubActivity - handleSearchViewUi() called")
        if(this.searchHistoryList.size > 0){
            search_history_recycler_view.visibility = View.VISIBLE
            search_history_recycler_view_label.visibility = View.VISIBLE
            clear_search_history_button.visibility = View.VISIBLE
        }
        else{
            search_history_recycler_view.visibility = View.INVISIBLE
            search_history_recycler_view_label.visibility = View.INVISIBLE
            clear_search_history_button.visibility = View.INVISIBLE
        }
    }
    // 검색어 저장
    private fun insertSearchTermHistory(searchTerm : String){
        Log.d(TAG, "insertSearchTermHistory: ")
        if(SharedPrefManager.checkSearchHistoryMode() == true){
            // 중복 삭제 로직
            var indexListToRemove = ArrayList<Int>()

            this.searchHistoryList.forEachIndexed{ index, searchDataItem ->
                if(searchDataItem.term == searchTerm){
                    indexListToRemove.add(index)
                }
            }
            indexListToRemove.forEach{
                this.searchHistoryList.removeAt(it)
            }

            var newSearchData = SearchData(term = searchTerm, timestamp = Date().toSimpleString())
            this.searchHistoryList.add(newSearchData)
            // 기존 데이터 덮어쓰기
            SharedPrefManager.storeSearchHistoryList(this.searchHistoryList)
            this.mySearchHistoryRecyclerViewAdapter.notifyDataSetChanged()
        }
    }
}