package com.example.finalproject.utils

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

data class FoodDetails(
    val barcode: String,
    val name: String,
    val totalEnergyKJ: Double,
    val totalEnergyKcal: Double,
    val carbohydrates: Double,
    val fat: Double,
    val protein: Double,
    val sodium: Double
)

class DatabaseHelper(private val context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val TAG = "DatabaseHelper"
        private const val DATABASE_NAME = "food.db"
        private const val DATABASE_VERSION = 1
        private const val TABLE_NAME = "products"
    }

    fun isDatabaseValid(): Boolean {
        return try {
            readableDatabase.version
            Log.d(TAG, "数据库验证成功")
            true
        } catch (e: Exception) {
            Log.e(TAG, "数据库验证失败", e)
            false
        }
    }

    override fun onCreate(db: SQLiteDatabase) {
        Log.d(TAG, "onCreate: 开始创建表")
        db.execSQL("""
            CREATE TABLE IF NOT EXISTS $TABLE_NAME (
                barcode TEXT PRIMARY KEY,
                name TEXT,
                totalEnergyKJ REAL,
                totalEnergyKcal REAL,
                carbohydrates REAL,
                fat REAL,
                protein REAL,
                sodium REAL
            )
        """.trimIndent())
        Log.d(TAG, "表创建完成")

        seedInitialData(db)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        Log.d(TAG, "onUpgrade: $oldVersion -> $newVersion，删除旧表并重新创建")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    private fun seedInitialData(db: SQLiteDatabase) {
        Log.d(TAG, "初始化数据")
        db.execSQL("""
            INSERT INTO $TABLE_NAME (
                barcode, 
                name, 
                totalEnergyKJ,
                totalEnergyKcal,
                carbohydrates,
                fat,
                protein,
                sodium
            )
            VALUES (
                '6903252710175',
                '康师傅经典香辣牛肉袋面',
                2018.0,
                482.0,
                52.60,
                27.10,
                7.10,
                2606.0
            )
        """.trimIndent())
        Log.d(TAG, "初始数据插入完成")
    }

    fun getFoodDetailsByBarcode(barcode: String): FoodDetails? {
        Log.d(TAG, "开始查询条形码: $barcode")
        return try {
            val db = readableDatabase
            val cursor = db.query(
                TABLE_NAME,
                null,
                "barcode = ?",
                arrayOf(barcode),
                null,
                null,
                null
            )
            cursor.use {
                if (it.moveToFirst()) {
                    Log.d(TAG, "找到匹配记录")
                    FoodDetails(
                        barcode = it.getString(it.getColumnIndexOrThrow("barcode")),
                        name = it.getString(it.getColumnIndexOrThrow("name")),
                        totalEnergyKJ = it.getDouble(it.getColumnIndexOrThrow("totalEnergyKJ")),
                        totalEnergyKcal = it.getDouble(it.getColumnIndexOrThrow("totalEnergyKcal")),
                        carbohydrates = it.getDouble(it.getColumnIndexOrThrow("carbohydrates")),
                        fat = it.getDouble(it.getColumnIndexOrThrow("fat")),
                        protein = it.getDouble(it.getColumnIndexOrThrow("protein")),
                        sodium = it.getDouble(it.getColumnIndexOrThrow("sodium"))
                    )
                } else {
                    Log.d(TAG, "未找到匹配记录")
                    null
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "查询失败", e)
            null
        }
    }
}