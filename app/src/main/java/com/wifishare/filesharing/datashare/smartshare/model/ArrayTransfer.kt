package com.wifishare.filesharing.datashare.smartshare.model

import java.io.Serializable

class ArrayTransfer : Serializable {
    var index: Int=0
    var arrTr : ArrayList<FileTransfer> =ArrayList()
   // var fileTransfer: FileTransfer? = null
}