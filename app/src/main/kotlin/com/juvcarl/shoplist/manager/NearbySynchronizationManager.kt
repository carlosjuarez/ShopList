package com.juvcarl.shoplist.manager

import android.content.Context
import android.widget.Toast
import com.google.android.gms.nearby.connection.Payload
import com.google.android.gms.nearby.connection.PayloadCallback
import com.google.android.gms.nearby.connection.PayloadTransferUpdate
import com.juvcarl.shoplist.di.ApplicationScope
import com.juvcarl.shoplist.di.DefaultDispatcher
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NearbySynchronizationManager @Inject constructor(
    @ApplicationContext private val context: Context,
    @ApplicationScope private val externalScope: CoroutineScope,
    @DefaultDispatcher private val dispatcher: CoroutineDispatcher,
    nearbyConnectionManagerFactory: NearbyConnectionManagerFactory
){

    val payloadCallback = object : PayloadCallback(){
        override fun onPayloadReceived(p0: String, p1: Payload) {
            Toast.makeText(context, "Payload received", Toast.LENGTH_SHORT).show()
        }

        override fun onPayloadTransferUpdate(p0: String, p1: PayloadTransferUpdate) {
            //Toast.makeText(context, R.string.advertising_initiated, Toast.LENGTH_SHORT).show()
        }
    }


    private var nearbyConnectionManager = nearbyConnectionManagerFactory.create(payloadCallback)


    fun startSynchronization(){
        externalScope.launch {
            nearbyConnectionManager.startDiscovery()
            nearbyConnectionManager.startAdvertising()
        }
    }



    fun stopSynchronization(){
        externalScope.launch {
            nearbyConnectionManager.stopDiscovering()
            nearbyConnectionManager.stopAdvertising()
        }
    }

}