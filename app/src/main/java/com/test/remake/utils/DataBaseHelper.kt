package com.test.remake.utils

import android.content.Context
import android.database.sqlite.SQLiteDatabase

import java.io.IOException

import java.io.FileOutputStream

import java.io.OutputStream

import java.io.InputStream

import java.io.File

import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.test.remake.utils.Constants.TAG


class DataBaseHelper(context: Context) : SQLiteOpenHelper(context, DB_NAME, null, 1) {
    private val mContext: Context
    private fun dataBaseCheck() {
        val dbFile = File(DB_PATH + DB_NAME)
        if (!dbFile.exists()) {
            Log.d(TAG, "dataBaseCheck : No have DB ${dbFile}")
            dbCopy()
        }
    }

    // db를 assets에서 복사해온다.
    private fun dbCopy() {
        try {
            val folder = File(DB_PATH)
            if (!folder.exists()) {
                folder.mkdir()
            }
            val inputStream: InputStream = mContext.getAssets().open(DB_NAME)
            Log.d(TAG, "dbCopy: infile ${DB_NAME}")
            val out_filename = DB_PATH + DB_NAME
            Log.d(TAG, "dbCopy: outfile ${out_filename}")
            val outputStream: OutputStream = FileOutputStream(out_filename)
            val mBuffer = ByteArray(5555555)
            var mLength: Int
            while (inputStream.read(mBuffer).also { mLength = it } > 0) {
                outputStream.write(mBuffer, 0, mLength)
            }
            outputStream.flush()
            outputStream.close()
            inputStream.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    override fun onCreate(db: SQLiteDatabase) {}
    override fun onOpen(db: SQLiteDatabase) {
        super.onOpen(db)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {}

    companion object {
        // database 의 파일 경로
        private var DB_PATH = ""
        private const val DB_NAME = "DrinkDB.db"
    }

    init {
        DB_PATH = "/data/data/" + context.getPackageName().toString() + "/databases/"
        mContext = context
        dataBaseCheck()
    }
}