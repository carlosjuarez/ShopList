package com.juvcarl.shoplist.features.itemDetail

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import com.juvcarl.shoplist.R
import com.juvcarl.shoplist.data.model.BUYSTATUS
import com.juvcarl.shoplist.data.model.Item
import com.juvcarl.shoplist.ui.ShopListIcons
import com.juvcarl.shoplist.ui.component.ErrorScreen
import com.juvcarl.shoplist.ui.component.LoadingScreen
import com.juvcarl.shoplist.ui.component.ShopListTopAppBar
import com.juvcarl.shoplist.ui.theme.ShopListTheme
import kotlinx.datetime.Clock

@OptIn(ExperimentalLifecycleComposeApi::class)
@Composable
fun ItemDetailRoute(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ItemDetailViewModel = hiltViewModel()
){
    val itemState by viewModel.itemUIState.collectAsState()
    ItemDetailScreen(
        itemState,
        onBackClick,
        viewModel::updateItem,
        viewModel::deleteItem
    )

}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ItemDetailScreen(
    itemState: ItemUIState,
    onBackClick: () -> Unit,
    updateItem: (Item) -> Unit = {},
    deleteItem: (Long) -> Unit = {}
){

    var item by remember{mutableStateOf<Item?>(null)}


        Scaffold (
            topBar = {
                ShopListTopAppBar(
                    titleString = item?.name,
                    navigationIcon = ShopListIcons.Back,
                    onNavigationClick = {
                        onBackClick()
                    },
                    actionIcon = if(item != null) ShopListIcons.Remove else null,
                    onActionClick = {
                        item?.let {
                            deleteItem(it.id)
                            onBackClick()
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = Color.Transparent
                    ),
                    modifier = Modifier.windowInsetsPadding(
                        WindowInsets.safeDrawing.only(WindowInsetsSides.Top)
                    )
                )
            },
            containerColor = Color.Transparent,
            contentColor = MaterialTheme.colorScheme.onBackground,
        ) { innerPadding ->
            Surface(
                modifier = Modifier
                    .padding(innerPadding)
                    .consumedWindowInsets(innerPadding)
                    .fillMaxSize()
            ) {
                when(itemState){
                    ItemUIState.Error -> ErrorScreen()
                    ItemUIState.Loading -> LoadingScreen()
                    is ItemUIState.Success -> {
                        item = itemState.item
                        ItemDisplay(item = itemState.item, updateItem = updateItem, onBackClick = onBackClick)
                    }
                }
            }
        }
}

@Composable
fun ItemDisplay(item: Item, updateItem: (Item) -> Unit = {}, onBackClick: () -> Unit){
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp)
    ) {
        Spacer(Modifier.windowInsetsTopHeight(WindowInsets.safeDrawing))
        ItemForm(item = item, updateItem = updateItem, onBackClick = onBackClick)

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemForm(
    item: Item,
    modifier: Modifier = Modifier,
    updateItem: (Item) -> Unit = {},
    onBackClick: () -> Unit = {}
){
    var name by remember { mutableStateOf(item.name) }
    var expanded by remember { mutableStateOf(false ) }
    var type by remember { mutableStateOf(item.type ?: "") }
    var buyNow by remember { mutableStateOf( item.buyAgain ) }
    Column {
        Spacer(modifier = Modifier.padding(4.dp))
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = name,
            onValueChange = { name = it },
            label = { Text(text = stringResource(id = R.string.name)) })
        Spacer(modifier = Modifier.padding(4.dp))
        Box(modifier = Modifier.fillMaxWidth()){
            Text(if(type.isEmpty()) stringResource(id = R.string.product_type) else type, modifier = Modifier
                .fillMaxWidth()
                .align(alignment = Alignment.CenterStart).padding(start = 12.dp)
                .clickable { expanded = true })
            IconButton(onClick = { expanded = true }, modifier = Modifier.align(alignment = Alignment.BottomEnd)) {
                Icon(Icons.Default.ArrowDropDown, contentDescription = stringResource(id = R.string.cd_select_type))
            }
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                DropdownMenuItem(text = { Text("Basic") },
                    onClick = { type = "basic"; expanded = false })
                DropdownMenuItem(text = { Text("Special") },
                    onClick = { type = "Special"; expanded = false })
                DropdownMenuItem(text = { Text("Unique") },
                    onClick = { type = "Unique"; expanded = false })
            }
        }
        Spacer(modifier = Modifier.padding(4.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ){
            Checkbox(checked = buyNow, onCheckedChange = { buyNow = !buyNow })
            Text(stringResource(id = R.string.buy_now_question))
        }
        Spacer(modifier = Modifier.padding(4.dp))
        Button(
            onClick = {
                val newItem = item.copy(
                    name = name.trim(),
                    buyAgain = buyNow,
                    type = type
                )
                updateItem(newItem)
                onBackClick()
            },
            modifier = modifier.align(Alignment.CenterHorizontally), shape = RoundedCornerShape(20)
        ) {
            Text(stringResource(id = R.string.update_item))
        }
    }
}


@Preview(showBackground = true)
@Composable
fun ItemFormPreview(){
    ShopListTheme {
        val item = Item(id = 0,name = "Product 1", date = Clock.System.now(),true, type = "Basic", buyQty = 4.0, buyStatus = BUYSTATUS.BUY.name)
        ItemForm(item = item)
    }
}