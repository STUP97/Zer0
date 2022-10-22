package com.test.remake.activitys

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.test.remake.R
import com.test.remake.utils.Constants
import com.test.remake.utils.onMyTextChanged
import kotlinx.android.synthetic.main.activity_drink.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.layout_button_search.*

class DrinkActivity : AppCompatActivity() {

    lateinit var name : TextView
    lateinit var maker : TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_drink)
        val Term_name = intent.getStringExtra("name")
        val Term_maker = intent.getStringExtra("maker")
        name = findViewById(R.id.name)
        maker = findViewById(R.id.maker)
        var factoring_data = Term_maker

        factoring_data = factoring_data?.replace(" ","")
        factoring_data = factoring_data?.replace(",","\n")
        name.text = Term_name
        maker.text = factoring_data
        search_add_edit_text.onMyTextChanged {
            // 입력된 글자가 있다면
            if(it.toString().count() > 0){
                // 검색버튼 visible
                frame_search_btn.visibility = View.VISIBLE
                search_add_text_layout.helperText = " "
                // 스크롤 뷰 올려서 검색버튼 보이기
            }
            else{
                // 검색버튼 invisible
                frame_search_btn.visibility = View.INVISIBLE
            }

            if(it.toString().count() == 12){
                Toast.makeText(this, "검색어는 12자까지 입력 가능합니다.", Toast.LENGTH_SHORT).show()
            }
        }
        btn_search.setOnClickListener{
            this.handleSearchButtonUi()
            var mIntent = Intent(this, WebActivity::class.java)
            var search_key = search_add_edit_text.text.toString()
            mIntent.putExtra("search_key", search_key)
            Log.d(Constants.TAG, "DrinkActivity - 검색 버튼이 클릭 됨. / WebActivity Load")
            startActivity(mIntent)
        }
    }
    private fun handleSearchButtonUi(){
        btn_progress.visibility = View.VISIBLE
        btn_search.visibility = View.INVISIBLE

        Handler().postDelayed({
            btn_progress.visibility = View.INVISIBLE
            btn_search.visibility = View.VISIBLE
        }, 1500)
    }
}