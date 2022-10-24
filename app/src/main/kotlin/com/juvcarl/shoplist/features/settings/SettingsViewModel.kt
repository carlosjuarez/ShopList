package com.juvcarl.shoplist.features.settings

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.juvcarl.shoplist.data.model.BUYSTATUS
import com.juvcarl.shoplist.data.model.Item
import com.juvcarl.shoplist.extensions.separateItems
import com.juvcarl.shoplist.repository.ItemRepository
import com.juvcarl.shoplist.repository.Preference
import com.juvcarl.shoplist.repository.SharedPrefencesRepository

import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    val itemRepository: ItemRepository,
    val sharedPrefencesRepository: SharedPrefencesRepository
) : ViewModel() {

    val connectWithNearbyUsers: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val bulkInsertStatus: MutableStateFlow<BulkInsertStatus> = MutableStateFlow(BulkInsertStatus.Empty)

    init {
        checkConnectNearbyStatus()
    }

    private fun checkConnectNearbyStatus(){
        viewModelScope.launch {
            connectWithNearbyUsers.update {
                sharedPrefencesRepository.getBooleanValue(Preference.CONNECT_NEARBY,false)
            }
        }
    }

    fun toggleConnectNearby(connectNearby: Boolean){
        viewModelScope.launch {
            sharedPrefencesRepository.setBooleanValue(Preference.CONNECT_NEARBY,connectNearby)
            checkConnectNearbyStatus()
        }
        if(connectNearby){
            //TODO start nearby API connection
        }else{
            //TODO Stop connections and don't call them again until activated
        }
    }

    fun addItemsInBulk(input: String, buyAgain: Boolean, type: String = "Basic" ){
        viewModelScope.launch(Dispatchers.IO) {
            bulkInsertStatus.update {
                BulkInsertStatus.Loading
            }
            val itemNames = input.separateItems()
            val itemsToInsert = itemNames.map { name ->
                Item(name = name, buyAgain = buyAgain, buyQty = 0.0, buyStatus = BUYSTATUS.BUY.name, date = Clock.System.now())
            }
            val result = itemRepository.insertMultipleItems(itemsToInsert)
            bulkInsertStatus.update {
                if(result.size == itemsToInsert.size){
                    BulkInsertStatus.Success
                }else{
                    BulkInsertStatus.Error
                }
            }

        }
    }
}

sealed interface BulkInsertStatus{
    object Empty : BulkInsertStatus
    object Loading : BulkInsertStatus
    object Success: BulkInsertStatus
    object Error: BulkInsertStatus
}