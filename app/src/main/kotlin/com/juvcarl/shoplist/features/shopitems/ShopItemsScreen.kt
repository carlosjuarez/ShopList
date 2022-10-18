package com.juvcarl.shoplist.features.shopitems

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.juvcarl.shoplist.R
import com.juvcarl.shoplist.data.model.BUYSTATUS
import com.juvcarl.shoplist.data.model.Item
import com.juvcarl.shoplist.extensions.StringWithoutZeroDecimal
import com.juvcarl.shoplist.ui.Icon
import com.juvcarl.shoplist.ui.ShopListIconWithLabel
import com.juvcarl.shoplist.ui.ShopListIcons
import com.juvcarl.shoplist.ui.component.*
import com.juvcarl.shoplist.ui.theme.ShopListTheme
import kotlinx.coroutines.delay
import kotlinx.datetime.Clock

@OptIn(ExperimentalLifecycleComposeApi::class)
@Composable
fun ShopItemsRoute(
    modifier: Modifier = Modifier,
    viewModel: ShopItemsViewModel = hiltViewModel()
){
    val itemsState: ShopItemsUIState by viewModel.itemsUIState.collectAsStateWithLifecycle()
    val expandedItemsList: List<Long> by viewModel.expandedItemList.collectAsStateWithLifecycle()

    ShopItemsScreen(shopItemsState = itemsState, expandedItemsList, viewModel::searchItem, viewModel::changeBuyStatus, viewModel::updateBuyQty, viewModel::toggleExpandedListItem)
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ShopItemsScreen(
    shopItemsState: ShopItemsUIState,
    expandedItemsList: List<Long>,
    searchProduct: (String) -> Unit = {},
    changeBuyStatus: (Item) -> Unit = {},
    updateBuyQty: (Item, Double, String?) -> Unit = { item: Item, d: Double, s: String? -> },
    toggleExpansion: (Long) -> Unit
){
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
                    is ShopItemsUIState.Success -> ShopItemsList(shopItemsState.items, expandedItemsList, showSearchBar, searchProduct, changeBuyStatus, updateBuyQty, toggleExpansion)
                }
            }
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ShopItemsList(
    itemsList: List<Item>,
    expandedItemsList: List<Long> = listOf(),
    showSearchBar: Boolean,
    searchProduct: (String) -> Unit,
    changeBuyStatus: (Item) -> Unit = {},
    updateBuyQty: (Item, Double, String?) -> Unit = { item: Item, d: Double, s: String? -> },
    toggleExpansion: (Long) -> Unit = {}
) {

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

                val showQtyForm = expandedItemsList.contains(product.id)
                ItemCard(item = product, changeBuyStatus, updateBuyQty, showQtyForm) {
                    toggleExpansion(product.id)
                }
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
fun ItemCard(
    item: Item,
    ChangeBuyStatus: (Item) -> Unit = {},
    updateBuyQty: (Item, Double, String?) -> Unit = { item: Item, d: Double, s: String? -> },
    showQtyForm: Boolean,
    toggleFormVisibility: () -> Unit = {}
){
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .clickable {
                    toggleFormVisibility()
                },
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

            Text(text = if(item.buyQty != null && item.buyQty > 0) item.buyQty.StringWithoutZeroDecimal() else "",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.End,
                modifier = Modifier
                    .padding(horizontal = 10.dp)
                    .weight(0.3f))


            val iconDescription: Pair<Icon,Int> = when(item.buyStatus){
                BUYSTATUS.BUY.name -> Pair(ShopListIcons.BuyNow, R.string.buy_now)
                BUYSTATUS.BOUGHT.name -> Pair(ShopListIcons.Bought, R.string.bought)
                BUYSTATUS.WAIT_TO_BUY.name -> Pair(ShopListIcons.WaitToBuy, R.string.wait_to_buy)
                else -> Pair(ShopListIcons.WaitToBuy, R.string.wait_to_buy)
            }

            ShopListIconWithLabel(icon = iconDescription.first,
                {
                    Text(text = stringResource(id = iconDescription.second),
                        style = MaterialTheme.typography.labelSmall)
                },
                modifier = Modifier
                    .weight(0.3f)
                    .clickable {
                        ChangeBuyStatus(item)
                    }
            )
        }
        if(showQtyForm){
            Row {
                UpdateQtyForm(item, updateBuyQty = updateBuyQty, toggleFormVisibility = toggleFormVisibility)
            }
        }
    }
}


