package com.himanshoe.charty.line

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.himanshoe.charty.common.axis.AxisConfig
import com.himanshoe.charty.common.dimens.ChartDimens
import com.himanshoe.charty.line.config.LineConfig
import com.himanshoe.charty.line.model.LineData
import com.himanshoe.charty.pie.config.PieConfig
import com.himanshoe.charty.pie.config.PieData

@Composable
fun LineChart(lineData: List<LineData>, color: Color, modifier: Modifier = Modifier, chartDimens: ChartDimens = ChartDimens(), axisConfig: AxisConfig = AxisConfig(), lineConfig: LineConfig = LineConfig()) {
  Canvas(modifier) {
    if (lineData.isEmpty()) return@Canvas
    val pad = chartDimens.padding.toPx()
    val w = size.width - pad * 2
    val h = size.height - pad * 2
    val maxY = (lineData.maxOf { it.yValue }).coerceAtLeast(1f)
    val step = if (lineData.size > 1) w / (lineData.size - 1) else 0f
    val pts = lineData.mapIndexed { i, p -> Offset(pad + i * step, pad + h - p.yValue / maxY * h) }
    if (axisConfig.showAxis) {
      drawLine(axisConfig.xAxisColor, Offset(pad, pad + h), Offset(pad + w, pad + h), 2f)
      drawLine(axisConfig.yAxisColor, Offset(pad, pad), Offset(pad, pad + h), 2f)
    }
    val path = Path().apply { pts.forEachIndexed { i, o -> if (i == 0) moveTo(o.x, o.y) else lineTo(o.x, o.y) } }
    drawPath(path, color, style = Stroke(4f))
    if (lineConfig.hasDotMarker) pts.forEach { drawCircle(color, 6f, it) }
  }
}
