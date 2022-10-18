package com.juvcarl.shoplist.features.itemDetail.navigation

import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.juvcarl.shoplist.features.itemDetail.ItemDetailRoute
import com.juvcarl.shoplist.navigation.ShopListNavigationDestination

object ItemDetailDestination : ShopListNavigationDestination {
    const val itemIdArg = "itemId"
    override val route: String = "item_detail_route/{$itemIdArg}"
    override val destination: String = "item_detail_destination"

    fun createNavigationRoute(itemIdArg: Long): String{
        val itemId = itemIdArg
        return "item_detail_route/$itemId"

    }

    fun fromNavArgs(entry: NavBackStackEntry): Long{
        val itemId = entry.arguments?.getLong(itemIdArg)!!
        return itemId
    }
}

fun NavGraphBuilder.itemDetailGraph(
    onBackClick: () -> Unit
){
    composable(
        route = ItemDetailDestination.route,
        arguments = listOf(
            navArgument(ItemDetailDestination.itemIdArg) { type = NavType.LongType }
        )
    ){
        ItemDetailRoute(onBackClick)
    }
}