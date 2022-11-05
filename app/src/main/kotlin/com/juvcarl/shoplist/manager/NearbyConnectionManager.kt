package com.juvcarl.shoplist.manager

import android.content.Context
import android.widget.Toast
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.connection.*
import com.juvcarl.shoplist.R
import com.juvcarl.shoplist.util.IdentityUtils
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import java.nio.charset.StandardCharsets
import javax.inject.Inject
import javax.inject.Singleton

class NearbyConnectionManager @AssistedInject constructor(
    @ApplicationContext val context: Context,
    identityUtils: IdentityUtils,
    @Assisted payloadCallback: PayloadCallback

){

    companion object{
        private val STRATEGY = Strategy.P2P_POINT_TO_POINT
        private const val SERVICE_ID = "com.juvcarl.shoplist"
    }

    private val localUserName = identityUtils.getLocalUserName()
    private val connectionsClient = Nearby.getConnectionsClient(context)

    val connectionLifecycleCallback = object : ConnectionLifecycleCallback() {

        override fun onConnectionInitiated(endpointId: String, connectionInfo: ConnectionInfo) {
            //Show notification and wait for accepting dialog
            Toast.makeText(context, "Show notification", Toast.LENGTH_SHORT).show()
            connectionsClient.acceptConnection(endpointId, payloadCallback)
        }

        override fun onConnectionResult(endPoint: String, result: ConnectionResolution) {
            when (result.getStatus().getStatusCode()) {
                ConnectionsStatusCodes.STATUS_OK -> {
                    //Start sending information
                    Toast.makeText(context, "Connected with endPointId", Toast.LENGTH_SHORT).show()
                }
                ConnectionsStatusCodes.STATUS_CONNECTION_REJECTED -> {
                    //Connection rejected
                    Toast.makeText(context, "Connection rejected", Toast.LENGTH_SHORT).show()
                }
                ConnectionsStatusCodes.STATUS_ERROR -> {
                    //Connection Broke
                    Toast.makeText(context, "Connection error", Toast.LENGTH_SHORT).show()
                }
                else -> {}
            }
        }

        override fun onDisconnected(endPoint: String) {
            //discnnected from endpoint
            Toast.makeText(context, "Disconnected", Toast.LENGTH_SHORT).show()
        }
    }

    val endpointDiscoveryCallback = object : EndpointDiscoveryCallback(){
        override fun onEndpointFound(endpointId: String, info: DiscoveredEndpointInfo) {
            connectionsClient
                .requestConnection(
                    localUserName,
                    endpointId,
                    connectionLifecycleCallback
                )
                .addOnSuccessListener{
                    Toast.makeText(context, "Connection Success", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener{
                    Toast.makeText(context, "Connection Failure", Toast.LENGTH_SHORT).show()
                }
        }

        override fun onEndpointLost(p0: String) {
            Toast.makeText(context, "EndPoint lost", Toast.LENGTH_SHORT).show()
        }
    }

    fun startAdvertising(){
        val advertisingOptions = AdvertisingOptions.Builder().setStrategy(STRATEGY).build()

        connectionsClient.startAdvertising(
            localUserName, SERVICE_ID, connectionLifecycleCallback, advertisingOptions
        ).addOnSuccessListener {
            Toast.makeText(context, R.string.advertising_initiated, Toast.LENGTH_SHORT).show()
        }.addOnFailureListener {
            Toast.makeText(context, R.string.failed_advertising, Toast.LENGTH_SHORT).show()
        }
    }

    fun stopAdvertising(){
        connectionsClient.stopAdvertising()
    }

    fun startDiscovery() {
        val discoveryOptions = DiscoveryOptions.Builder().setStrategy(STRATEGY).build()
        Nearby.getConnectionsClient(context)
            .startDiscovery(SERVICE_ID, endpointDiscoveryCallback, discoveryOptions)
            .addOnSuccessListener { Toast.makeText(context, R.string.discovering_initiated, Toast.LENGTH_SHORT).show() }
            .addOnFailureListener { Toast.makeText(context, R.string.discovering_failed, Toast.LENGTH_SHORT).show() }
    }

    fun stopDiscovering(){
        connectionsClient.stopDiscovery()
    }

    fun sendPayload(message: String, endpointId: String){
        val payload = Payload.fromBytes(message.toByteArray(StandardCharsets.UTF_8))
        connectionsClient.sendPayload(endpointId, payload)
    }

}

@AssistedFactory
interface NearbyConnectionManagerFactory{
    fun create(payloadCallback: PayloadCallback) : NearbyConnectionManager
}