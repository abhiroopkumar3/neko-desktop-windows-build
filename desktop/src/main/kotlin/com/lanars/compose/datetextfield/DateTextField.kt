package com.lanars.compose.datetextfield

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import java.time.LocalDate

enum class Format(val hint: String) { MMDDYYYY("MM/DD/YYYY"), YYYYMMDD("YYYY/MM/DD"), DDMMYYYY("DD/MM/YYYY") }

/** Typed date entry: digits only, slashes inserted, reports a date only once it is complete and valid. */
@Composable
fun DateTextField(
    onValueChanged: (LocalDate?) -> Unit,
    format: Format,
    maxDate: LocalDate? = null,
    modifier: Modifier = Modifier,
    textStyle: TextStyle = TextStyle.Default,
    cursorBrush: Brush = SolidColor(androidx.compose.ui.graphics.Color.Black),
    hintTextStyle: TextStyle = textStyle,
    delimiterSpacing: Dp = 2.dp,
) {
  var digits by remember { mutableStateOf("") }
  fun render(d: String): String {
    val cuts = when (format) { Format.YYYYMMDD -> listOf(4, 6); else -> listOf(2, 4) }
    return buildString { d.forEachIndexed { i, c -> if (i in cuts) append('/'); append(c) } }
  }
  fun parse(d: String): LocalDate? {
    if (d.length != 8) return null
    return runCatching {
      when (format) {
        Format.MMDDYYYY -> LocalDate.of(d.substring(4).toInt(), d.substring(0, 2).toInt(), d.substring(2, 4).toInt())
        Format.DDMMYYYY -> LocalDate.of(d.substring(4).toInt(), d.substring(2, 4).toInt(), d.substring(0, 2).toInt())
        Format.YYYYMMDD -> LocalDate.of(d.substring(0, 4).toInt(), d.substring(4, 6).toInt(), d.substring(6).toInt())
      }
    }.getOrNull()?.takeIf { maxDate == null || !it.isAfter(maxDate) }
  }
  BasicTextField(
      value = render(digits),
      onValueChange = { raw ->
        digits = raw.filter { it.isDigit() }.take(8)
        onValueChanged(parse(digits))
      },
      textStyle = textStyle,
      cursorBrush = cursorBrush,
      singleLine = true,
      keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
      modifier = modifier.width(220.dp),
      decorationBox = { inner -> if (digits.isEmpty()) Text(format.hint, style = hintTextStyle) else inner() },
  )
}
