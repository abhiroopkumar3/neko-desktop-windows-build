package androidx.activity.compose

import android.app.Activity
import androidx.compose.runtime.Composable

/** Desktop: an Activity's content is kept here and rendered by the window host (see ActivityHost). */
fun Activity.setContent(content: @Composable () -> Unit) {
  composeContent = content
}
