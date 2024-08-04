package br.com.mspandrade.navigation_stories_kit.ui.framents

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.viewpager2.adapter.FragmentStateAdapter
import br.com.mspandrade.navigation_stories_kit.data.ChannelContentData
import br.com.mspandrade.navigation_stories_kit.data.viewmodel.StoryViewModel
import br.com.mspandrade.navigation_stories_kit.databinding.FragmentStoryChannelBinding
import br.com.mspandrade.navigation_stories_kit.service.ChannelContentAdapter
import br.com.mspandrade.navigation_stories_kit.ui.custom.NavigationStoriesIndicator
import br.com.mspandrade.navigation_stories_kit.ui.disableNestedScrolling
import org.koin.android.ext.android.inject

interface StoryChannelNavigation {
    fun next()
    fun previous()
}

abstract class FragmentBaseContent: Fragment() {
    lateinit var navigation: StoryChannelNavigation
}

internal class StoryChannelFragment: Fragment()
    , NavigationStoriesIndicator.OnNavigationStoriesEventListener, StoryChannelNavigation {

    companion object {
        const val ARG_CHANNEL_POSITION = "arg_channel_position"
    }

    var onFinishAnimation: () -> Unit = {}

    private lateinit var binding: FragmentStoryChannelBinding
    private lateinit var contentData: ChannelContentData
    private lateinit var adapter: ChannelStoryAdapter

    private val contentAdapter by inject<ChannelContentAdapter>()

    private val indicator get() = binding.navStoriesIndicator

    private val viewModel: StoryViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = FragmentStoryChannelBinding.inflate(inflater).root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentStoryChannelBinding.bind(view)

        indicator.setTheme(viewModel.indicatorTheme)

        val position = arguments?.getInt(ARG_CHANNEL_POSITION, 0) ?: 0

        Log.d(StoryChannelFragment::onCreateView.name, "Render channel of position $position")

        val contentData = contentAdapter.getChannelContent(position)
        this.contentData = contentData

        adapter = ChannelStoryAdapter(
            contentData, contentAdapter, fragment = this, navigation = this).apply {
            binding.viewPager.adapter = this
        }

        binding.viewPager.postOnAnimation {
            binding.viewPager.setCurrentItem(contentData.beginAt, false)
        }

        binding.viewPager.disableNestedScrolling()

        indicator.apply {
            segmentCount = adapter.itemCount
            setOnSegmentChanged(this@StoryChannelFragment)
            start(contentData.beginAt)
        }
    }

    override fun next() {
        if ((indicator.currentSegment + 1) == indicator.segmentCount) {
            onFinishAnimation.invoke()
            return
        }
        indicator.next()
    }

    override fun previous() {
        indicator.previous()
    }

    override fun onFinish() {
        onFinishAnimation.invoke()
    }

    override fun onDetach() {
        super.onDetach()
        binding.viewPager.adapter = null
    }

    override fun onChange(segment: Int) {
        Log.d("OnSegmentChanged", "segmentIndex: $segment")
        binding.viewPager.setCurrentItem(segment, false)
    }

    override fun onResume() {
        super.onResume()
        indicator.resume()
    }

    override fun onPause() {
        super.onPause()
        indicator.pause()
    }
}

private class ChannelStoryAdapter(
    val contentData: ChannelContentData,
    val contentAdapter: ChannelContentAdapter,
    val navigation: StoryChannelNavigation,
    fragment: Fragment
): FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = contentAdapter.getStoryBundles(contentData).size

    override fun createFragment(position: Int): Fragment = contentAdapter.onStoryFragmentCreate().apply {
        arguments = contentAdapter.getStoryBundles(contentData)[position]
        navigation = this@ChannelStoryAdapter.navigation
    }

}