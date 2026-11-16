package com.mikepenz.aboutlibraries.ui.compose.android

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember

/** Third-party notices on desktop ship as a THIRD-PARTY-NOTICES file with the installer; the in-app list is a pointer. */
class Libraries

@Composable
fun produceLibraries(rawResId: Int): State<Libraries?> = remember { mutableStateOf<Libraries?>(Libraries()) }
