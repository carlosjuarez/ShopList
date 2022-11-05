package com.juvcarl.shoplist.features.settings

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.juvcarl.shoplist.data.model.BUYSTATUS
import com.juvcarl.shoplist.data.model.Item
import com.juvcarl.shoplist.extensions.separateItems
import com.juvcarl.shoplist.manager.NearbySynchronizationManager
import com.juvcarl.shoplist.repository.ItemRepository
import com.juvcarl.shoplist.repository.Preference
import com.juvcarl.shoplist.repository.SharedPrefencesRepository
import com.juvcarl.shoplist.util.IdentityUtils

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
    private val itemRepository: ItemRepository,
    private val sharedPrefencesRepository: SharedPrefencesRepository,
    private val identityUtils: IdentityUtils,
    private val NearbySyncrhonizationManager: NearbySynchronizationManager
) : ViewModel() {

    val connectWithNearbyUsers: MutableStateFlow<Pair<Boolean,String>> = MutableStateFlow(Pair(false,""))
    val bulkInsertStatus: MutableStateFlow<BulkInsertStatus> = MutableStateFlow(BulkInsertStatus.Empty)

    fun toggleConnectNearby(connectNearby: Boolean){
        viewModelScope.launch {
            connectWithNearbyUsers.update {
                Pair(
                    connectNearby,
                    identityUtils.getLocalUserName()
                )
            }
            if(connectNearby){
                NearbySyncrhonizationManager.startSynchronization()
            }else{
                NearbySyncrhonizationManager.stopSynchronization()
            }
        }
    }

    fun addItemsInBulk(input: String, buyAgain: Boolean, type: String = "Basic" ){
        viewModelScope.launch(Dispatchers.IO) {
            bulkInsertStatus.update {
                BulkInsertStatus.Loading
            }
            val itemNames = input.separateItems()
            val itemsToInsert = itemNames.map { name ->
                Item(name = name, buyAgain = buyAgain, buyQty = 0.0, buyStatus = BUYSTATUS.BUY.name, date = Clock.System.now(), type = type)
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