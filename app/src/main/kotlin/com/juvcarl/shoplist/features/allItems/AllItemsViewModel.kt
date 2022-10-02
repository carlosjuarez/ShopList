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

    val itemUIState : StateFlow<AllItemsUIState> = flow{
        itemsRepository.getItemsStream()
            .catch {
                emit(AllItemsUIState.Error)
            }
            .collect{
                emit(AllItemsUIState.Success(it))
            }
    }.stateIn(scope = viewModelScope, started = SharingStarted.WhileSubscribed(5000), initialValue = AllItemsUIState.Loading)

    fun deleteItem(item: Item){
        viewModelScope.launch {
            itemsRepository.deleteItem(item.id)
        }
    }

    fun toggleItem(item: Item){
        viewModelScope.launch {
            val updatedItem = item.copy(buyAgain = !item.buyAgain)
            itemsRepository.updateitem(updatedItem)
        }
    }

    fun addNewItem(item: Item){
        viewModelScope.launch {
            itemsRepository.insertItem(item)
        }
    }

}

sealed interface AllItemsUIState{
    data class Success(val items: List<Item>) : AllItemsUIState
    object Loading : AllItemsUIState
    object Error : AllItemsUIState
}