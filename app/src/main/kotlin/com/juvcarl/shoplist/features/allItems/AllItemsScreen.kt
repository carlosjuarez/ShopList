package com.juvcarl.shoplist.features.allItems

import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.juvcarl.shoplist.AllItemsViewModel
import com.juvcarl.shoplist.ItemsUIState
import com.juvcarl.shoplist.data.model.Item
import com.juvcarl.shoplist.ui.theme.ShopListTheme
import kotlinx.datetime.Clock
import com.juvcarl.shoplist.R

@OptIn(ExperimentalLifecycleComposeApi::class)
@Composable
fun AllItemsRoute(
    modifier: Modifier = Modifier,
    viewModel: AllItemsViewModel = hiltViewModel()
){
    val itemsState: ItemsUIState by viewModel.itemUIState.collectAsStateWithLifecycle()

    AllItemsScreen(allItemsState = itemsState, addItem = viewModel::addNewItem, deleteItem = viewModel::deleteItem)
}

@Composable
fun AllItemsAppBar(){

}


@Composable
fun AllItemsScreen(allItemsState: ItemsUIState, addItem: (Item) -> Unit, deleteItem: (Item) -> Unit){
    ShopListTheme {
        Scaffold (
            topBar = {

            })
        {
            when(allItemsState){
                com.juvcarl.shoplist.ItemsUIState.Error -> ErrorDisplay()
                com.juvcarl.shoplist.ItemsUIState.Loading -> LoadingDisplay()
                is com.juvcarl.shoplist.ItemsUIState.Success -> AllItemsDisplay(allItemsState.items, addItem, deleteItem)
                else -> {}
            }
        }
    }

}

@Composable
fun ErrorDisplay(){
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = stringResource(id = R.string.error_occurred), textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.error)
    }

}

@Composable
fun LoadingDisplay(){
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator()
    }
}

@Composable
fun AllItemsDisplay(itemsList: List<Item>, addItem: (Item) -> Unit, deleteItem: (Item) -> Unit){
    LazyColumn(modifier = Modifier
        .fillMaxSize()
        .padding(12.dp)){
        item {
            Spacer(Modifier.windowInsetsTopHeight(WindowInsets.safeDrawing))
        }
        if(itemsList.isEmpty()){
            item{
                AddNewItemForm(addItem)
            }
        }else{
            items(itemsList){ product ->
                ItemCard(item = product, deleteItem)
            }
        }
        item {
            Spacer(Modifier.windowInsetsTopHeight(WindowInsets.safeDrawing))
        }
    }
}

@Composable
fun ItemCard(item: Item, deleteItem: (Item) -> Unit){
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
            Text(text = item.name, style = MaterialTheme.typography.bodyLarge)
            Spacer(modifier = Modifier.padding(4.dp))
            Text(text = item.type, style = MaterialTheme.typography.bodyMedium)
        }
        Icon(
            if (item.buyAgain) Icons.Default.ShoppingCart else Icons.Default.Lock,
            contentDescription = stringResource(id = if(item.buyAgain) R.string.cd_buyAgain else R.string.cd_wait_to_buy),
            modifier = Modifier.weight(0.2f)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddNewItemDialog(addItem: (Item) -> Unit){
    Card(
        modifier = Modifier.padding(16.dp),
        colors = CardDefaults.cardColors( containerColor = MaterialTheme.colorScheme.surface)
    ) {
        AddNewItemForm(addItem)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddNewItemForm(addItem: (Item) -> Unit) {
    var name by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false ) }
    var type by remember { mutableStateOf("") }
    var buyNow by remember { mutableStateOf( false ) }

    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp)) {
        Text(stringResource(id = R.string.add_new_item), modifier = Modifier.align(alignment = Alignment.CenterHorizontally))
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
            Text(stringResource(id = R.string.buy_now))
        }
        Spacer(modifier = Modifier.padding(4.dp))
        OutlinedButton(onClick = {
            val newItem = Item(
                name = name,
                date = Clock.System.now(),
                buyAgain = buyNow,
                type = type
            )
            addItem(newItem)
        },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text(stringResource(id = R.string.add))
        }
    }
}

@Preview
@Composable
fun AddNewItemPreview(){
    AddNewItemDialog(addItem = {})
}

@Preview(showBackground = true)
@Composable
fun ErrorDisplayPreview() {
    ShopListTheme {
        ErrorDisplay()
    }
}

@Preview(showBackground = true)
@Composable
fun AllProductsListPreview(){
    ShopListTheme {
        var item = Item(0L,"test product", Clock.System.now(),true,"special")
        ItemCard(item = item, {})
    }
}

@Preview(showBackground = true)
@Composable
fun LoadDisplayPreview() {
    ShopListTheme {
        LoadingDisplay()
    }
}