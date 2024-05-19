package com.wifishare.filesharing.datashare.smartshare.util

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class CorTask {
    companion object {
        private var result: String? = null
        fun runDefault(onRun: (() -> (String?)), onComplete: ((String?) -> (Unit))? = null) {
            val process = CoroutineScope(Dispatchers.Default).launch {
                result = onRun.invoke()
            }
            process.invokeOnCompletion {
                CoroutineScope(Dispatchers.Main).launch {
                    onComplete?.invoke(result)
                }
            }
        }

        fun runIO(onRun: (() -> (String?)), onComplete: ((String?) -> (Unit))? = null) {
            val process = CoroutineScope(Dispatchers.IO).launch {
                result = onRun.invoke()
            }
            process.invokeOnCompletion {
                CoroutineScope(Dispatchers.Main).launch {
                    onComplete?.invoke(result)
                }
            }
        }

        fun runMain(onRun: (() -> (Unit))) {
            CoroutineScope(Dispatchers.Main).launch {
                onRun.invoke()
            }
        }

        fun runGlobalMain(onGlobalRun: (() -> (Unit)), onComplete: (() -> (Unit))? = null) {
            val process = GlobalScope.launch(Dispatchers.Main) {
                onGlobalRun.invoke()
            }
            process.invokeOnCompletion {
                onComplete?.invoke()
            }
        }

    }
}

