package com.juvcarl.shoplist.ui.component

import androidx.annotation.StringRes
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.juvcarl.shoplist.ui.Icon
import com.juvcarl.shoplist.ui.ShopListIcons
import com.juvcarl.shoplist.R
import com.juvcarl.shoplist.ui.ShopListIcon

@Composable
fun ShopListTopAppBar(
    @StringRes titleRes: Int? = null,
    navigationIcon: Icon? = null,
    actionIcon: Icon? = null,
    modifier: Modifier = Modifier,
    colors: TopAppBarColors = TopAppBarDefaults.centerAlignedTopAppBarColors(),
    onNavigationClick: () -> Unit = {},
    onActionClick: () -> Unit = {},
    titleString: String? = null,
    actionsContent: @Composable () -> Unit = {}
){
    CenterAlignedTopAppBar(
        title = {
            if(titleRes != null){
                Text(text = stringResource(id = titleRes))
            }else{
                Text(text = titleString ?: "")
            }
        },
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
            }else{
                actionsContent()
            }
        },
        colors = colors,
        modifier = modifier
    )
}

@Preview("Top App Bar")
@Composable
fun AllItemsTopAppBarPreview() {
    ShopListTopAppBar(
        titleRes = R.string.all_items,
        navigationIcon = ShopListIcons.SearchUnselected,
        actionIcon = ShopListIcons.Add
    )
}
