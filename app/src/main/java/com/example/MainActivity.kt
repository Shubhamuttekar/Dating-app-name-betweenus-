package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.Message
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.ui.PresenceViewModel
import com.example.ui.UiEvent
import com.example.ui.screens.ChatDetailScreen
import com.example.ui.screens.DiscoverScreen
import com.example.ui.screens.InnerCircleScreen
import com.example.ui.screens.MyProfileScreen
import com.example.ui.screens.SanctuaryMessagesScreen
import com.example.ui.theme.MidnightDark
import com.example.ui.theme.MidnightPresenceTheme
import com.example.ui.theme.MidnightPrimary
import com.example.ui.theme.MutedRose
import com.example.ui.theme.SoftLavender
import com.example.ui.theme.SoftLavenderContainer
import com.example.ui.theme.SurfaceIvory
import kotlinx.coroutines.flow.collectLatest

sealed class NavItem(val route: String, val title: String, val activeIcon: @Composable () -> Unit, val inactiveIcon: @Composable () -> Unit) {
    object Discover : NavItem("discover", "Presence", { Icon(Icons.Filled.AutoAwesome, contentDescription = "Presence") }, { Icon(Icons.Outlined.AutoAwesome, contentDescription = "Presence") })
    object Messages : NavItem("messages", "Dialogue", { Icon(Icons.Filled.Message, contentDescription = "Dialogue") }, { Icon(Icons.Outlined.Message, contentDescription = "Dialogue") })
    object InnerCircle : NavItem("inner_circle", "Inner Circle", { Icon(Icons.Filled.Bookmark, contentDescription = "Inner Circle") }, { Icon(Icons.Outlined.BookmarkBorder, contentDescription = "Inner Circle") })
    object Profile : NavItem("profile", "Sanctuary", { Icon(Icons.Filled.Person, contentDescription = "Sanctuary") }, { Icon(Icons.Outlined.Person, contentDescription = "Sanctuary") })
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MidnightPresenceTheme {
                PresenceApp()
            }
        }
    }
}

@Composable
fun PresenceApp() {
    val navController = rememberNavController()
    val viewModel: PresenceViewModel = viewModel()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.eventFlow.collectLatest { event ->
            when (event) {
                is UiEvent.ShowToast -> {
                    snackbarHostState.showSnackbar(event.message)
                }
                is UiEvent.NavigateToChat -> {
                    navController.navigate("chat/${event.profileId}")
                }
            }
        }
    }

    val navItems = listOf(
        NavItem.Discover,
        NavItem.Messages,
        NavItem.InnerCircle,
        NavItem.Profile
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Hide bottom navigation bar on full-screen chat detail screen
    val showBottomBar = currentRoute?.startsWith("chat/") != true

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(
                    containerColor = SurfaceIvory,
                    contentColor = MidnightPrimary,
                    modifier = Modifier
                        .windowInsetsPadding(WindowInsets.navigationBars)
                        .testTag("bottom_navigation_bar")
                ) {
                    navItems.forEach { item ->
                        val selected = currentRoute == item.route

                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                if (currentRoute != item.route) {
                                    navController.navigate(item.route) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            },
                            icon = {
                                if (selected) item.activeIcon() else item.inactiveIcon()
                            },
                            label = {
                                Text(
                                    text = item.title,
                                    color = if (selected) MidnightPrimary else MidnightPrimary.copy(alpha = 0.5f)
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                indicatorColor = SoftLavenderContainer,
                                selectedIconColor = MidnightPrimary,
                                unselectedIconColor = MidnightPrimary.copy(alpha = 0.5f)
                            ),
                            modifier = Modifier.testTag("nav_item_${item.route}")
                        )
                    }
                }
            }
        },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = NavItem.Discover.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(NavItem.Discover.route) {
                DiscoverScreen(viewModel = viewModel)
            }

            composable(NavItem.Messages.route) {
                SanctuaryMessagesScreen(
                    viewModel = viewModel,
                    onSelectProfileForChat = { profileId ->
                        navController.navigate("chat/$profileId")
                    }
                )
            }

            composable(NavItem.InnerCircle.route) {
                InnerCircleScreen(
                    viewModel = viewModel,
                    onSelectProfileForChat = { profileId ->
                        navController.navigate("chat/$profileId")
                    }
                )
            }

            composable(NavItem.Profile.route) {
                MyProfileScreen(viewModel = viewModel)
            }

            composable("chat/{profileId}") { backStackEntry ->
                val profileId = backStackEntry.arguments?.getString("profileId") ?: ""
                ChatDetailScreen(
                    profileId = profileId,
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}
