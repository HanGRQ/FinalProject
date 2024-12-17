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
        private const val DATABASE_NAME = "test.db"
        private const val DATABASE_VERSION = 1
        private const val TABLE_NAME = "products"  // 表名
    }

    fun isDatabaseValid(): Boolean {
        return try {
            readableDatabase.version  // 尝试读取数据库版本
            Log.d(TAG, "数据库验证成功")
            true
        } catch (e: Exception) {
            Log.e(TAG, "数据库验证失败", e)
            false
        }
    }


    init {
        Log.d(TAG, "初始化 DatabaseHelper")
        copyDatabaseIfNeeded()
    }

    /**
     * 检查并复制数据库文件到应用内部目录
     */
    private fun copyDatabaseIfNeeded() {
        val dbFile = context.getDatabasePath(DATABASE_NAME)
        if (dbFile.exists()) {
            Log.d(TAG, "数据库文件已存在")
            return
        }
        try {
            dbFile.parentFile?.mkdirs()
            context.assets.open(DATABASE_NAME).use { input ->
                dbFile.outputStream().use { output ->
                    input.copyTo(output)
                    Log.d(TAG, "数据库文件复制完成")
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "复制数据库失败", e)
            throw RuntimeException("无法复制数据库", e)
        }
    }

    override fun onCreate(db: SQLiteDatabase) {
        Log.d(TAG, "onCreate: 不执行创建表，因为使用预制数据库")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        Log.d(TAG, "onUpgrade: $oldVersion -> $newVersion")
    }

    /**
     * 根据条形码查询商品详情
     */
    fun getFoodDetailsByBarcode(barcode: String): FoodDetails? {
        Log.d(TAG, "开始查询条形码: $barcode")
        try {
            val db = readableDatabase

            // 检查表结构
            val cursor = db.rawQuery("SELECT * FROM $TABLE_NAME LIMIT 1", null)
            val columnNames = cursor.columnNames
            Log.d(TAG, "表结构: ${columnNames.joinToString()}")
            cursor.close()

            // 查询数据
            val productCursor = db.query(
                TABLE_NAME,
                null,
                "barcode = ?",
                arrayOf(barcode),
                null,
                null,
                null
            )

            return productCursor.use { cursor ->
                if (cursor.moveToFirst()) {
                    Log.d(TAG, "找到匹配记录")
                    FoodDetails(
                        barcode = cursor.getString(cursor.getColumnIndexOrThrow("barcode")),
                        name = cursor.getString(cursor.getColumnIndexOrThrow("name")),
                        spec = cursor.getString(cursor.getColumnIndexOrThrow("spec")),
                        unit = cursor.getString(cursor.getColumnIndexOrThrow("unit")),
                        price = cursor.getDouble(cursor.getColumnIndexOrThrow("price")),
                        brand = cursor.getString(cursor.getColumnIndexOrThrow("brand")),
                        supplier = cursor.getString(cursor.getColumnIndexOrThrow("supplier")),
                        madeIn = cursor.getString(cursor.getColumnIndexOrThrow("madeIn")),
                        createdAt = parseDateTime(cursor.getString(cursor.getColumnIndexOrThrow("createdAt"))),
                        updatedAt = parseDateTime(cursor.getString(cursor.getColumnIndexOrThrow("updatedAt"))),
                        deletedAt = parseDateTime(cursor.getString(cursor.getColumnIndexOrThrow("deletedAt")))
                    )
                } else {
                    Log.d(TAG, "未找到匹配记录")
                    null
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "查询失败", e)
            return null
        }
    }

    /**
     * 解析日期时间字符串
     */
    private fun parseDateTime(dateStr: String?): LocalDateTime? {
        if (dateStr == null) return null
        return try {
            LocalDateTime.parse(dateStr.replace(" ", "T"))
        } catch (e: Exception) {
            Log.e(TAG, "解析日期失败: $dateStr", e)
            null
        }
    }

    /**
     * 关闭数据库连接
     */
    override fun close() {
        try {
            super.close()
            Log.d(TAG, "数据库连接已关闭")
        } catch (e: Exception) {
            Log.e(TAG, "关闭数据库失败", e)
        }
    }
}
