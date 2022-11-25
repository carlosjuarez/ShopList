package com.juvcarl.shoplist.features.settings

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.juvcarl.shoplist.data.model.BUYSTATUS
import com.juvcarl.shoplist.data.model.Item
import com.juvcarl.shoplist.extensions.separateItems
import com.juvcarl.shoplist.manager.NearbySynchronizationManager
import com.juvcarl.shoplist.repository.ItemRepository
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
    val importStatus: MutableStateFlow<ImportStatus> = MutableStateFlow(ImportStatus.Empty)
    val exportString: MutableStateFlow<String> = MutableStateFlow("")

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

    fun importItems(input: String){
        viewModelScope.launch(Dispatchers.IO) {
            importStatus.update {
                ImportStatus.Loading
            }
            val itemNames = input.separateItems()
            val itemsToImport = itemNames.map { name ->
                Item(name = name, buyAgain = buyAgain, buyQty = 0.0, buyStatus = BUYSTATUS.BUY.name, date = Clock.System.now(), type = type)
            }
            val result = itemRepository.ImportItems(itemsToImport)
            importStatus.update {
                if(result.size == itemsToImport.size){
                    ImportStatus.Success
                }else{
                    ImportStatus.Error
                }
            }

        }
    }

    fun exportList(){
        viewModelScope.launch {
            exportString.update { itemRepository.createExportString() }
        }
    }

    fun listShared(){
        viewModelScope.launch {
            exportString.update { "" }
        }
    }

    fun clearAll(){
        viewModelScope.launch {
            itemRepository.clearItems()
        }
    }
}

sealed interface ImportStatus{
    object Empty : ImportStatus
    object Loading : ImportStatus
    object Success: ImportStatus
    object Error: ImportStatus
}