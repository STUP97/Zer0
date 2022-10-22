package com.test.remake.activitys

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.webkit.WebView
import android.widget.Toast
import com.test.remake.R
import com.test.remake.utils.Constants.TAG
import com.test.remake.utils.SEARCH_TYPE
import com.test.remake.utils.onMyTextChanged
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.layout_button_search.*
import android.webkit.WebChromeClient
import android.webkit.WebViewClient
import com.test.remake.utils.DataBaseHelper
import com.test.remake.utils.SqliteHelper
import java.io.File
import java.io.InputStream
import java.io.OutputStream
import java.lang.Exception

class MainActivity : AppCompatActivity() {

    private var currentSearchType : SEARCH_TYPE = SEARCH_TYPE.DRINK

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.d(TAG, "MainActivity - onCreate() called")
        DataBaseHelper(this)
        //라디오 그룹 불러오기
        search_term_radio_group.setOnCheckedChangeListener{ _, checkedId ->
            // switch문
            when(checkedId){
                R.id.drink_search_radio_btn -> {
                    Log.d(TAG, "음료검색 버튼 클릭.")
                    search_term_text_layout.hint = "음료검색"
                    this.currentSearchType = SEARCH_TYPE.DRINK
                }
                R.id.add_search_radio_btn ->{
                    Log.d(TAG, "성분검색 버튼 클릭.")
                    search_term_text_layout.hint = "성분검색"
                    this.currentSearchType = SEARCH_TYPE.ADDITIVE
                }
            }
            Log.d(TAG, "MainActivity - onCreate() called / currentSearchType : $currentSearchType")
        }
        // text가 변경 되었을 때.
        search_term_edit_text.onMyTextChanged {
            // 입력된 글자가 있다면
            if(it.toString().count() > 0){
                // 검색버튼 visible
                frame_search_btn.visibility = View.VISIBLE
                search_term_text_layout.helperText = " "
                // 스크롤 뷰 올려서 검색버튼 보이기
                main_scrollview.scrollTo(0, 400)
            }
            else{
                // 검색버튼 invisible
                frame_search_btn.visibility = View.INVISIBLE
            }

            if(it.toString().count() == 12){
                Log.d(TAG, "MainActivity - 글자 최대 입력 수 초과 에러")
                Toast.makeText(this, "검색어는 12자까지 입력 가능합니다.", Toast.LENGTH_SHORT).show()
            }
        }
        btn_search.setOnClickListener{
            Log.d(TAG, "MainActivity - 검색 버튼이 클릭 됨. / currentSearchType : $currentSearchType")
            this.handleSearchButtonUi()
            if(add_search_radio_btn.isChecked) {
                var mIntent = Intent(this, WebActivity::class.java)
                var search_key = search_term_edit_text.text.toString()
                mIntent.putExtra("search_key", search_key)
                Log.d(TAG, "MainActivity - 검색 버튼이 클릭 됨. / WebActivity Load")
                startActivity(mIntent)
            }
            if(drink_search_radio_btn.isChecked) {
                var mIntent = Intent(this, SubActivity::class.java)
                var search_key = search_term_edit_text.text.toString()
                mIntent.putExtra("search_key", search_key)
                startActivity(mIntent)
            }
        }
    }// onCreate
    private fun handleSearchButtonUi(){
        btn_progress.visibility = View.VISIBLE
        btn_search.visibility = View.INVISIBLE

        Handler().postDelayed({
            btn_progress.visibility = View.INVISIBLE
            btn_search.visibility = View.VISIBLE
        }, 1500)
    }
}