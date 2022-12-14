package com.juvcarl.shoplist.main.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.juvcarl.shoplist.features.allItems.navigation.AllItemsDestination
import com.juvcarl.shoplist.features.allItems.navigation.allItemsGraph
import com.juvcarl.shoplist.features.itemDetail.navigation.ItemDetailDestination
import com.juvcarl.shoplist.features.itemDetail.navigation.itemDetailGraph
import com.juvcarl.shoplist.features.settings.navigation.settingsGraph
import com.juvcarl.shoplist.features.shopitems.navigation.shopItemsGraph
import com.juvcarl.shoplist.navigation.ShopListNavigationDestination

@Composable
fun ShopListNavHost(
    navController: NavHostController,
    onNavigateToDestination: (ShopListNavigationDestination,String) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    startDestination: String = AllItemsDestination.route
){
    NavHost(
        navController = navController,startDestination = startDestination,modifier = modifier,
    ){
        allItemsGraph(
            navigateToDetail = {
                onNavigateToDestination(
                    ItemDetailDestination, ItemDetailDestination.createNavigationRoute(it)
                )
            },
            nestedGraph = {
                itemDetailGraph(onBackClick)
            }
        )
        shopItemsGraph()
        settingsGraph()
    }
}