package com.wifishare.filesharing.datashare.smartshare.callback


interface DeleteMediaSelectionCallback {
    fun isArrayEmpty(arrEmpty:Boolean)
    fun isArrayFilled(arrFull:Boolean)
    fun isArrayOther(arrHavingItems:Boolean, arrSize: Int)
    fun hideShowLayouts(hideShow:Boolean)
}