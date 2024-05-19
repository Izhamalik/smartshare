package com.wifishare.filesharing.datashare.smartshare.model

import java.io.Serializable

class FileTransfer : Serializable {
    var fileName: String? = null
    var fileLength: Long = 0
    var md5: String? = null
    var totalFiles: Int = 0
    var filePath: String? = null
}