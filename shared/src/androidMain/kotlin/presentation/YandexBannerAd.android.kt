package presentation

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.yandex.mobile.ads.banner.BannerAdSize
import com.yandex.mobile.ads.banner.BannerAdView
import com.yandex.mobile.ads.common.AdRequest

@Composable
actual fun YandexBannerAd(adUnitId: String, modifier: Modifier) {
    AndroidView(
        modifier = modifier.fillMaxWidth(),
        factory = { context ->
            BannerAdView(context).apply {
                setAdSize(BannerAdSize.sticky(context, 350))
                loadAd(AdRequest.Builder(adUnitId).build())
            }
        }
    )
}