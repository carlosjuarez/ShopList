package com.juvcarl.shoplist.ui.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.juvcarl.shoplist.ui.theme.ShopListTheme

@Composable
fun ProductName(
    productName: String,
    modifier: Modifier = Modifier
){
    Text(text = productName, style = MaterialTheme.typography.titleLarge)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductTag(
    tag: String,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.secondary
){
    Card(
        shape = RoundedCornerShape(40),
        border = BorderStroke(1.dp, color = color),
    ) {
        Text(text = tag, style = MaterialTheme.typography.labelMedium, color = color, modifier = Modifier.padding(4.dp))
    }
}

@Preview(showBackground = true)
@Composable
fun listOfProductsWithTagsPreview(){

    var products = listOf("Product1","Product2","Product3")
    var tags = listOf("Tag1","Tag2","Tag3")

    ShopListTheme {
        Column(
            Modifier.fillMaxSize()
        ) {
            for(product in products){
                ProductName(productName = product)
                Row {
                    for(tag in tags){
                        ProductTag(tag = tag)
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ProductNamePreview(){
    ProductName(productName = "Product Name")
}

@Preview(showBackground = true)
@Composable
fun ProductTagPreview(){
    ProductTag(tag = "basic", color = Color.Red)
}

