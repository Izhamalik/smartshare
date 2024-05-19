package com.wifishare.filesharing.datashare.smartshare.callback

import com.google.android.material.snackbar.Snackbar

fun interface SnackbarPlacementProvider {
    fun createSnackbar(resId: Int, vararg objects: Any?): Snackbar?
}