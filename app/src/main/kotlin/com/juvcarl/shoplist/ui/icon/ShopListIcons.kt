package com.juvcarl.shoplist.ui

import androidx.annotation.DrawableRes
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.juvcarl.shoplist.R
import com.juvcarl.shoplist.ui.Icon.ImageVectorIcon

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

object ShopListIcons {
    val Search = Icon.DrawableResourceIcon(R.drawable.ic_outline_search, R.string.search)
    val Add = Icon.DrawableResourceIcon(R.drawable.ic_outline_add, R.string.add_new_item)
    val BuyAgain = Icon.DrawableResourceIcon(R.drawable.ic_outline_buy_now, R.string.buy_again)
    val WaitToBuy = Icon.DrawableResourceIcon(R.drawable.ic_outline_wait_to_buy, R.string.wait_to_buy)
}

sealed class Icon {
    data class ImageVectorIcon(val imageVector: ImageVector, val description: Int) : Icon()
    data class DrawableResourceIcon(@DrawableRes val id: Int, val description: Int) : Icon()
}
