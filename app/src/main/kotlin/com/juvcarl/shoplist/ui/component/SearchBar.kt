package com.juvcarl.shoplist.ui.component

import androidx.annotation.IdRes
import androidx.annotation.StringRes
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.juvcarl.shoplist.ui.theme.ShopListTheme
import com.juvcarl.shoplist.R
import com.juvcarl.shoplist.ui.ShopListIcon
import com.juvcarl.shoplist.ui.ShopListIcons

@Composable
fun SearchBar(
    modifier: Modifier = Modifier,
    @StringRes hint : Int = R.string.search_label,
    @StringRes label : Int = R.string.search_hint,
    searchAction: (String) -> Unit = {}
){
    var searchString by remember { mutableStateOf("") }

    OutlinedTextField(value = searchString, onValueChange = {
        searchString = it
        searchAction(it)
    }
        , modifier = modifier,label = {
        Text(text = stringResource(id = label))
    }
        , singleLine = true, placeholder = {
        Text(text = stringResource(id = hint))
    },
        trailingIcon = {
        ShopListIcon(icon = ShopListIcons.SearchUnselected)
    })
}

@Preview(showBackground = true)
@Composable
fun SearchBarPreview(){
    ShopListTheme {
        SearchBar()
    }
}