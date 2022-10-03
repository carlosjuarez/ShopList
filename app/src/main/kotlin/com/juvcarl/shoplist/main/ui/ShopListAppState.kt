package com.juvcarl.shoplist.main.ui

import androidx.compose.material3.windowsizeclass.WindowHeightSizeClass
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.juvcarl.shoplist.R
import com.juvcarl.shoplist.features.allItems.navigation.AllItemsDestination
import com.juvcarl.shoplist.features.shopitems.navigation.ShopItemsDestination
import com.juvcarl.shoplist.main.navigation.TopLevelDestination
import com.juvcarl.shoplist.navigation.ShopListNavigationDestination
import com.juvcarl.shoplist.ui.ShopListIcons


@Composable
fun rememberShopListAppState(
    windowSizeClass: WindowSizeClass,
    navController: NavHostController = rememberNavController()
): ShopListAppState{
    return remember(navController,windowSizeClass){
        ShopListAppState(windowSizeClass = windowSizeClass, navController = navController)
    }
}

class ShopListAppState(
    val navController: NavHostController,
    val windowSizeClass: WindowSizeClass
){
    val currentDestination: NavDestination?
        @Composable get() = navController.currentBackStackEntryAsState().value?.destination

    val shouldShowBottomBar: Boolean
        get() = windowSizeClass.widthSizeClass == WindowWidthSizeClass.Compact || windowSizeClass.heightSizeClass == WindowHeightSizeClass.Compact

    /*val shouldShowNavRail: Boolean
        get() = !shouldShowBottomBar*/

    val topLevelDestinations: List<TopLevelDestination> = listOf(
        TopLevelDestination(
            route = ShopItemsDestination.route,
            destination = ShopItemsDestination.destination,
            selectedIcon = ShopListIcons.ShopListSelected,
            unselectedIcon = ShopListIcons.ShopListUnselected,
            iconTextId = R.string.shop_list
        )
    )

    fun navigate(destination: ShopListNavigationDestination, route: String? = null){
        if(destination is TopLevelDestination){
            navController.navigate(route ?: destination.route){
                popUpTo(navController.graph.findStartDestination().id){
                    saveState = true
                }
                launchSingleTop = true
                restoreState = true
            }
        } else {
            navController.navigate(route?: destination.route)
        }
    }

    fun onBackClick(){
        navController.popBackStack()
    }
}