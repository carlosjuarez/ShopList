package com.juvcarl.shoplist.ui.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.juvcarl.shoplist.R
import com.juvcarl.shoplist.ui.theme.ShopListTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddItemForm(
    addFunction: (String, Boolean, String) -> Unit,
    modifier: Modifier = Modifier,
    isSingleLine: Boolean = true
) {
    var input by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false ) }
    var type by remember { mutableStateOf("") }
    var buyNow by remember { mutableStateOf( false ) }

    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Spacer(modifier = Modifier.padding(4.dp))
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = input,
            onValueChange = { input = it },
            singleLine = isSingleLine,
            maxLines = 3,
            label = { Text(text = stringResource(id = R.string.items_to_insert)) })
        Spacer(modifier = Modifier.padding(4.dp))
        Box(modifier = Modifier.fillMaxWidth()){
            Text(
                type.ifEmpty { stringResource(id = R.string.product_type) }, modifier = Modifier
                .fillMaxWidth()
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
                addFunction(input,buyNow,type) },
            modifier = modifier, shape = RoundedCornerShape(20)
        ) {
            Text(stringResource(id = R.string.add))
        }
    }

}

@Preview(showBackground = true)
@Composable
fun AddNewInsertItem(){
    ShopListTheme {
        AddItemForm({ _: String, _: Boolean, _: String -> })
    }
}