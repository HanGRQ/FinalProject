package com.example.finalproject.ui.components

import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.example.finalproject.utils.FoodResponse
import android.graphics.Color as AndroidColor
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter

@Composable
fun NutritionBarChart(foodItems: List<FoodResponse>, modifier: Modifier = Modifier) {
    val context = LocalContext.current

    AndroidView(
        modifier = modifier,
        factory = { context ->
            BarChart(context).apply {
                layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                description.isEnabled = false
                legend.textColor = AndroidColor.GRAY
                legend.textSize = 12f
                setDrawGridBackground(false)

                xAxis.position = XAxis.XAxisPosition.BOTTOM
                xAxis.granularity = 1f
                xAxis.textColor = AndroidColor.GRAY
                xAxis.setDrawGridLines(false)

                axisLeft.textColor = AndroidColor.GRAY
                axisLeft.setDrawGridLines(true)
                axisRight.isEnabled = false

                animateY(1000)
            }
        },
        update = { chart ->
            // 定义一个颜色数组，每个食物使用不同的颜色
            val colorPalette = listOf(
                AndroidColor.parseColor("#FF4CAF50"),  // 绿色
                AndroidColor.parseColor("#FF2196F3"),  // 蓝色
                AndroidColor.parseColor("#FFFF9800"),  // 橙色
                AndroidColor.parseColor("#FFF44336"),  // 红色
                AndroidColor.parseColor("#FF9C27B0"),  // 紫色
                AndroidColor.parseColor("#FF009688"),  // 青色
                AndroidColor.parseColor("#FFFF5722"),  // 深橙色
                AndroidColor.parseColor("#FF795548")   // 棕色
            )

            val sugarEntries = foodItems.mapIndexed { index, item ->
                BarEntry(index.toFloat(), item.sugars.toFloat())
            }

            val sugarSet = BarDataSet(sugarEntries, "Sugars").apply {
                // 为每个 Bar 设置不同的颜色
                colors = colorPalette.take(foodItems.size)
            }

            val barData = BarData(sugarSet)
            barData.barWidth = 0.6f

            chart.data = barData

            chart.xAxis.valueFormatter = IndexAxisValueFormatter(
                foodItems.mapIndexed { index, food ->
                    food.product_name.ifEmpty { "Meal ${index + 1}" }
                }
            )

            chart.invalidate()
        }
    )
}