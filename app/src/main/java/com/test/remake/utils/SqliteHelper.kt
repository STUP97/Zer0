package com.test.remake.utils

import android.annotation.SuppressLint
import com.test.remake.model.drink
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.test.remake.utils.Constants.TAG

class SqliteHelper(context: Context, name: String, version: Int) : SQLiteOpenHelper(context, name, null, version) {
    override fun onCreate(db: SQLiteDatabase?) {
  //      val create = "create table DrinkTable('id' integer primary key, name text, maker text, image_url text)"
  //      db?.execSQL(create)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        TODO("Not yet implemented")
    }
    // 데이터 선택
    @SuppressLint("Range")
    fun selectDrink(): MutableList<drink>{
        val list = mutableListOf<drink>()

        val select = "select * from DrinkTable"
        val rd = readableDatabase
        val cursor = rd.rawQuery(select, null)
        while(cursor.moveToNext()){
            val id = cursor.getInt(cursor.getColumnIndex("id"))
            val name = cursor.getString(cursor.getColumnIndex("name"))
            val maker = cursor.getString(cursor.getColumnIndex("maker"))
            val image_url = cursor.getString(cursor.getColumnIndex("image_url"))

            val drink = drink(id,name,maker,image_url)
            list.add(drink)
        }
        cursor.close()
        rd.close()


        return list
    }
    // 데이터 삽입
    fun insertDrink(drink : drink){
        // db 가져오기
        var wd = writableDatabase
        // drink를 입력타입으로 변환
        var values = ContentValues()
        values.put("name", drink.name)
        values.put("maker", drink.maker)
        values.put("image_url", drink.imagelink)
        // DB에 넣기
        wd.insert("DrinkTable", null, values)
        wd.close()
    }
    // 데이터 수정
    fun updateDrink(drink: drink){
        var wd = writableDatabase

        var values = ContentValues()
        values.put("id", drink.id)
        values.put("name", drink.name)
        values.put("maker", drink.maker)
        values.put("image_url", drink.imagelink)
        Log.d(TAG, "updateDrink: ${drink.id}")
        wd.update("DrinkTable", values, "id = ${drink.id}", null)

        wd.close()
    }
    // 데이터 삭제
    fun deleteDrink(drink: drink){
        val wd = writableDatabase

        wd.delete("DrinkTable", "id = ${drink.id}", null)

        wd.close()
    }
}
