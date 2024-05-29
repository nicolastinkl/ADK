package com.vungle.ads

enum class BannerAdSize( val sizeName: String,  val width: Int,  val height: Int) {
    VUNGLE_MREC("mrec", 300, 250),
    BANNER("banner", 320, 50),
    BANNER_SHORT("banner_short", 300, 50),
    BANNER_LEADERBOARD("banner_leaderboard", 728, 90);
}
