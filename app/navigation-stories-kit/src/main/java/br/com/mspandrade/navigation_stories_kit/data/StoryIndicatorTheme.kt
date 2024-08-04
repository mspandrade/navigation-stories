package br.com.mspandrade.navigation_stories_kit.data

import androidx.annotation.ColorRes

data class StoryIndicatorTheme(
    @ColorRes val indicatorEnabledColor: Int,
    @ColorRes val indicatorDisabledColor: Int,
    val indicatorCorner: Float,
    val gapSize: Float,
    val height: Int
)