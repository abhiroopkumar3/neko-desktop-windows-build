package me.saket.cascade

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.width
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem as MaterialDropdownMenuItem
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.PopupProperties
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text

/** Desktop implementation of Cascade's drop-down menu: items with [children] open as a sub-page. */
interface CascadeColumnScope : ColumnScope {
  @Composable
  fun DropdownMenuHeader(modifier: Modifier = Modifier, text: @Composable () -> Unit)

  @Composable
  fun DropdownMenuItem(
      text: @Composable () -> Unit,
      children: @Composable CascadeColumnScope.() -> Unit,
      modifier: Modifier = Modifier,
      enabled: Boolean = true,
      leadingIcon: (@Composable () -> Unit)? = null,
      childrenHeader: (@Composable CascadeColumnScope.() -> Unit)? = null,
  )
}

private class Page(val header: (@Composable CascadeColumnScope.() -> Unit)?, val content: @Composable CascadeColumnScope.() -> Unit)

private class ScopeImpl(delegate: ColumnScope, val push: (Page) -> Unit) : CascadeColumnScope, ColumnScope by delegate {
  @Composable
  override fun DropdownMenuHeader(modifier: Modifier, text: @Composable () -> Unit) {
    MaterialDropdownMenuItem(text = text, onClick = {}, modifier = modifier, enabled = false)
  }

  @Composable
  override fun DropdownMenuItem(
      text: @Composable () -> Unit, children: @Composable CascadeColumnScope.() -> Unit, modifier: Modifier, enabled: Boolean,
      leadingIcon: (@Composable () -> Unit)?, childrenHeader: (@Composable CascadeColumnScope.() -> Unit)?,
  ) {
    MaterialDropdownMenuItem(
        text = text, onClick = { push(Page(childrenHeader, children)) }, modifier = modifier, enabled = enabled,
        leadingIcon = leadingIcon, trailingIcon = { Text("›", style = MaterialTheme.typography.titleMedium) },
    )
  }
}

@Composable
fun CascadeDropdownMenu(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    offset: DpOffset = DpOffset(0.dp, 0.dp),
    fixedWidth: Dp? = null,
    shape: Shape = MaterialTheme.shapes.extraSmall,
    properties: PopupProperties = PopupProperties(focusable = true),
    content: @Composable CascadeColumnScope.() -> Unit,
) {
  val stack = remember { mutableStateListOf<Page>() }
  DropdownMenu(
      expanded = expanded,
      onDismissRequest = { stack.clear(); onDismissRequest() },
      modifier = if (fixedWidth != null) modifier.width(fixedWidth) else modifier,
      offset = offset, shape = shape, properties = properties,
  ) {
    val scope = ScopeImpl(this) { stack.add(it) }
    val page = stack.lastOrNull()
    if (page == null) scope.content()
    else {
      MaterialDropdownMenuItem(text = { Text("‹ Back") }, onClick = { stack.removeAt(stack.lastIndex) })
      page.header?.let { scope.it() }
      scope.(page.content)()
    }
  }
}
