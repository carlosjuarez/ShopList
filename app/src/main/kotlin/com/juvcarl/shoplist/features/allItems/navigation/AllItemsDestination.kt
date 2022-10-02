package com.juvcarl.shoplist.features.allItems.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.juvcarl.shoplist.features.allItems.AllItemsRoute
import com.juvcarl.shoplist.navigation.ShopListNavigationDestination

object AllItemsDestination : ShopListNavigationDestination{
    override val route = "all_items_route"
    override val destination = "all_items_destination"
}

fun NavGraphBuilder.allItemsGraph(){
    composable(route = AllItemsDestination.route){
        AllItemsRoute()
    }
}