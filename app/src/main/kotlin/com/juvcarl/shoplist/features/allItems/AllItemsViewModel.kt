package com.juvcarl.shoplist

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.juvcarl.shoplist.data.model.Item
import com.juvcarl.shoplist.repository.ItemRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AllItemsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val itemsRepository: ItemRepository
) : ViewModel() {

    val itemUIState : StateFlow<ItemsUIState> = flow{
        itemsRepository.getItemsStream()
            .catch {
                emit(ItemsUIState.Error)
            }
            .collect{
                emit(ItemsUIState.Success(it))
            }
    }.stateIn(scope = viewModelScope, started = SharingStarted.WhileSubscribed(5000), initialValue = ItemsUIState.Loading)

    fun deleteItem(item: Item){
        viewModelScope.launch {
            itemsRepository.deleteItem(item.id)
        }
    }

    fun toggleItem(item: Item){
        viewModelScope.launch {
            itemsRepository.updateitem(item)
        }
    }

    fun addNewItem(item: Item){
        viewModelScope.launch {
            itemsRepository.insertItem(item)
        }
    }

}

sealed interface ItemsUIState{
    data class Success(val items: List<Item>) : ItemsUIState
    object Loading : ItemsUIState
    object Error : ItemsUIState
}