package androidx.core.text

fun CharSequence.isDigitsOnly(): Boolean = all { it in '0'..'9' }
