package com.test.remake.recyclerview

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.test.remake.R
import com.test.remake.activitys.DrinkActivity
import com.test.remake.activitys.SubActivity
import com.test.remake.model.drink
import com.test.remake.utils.Constants.TAG
import kotlinx.android.synthetic.main.activity_main.*




class RecyclerViewAdapter(val context : Context, val itemList: MutableList<drink>) : RecyclerView.Adapter<RecyclerViewAdapter.Holder>() {
    var filteredItemList = itemList
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_drink_item, parent, false)
        return Holder(view)
    }
    // (2) 리스트 내 아이템 개수
    override fun getItemCount(): Int {
        return filteredItemList.size
    }
    // (3) View에 내용 입력
    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder?.bind(filteredItemList[position], context)
    }
    // (4) 레이아웃 내 View 연결
    inner class Holder(itemView: View?) : RecyclerView.ViewHolder(itemView!!) {
        val image_link = itemView?.findViewById<ImageView>(R.id.image_link)
        val name = itemView?.findViewById<TextView>(R.id.name)
        val maker = itemView?.findViewById<TextView>(R.id.maker)

        init{
            itemView?.setOnClickListener{
                var Intent = Intent(itemView.context, DrinkActivity::class.java)
                var name = name?.text.toString()
                var maker = maker?.text.toString()
                Intent.putExtra("name", name)
                Intent.putExtra("maker", maker)
                Intent.putExtra("image", image_link.toString())
                Log.d(TAG, "Item - OnClickListener maker : ${maker}" )
                Log.d(TAG, "Item - OnClickListener itemView.context : ${itemView.context}" )
                Log.d(TAG, "Item - OnClickListener Intent.data : ${Intent.data}" )
                startActivity(itemView.context, Intent, null)
            }
        }

        fun bind (drink: drink, context: Context) {
            Glide.with(context).load(drink.imagelink).into(image_link!!)
            name?.text = drink.name
            maker?.text = drink.maker
        }
    }
}