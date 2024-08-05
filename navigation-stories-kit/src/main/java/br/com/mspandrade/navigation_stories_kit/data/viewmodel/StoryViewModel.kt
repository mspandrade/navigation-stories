package br.com.mspandrade.navigation_stories_kit.data.viewmodel

import androidx.lifecycle.ViewModel
import br.com.mspandrade.navigation_stories_kit.data.StoryIndicatorTheme

internal class StoryViewModel: ViewModel() {

    var indicatorTheme: StoryIndicatorTheme = StoryIndicatorTheme(
        indicatorCorner = 10f,
        gapSize = 10f,
        indicatorEnabledColor = android.R.color.holo_green_light,
        indicatorDisabledColor = android.R.color.holo_green_dark,
        height = 10
    )

}