package com.juvcarl.shoplist.features.shopitems

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.juvcarl.shoplist.data.model.BUYSTATUS
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

    val itemsUIState : StateFlow<ShopItemsUIState> = searchQuery
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

    val expandedItemList : MutableStateFlow<List<Long>> = MutableStateFlow(listOf())

    fun searchItem(name: String){
        searchQuery.value = name
    }

    fun changeBuyStatus(item: Item){
        val newStatus = when(item.buyStatus){
            BUYSTATUS.BUY.name -> BUYSTATUS.BOUGHT
            BUYSTATUS.BOUGHT.name -> BUYSTATUS.WAIT_TO_BUY
            BUYSTATUS.WAIT_TO_BUY.name -> BUYSTATUS.BUY
            else -> BUYSTATUS.BUY
        }
        viewModelScope.launch {
            itemsRepository.updateitem(item.copy(buyStatus = newStatus.name))
        }
    }

    fun updateBuyQty(item: Item, qty: Double, measure: String?){
        val updatedItem = item.copy(buyQty = qty)
        viewModelScope.launch {
            itemsRepository.updateitem(updatedItem)
        }
    }

    fun toggleExpandedListItem(id: Long){
        expandedItemList.update {
            it.toMutableList().also {
                if(it.contains(id)){
                    it.remove(id)
                }else{
                    it.add(id)
                }
            }
        }
    }

    fun finishShopping(){
        viewModelScope.launch {
            itemsRepository.finishShopping(true)
        }
    }

}

sealed interface ShopItemsUIState{
    data class Success(val items: List<Item>) : ShopItemsUIState
    object Loading : ShopItemsUIState
    object Error : ShopItemsUIState
}