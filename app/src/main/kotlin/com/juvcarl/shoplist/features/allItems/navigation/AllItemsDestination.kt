package com.juvcarl.shoplist.features.allItems.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.juvcarl.shoplist.features.allItems.AllItemsRoute
import com.juvcarl.shoplist.navigation.ShopListNavigationDestination

object AllItemsDestination : ShopListNavigationDestination{
    override val route = "all_items_route"
    override val destination = "all_items_destination"
}

fun NavGraphBuilder.allItemsGraph(
    navigateToDetail: (Long) -> Unit,
    nestedGraph: NavGraphBuilder.() -> Unit
){
    navigation(
        route = AllItemsDestination.route,
        startDestination = AllItemsDestination.destination
    ){
        composable(
            route = AllItemsDestination.destination
        ){
            AllItemsRoute(
                navigateToDetail = navigateToDetail
            )
        }
        nestedGraph()
    }

}