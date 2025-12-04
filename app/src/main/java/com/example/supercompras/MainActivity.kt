package com.example.supercompras

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.supercompras.ui.theme.SuperComprasTheme
import com.example.supercompras.ui.theme.Typography
import com.example.supercompras.ui.theme.coral
import com.example.supercompras.ui.theme.navyBlue
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SuperComprasTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    ShoppingList(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

data class Item(
    val text: String,
    val isBought: Boolean = false,
    val additionDate: LocalDateTime? = null,
    val purchaseDate: LocalDateTime? = null
)

@Composable
fun ShoppingList(modifier: Modifier = Modifier) {
    var items by rememberSaveable { mutableStateOf(listOf<Item>()) }
    var editedItem: Item? by rememberSaveable { mutableStateOf(null) }

    LazyColumn(
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        item {
            TopImage()

            AddItem(
                editedItem = editedItem,
                saveItem = { newItem ->
                    items = items + newItem
                },
                editItem = { newItem ->
                    items = items.map { mapItem ->
                        if (mapItem == editedItem) {
                            mapItem.copy(text = newItem.text)
                        } else {
                            mapItem
                        }
                    }
                    editedItem = null
                }
            )

            Spacer(modifier = Modifier.height(48.dp))

            Title("Lista de Compras")
        }

        itemsList(
            items = items.filter { !it.isBought },
            changeItemStatus = { selectedItem ->
                items = changeItemStatus(selectedItem, items)
            },
            removeItem = { removedItem ->
                items = removeItem(removedItem, items)
            },
            editItem = {
                editedItem = it
            },
        )

        item {
            Title("Comprados")
        }

        itemsList(
            items = items.filter { it.isBought },
            changeItemStatus = { selectedItem ->
                items = changeItemStatus(selectedItem, items)
            },
            removeItem = { removedItem ->
                items = removeItem(removedItem, items)
            },
            editItem = {
                editedItem = it
            },
        )
    }
}

@Composable
fun TopImage(modifier: Modifier = Modifier) {
    Image(
        painter = painterResource(R.drawable.top_img),
        contentDescription = null,
        modifier = modifier.size(160.dp)
    )
}

@Composable
fun AddItem(
    modifier: Modifier = Modifier,
    editedItem: Item? = null,
    saveItem: (item: Item) -> Unit = {},
    editItem: (item: Item) -> Unit = {}
) {
    var text by rememberSaveable(editedItem?.text) { mutableStateOf(editedItem?.text ?: "") }

    OutlinedTextField(
        value = text,
        onValueChange = { text = it },
        placeholder = {
            Text(
                "Digite o item que deseja adicionar",
                color = Color.Gray,
                style = Typography.bodyMedium
            )
        },
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 24.dp, bottom = 8.dp, start = 10.dp, end = 10.dp),
        singleLine = true,
        shape = RoundedCornerShape(24.dp),
    )
    Button(
        shape = RoundedCornerShape(24.dp),
        onClick = {
            if (editedItem != null) {
                editItem(Item(text = text))
            } else {
                saveItem(Item(text = text, additionDate = LocalDateTime.now()))
            }
            text = ""
        },
        modifier = modifier,
        colors = ButtonColors(
            containerColor = coral,
            contentColor = Color.White,
            disabledContainerColor = Color.Gray,
            disabledContentColor = Color.LightGray
        )
    ) {
        Text(
            text = "Salvar item",
            color = Color.White,
            style = Typography.bodyLarge,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
        )
    }
}

@Composable
fun Title(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        style = Typography.headlineLarge,
        textAlign = TextAlign.Start,
        modifier = modifier
    )
}

fun LazyListScope.itemsList(
    items: List<Item>,
    changeItemStatus: (item: Item) -> Unit = {},
    editItem: (item: Item) -> Unit = {},
    removeItem: (item: Item) -> Unit = {},
) {
    items(items.size) { index ->
        ListItem(
            item = items[index],
            changeItemStatus = changeItemStatus,
            removeItem = removeItem,
            editItem = editItem
        )
    }
}

fun changeItemStatus(selectedItem: Item, items: List<Item>): List<Item> {
    return items.map { mapItem ->
        if (mapItem == selectedItem) {
            if (selectedItem.isBought) {
                mapItem.copy(
                    isBought = false,
                    purchaseDate = null
                )
            } else {
                mapItem.copy(
                    isBought = true,
                    purchaseDate = LocalDateTime.now()
                )
            }
        } else {
            mapItem
        }
    }
}

fun removeItem(selectedItem: Item, items: List<Item>): List<Item> {
    return items.filter { it != selectedItem }
}

@Composable
fun ListItem(
    item: Item,
    modifier: Modifier = Modifier,
    changeItemStatus: (item: Item) -> Unit = {},
    editItem: (item: Item) -> Unit = {},
    removeItem: (item: Item) -> Unit = {},
) {
    var dateItem by rememberSaveable(item.purchaseDate) {
        mutableStateOf(
            item.purchaseDate ?: item.additionDate
        )
    }

    Column(verticalArrangement = Arrangement.Top, modifier = modifier) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            Checkbox(
                item.isBought,
                onCheckedChange = {
                    changeItemStatus(item)
                },
                modifier = Modifier
                    .padding(end = 8.dp)
                    .requiredSize(24.dp)
            )
            Text(item.text, style = Typography.bodyMedium, modifier = Modifier.weight(1f))
            IconButton(
                onClick = {
                    removeItem(item)
                },
                modifier = Modifier.padding(end = 8.dp)
            ) {
                Icon(
                    Icons.Default.Delete, modifier = Modifier
                        .size(16.dp)
                )
            }
            IconButton(onClick = { editItem(item) }) {
                Icon(Icons.Default.Edit, modifier = Modifier.size(16.dp))
            }
        }
        Text(
            getStringDateTime(dateItem),
            style = Typography.labelSmall,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}

@Composable
fun Icon(icon: ImageVector, modifier: Modifier = Modifier) {
    Icon(icon, contentDescription = "Edit", modifier = modifier, tint = navyBlue)
}

fun getStringDateTime(dateTime: LocalDateTime?): String {
    val formattedDate = DateTimeFormatter.ofPattern("dd/MM/yyyy").format(dateTime?.toLocalDate())
    val formattedHour = DateTimeFormatter.ofPattern("HH:mm").format(dateTime?.toLocalTime())
    val dayOfWeek = dateTime?.dayOfWeek?.getDisplayName(
        TextStyle.FULL,
        Locale.of("pt", "BR")
    )?.replaceFirstChar { it.uppercase() }

    return "$dayOfWeek ($formattedDate) às $formattedHour"
}

@Preview
@Composable
private fun TitlePreview() {
    SuperComprasTheme {
        Title("Lista de Compras")
    }
}