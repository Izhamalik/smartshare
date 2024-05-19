package com.wifishare.filesharing.datashare.smartshare.interfaces

interface OnProgressChangListener {
    fun totalFiles(totalFiles: Int)

    // when the transfer progress changes
    fun onProgressChanged(fileName: String?, progress: Float)

    // when the transfer is over
    fun onTransferFinished(file: String?)
}