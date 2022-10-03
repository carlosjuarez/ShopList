package com.juvcarl.shoplist.features.shopitems

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.juvcarl.shoplist.R
import com.juvcarl.shoplist.data.model.Item
import com.juvcarl.shoplist.features.allItems.*
import com.juvcarl.shoplist.ui.ShopListIcons
import com.juvcarl.shoplist.ui.component.ErrorScreen
import com.juvcarl.shoplist.ui.component.LoadingScreen
import com.juvcarl.shoplist.ui.component.ShopListTopAppBar
import com.juvcarl.shoplist.ui.theme.ShopListTheme

@OptIn(ExperimentalLifecycleComposeApi::class)
@Composable
fun ShopItemsRoute(
    modifier: Modifier = Modifier,
    viewModel: ShopItemsViewModel = hiltViewModel()
){
    val itemsState: ShopItemsUIState by viewModel.itemUIState.collectAsStateWithLifecycle()

    ShopItemsScreen(shopItemsState = itemsState)
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ShopItemsScreen(shopItemsState: ShopItemsUIState){
    ShopListTheme {
        Scaffold (
            topBar = {
                ShopListTopAppBar(
                    titleRes = R.string.shop_list,
                    navigationIcon = ShopListIcons.Search,
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = Color.Transparent
                    ),
                    modifier = Modifier.windowInsetsPadding(
                        WindowInsets.safeDrawing.only(WindowInsetsSides.Top)
                    )
                )
            },
            containerColor = MaterialTheme.colorScheme.background,
            contentColor = MaterialTheme.colorScheme.onBackground,
        ) { innerPadding ->
            Surface(
                modifier = Modifier
                    .padding(innerPadding)
                    .consumedWindowInsets(innerPadding)
                    .fillMaxSize()
            ) {
                when(shopItemsState){
                    ShopItemsUIState.Error -> ErrorScreen()
                    ShopItemsUIState.Loading -> LoadingScreen()
                    is ShopItemsUIState.Success -> ShopItemsList(shopItemsState.items)
                }
            }
        }
    }
}

@Composable
fun ShopItemsList(itemsList: List<Item>) {
    LazyColumn(modifier = Modifier
        .fillMaxSize()
        .padding(12.dp)){
        item {
            Spacer(Modifier.windowInsetsTopHeight(WindowInsets.safeDrawing))
        }
        if(itemsList.isEmpty()){
            item{
                Column(modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)) {
                    Text(stringResource(id = R.string.no_items_to_shop), modifier = Modifier.align(alignment = Alignment.CenterHorizontally))
                }
            }
        }else{
            items(itemsList){ product ->
                ItemCard(item = product)
                Divider()
            }
        }
        item {
            Spacer(Modifier.windowInsetsTopHeight(WindowInsets.safeDrawing))
        }
    }
}

@Composable
fun ItemCard(item: Item){
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ){
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.8f)
        ) {
            Text(text = item.name, style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.padding(4.dp))
            Text(text = item.type, style = MaterialTheme.typography.titleMedium)
        }
    }
}