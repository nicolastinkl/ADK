package com.test.adsdk_demo

import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.widget.FrameLayout
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.test.adsdk_demo.ui.theme.AdSDKDemoTheme
import com.vungle.ads.BannerAd
import com.vungle.ads.BannerAdListener
import com.vungle.ads.BannerAdSize
import com.vungle.ads.BannerView
import com.vungle.ads.BaseAd
import com.vungle.ads.InitializationListener
import com.vungle.ads.VungleAds
import com.vungle.ads.VungleError


class MainActivity : ComponentActivity(), BannerAdListener {

    private var bannerAd: BannerAd? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        VungleAds.init(this,"7fff6a6f8920", object : InitializationListener {
            override fun onSuccess() {

                ShowBanner("BANNER_NON_BIDDING-4570799");
                setContent {
                    AdSDKDemoTheme {
                        // A surface container using the 'background' color from the theme
                        Surface(
                            modifier = Modifier.fillMaxSize(),
                            color = MaterialTheme.colorScheme.background
                        ) {
                            Greeting("Success")
                        }
                    }
                }
            }



            override fun onError(vungleError: VungleError) {
               Log.e("onError",""+vungleError.errorMessage)
                setContent {
                    AdSDKDemoTheme {
                        // A surface container using the 'background' color from the theme
                        Surface(
                            modifier = Modifier.fillMaxSize(),
                            color = MaterialTheme.colorScheme.background
                        ) {
                            Greeting(""+vungleError.errorMessage)
                        }
                    }
                }
            }
        })


    }

    fun ShowBanner(bannerID: String){
        bannerAd = BannerAd(this, "", BannerAdSize.BANNER).apply {
            adListener = this@MainActivity
            load()
        }

    }

    fun playAd() {

        if (bannerAd?.canPlayAd() == true) {
            bannerAd?.let {
                val bannerView: BannerView? = bannerAd?.getBannerView()
                val params = FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.WRAP_CONTENT,
                    FrameLayout.LayoutParams.WRAP_CONTENT,
                    Gravity.CENTER_HORIZONTAL
                )
                this.addContentView(bannerView, params)
            }
        }
    }




    override fun onAdLoaded(baseAd: BaseAd) {
        TODO("Not yet implemented")
    }

    override fun onAdStart(baseAd: BaseAd) {
        TODO("Not yet implemented")
    }

    override fun onAdImpression(baseAd: BaseAd) {
        TODO("Not yet implemented")
    }

    override fun onAdEnd(baseAd: BaseAd) {
        TODO("Not yet implemented")
    }

    override fun onAdClicked(baseAd: BaseAd) {
        TODO("Not yet implemented")
    }

    override fun onAdLeftApplication(baseAd: BaseAd) {
        TODO("Not yet implemented")
    }

    override fun onAdFailedToLoad(baseAd: BaseAd, adError: VungleError) {
        TODO("Not yet implemented")
    }

    override fun onAdFailedToPlay(baseAd: BaseAd, adError: VungleError) {
        TODO("Not yet implemented")
    }

}


@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = " $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    AdSDKDemoTheme {
        //Greeting("Android")
    }
}