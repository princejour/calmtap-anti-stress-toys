package com.example.ads

import android.app.Activity
import android.content.Context
import android.util.Log
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAd
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAdLoadCallback

class AdManager(private val context: Context) {
    private var interstitialAd: InterstitialAd? = null
    private var rewardedInterstitialAd: RewardedInterstitialAd? = null
    private var isInterstitialLoading = false
    private var isRewardedLoading = false

    // Test IDs
    private val interstitialId = "ca-app-pub-3940256099942544/1033173712"
    private val rewardedInterstitialId = "ca-app-pub-3940256099942544/5354046379"

    init {
        loadInterstitial()
        loadRewardedInterstitial()
    }

    private fun loadInterstitial() {
        if (isInterstitialLoading || interstitialAd != null) return
        isInterstitialLoading = true
        val adRequest = AdRequest.Builder().build()
        InterstitialAd.load(context, interstitialId, adRequest, object : InterstitialAdLoadCallback() {
            override fun onAdFailedToLoad(adError: LoadAdError) {
                Log.d("AdManager", adError.toString())
                interstitialAd = null
                isInterstitialLoading = false
            }

            override fun onAdLoaded(ad: InterstitialAd) {
                Log.d("AdManager", "Interstitial Ad was loaded.")
                interstitialAd = ad
                isInterstitialLoading = false
            }
        })
    }

    private fun loadRewardedInterstitial() {
        if (isRewardedLoading || rewardedInterstitialAd != null) return
        isRewardedLoading = true
        val adRequest = AdRequest.Builder().build()
        RewardedInterstitialAd.load(context, rewardedInterstitialId, adRequest, object : RewardedInterstitialAdLoadCallback() {
            override fun onAdFailedToLoad(adError: LoadAdError) {
                Log.d("AdManager", adError.toString())
                rewardedInterstitialAd = null
                isRewardedLoading = false
            }

            override fun onAdLoaded(ad: RewardedInterstitialAd) {
                Log.d("AdManager", "Rewarded Interstitial Ad was loaded.")
                rewardedInterstitialAd = ad
                isRewardedLoading = false
            }
        })
    }

    fun showInterstitial(activity: Activity, onAdDismissed: () -> Unit) {
        if (interstitialAd != null) {
            interstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    interstitialAd = null
                    loadInterstitial()
                    onAdDismissed()
                }

                override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                    interstitialAd = null
                    onAdDismissed()
                }
            }
            interstitialAd?.show(activity)
        } else {
            loadInterstitial()
            onAdDismissed()
        }
    }

    fun showRewardedInterstitial(activity: Activity, onRewardEarned: () -> Unit, onAdDismissed: () -> Unit) {
        if (rewardedInterstitialAd != null) {
            var rewardEarned = false
            rewardedInterstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    rewardedInterstitialAd = null
                    loadRewardedInterstitial()
                    if (rewardEarned) {
                        onRewardEarned()
                    }
                    onAdDismissed()
                }

                override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                    rewardedInterstitialAd = null
                    onAdDismissed()
                }
            }
            rewardedInterstitialAd?.show(activity) { rewardItem ->
                rewardEarned = true
            }
        } else {
            loadRewardedInterstitial()
            onAdDismissed()
        }
    }

    fun isRewardedAvailable(): Boolean = rewardedInterstitialAd != null
}