@Composable
fun UpdateQtyForm(item: Item, modifier: Modifier = Modifier, updateBuyQty: (Item, Double, String?) -> Unit = { item: Item, d: Double, s: String? -> }, toggleFormVisibility: () -> Unit = {}) {

    var qty by remember { mutableStateOf("") }
    var measure by remember { mutableStateOf("" ) }

    Column(modifier = modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp)
        ){
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.5f)
                    .padding(horizontal = 8.dp),
                value = qty,
                onValueChange = { qty = it },
                label = { Text(text = stringResource(id = R.string.quantity)) })

            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.5f)
                    .padding(horizontal = 8.dp),
                value = measure,
                onValueChange = { measure = it },
                label = { Text(text = stringResource(id = R.string.measure)) })
        }

        Button(
            onClick = {
                val qtyData = qty.toDoubleOrNull()

                if(qtyData != null){
                    updateBuyQty(item, qty.toDouble(), measure)
                    toggleFormVisibility()
                }
            },
            shape = RoundedCornerShape(20), modifier = Modifier.padding(vertical = 8.dp)
        ) {
            Text(stringResource(id = R.string.update_qty))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ItemCardPreviw(){
    val item = Item(id = 0,
        name = "test",
        date = Clock.System.now(),
        buyAgain = true,
        buyQty = 0.0,
        type = "test",
        buyStatus = BUYSTATUS.BUY.name)
    ItemCard(item = item, showQtyForm = false)
}

@Preview(showBackground = true)
@Composable
fun ShopItemsListPreview(){

    val listItems = listOf(
        Item(id = 0,
        name = "test",
        date = Clock.System.now(),
        buyAgain = true,
        buyQty = 100.0,
        type = "test",
        buyStatus = BUYSTATUS.BUY.name),
        Item(id = 1,
            name = "test1",
            date = Clock.System.now(),
            buyAgain = true,
            buyQty = 21.0,
            type = "test",
            buyStatus = BUYSTATUS.BOUGHT.name),
        Item(id = 0,
            name = "test2",
            date = Clock.System.now(),
            buyAgain = true,
            buyQty = 0.0,
            type = "test",
            buyStatus = BUYSTATUS.WAIT_TO_BUY.name)

    )
    ShopItemsList(
        itemsList = listItems,
        expandedItemsList = listOf(),
        showSearchBar = false,
        searchProduct = {},
        updateBuyQty = { item: Item, d: Double, s: String? -> },
        toggleExpansion = {}
    )

}

@Preview(showBackground = true)
@Composable
fun ShopItemsListWithSearchBarPreview(){

    val listItems = listOf(
        Item(id = 0,
            name = "test",
            date = Clock.System.now(),
            buyAgain = true,
            buyQty = 0.0,
            type = "test",
            buyStatus = BUYSTATUS.BUY.name),
        Item(id = 1,
            name = "test1",
            date = Clock.System.now(),
            buyAgain = true,
            buyQty = 0.0,
            type = "test",
            buyStatus = BUYSTATUS.BUY.name),
        Item(id = 0,
            name = "test2",
            date = Clock.System.now(),
            buyAgain = true,
            buyQty = 0.0,
            type = "test",
            buyStatus = BUYSTATUS.BUY.name)

    )
    ShopItemsList(
        itemsList = listItems,
        expandedItemsList = listOf(),
        showSearchBar = true,
        searchProduct = {},
        updateBuyQty = { item: Item, d: Double, s: String? -> },
        toggleExpansion = {}
    )

}

@Preview(showBackground = true)
@Composable
fun UpdateQtyFormPreview(){
    val item = Item(id = 0,
        name = "test2",
        date = Clock.System.now(),
        buyAgain = true,
        buyQty = 0.0,
        type = "test",
        buyStatus = BUYSTATUS.BUY.name)
    UpdateQtyForm(item)
}