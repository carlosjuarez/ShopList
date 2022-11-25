package com.juvcarl.shoplist.features.settings

import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import com.juvcarl.shoplist.R
import com.juvcarl.shoplist.ui.component.RequestNearbyPermissions
import com.juvcarl.shoplist.ui.component.ShopListTopAppBar
import com.juvcarl.shoplist.ui.theme.ShopListTheme

@Composable
fun SettingsRoute(
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = hiltViewModel()
){
    val connectNearbyState by viewModel.connectWithNearbyUsers.collectAsState()
    val importStatus by viewModel.importStatus.collectAsState()
    val exportString by viewModel.exportString.collectAsState()

    SettingsScreen(
        connectNearbyState,
        importStatus,
        viewModel::importItems,
        viewModel::toggleConnectNearby,
        viewModel::exportList,
        viewModel::clearAll
    )

    if(!exportString.isEmpty()){
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, exportString)
            type = "text/plain"
        }
        val context = LocalContext.current
        val shareIntent = Intent.createChooser(sendIntent, stringResource(id = R.string.share_list))
        context.startActivity(shareIntent)

        viewModel.listShared()
    }


}

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    connectNearby: Pair<Boolean, String>,
    importStatus: ImportStatus = ImportStatus.Empty,
    importItems: (String) -> Unit,
    connectNearbyUsers: (Boolean) -> Unit,
    exportList: () -> Unit,
    clearAll: () -> Unit
){

    var openImportDialog by remember {
        mutableStateOf(false)
    }

    ImportDialog(openImportDialog, importItems){
        openImportDialog = false
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
                    importStatus,{
                        openImportDialog = true
                    },
                    exportList,
                    clearAll
                )
            }
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ImportDialog(showDialog: Boolean = false, importItems: (String) -> Unit, onDismissDialog: () -> Unit){
    if(showDialog){
        AlertDialog(
            onDismissRequest = onDismissDialog,
            properties = DialogProperties(usePlatformDefaultWidth = false),
            title = {
                Text(text = stringResource(id = R.string.import_items), textAlign = TextAlign.Center, fontSize = MaterialTheme.typography.titleLarge.fontSize, modifier = Modifier.fillMaxWidth())
            },
            text = {
                Column(modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)) {

                    var input by remember {
                        mutableStateOf("")
                    }

                    OutlinedTextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = input,
                        onValueChange = { input = it },
                        singleLine = false,
                        maxLines = 3,
                        label = { Text(text = stringResource(id = R.string.items_to_insert)) })

                    Spacer(modifier = Modifier.padding(8.dp))
                    Button(
                        onClick = { importItems(input) },
                        modifier = Modifier.align(alignment = Alignment.CenterHorizontally), shape = RoundedCornerShape(20)
                    ) {
                        Text(stringResource(id = R.string.add))
                    }
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
    importStatus: ImportStatus = ImportStatus.Empty,
    openImportDialog: () -> Unit,
    exportList: () -> Unit,
    removeAll: () -> Unit
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
            ImportSection(importStatus){
                openImportDialog()
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            ExportListSection(exportList)
        }
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            RemoveAllSection(removeAll = removeAll)
        }
    }
}

@Composable
fun RemoveAllSection(removeAll: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(stringResource(id = R.string.remove_all), style = MaterialTheme.typography.titleLarge)
        Text(stringResource(id = R.string.clean_list_of_items), style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(8.dp))
        Button(
            onClick = {
                removeAll()
            },
            modifier = Modifier.align(alignment = Alignment.CenterHorizontally),
            shape = RoundedCornerShape(20)
        ) {
            Text(stringResource(id = R.string.remove_all))
        }
    }
}

@Composable
fun ExportListSection(exportList: () -> Unit){
    Column {
        Text(stringResource(id = R.string.export_list), style = MaterialTheme.typography.titleLarge)
        Text(stringResource(id = R.string.export_list_instruction), style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(8.dp))
        Button(
            onClick = {
                exportList()
            },
            modifier = Modifier.align(alignment = Alignment.CenterHorizontally),
            shape = RoundedCornerShape(20)
        ) {
            Text(stringResource(id = R.string.export_list))
        }
    }
}

@Composable
fun ImportSection(
    importStatus: ImportStatus = ImportStatus.Empty,
    openImportDialog: () -> Unit = {}
) {

    Column {
        Text(stringResource(id = R.string.import_items), style = MaterialTheme.typography.titleLarge)
        Text(stringResource(id = R.string.import_items_instructions), style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(8.dp))
        if(importStatus == ImportStatus.Loading){
            CircularProgressIndicator(
                modifier = Modifier.align(alignment = Alignment.CenterHorizontally),
            )
        }else{
            Button(
                onClick = {
                    openImportDialog()
                },
                enabled = if(importStatus == ImportStatus.Loading) false else true,
                modifier = Modifier.align(alignment = Alignment.CenterHorizontally),
                shape = RoundedCornerShape(20)
            ) {
                Text(stringResource(id = R.string.import_items))
            }
            if(importStatus == ImportStatus.Success){
                Text(stringResource(id = R.string.add_items_success), style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
            }
            if(importStatus == ImportStatus.Error){
                Text(stringResource(id = R.string.error_adding_items), style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.error, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
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
fun ImportItemsSectionEmptyPreview(){
    ShopListTheme {
        Column {
            ImportSection(importStatus = ImportStatus.Empty)
            ImportSection(importStatus = ImportStatus.Error)
            ImportSection(importStatus = ImportStatus.Success)
            ImportSection(importStatus = ImportStatus.Loading)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ExportListSectionEmptyPreview(){
    ShopListTheme {
        Column {
            ExportListSection({})
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RemoveALlSectionPreview(){
    ShopListTheme {
        Column {
            RemoveAllSection({})
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