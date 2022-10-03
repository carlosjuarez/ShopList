package com.juvcarl.shoplist.features.shopitems.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.juvcarl.shoplist.features.shopitems.ShopItemsRoute
import com.juvcarl.shoplist.navigation.ShopListNavigationDestination

object ShopItemsDestination : ShopListNavigationDestination {
    override val route: String = "shop_items_route"
    override val destination: String = "shop_items_destination"
}

fun NavGraphBuilder.shopItemsGraph(){
    composable(route = ShopItemsDestination.route){
        ShopItemsRoute()
    }
}