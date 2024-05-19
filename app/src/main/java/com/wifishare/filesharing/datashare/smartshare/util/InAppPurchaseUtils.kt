//package com.app.wifishare.util
//
//import android.app.Activity
//import android.content.Context
//import android.content.Intent
//import android.os.Handler
//import android.os.Looper
//import android.util.Log
//import android.widget.Toast
//import com.android.billingclient.api.*
//import com.app.wifishare.R
//import com.app.wifishare.activities.Controller
//import com.app.wifishare.activities.SplashScreen.Companion.priceSplash
//import kotlinx.coroutines.CoroutineScope
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.launch
//
//
//object InAppPurchaseUtils {
//
//    var weeklyPrice = ""
//    var howManyDaysTrial = ""
//    var AfterFreeTrial = ""
//    val TAG = "PurchaseUtils"
//
//    private lateinit var billingClient: BillingClient
//    var productDetailsList: MutableList<ProductDetails>? = null
//
//    fun openIAPDialog(context: Activity) {
////        val prefClass = PrefClass(context)
//
//        billingClient = BillingClient
//            .newBuilder(context)
//            .enablePendingPurchases()
//            .setListener { billingResult, purchaseList ->
//                if (purchaseList != null && billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
////                    prefClass.isRewarded = System.currentTimeMillis()
////                    prefClass.isAppInPurchase = true
//                    Controller.isPurchasedApp=true
//                    purchaseList.forEach {
//                        verifyPurchaseList(context, it)
//                    }
//                } else if (billingResult.responseCode == BillingClient.BillingResponseCode.USER_CANCELED) {
//                    // Handle an error caused by a user cancelling the purchase flow.
//                    Toast.makeText(context,
//                        "Purchase Cancel by user",
//                        Toast.LENGTH_SHORT
//                    )
//                        .show()
//                } else {
//
//                    // Handle any other error codes.
//                    Toast.makeText(context, "Item not found", Toast.LENGTH_SHORT)
//                        .show()
//                }
//            }
//            .build()
//        showPriceProducts(context, true)
//
//    }
//
//    fun onlyPriceShow(context: Activity) {
//        Log.e(TAG, "showPrice")
//
//        try {
//            billingClient = BillingClient.newBuilder(context)
//                .enablePendingPurchases()
//                .setListener { billingResult, list ->
//                    if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && list != null) {
//
//                    }
//                }.build()
//            showPriceProducts(context, false)
//        } catch (ex: Throwable) {
//            ex.printStackTrace()
//            Log.e(TAG, "showPrice ${ex.message}")
//
//        }
//    }
//
//
//    private fun showPriceProducts(context: Context, onlyProduct: Boolean) {
//
//        try {
//            Log.e("aaaaaaaaa","showPriceProducts")
//
//            billingClient.startConnection(object : BillingClientStateListener {
//                override fun onBillingSetupFinished(billingResult: BillingResult) {
//
//                    if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
//                        Log.e("aaaaaaaaa","SSSSSSSSSSSS")
//
//                        productDetailsList = ArrayList()
////                        var iapKey = try {
////                            context.getRemoteIAP("iap_key").trim()
////                        } catch (e: Exception) {
////                            context.resources.getString(R.string.share_monthly)
////                        }
//                        var iapKey:String=context.resources.getString(R.string.share_monthly)
//
//                        Log.e(TAG, "showPriceProducts: $iapKey")
//                        val productList: MutableList<QueryProductDetailsParams.Product> =
//                            mutableListOf(
//                                QueryProductDetailsParams.Product.newBuilder()
//                                    .setProductId(iapKey)
//                                    .setProductType(BillingClient.ProductType.SUBS)
//                                    .build(),
//                            )
//                        val params = QueryProductDetailsParams.newBuilder()
//                            .setProductList(productList)
//                            .build()
//                        billingClient.queryProductDetailsAsync(
//                            params
//                        ) { _: BillingResult?, prodDetailsList: List<ProductDetails?> ->
//                            if (prodDetailsList.isNotEmpty()) { // checking if there's a product returned then set the product(s)
//                                for (qRSkuDetail in prodDetailsList) {
//                                    if (qRSkuDetail!!.subscriptionOfferDetails!![0].pricingPhases.pricingPhaseList.size > 1) {
//                                        howManyDaysTrial =
//                                            qRSkuDetail.subscriptionOfferDetails!![0].pricingPhases.pricingPhaseList[0].billingPeriod
//                                        AfterFreeTrial =
//                                            qRSkuDetail.subscriptionOfferDetails!![0].pricingPhases.pricingPhaseList[1].formattedPrice
//                                        weeklyPrice =
//                                            qRSkuDetail.subscriptionOfferDetails!![0].pricingPhases.pricingPhaseList[0].formattedPrice
//
//                                        priceSplash = "$AfterFreeTrial /Week"
//
//                                    } else {
//                                        weeklyPrice = qRSkuDetail.subscriptionOfferDetails!![0].pricingPhases.pricingPhaseList[0].formattedPrice
//                                        CoroutineScope(Dispatchers.Main).launch {
//                                            if(weeklyPrice!=""){
//                                                priceSplash= "$weeklyPrice /Week"
//                                                Log.e("aaaaaaaaa","iffffff")
//                                            }
//                                            else {
//                                                priceSplash="$6.00 /Week"
//                                                Log.e("aaaaaaaaa","elseee")
//                                            }
//                                        }
//                                    }
//                                    if (onlyProduct)
//                                        launchPurchaseFlow(context as Activity, qRSkuDetail)
//                                }
//                            }
//                        }
//                    }
//                }
//
//                override fun onBillingServiceDisconnected() {
//                    Log.e(TAG, "onBillingServiceDisconnected: " )
//
//                    Handler(Looper.getMainLooper()).postDelayed(
//                        { showPriceProducts(context, onlyProduct) },
//                        1000
//                    )
//                }
//            })
//        } catch (e: Exception) {
//            Log.e(TAG, "Exception: "+e.message )
//        }
//
//
//    }
//
//    private fun verifyPurchaseList(context: Context, purchases: Purchase) {
//        val acknowledgePurchaseParams: AcknowledgePurchaseParams = AcknowledgePurchaseParams
//            .newBuilder()
//            .setPurchaseToken(purchases.purchaseToken)
//            .build()
//        billingClient.acknowledgePurchase(acknowledgePurchaseParams) { billingResult ->
//            if (billingResult.responseCode === BillingClient.BillingResponseCode.OK) {
//                val intent =
//                    context.packageManager.getLaunchIntentForPackage(context.packageName)
//                intent!!.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
//                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
//                context.startActivity(intent)
//            }
//        }
//    }
//
//    private fun launchPurchaseFlow(context: Activity, productDetails: ProductDetails) {
//        assert(productDetails.subscriptionOfferDetails != null)
//        val productDetailsParamsList: MutableList<BillingFlowParams.ProductDetailsParams> =
//            mutableListOf(
//                BillingFlowParams.ProductDetailsParams.newBuilder()
//                    .setProductDetails(productDetails)
//                    .setOfferToken(productDetails.subscriptionOfferDetails!![0].offerToken)
//                    .build()
//            )
//        val billingFlowParams = BillingFlowParams.newBuilder()
//            .setProductDetailsParamsList(productDetailsParamsList)
//            .build()
//        billingClient.launchBillingFlow(context, billingFlowParams)
//    }
//
//
//
//}
//
