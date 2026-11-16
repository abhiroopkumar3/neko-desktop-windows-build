package org.nekomanga.desktop

import androidx.activity.compose.BackDispatcher
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.input.pointer.PointerButton
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.navigation3.runtime.rememberNavBackStack
import android.content.DesktopContext
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.LibraryBooks
import androidx.compose.material.icons.automirrored.outlined.LibraryBooks
import androidx.compose.material.icons.filled.AccessTimeFilled
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.ui.res.stringResource
import eu.kanade.tachiyomi.ui.main.MainActivityViewModel
import eu.kanade.tachiyomi.ui.main.SideNavMode
import org.nekomanga.R
import org.nekomanga.presentation.components.dialog.AppUpdateDialog
import org.nekomanga.presentation.screens.MainScreen
import org.nekomanga.presentation.screens.Screens
import org.nekomanga.presentation.screens.main.BottomBar
import org.nekomanga.presentation.screens.main.NavigationItem
import org.nekomanga.presentation.screens.main.NavigationSideBar
import org.nekomanga.presentation.theme.NekoTheme
import androidx.lifecycle.compose.collectAsStateWithLifecycle

private val storeOwner = object : ViewModelStoreOwner { override val viewModelStore = ViewModelStore() }

fun main() {
  DesktopStartup.start()
  application {
    val windowState = rememberWindowState(size = DpSize(1280.dp, 800.dp))
    Window(
        onCloseRequest = ::exitApplication,
        title = DesktopContext.getString(R.string.app_name),
        state = windowState,
        onPreviewKeyEvent = { e ->
          // Esc acts as Back, like the Android back gesture.
          if (e.type == KeyEventType.KeyDown && e.key == Key.Escape) BackDispatcher.dispatch() else false
        },
    ) {
      CompositionLocalProvider(LocalViewModelStoreOwner provides storeOwner) {
        NekoTheme { DesktopShell() }
      }
    }
  }
}

/** The desktop twin of MainActivity's compose content: starting screen, rail or bottom bar, MainScreen. */
@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun DesktopShell() {
  val viewModel = remember { MainActivityViewModel() }
  val mainScreenState by viewModel.mainScreenState.collectAsStateWithLifecycle()

  val startingScreen = remember {
    when {
      !viewModel.preferences.hasShownOnboarding().get() -> Screens.Onboarding
      else ->
          when (viewModel.preferences.startingTab().get()) {
            1 -> if (viewModel.preferences.lastUsedStartingTab().get() == 0) Screens.Library() else Screens.Feed
            -2 -> Screens.Feed
            -3 -> Screens.Browse()
            else -> Screens.Library()
          }
    }
  }
  val backStack = rememberNavBackStack(startingScreen)

  val selectedItemIndex =
      remember(backStack.lastOrNull()) {
        when (backStack.lastOrNull()) {
          is Screens.Library -> 0
          is Screens.Feed -> 1
          is Screens.Browse -> 2
          else -> -1
        }
      }

  val windowSizeClass = calculateWindowSizeClass()
  val navItems =
      listOf(
          NavigationItem(Screens.Library(), stringResource(R.string.library), Icons.AutoMirrored.Outlined.LibraryBooks, Icons.AutoMirrored.Filled.LibraryBooks),
          NavigationItem(Screens.Feed, stringResource(R.string.feed), Icons.Outlined.AccessTime, Icons.Filled.AccessTimeFilled),
          NavigationItem(Screens.Browse(), stringResource(R.string.browse), Icons.Outlined.Explore, Icons.Filled.Explore),
      )
  val showNavigationRail =
      remember(windowSizeClass.widthSizeClass, mainScreenState.sideNavMode) {
        when {
          mainScreenState.sideNavMode == SideNavMode.Always -> true
          mainScreenState.sideNavMode == SideNavMode.Never -> false
          else -> windowSizeClass.widthSizeClass != WindowWidthSizeClass.Compact
        }
      }

  Box(Modifier.fillMaxSize().backNavigation()) {
    MainScreen(
        backStack = backStack,
        windowSizeClass = windowSizeClass,
        incognitoMode = mainScreenState.incognitoMode,
        incognitoClick = viewModel::toggleIncoginito,
        onboardingCompleted = viewModel::onboardingCompleted,
        navigationRail = {
          if (showNavigationRail) {
            NavigationSideBar(
                items = navItems,
                sideNavAlignment = mainScreenState.sideNavAlignment,
                libraryUpdating = false,
                downloaderRunning = false,
                selectedItemIndex = selectedItemIndex,
                onNavigate = { screen -> backStack.clear(); backStack.add(screen) },
            )
          }
        },
        bottomBar = {
          if (!showNavigationRail) {
            BottomBar(
                items = navItems,
                libraryUpdating = false,
                downloaderRunning = false,
                selectedItemIndex = selectedItemIndex,
                onNavigate = { screen -> backStack.clear(); backStack.add(screen) },
            )
          }
        },
    )
    mainScreenState.appUpdateResult?.let {
      AppUpdateDialog(release = it.release, onDismissRequest = { viewModel.consumeAppUpdateResult() }, onConfirm = { r -> viewModel.downloadUpdate(r) })
    }
  }
}

/** Mouse back button (button 4) goes back, like Esc. */
private fun Modifier.backNavigation(): Modifier =
    pointerInput(Unit) {
      awaitPointerEventScope {
        while (true) {
          val event = awaitPointerEvent(androidx.compose.ui.input.pointer.PointerEventPass.Initial)
          if (event.type == PointerEventType.Press && event.button == PointerButton.Back) {
            BackDispatcher.dispatch()
            event.changes.forEach { it.consume() }
          }
        }
      }
    }
