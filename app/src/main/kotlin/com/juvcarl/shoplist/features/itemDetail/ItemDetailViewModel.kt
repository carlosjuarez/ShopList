package com.juvcarl.shoplist.features.itemDetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.juvcarl.shoplist.data.model.Item
import com.juvcarl.shoplist.features.itemDetail.navigation.ItemDetailDestination
import com.juvcarl.shoplist.repository.ItemRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ItemDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val itemsRepository: ItemRepository
): ViewModel() {

    private val itemId: Long = checkNotNull(savedStateHandle[ItemDetailDestination.itemIdArg])

    val itemUIState : StateFlow<ItemUIState> = itemsRepository.getItemByIdStream(itemId).map{ result ->
        ItemUIState.Success(item = result)
    }.catch {
        ItemUIState.Error
    }.stateIn(scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ItemUIState.Loading)

    fun updateItem(item: Item){
        viewModelScope.launch {
            itemsRepository.updateitem(item)
        }
    }

    fun deleteItem(id: Long){
        viewModelScope.launch {
            itemsRepository.deleteItem(id)
        }
    }


}

sealed interface ItemUIState{
    data class Success(val item: Item) : ItemUIState
    object Loading : ItemUIState
    object Error : ItemUIState
}