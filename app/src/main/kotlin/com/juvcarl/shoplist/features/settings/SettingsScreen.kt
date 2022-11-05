package com.juvcarl.shoplist.features.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import com.juvcarl.shoplist.R
import com.juvcarl.shoplist.ui.component.AddItemForm
import com.juvcarl.shoplist.ui.component.RequestNearbyPermissions
import com.juvcarl.shoplist.ui.component.ShopListTopAppBar
import com.juvcarl.shoplist.ui.theme.ShopListTheme

@Composable
fun SettingsRoute(
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = hiltViewModel()
){
    val connectNearbyState by viewModel.connectWithNearbyUsers.collectAsState()
    val bulkInsertStatus by viewModel.bulkInsertStatus.collectAsState()

    SettingsScreen(
        connectNearbyState,
        bulkInsertStatus,
        viewModel::addItemsInBulk,
        viewModel::toggleConnectNearby
    )


}

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    connectNearby: Pair<Boolean,String>,
    bulkInsertStatus: BulkInsertStatus = BulkInsertStatus.Empty,
    addMultipleItemsFunction: (String, Boolean, String) -> Unit = { _: String, _: Boolean, _: String -> },
    connectNearbyUsers: (Boolean) -> Unit = { _: Boolean ->}
){

    var openBulkInsertDialog by remember {
        mutableStateOf(false)
    }

    BulkInsertDialog(openBulkInsertDialog, addMultipleItemsFunction){
        openBulkInsertDialog = false
    }

    ShopListTheme {
        Scaffold (
            topBar = {
                ShopListTopAppBar(
                    titleRes = R.string.settings,
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
                SettingsDisplay(
                    connectNearby,
                    connectNearbyUsers,
                    bulkInsertStatus
                ){
                    openBulkInsertDialog = true
                }
            }
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun BulkInsertDialog(showDialog: Boolean = false, addMultipleItemsFunction: (String, Boolean, String) -> Unit = { _: String, _: Boolean, _: String -> }, onDismissDialog: () -> Unit = {}){
    if(showDialog){
        AlertDialog(
            onDismissRequest = onDismissDialog,
            properties = DialogProperties(usePlatformDefaultWidth = false),
            title = {
                Text(text = stringResource(id = R.string.add_multiple_items), textAlign = TextAlign.Center, fontSize = MaterialTheme.typography.titleLarge.fontSize, modifier = Modifier.fillMaxWidth())
            },
            text = {
                Column(modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)) {
                    AddItemForm(addFunction = { input: String, buyAgain: Boolean, type: String ->
                        addMultipleItemsFunction(input,buyAgain,type)
                        onDismissDialog()
                    }, modifier = Modifier.align(Alignment.CenterHorizontally), isSingleLine = false)
                }

            },
            confirmButton = {},
            modifier = Modifier
                .padding(28.dp)
                .fillMaxWidth()
                .wrapContentHeight(),
        )
    }
}

@Composable
fun SettingsDisplay(
    connectNearby: Pair<Boolean, String>,
    connectNearbyUsers: (Boolean) -> Unit,
    bulkInsertStatus: BulkInsertStatus = BulkInsertStatus.Empty,
    openBulkDialog: () -> Unit
){

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            ToggleSetting(connectNearby,connectNearbyUsers)
            Divider(color = MaterialTheme.colorScheme.outline)
        }
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            BulkInsertSection(bulkInsertStatus){
                openBulkDialog()
            }
        }
    }

}

@Composable
fun BulkInsertSection(
    bulkInsertStatus: BulkInsertStatus = BulkInsertStatus.Empty,
    openBulkDialog: () -> Unit = {}
) {

    Column {
        Text(stringResource(id = R.string.add_multiple_items), style = MaterialTheme.typography.titleLarge)
        Text(stringResource(id = R.string.add_multiple_items_instructions), style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(8.dp))
        if(bulkInsertStatus == BulkInsertStatus.Loading){
            CircularProgressIndicator(
                modifier = Modifier.align(alignment = Alignment.CenterHorizontally),
            )
        }else{
            Button(
                onClick = {
                    openBulkDialog()
                },
                enabled = if(bulkInsertStatus == BulkInsertStatus.Loading) false else true,
                modifier = Modifier.align(alignment = Alignment.CenterHorizontally),
                shape = RoundedCornerShape(20)
            ) {
                Text(stringResource(id = R.string.add_multiple_items))
            }
            if(bulkInsertStatus == BulkInsertStatus.Success){
                Text(stringResource(id = R.string.add_items_success), style = MaterialTheme.typography.labelSmall, textAlign = TextAlign.Center)
            }
            if(bulkInsertStatus == BulkInsertStatus.Error){
                Text(stringResource(id = R.string.error_adding_items), style = MaterialTheme.typography.labelSmall, textAlign = TextAlign.Center)
            }
        }
    }

}

@Composable
fun ToggleSetting(connectNearby: Pair<Boolean, String>, connectNearbyUsers: (Boolean) -> Unit) {
    Column {
        Row{
            Text(
                stringResource(id = R.string.connect_with_nearby_users),
                style = MaterialTheme.typography.titleLarge
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically
        ) {
            Text(stringResource(id = R.string.connect_with_nearby_users))
            Switch(checked = connectNearby.first, onCheckedChange = connectNearbyUsers)
        }
        if(connectNearby.first){
            RequestNearbyPermissions {
                Row {
                    Text(stringResource(id = R.string.local_username_label))
                    Text(connectNearby.second)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BulkInsertSectionEmptyPreview(){
    ShopListTheme {
        Column {
            BulkInsertSection(bulkInsertStatus = BulkInsertStatus.Empty)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BulkInsertSectionErrorPreview(){
    ShopListTheme {
        Column {
            BulkInsertSection(bulkInsertStatus = BulkInsertStatus.Error)
        }
    }
}
@Preview(showBackground = true)
@Composable
fun BulkInsertSectionSuccessPreview(){
    ShopListTheme {
        Column {
            BulkInsertSection(bulkInsertStatus = BulkInsertStatus.Success)
        }
    }
}
@Preview(showBackground = true)
@Composable
fun BulkInsertSectionLoadingPreview(){
    ShopListTheme {
        Column {
            BulkInsertSection(bulkInsertStatus = BulkInsertStatus.Loading)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ToggleSettingPreview(){
    ShopListTheme {
        Column {
            ToggleSetting(Pair(true,"test"),{})
        }
    }
}