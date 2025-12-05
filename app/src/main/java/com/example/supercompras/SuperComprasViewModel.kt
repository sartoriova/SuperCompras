package com.example.supercompras

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import kotlin.collections.map

class SuperComprasViewModel : ViewModel() {
    private val _items = MutableStateFlow<List<Item>>(emptyList())
    val items: StateFlow<List<Item>> = _items

    fun saveItem(newItem: Item) {
        viewModelScope.launch {
            _items.update { it + newItem }
        }
    }

    fun removeItem(removedItem: Item) {
        viewModelScope.launch {
            _items.update { it - removedItem }
        }
    }

    fun editItem(itemToEdit: Item?, editedItem: Item) {
        viewModelScope.launch {
            _items.update { itemsList ->
                itemsList.map { currentItem ->
                    if (currentItem == itemToEdit) {
                        currentItem.copy(text = editedItem.text)
                    } else {
                        currentItem
                    }
                }
            }
        }
    }

    fun changeItemStatus(selectedItem: Item) {
        viewModelScope.launch {
            _items.update { items ->
                items.map { mapItem ->
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
        }
    }
}