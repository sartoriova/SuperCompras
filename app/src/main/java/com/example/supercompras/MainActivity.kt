package com.example.supercompras

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
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
    val viewModel: SuperComprasViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SuperComprasTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    ShoppingList(modifier = Modifier.padding(innerPadding), viewModel = viewModel)
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
fun ShoppingList(viewModel: SuperComprasViewModel, modifier: Modifier = Modifier) {
    val items by viewModel.items.collectAsState()
    var editedItem: Item? by rememberSaveable { mutableStateOf(null) }

    LazyColumn(
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.padding(horizontal = 16.dp)
    ) {
        item {
            TopImage()

            AddItem(
                editedItem = editedItem,
                saveItem = { newItem ->
                    viewModel.saveItem(newItem)
                },
                editItem = { newItem ->
                    viewModel.editItem(editedItem, newItem)
                    editedItem = null
                }
            )

            Spacer(modifier = Modifier.height(48.dp))

            Title("Lista de Compras")

            Spacer(modifier = Modifier.height(24.dp))
        }

        if (items.none { !it.isBought }) {
            item {
                Text(
                    text = "Sua lista está vazia. Adicione itens a ela para não esquecer nada na próxima compra!",
                    style = Typography.bodyLarge,
                )
            }
        } else {
            itemsList(
                items = items.filter { !it.isBought },
                changeItemStatus = { selectedItem ->
                    viewModel.changeItemStatus(selectedItem)
                },
                removeItem = { removedItem ->
                    viewModel.removeItem(removedItem)
                },
                editItem = {
                    editedItem = it
                },
            )
        }

        item {
            Spacer(modifier = Modifier.height(40.dp))

            Title("Comprados")

            Spacer(modifier = Modifier.height(24.dp))
        }

        if (items.none { it.isBought }) {
            item {
                Text(
                    text = "Os itens comprados aparecerão aqui.",
                    style = Typography.bodyLarge,
                )
            }
        } else {
            itemsList(
                items = items.filter { it.isBought },
                changeItemStatus = { selectedItem ->
                    viewModel.changeItemStatus(selectedItem)
                },
                removeItem = { removedItem ->
                    viewModel.removeItem(removedItem)
                },
                editItem = {
                    editedItem = it
                },
            )
        }
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
                "Digite o nome do item",
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
        contentPadding = PaddingValues(16.dp, 12.dp),
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
        )
    }
}

@Composable
fun Title(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        style = Typography.headlineLarge,
        textAlign = TextAlign.Start,
        modifier = modifier.fillMaxWidth(),
    )
    DottedLine(modifier = modifier.padding(top = 8.dp))
}

@Composable
fun DottedLine(modifier: Modifier = Modifier) {
    val pathEffect = PathEffect.dashPathEffect(floatArrayOf(5f, 5f), 2.5f)
    Canvas(modifier = modifier.fillMaxWidth()) {
        drawLine(
            color = coral,
            pathEffect = pathEffect,
            start = Offset(0f, 0f),
            end = Offset(size.width, 0f),
            strokeWidth = 4f,
        )
    }
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
        Spacer(modifier = Modifier.height(16.dp))
    }
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
                modifier = Modifier
                    .padding(end = 8.dp)
                    .size(16.dp)
            ) {
                Icon(
                    Icons.Default.Delete, modifier = Modifier
                )
            }
            IconButton(onClick = { editItem(item) }, modifier = Modifier.size(16.dp)) {
                Icon(Icons.Default.Edit)
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