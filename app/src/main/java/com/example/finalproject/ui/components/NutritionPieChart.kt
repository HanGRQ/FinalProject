package com.example.finalproject.ui.components

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import android.graphics.Color as AndroidColor

@Composable
fun NutritionPieChart(
    totalEnergy: Float,
    carbs: Float,
    fat: Float,
    protein: Float,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    AndroidView(
        modifier = modifier,
        factory = { ctx: Context ->
            PieChart(ctx).apply {
                description.isEnabled = false
                isDrawHoleEnabled = true  // ✅ 让饼图有个中心空洞
                holeRadius = 40f  // ✅ 设置中心孔的大小
                transparentCircleRadius = 45f  // ✅ 设置透明圆环
                setUsePercentValues(true)  // ✅ 显示百分比
                animateY(1000)  // ✅ 让饼图有动画
            }
        },
        update = { chart ->
            // **✅ 确保至少有一个非零数据，否则清除图表**
            if (totalEnergy == 0f && carbs == 0f && fat == 0f && protein == 0f) {
                chart.clear()
                return@AndroidView
            }

            val entries = listOf(
                PieEntry(totalEnergy, "Energy"),
                PieEntry(carbs, "Carbs"),
                PieEntry(fat, "Fat"),
                PieEntry(protein, "Proteins")
            )

            val dataSet = PieDataSet(entries, "Nutrition Breakdown").apply {
                colors = listOf(
                    AndroidColor.parseColor("#FF2196F3"),
                    AndroidColor.parseColor("#FF4CAF50"), // 蓝色 - Carbohydrates
                    AndroidColor.parseColor("#FFFF9800"),  // 橙色 - Sugars
                    AndroidColor.parseColor("#FFF44336"),  // 红色 - Fat
                    AndroidColor.parseColor("#FF9C27B0")   // 紫色 - Proteins
                )
                valueTextSize = 12f
                setDrawValues(true)
                setValueLinePart1Length(0.5f)
                setValueLinePart2Length(0.4f)
                setValueLineColor(AndroidColor.BLACK)
                setYValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE)
                setXValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE)
            }

            val pieData = PieData(dataSet)

            chart.apply {
                data = pieData
                setDrawEntryLabels(false) // ✅ 直接关闭 Entry Label
                setDrawSliceText(false)   // ✅ 确保 PieChart 内部不显示 Slice 文字
                invalidate()
            }
        }
    )
}
