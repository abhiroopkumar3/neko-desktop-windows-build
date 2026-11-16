package com.himanshoe.charty.pie

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
fun PieChart(modifier: Modifier = Modifier, pieData: List<PieData>, config: PieConfig = PieConfig()) {
  val total = pieData.sumOf { it.data.toDouble() }.toFloat().coerceAtLeast(0.0001f)
  Canvas(modifier.then(Modifier.androidxSquare())) {
    val d = minOf(size.width, size.height)
    val stroke = if (config.isDonut) d * 0.22f else d
    val inset = stroke / 2
    var start = -90f
    pieData.forEach { slice ->
      val sweep = slice.data / total * 360f
      drawArc(slice.color, start, sweep, useCenter = !config.isDonut, topLeft = Offset(inset, inset),
          size = Size(d - stroke, d - stroke), style = if (config.isDonut) Stroke(stroke) else androidx.compose.ui.graphics.drawscope.Fill)
      start += sweep
    }
  }
}

private fun Modifier.androidxSquare(): Modifier = this.aspectRatio(1f)
