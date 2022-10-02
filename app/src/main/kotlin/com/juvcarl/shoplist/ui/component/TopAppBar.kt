package com.juvcarl.shoplist.ui.component

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.juvcarl.shoplist.ui.Icon
import com.juvcarl.shoplist.ui.ShopListIcons
import com.juvcarl.shoplist.R
import com.juvcarl.shoplist.ui.ShopListIcon

@Composable
fun AllItemsTopAppBar(
    @StringRes titleRes: Int,
    navigationIcon: Icon?,
    actionIcon: Icon?,
    modifier: Modifier = Modifier,
    colors: TopAppBarColors = TopAppBarDefaults.centerAlignedTopAppBarColors(),
    onNavigationClick: () -> Unit = {},
    onActionClick: () -> Unit = {}
){
    CenterAlignedTopAppBar(
        title = { Text(text = stringResource(id = titleRes)) },
        navigationIcon = {
            if(navigationIcon!=null){
                IconButton(onClick = onNavigationClick) {
                    ShopListIcon(icon = navigationIcon)
                }
            }
        },
        actions = {
            if(actionIcon != null){
                IconButton(onClick = onActionClick) {
                    ShopListIcon(icon = actionIcon)
                }
            }
        },
        colors = colors,
        modifier = modifier
    )
}

@Preview("Top App Bar")
@Composable
fun AllItemsTopAppBarPreview() {
    AllItemsTopAppBar(
        titleRes = R.string.all_items,
        navigationIcon = ShopListIcons.Search,
        actionIcon = ShopListIcons.Add
    )
}
