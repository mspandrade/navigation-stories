package br.com.mspandrade.navigation_stories_kit.ui

import androidx.core.view.children
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2

fun ViewPager2.disableNestedScrolling() {
    children.find { it is RecyclerView }?.let {
        (it as RecyclerView).isNestedScrollingEnabled = false
    }
}