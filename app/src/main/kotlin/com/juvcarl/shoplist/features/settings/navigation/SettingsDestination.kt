package com.juvcarl.shoplist.features.settings.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.juvcarl.shoplist.features.settings.SettingsRoute
import com.juvcarl.shoplist.navigation.ShopListNavigationDestination

object SettingsDestination : ShopListNavigationDestination {
    override val route: String = "settings_route"
    override val destination: String = "settings_destination"
}

fun NavGraphBuilder.settingsGraph(){
    composable(route = SettingsDestination.route){
        SettingsRoute()
    }
}