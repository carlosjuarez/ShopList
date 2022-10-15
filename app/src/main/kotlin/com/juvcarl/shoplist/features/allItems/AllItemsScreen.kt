package com.juvcarl.shoplist.features.allItems

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
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
    modifier: Modifier = Modifier,
    viewModel: AllItemsViewModel = hiltViewModel()
){
    val itemsState: AllItemsUIState by viewModel.itemUIState.collectAsStateWithLifecycle()

    AllItemsScreen(allItemsState = itemsState, addItem = viewModel::addNewItem, deleteItem = viewModel::deleteItem, toggleBuyStatus = viewModel::toggleItem, searchProduct = viewModel::searchProduct)
}



@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AllItemsScreen(
    allItemsState: AllItemsUIState,
    addItem: (Item) -> Unit,
    deleteItem: (Item) -> Unit,
    toggleBuyStatus: (Item) -> Unit,
    searchProduct: (String) -> Unit
){

    var showSearchBar by remember { mutableStateOf(false) }
    var openAddItemDialog by remember { mutableStateOf(false) }

    AddNewItemAlertDialog(showDialog = openAddItemDialog, addItem) {
        openAddItemDialog = false
    }

    ShopListTheme {
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
            containerColor = MaterialTheme.colorScheme.background,
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
                    is AllItemsUIState.Success -> AllItemsList(allItemsState.items, addItem, deleteItem, toggleBuyStatus, showSearchBar, searchProduct)
                    AllItemsUIState.EmptyList -> AddNewItem(addItem)
                    else -> {}
                }
            }
        }
    }
}

@Composable
fun AddNewItem(addItem: (Item) -> Unit){
    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)
    ) {
        Spacer(Modifier.windowInsetsTopHeight(WindowInsets.safeDrawing))
        Text(
            stringResource(id = R.string.add_new_item),
            modifier = Modifier.align(alignment = Alignment.CenterHorizontally)
        )
        AddNewItemForm(
            addItem,
            modifier = Modifier.align(alignment = Alignment.CenterHorizontally)
        )
    }
}

@Composable
fun AddNewItemAlertDialog(showDialog: Boolean, addItem: (Item) -> Unit, onDismissDialog : () -> Unit){
    if(showDialog){
        AlertDialog(
            onDismissRequest = onDismissDialog,
            title = {
                Text(text = stringResource(id = R.string.add_new_item), textAlign = TextAlign.Center, fontSize = MaterialTheme.typography.titleLarge.fontSize, modifier = Modifier.fillMaxWidth())
            },
            text = {
                Column(modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)) {
                    AddNewItemForm(addItem = { item ->
                        addItem.invoke(item)
                        onDismissDialog.invoke()
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
    addItem: (Item) -> Unit,
    deleteItem: (Item) -> Unit,
    toggleBuyStatus: (Item) -> Unit,
    showSearchBar: Boolean = false,
    searchProduct: (String) -> Unit
){
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
                    Text(stringResource(id = R.string.no_item_found), modifier = Modifier.align(alignment = Alignment.CenterHorizontally))
                }
            }
        }else{
            items(itemsList){ product ->
                ItemCard(item = product, deleteItem, toggleBuyStatus)
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
fun ItemCard(item: Item, deleteItem: (Item) -> Unit, toggleBuyStatus: (Item) -> Unit){
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .pointerInput(Unit) {
                detectTapGestures(
                    onLongPress = { deleteItem(item) }
                )
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
            ProductTag(tag = item.type)
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddNewItemForm(addItem: (Item) -> Unit, modifier: Modifier = Modifier) {
    var name by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false ) }
    var type by remember { mutableStateOf("") }
    var buyNow by remember { mutableStateOf( false ) }

    Spacer(modifier = Modifier.padding(4.dp))
    OutlinedTextField(
        modifier = Modifier.fillMaxWidth(),
        value = name,
        onValueChange = { name = it },
        label = { Text(text = stringResource(id = R.string.name)) })
    Spacer(modifier = Modifier.padding(4.dp))
    Box(modifier = Modifier.fillMaxWidth()){
        Text(if(type.isEmpty()) stringResource(id = R.string.product_type) else type, modifier = Modifier
            .align(alignment = Alignment.CenterStart)
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
            val newItem = Item(
                name = name,
                date = Clock.System.now(),
                buyAgain = buyNow,
                type = type
            )
            addItem(newItem) },
        modifier = modifier, shape = RoundedCornerShape(20)
    ) {
        Text(stringResource(id = R.string.add))
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
        addItem = {},
        deleteItem = {},
        toggleBuyStatus = {}
    ) {}
}

@Preview(showBackground = true)
@Composable
fun AddNewItemAlertDialogPreview(){
    AddNewItemAlertDialog(showDialog = true, addItem = {}, onDismissDialog = {})
}

@Preview
@Composable
fun AllItemsEmptyItemsScreenPreview(){
    val success = AllItemsUIState.Success(listOf())
    AllItemsScreen(
        allItemsState = success,
        addItem = {},
        deleteItem = {},
        toggleBuyStatus = {}
    ) {}
}

@Preview
@Composable
fun AddNewItemPreview(){
    AddNewItemAlertDialog(true,addItem = {}, onDismissDialog = {})
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
    AddNewItem(addItem = {})
}
