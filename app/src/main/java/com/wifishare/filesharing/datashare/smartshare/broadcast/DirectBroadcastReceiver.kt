package com.wifishare.filesharing.datashare.smartshare.broadcast

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.NetworkInfo
import android.net.wifi.p2p.WifiP2pDevice
import android.net.wifi.p2p.WifiP2pDeviceList
import android.net.wifi.p2p.WifiP2pInfo
import android.net.wifi.p2p.WifiP2pManager
import android.util.Log
import androidx.core.app.ActivityCompat
import com.wifishare.filesharing.datashare.smartshare.callback.DirectActionListener


class DirectBroadcastReceiver(
    private val mWifiP2pManager: WifiP2pManager,
    private val mChannel: WifiP2pManager.Channel,
    private val mDirectActionListener: DirectActionListener
) : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        if (action != null) {
//            try {
//                if (serverSocket.isClosed  && StartActivity.isSocketClosed) {
//                    // The group has been removed.
//                    Log.e("caaaaaaa","aaaaaaaaaaaaaaaaaa")
//                    val intent = Intent(context, StartActivity::class.java)
//                    intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
//                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NO_HISTORY
//                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//                    context.startActivity(intent)
//                    ReceiveFileActivity.wifiP2pManager!!.removeGroup(ReceiveFileActivity.channel,null)
//                    ReceiveFileActivity.channel.close()
//                    ForegroundService.stopService(context)
////                channel.close()
//                }
//                else{
//                    Log.e("caaaaaaa","===============bbbabababababbbbbbbbbb")
//                }
//            }catch (e:Exception){
//
//            }
//
//            try {
//                if (socket!!.isClosed && StartActivity.isSocketClosed) {
//                    // The group has been removed.
//                    Log.e("caaaaaaa","aaaaaaaaaaaaaaaaaa")
//                    val intent = Intent(context, StartActivity::class.java)
//                    intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
//                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NO_HISTORY
//                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//                    context.startActivity(intent)
//                    SendFileActivity.wifiP2pManager!!.removeGroup(SendFileActivity.channel,null)
//                    SendFileActivity.channel.close()
//                    ForegroundService.stopService(context)
//
////                channel.close()
//                }
//                else{
//                    Log.e("caaaaaaa","===============bbbabababababbbbbbbbbb")
//
//                }
//            }catch (e:Exception){
//
//            }

            when (action) {


                WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION -> {
                    val state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -100)
                    if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
                        Log.e(TAG, "wifiP2pEnabled True   ")

                        mDirectActionListener.wifiP2pEnabled(true)
                    } else {
                        Log.e(TAG, "wifiP2pEnabled false   ")

                        mDirectActionListener.wifiP2pEnabled(false)
                        val wifiP2pDeviceList: List<WifiP2pDevice> = ArrayList()
                        mDirectActionListener.onPeersAvailable(wifiP2pDeviceList)
                    }
                }

                WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION -> {
                    if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        return
                    }

                    mWifiP2pManager.requestPeers(mChannel) { peers: WifiP2pDeviceList ->
                        Log.e(TAG, "WIFI_P2P_PEERS_CHANGED_ACTION   ${peers.deviceList.size}")
                        mDirectActionListener.onPeersAvailable(peers.deviceList)
                    }



                }

                WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION -> {
                    val networkInfo = intent.getParcelableExtra<NetworkInfo>(WifiP2pManager.EXTRA_NETWORK_INFO)
                    if (networkInfo != null && networkInfo.isConnected) {
                        mWifiP2pManager.requestConnectionInfo(mChannel) { wifiP2pInfo: WifiP2pInfo? ->
                            mDirectActionListener.onConnectionInfoAvailable(wifiP2pInfo)
                        }
                        Log.e(TAG, "connected p2p device   $networkInfo")


                    } else {
                        mDirectActionListener.onDisconnection()
                        Log.e(TAG, "Disconnected from p2p device")
                    }
                }

                WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION -> {
                    Log.e(TAG, "DEVICE_CHANGED_ACTION")
                    val wifiP2pDevice = intent.getParcelableExtra<WifiP2pDevice>(WifiP2pManager.EXTRA_WIFI_P2P_DEVICE)
                    mDirectActionListener.onSelfDeviceAvailable(wifiP2pDevice)
                }

            }
        }
    }

    companion object {
        private const val TAG = "DirectBroadcastReceiver"

        @JvmStatic
        val intentFilter: IntentFilter
            get() {
                val intentFilter = IntentFilter()
                intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION)
                intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION)
                intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION)
                intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION)
                return intentFilter
            }
    }
}