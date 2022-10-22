package com.test.remake.recyclerview

import android.util.Log
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.test.remake.model.SearchData
import com.test.remake.utils.Constants.TAG
import kotlinx.android.synthetic.main.layout_search_item.view.*

class SearchItemViewHolder(itemView: View, searchRecyclerViewInterface: ISearchHistoryRecyclerView) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

    private var mySearchRecyclerViewInterface: ISearchHistoryRecyclerView

    private val searchTermTextView = itemView.search_term_text
    private val whenSearchTextView = itemView.when_searched_text
    private val deleteSearchDtn = itemView.delete_search_btn
    private val constraintSearchItem = itemView.constraint_search_item

    init{
        deleteSearchDtn.setOnClickListener(this)
        constraintSearchItem.setOnClickListener(this)
        this.mySearchRecyclerViewInterface = searchRecyclerViewInterface
    }
    
    // 데이터와 뷰를 묶는다.
    fun bindWithView(searchItem : SearchData){
        Log.d(TAG, "SearchItemViewHolder - bindWithView() called")
        whenSearchTextView.text = searchItem.timestamp

        searchTermTextView.text = searchItem.term

    }

    override fun onClick(view: View?) {
        Log.d(TAG, "SearchItemViewHolder - onClick() called ")
        when(view){
            deleteSearchDtn ->{
                Log.d(TAG, "SearchItemViewHolder - 검색 삭제 버튼 클릭")
                this.mySearchRecyclerViewInterface.onSearchItemDeleteClicked(adapterPosition)
            }
            constraintSearchItem ->{
                Log.d(TAG, "SearchItemViewHolder - 검색 아이템 클릭")
                this.mySearchRecyclerViewInterface.onSearchItemClicked(adapterPosition)
            }
        }
    }


}