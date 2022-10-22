package com.juvcarl.shoplist.ui

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.juvcarl.shoplist.R
import com.juvcarl.shoplist.ui.Icon.ImageVectorIcon
import com.juvcarl.shoplist.ui.theme.ShopListTheme

@Composable
fun ShopListIcon(icon: Icon, modifier: Modifier = Modifier, tint : Color = MaterialTheme.colorScheme.primary){
    when (icon) {
        is ImageVectorIcon -> Icon(
            imageVector = icon.imageVector,
            contentDescription = stringResource(id = icon.description),
            modifier = modifier,
            tint = tint
        )
        is Icon.DrawableResourceIcon -> Icon(
            painter = painterResource(id = icon.id),
            contentDescription = stringResource(id = icon.description),
            modifier = modifier,
            tint = tint
        )
        else -> {}
    }
}

@Composable
fun ShopListIconWithLabel(icon: Icon, label : @Composable () -> Unit, modifier: Modifier = Modifier, tint : Color = MaterialTheme.colorScheme.primary) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally) {
        ShopListIcon(icon = icon, tint = tint)
        label()
    }
}


object ShopListIcons {
    val Remove = Icon.DrawableResourceIcon(R.drawable.ic_outline_delete, R.string.remove_item)
    val SearchUnselected = Icon.DrawableResourceIcon(R.drawable.ic_outline_search_unselected, R.string.search)
    val SearchSelected = Icon.DrawableResourceIcon(R.drawable.ic_outline_search_selected, R.string.search)
    val Add = Icon.DrawableResourceIcon(R.drawable.ic_outline_add, R.string.add_new_item)
    val BuyAgain = Icon.DrawableResourceIcon(R.drawable.ic_outline_buy_now, R.string.buy_again)
    val WaitToBuy = Icon.DrawableResourceIcon(R.drawable.ic_outline_wait_to_buy, R.string.wait_to_buy)
    val ShopListSelected = Icon.DrawableResourceIcon(R.drawable.ic_outline_shoplist_selected, R.string.shop_list_selected)
    val ShopListUnselected = Icon.DrawableResourceIcon(R.drawable.ic_outline_shoplist_unselected, R.string.shop_list_unselected)
    val DontBuy = Icon.DrawableResourceIcon(R.drawable.ic_outline_wait_to_buy, R.string.dont_buy)
    val BuyNow = Icon.DrawableResourceIcon(R.drawable.ic_outline_buy_now, R.string.buy_now)
    val Bought = Icon.DrawableResourceIcon(R.drawable.ic_outline_done, R.string.bought)
    val Back = ImageVectorIcon(Icons.Default.ArrowBack,R.string.back)
    val SettingsUnselected = Icon.DrawableResourceIcon(R.drawable.ic_outline_settings_unselected, R.string.settings)
    val SettingsSelected = Icon.DrawableResourceIcon(R.drawable.ic_outline_settings_selected, R.string.settings)
}

sealed class Icon {
    data class ImageVectorIcon(val imageVector: ImageVector, val description: Int) : Icon()
    data class DrawableResourceIcon(@DrawableRes val id: Int, val description: Int) : Icon()
}


@Preview
@Composable
fun ShopListIconWithLabelPreview(){
    ShopListTheme {
        ShopListIconWithLabel(ShopListIcons.Add, {Text(text = "Add")})
    }
}