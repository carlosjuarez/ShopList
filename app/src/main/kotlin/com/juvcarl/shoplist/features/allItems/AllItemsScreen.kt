package com.juvcarl.shoplist.features.allItems

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.filled.*
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
import com.juvcarl.shoplist.AllItemsViewModel
import com.juvcarl.shoplist.AllItemsUIState
import com.juvcarl.shoplist.data.model.Item
import com.juvcarl.shoplist.ui.theme.ShopListTheme
import kotlinx.datetime.Clock
import com.juvcarl.shoplist.R
import com.juvcarl.shoplist.ui.ShopListIconWithLabel
import com.juvcarl.shoplist.ui.ShopListIcons
import com.juvcarl.shoplist.ui.component.*
import kotlinx.coroutines.delay

@OptIn(ExperimentalLifecycleComposeApi::class)
@Composable
fun AllItemsRoute(
    navigateToDetail: (Long) -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: AllItemsViewModel = hiltViewModel()
){
    val itemsState: AllItemsUIState by viewModel.itemUIState.collectAsStateWithLifecycle()

    AllItemsScreen(allItemsState = itemsState, addItem = viewModel::addNewItem, navigateToDetail = navigateToDetail, toggleBuyStatus = viewModel::toggleItem, searchProduct = viewModel::searchProduct)
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AllItemsScreen(
    allItemsState: AllItemsUIState,
    addItem: (String,Boolean,String) -> Unit,
    navigateToDetail: (Long) -> Unit,
    toggleBuyStatus: (Item) -> Unit,
    searchProduct: (String) -> Unit
){

    var showSearchBar by remember { mutableStateOf(false) }
    var openAddItemDialog by remember { mutableStateOf(false) }

    AddNewItemAlertDialog(showDialog = openAddItemDialog, addItem) {
        openAddItemDialog = false
    }


        Scaffold (
            topBar = {
                ShopListTopAppBar(
                    titleRes = R.string.all_items,
                    navigationIcon = if(showSearchBar) ShopListIcons.SearchSelected else ShopListIcons.SearchUnselected,
                    onNavigationClick = {
                        showSearchBar = !showSearchBar
                        if(!showSearchBar) searchProduct("")
                    },
                    actionIcon = ShopListIcons.Add,
                    onActionClick = { openAddItemDialog = true },
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
        ) {
            innerPadding ->
            Surface(
                modifier = Modifier
                    .padding(innerPadding)
                    .consumedWindowInsets(innerPadding)
                    .fillMaxSize()
            ) {
                when(allItemsState){
                    AllItemsUIState.Error -> ErrorScreen()
                    AllItemsUIState.Loading -> LoadingScreen()
                    is AllItemsUIState.Success -> AllItemsList(allItemsState.items, navigateToDetail, toggleBuyStatus, showSearchBar, searchProduct)
                    AllItemsUIState.EmptyList -> AddNewItem(addItem)
                    else -> {}
                }
            }
        }
}

@Composable
fun AddNewItem(addItem: (String,Boolean,String) -> Unit){
    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)
    ) {
        Spacer(Modifier.windowInsetsTopHeight(WindowInsets.safeDrawing))
        Text(
            stringResource(id = R.string.add_new_item),
            modifier = Modifier.align(alignment = Alignment.CenterHorizontally)
        )
        AddItemForm(
            addItem,
            modifier = Modifier.align(alignment = Alignment.CenterHorizontally)
        )
    }
}

@Composable
fun AddNewItemAlertDialog(showDialog: Boolean, addItem: (String,Boolean,String) -> Unit, onDismissDialog : () -> Unit){
    if(showDialog){
        AlertDialog(
            tonalElevation = 4.dp,
            onDismissRequest = onDismissDialog,
            title = {
                Text(text = stringResource(id = R.string.add_new_item), textAlign = TextAlign.Center, fontSize = MaterialTheme.typography.titleLarge.fontSize, modifier = Modifier.fillMaxWidth())
            },
            text = {
                Column(modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)) {
                    AddItemForm(addFunction = { input: String, buyAgain: Boolean, type: String ->
                        addItem(input,buyAgain,type)
                        onDismissDialog()
                    }, modifier = Modifier.align(Alignment.CenterHorizontally))
                }

            },
            confirmButton = {}
        )
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun AllItemsList(
    itemsList: List<Item>,
    navigateToDetail: (Long) -> Unit,
    toggleBuyStatus: (Item) -> Unit,
    showSearchBar: Boolean = false,
    searchProduct: (String) -> Unit
){
    val focusRequester = remember { FocusRequester() }
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
                    Text(stringResource(id = R.string.no_item_found), modifier = Modifier.align(alignment = Alignment.CenterHorizontally))
                }
            }
        }else{
            items(itemsList){ product ->
                ItemCard(item = product, navigateToDetail = navigateToDetail, toggleBuyStatus)
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
fun ItemCard(item: Item, navigateToDetail: (Long) -> Unit, toggleBuyStatus: (Item) -> Unit){
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable {
                navigateToDetail(item.id)
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

        ShopListIconWithLabel(icon = if (item.buyAgain) ShopListIcons.BuyAgain else ShopListIcons.WaitToBuy,
            {
                Text(text = stringResource(id = if (item.buyAgain) R.string.add_to_list else R.string.remove_from_list),
                    style = MaterialTheme.typography.labelSmall)
            },
            modifier = Modifier
                .weight(0.2f)
                .clickable {
                    toggleBuyStatus(item)
                }
        )
    }
}

@Preview
@Composable
fun AllItemsExistingItemsScreenPreview(){
    val success = AllItemsUIState.Success(listOf(
        Item(0L,"Test 1",Clock.System.now(),true,"test"),
        Item(1L,"Test 2",Clock.System.now(),false,"test")
    ))
    AllItemsScreen(
        allItemsState = success,
        addItem = { _: String, _: Boolean, _: String -> },
        navigateToDetail = {},
        toggleBuyStatus = {}
    ) {}
}

@Preview(showBackground = true)
@Composable
fun AddNewItemAlertDialogPreview(){
    AddNewItemAlertDialog(showDialog = true, addItem = { _: String, _: Boolean, _: String -> }, onDismissDialog = {})
}

@Preview
@Composable
fun AllItemsEmptyItemsScreenPreview(){
    val success = AllItemsUIState.Success(listOf())
    AllItemsScreen(
        allItemsState = success,
        addItem = { _: String, _: Boolean, _: String -> },
        navigateToDetail = {},
        toggleBuyStatus = {}
    ) {}
}

@Preview
@Composable
fun AddNewItemPreview(){
    AddNewItemAlertDialog(true,addItem = { _: String, _: Boolean, _: String -> }, onDismissDialog = {})
}

@Preview(showBackground = true)
@Composable
fun AllProductsListPreview(){
    ShopListTheme {
        var item = Item(0L,"test product", Clock.System.now(),true,"special")
        ItemCard(item = item, {},{})
    }
}

@Preview(showBackground = true)
@Composable
fun AddItemDisplayPreview(){
    AddNewItem(addItem = { _: String, _: Boolean, _: String -> })
}
