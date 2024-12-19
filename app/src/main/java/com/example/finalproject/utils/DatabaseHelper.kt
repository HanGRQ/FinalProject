package com.example.finalproject.utils

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import java.time.LocalDateTime

data class FoodDetails(
    val barcode: String,
    val name: String,
    val spec: String,
    val unit: String,
    val price: Double,
    val brand: String,
    val supplier: String,
    val madeIn: String,
    val createdAt: LocalDateTime?,
    val updatedAt: LocalDateTime?,
    val deletedAt: LocalDateTime?
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
            readableDatabase.version // 尝试读取数据库版本
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
                spec TEXT,
                unit TEXT,
                price REAL,
                brand TEXT,
                supplier TEXT,
                madeIn TEXT,
                createdAt TEXT,
                updatedAt TEXT,
                deletedAt TEXT
            )
        """.trimIndent())
        Log.d(TAG, "表创建完成")

        // 初始化数据
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
            INSERT INTO $TABLE_NAME (barcode, name, spec, unit, price, brand, supplier, madeIn, createdAt, updatedAt)
            VALUES (
                '6903252710175', 
                '康师傅经典香辣牛肉袋面', 
                '118g袋', 
                '包', 
                2.5, 
                '康师傅', 
                '天津顶益食品有限公司', 
                '天津', 
                '2024-11-01 13:46:42', 
                '2024-12-01 13:46:42'
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
                        spec = it.getString(it.getColumnIndexOrThrow("spec")),
                        unit = it.getString(it.getColumnIndexOrThrow("unit")),
                        price = it.getDouble(it.getColumnIndexOrThrow("price")),
                        brand = it.getString(it.getColumnIndexOrThrow("brand")),
                        supplier = it.getString(it.getColumnIndexOrThrow("supplier")),
                        madeIn = it.getString(it.getColumnIndexOrThrow("madeIn")),
                        createdAt = parseDateTime(it.getString(it.getColumnIndexOrThrow("createdAt"))),
                        updatedAt = parseDateTime(it.getString(it.getColumnIndexOrThrow("updatedAt"))),
                        deletedAt = parseDateTime(it.getString(it.getColumnIndexOrThrow("deletedAt")))
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

    private fun parseDateTime(dateStr: String?): LocalDateTime? {
        return try {
            dateStr?.let { LocalDateTime.parse(it.replace(" ", "T")) }
        } catch (e: Exception) {
            Log.e(TAG, "解析日期失败: $dateStr", e)
            null
        }
    }
}
