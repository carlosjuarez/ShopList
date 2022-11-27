package com.juvcarl.shoplist

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import com.juvcarl.shoplist.features.shopitems.navigation.ShopItemsDestination
import com.juvcarl.shoplist.main.navigation.ShopListNavHost
import com.juvcarl.shoplist.main.navigation.TopLevelDestination
import com.juvcarl.shoplist.main.ui.ShopListAppState
import com.juvcarl.shoplist.main.ui.rememberShopListAppState
import com.juvcarl.shoplist.ui.Icon
import com.juvcarl.shoplist.ui.component.ShopListBackground
import com.juvcarl.shoplist.ui.component.ShopListGradientBackground
import com.juvcarl.shoplist.ui.component.ShopListNavigationBar
import com.juvcarl.shoplist.ui.component.ShopListNavigationBarItem
import com.juvcarl.shoplist.ui.theme.ShopListTheme

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ShopListApp(
    windowSizeClass: WindowSizeClass,
    appState: ShopListAppState = rememberShopListAppState(windowSizeClass)
){
    ShopListTheme {

        val background: @Composable (@Composable () -> Unit) -> Unit =
            when (appState.currentDestination?.route) {
                ShopItemsDestination.route -> { content -> ShopListGradientBackground(content = content) }
                else -> { content -> ShopListBackground(content = content) }
            }

        background{
            Scaffold(
                containerColor = Color.Transparent,
                contentColor = MaterialTheme.colorScheme.onBackground,
                bottomBar = {
                    if(appState.shouldShowBottomBar){
                        ShopListBottomBar(
                            destinations = appState.topLevelDestinations,
                            onNavigateToDestination = appState::navigate,
                            currentDestination = appState.currentDestination
                        )
                    }
                }
            ){
                    paddingValues ->
                Row(
                    Modifier
                        .fillMaxSize()
                        .windowInsetsPadding(
                            WindowInsets.safeDrawing.only(
                                WindowInsetsSides.Horizontal
                            )
                        )
                ){
                    ShopListNavHost(
                        navController = appState.navController,
                        onNavigateToDestination = appState::navigate,
                        onBackClick = appState::onBackClick,
                        modifier = Modifier.padding(paddingValues)
                            .consumedWindowInsets(paddingValues))
                }
            }
        }
    }
}

@Composable
fun ShopListBottomBar(
    destinations: List<TopLevelDestination>,
    onNavigateToDestination: (TopLevelDestination) -> Unit,
    currentDestination: NavDestination?
) {
    Surface(color = Color.Transparent) {
        ShopListNavigationBar(
            modifier = Modifier.windowInsetsPadding(
                WindowInsets.safeDrawing.only(
                    WindowInsetsSides.Horizontal + WindowInsetsSides.Bottom
                )
            )
        ){
            destinations.forEach { destination ->
                val selected =
                    currentDestination?.hierarchy?.any { it.route == destination.route } == true
                ShopListNavigationBarItem(
                    selected = selected,
                    onClick = { onNavigateToDestination(destination) },
                    icon = {
                        val icon = if (selected) {
                            destination.selectedIcon
                        } else {
                            destination.unselectedIcon
                        }
                        when (icon) {
                            is Icon.ImageVectorIcon -> Icon(
                                imageVector = icon.imageVector,
                                contentDescription = null
                            )
                            is Icon.DrawableResourceIcon -> Icon(
                                painter = painterResource(id = icon.id),
                                contentDescription = null
                            )
                            else -> {}
                        }
                    },
                    label = { Text(stringResource(destination.iconTextId)) }
                )
            }
        }
    }
}
