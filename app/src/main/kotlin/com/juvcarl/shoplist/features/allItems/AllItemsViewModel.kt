package com.juvcarl.shoplist

import androidx.compose.ui.text.toLowerCase
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.juvcarl.shoplist.data.model.Item
import com.juvcarl.shoplist.repository.ItemRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import java.util.*
import javax.inject.Inject

@HiltViewModel
class AllItemsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val itemsRepository: ItemRepository
) : ViewModel() {

    var searchQuery = MutableStateFlow("")

    val itemUIState : StateFlow<AllItemsUIState> = searchQuery
        .debounce(100)
        .distinctUntilChanged()
        .combine(
            itemsRepository.getItemsStream()
        ){ search, items ->
            if(items.size > 0){
                if(!search.isEmpty()){
                    AllItemsUIState.Success(items.filter { it.name.contains(search,true) })
                }else{
                    AllItemsUIState.Success(items)
                }
            }else{
                if(!search.isEmpty()){
                    AllItemsUIState.Success(items.filter { it.name.contains(search,true) })
                }else{
                    AllItemsUIState.EmptyList
                }
            }
        }.catch {
            AllItemsUIState.Error
        }.stateIn(scope = viewModelScope, started = SharingStarted.WhileSubscribed(5000), initialValue = AllItemsUIState.Loading)

    fun searchProduct(name: String){
        searchQuery.value = name
    }

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

    fun addNewItem(name: String, buyAgain: Boolean, type: String){
        viewModelScope.launch {
            val newItem = Item(
                name = name.trim(),
                date = Clock.System.now(),
                buyAgain = buyAgain,
                type = type
            )
            itemsRepository.insertItem(newItem)
        }
    }

}

sealed interface AllItemsUIState{
    data class Success(val items: List<Item>) : AllItemsUIState
    object EmptyList : AllItemsUIState
    object Loading : AllItemsUIState
    object Error : AllItemsUIState
}