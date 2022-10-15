package com.juvcarl.shoplist.features.shopitems

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.juvcarl.shoplist.R
import com.juvcarl.shoplist.data.model.Item
import com.juvcarl.shoplist.ui.ShopListIcons
import com.juvcarl.shoplist.ui.component.*
import com.juvcarl.shoplist.ui.theme.ShopListTheme
import kotlinx.coroutines.delay

@OptIn(ExperimentalLifecycleComposeApi::class)
@Composable
fun ShopItemsRoute(
    modifier: Modifier = Modifier,
    viewModel: ShopItemsViewModel = hiltViewModel()
){
    val itemsState: ShopItemsUIState by viewModel.itemUIState.collectAsStateWithLifecycle()

    ShopItemsScreen(shopItemsState = itemsState, viewModel::searchItem)
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ShopItemsScreen(shopItemsState: ShopItemsUIState, searchProduct: (String) -> Unit = {}){
    var showSearchBar by remember { mutableStateOf(false) }


    ShopListTheme {
        Scaffold (
            topBar = {
                ShopListTopAppBar(
                    titleRes = R.string.shop_list,
                    navigationIcon = if(showSearchBar) ShopListIcons.SearchSelected else ShopListIcons.SearchUnselected,
                    onNavigationClick = {
                        showSearchBar = !showSearchBar
                        if(!showSearchBar) searchProduct("")
                    },
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
                    is ShopItemsUIState.Success -> ShopItemsList(shopItemsState.items, showSearchBar, searchProduct)
                }
            }
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ShopItemsList(itemsList: List<Item>, showSearchBar: Boolean, searchProduct: (String) -> Unit) {

    val focusRequester = FocusRequester()
    val keyboard = LocalSoftwareKeyboardController.current


    LazyColumn(modifier = Modifier
        .fillMaxSize()
        .padding(12.dp)){
        item {
            Spacer(Modifier.windowInsetsTopHeight(WindowInsets.safeDrawing))
        }
        if(showSearchBar){
            item {
                SearchBar(modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester),
                    searchAction = searchProduct)
            }
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

    LaunchedEffect(showSearchBar) {
        Log.d("ShopList","showKeyboard value = $showSearchBar")
        if (showSearchBar) {
            focusRequester.requestFocus()
            delay(100) // Make sure you have delay here
            keyboard?.show()
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
            ProductName(productName = item.name)
            Spacer(modifier = Modifier.padding(4.dp))
            item.type?.let {
                ProductTag(tag = item.type)
            }
        }
    }
}