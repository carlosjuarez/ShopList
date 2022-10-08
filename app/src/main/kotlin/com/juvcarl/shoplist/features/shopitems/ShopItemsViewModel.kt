package com.juvcarl.shoplist.features.shopitems

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
class ShopItemsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val itemsRepository: ItemRepository
) : ViewModel() {

    val searchQuery = MutableStateFlow("")

    val itemUIState : StateFlow<ShopItemsUIState> = searchQuery
        .debounce(200)
        .distinctUntilChanged().combine(
                itemsRepository.getItemsToBuyStream()
        ){ search,items ->
                if(search.isNotEmpty()){
                    ShopItemsUIState.Success(items.filter { it.name.contains(search) })
                }else{
                    ShopItemsUIState.Success(items)
                }
        }.catch {
                ShopItemsUIState.Error
        }.stateIn(scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = ShopItemsUIState.Loading)

    fun searchItem(name: String){
        searchQuery.value = name
    }

}

sealed interface ShopItemsUIState{
    data class Success(val items: List<Item>) : ShopItemsUIState
    object Loading : ShopItemsUIState
    object Error : ShopItemsUIState
}