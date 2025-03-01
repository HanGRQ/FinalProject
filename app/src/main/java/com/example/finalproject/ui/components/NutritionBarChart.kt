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
import com.github.mikephil.charting.components.Legend
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
                setPinchZoom(true)
                setScaleEnabled(true)
                isDoubleTapToZoomEnabled = true

                legend.apply {
                    isEnabled = true
                    form = Legend.LegendForm.SQUARE
                    formSize = 10f
                    textSize = 12f
                    formToTextSpace = 5f
                    xEntrySpace = 10f
                    textColor = AndroidColor.GRAY
                    verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
                    horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
                    orientation = Legend.LegendOrientation.HORIZONTAL
                }

                setDrawGridBackground(false)

                xAxis.apply {
                    position = XAxis.XAxisPosition.BOTTOM
                    granularity = 1f
                    textColor = AndroidColor.GRAY
                    setDrawGridLines(false)
                    labelRotationAngle = -45f
                    setCenterAxisLabels(true)
                }

                axisLeft.apply {
                    textColor = AndroidColor.GRAY
                    setDrawGridLines(true)
                }
                axisRight.isEnabled = false

                animateY(1000)
            }
        },
        update = { chart ->
            // **🚨 如果 `foodItems` 为空，清除图表**
            if (foodItems.isEmpty()) {
                chart.clear()
                return@AndroidView
            }

            // **营养属性的颜色**
            val nutritionColors = listOf(
                AndroidColor.parseColor("#FF4CAF50"),  // Energy - 绿色
                AndroidColor.parseColor("#FF2196F3"),  // Carbohydrates - 蓝色
                AndroidColor.parseColor("#FFFF9800"),  // Sugars - 橙色
                AndroidColor.parseColor("#FFF44336"),  // Fat - 红色
                AndroidColor.parseColor("#FF9C27B0")   // Proteins - 紫色
            )

            // **营养属性**
            val nutritionAttributes = listOf(
                "energy_kcal" to "Energy (kcal)",
                "carbohydrates" to "Carbohydrates",
                "sugars" to "Sugars",
                "fat" to "Fat",
                "proteins" to "Proteins"
            )

            val dataSets = nutritionAttributes.mapIndexed { index, (attribute, label) ->
                val entries = foodItems.mapIndexed { foodIndex, food ->
                    val value = when (attribute) {
                        "energy_kcal" -> food.energy_kcal
                        "carbohydrates" -> food.carbohydrates
                        "sugars" -> food.sugars
                        "fat" -> food.fat
                        "proteins" -> food.proteins
                        else -> 0.0
                    }
                    BarEntry(foodIndex.toFloat(), value.toFloat())
                }

                BarDataSet(entries, label).apply {
                    color = nutritionColors[index % nutritionColors.size]  // 防止颜色索引溢出
                    setDrawValues(true)
                }
            }

            val barData = BarData(dataSets)
            val groupCount = foodItems.size.takeIf { it > 0 } ?: 1  // **防止 groupCount 为 0**
            val groupSpace = 0.3f
            val barSpace = 0.05f
            val barWidth = 0.15f

            barData.barWidth = barWidth
            chart.data = barData

            // **设置 X 轴**
            chart.xAxis.apply {
                axisMinimum = 0f
                axisMaximum = groupCount.toFloat()
                valueFormatter = IndexAxisValueFormatter(
                    if (foodItems.isNotEmpty()) foodItems.map { it.product_name.ifEmpty { "Unknown" } }
                    else listOf("No Data")
                )
            }

            chart.groupBars(0f, groupSpace, barSpace)
            chart.setVisibleXRangeMaximum(5f)
            chart.isScaleXEnabled = true
            chart.setPinchZoom(true)
            chart.invalidate()
        }
    )
}
