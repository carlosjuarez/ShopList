package com.juvcarl.shoplist.main.navigation

import com.juvcarl.shoplist.navigation.ShopListNavigationDestination
import com.juvcarl.shoplist.ui.Icon

data class TopLevelDestination(
    override val route: String,
    override val destination: String,
    val selectedIcon: Icon,
    val unselectedIcon: Icon,
    val iconTextId: Int
): ShopListNavigationDestination