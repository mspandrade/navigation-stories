package br.com.mspandrade.navigation_stories_kit.ui.framents

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import br.com.mspandrade.navigation_stories_kit.R
import br.com.mspandrade.navigation_stories_kit.databinding.FragmentChannelsNavigationBinding
import br.com.mspandrade.navigation_stories_kit.service.ChannelContentAdapter
import br.com.mspandrade.navigation_stories_kit.ui.disableNestedScrolling
import br.com.mspandrade.navigation_stories_kit.ui.framents.StoryChannelFragment.Companion.ARG_CHANNEL_POSITION
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.BottomSheetCallback
import org.koin.android.ext.android.inject
import kotlin.math.abs


open class ChannelsNavigationFragment: DialogFragment() {

    companion object {
        const val MAX_SCALE_OUT = 0.9f
    }

    private val contentAdapter: ChannelContentAdapter by inject()
    private lateinit var adapter: ChannelsAdapter

    private lateinit var binding: FragmentChannelsNavigationBinding

    override fun getTheme(): Int {
        return R.style.DialogTheme
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState).apply {
            this.window?.setBackgroundDrawableResource(R.color.background_dialog)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = FragmentChannelsNavigationBinding.inflate(layoutInflater).root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentChannelsNavigationBinding.bind(view)
        setUpViewPagerAdapter()
        setUpBottomSheet()
    }

    private fun setUpViewPagerAdapter() {
        val viewPager = binding.viewPager
        adapter = ChannelsAdapter(
            contentAdapter,
            this@ChannelsNavigationFragment
        ) {
            val nextPosition = viewPager.currentItem + 1
            when {
                nextPosition >= adapter.itemCount -> dismiss()
                viewPager.adapter != null ->
                    viewPager.setCurrentItem(viewPager.currentItem + 1, true)
            }
        }
        viewPager.adapter = adapter
        viewPager.disableNestedScrolling()
        viewPager.setPageTransformer(ScalePageTransformation())
    }

    private fun setUpBottomSheet() {
        val behavior = BottomSheetBehavior.from(binding.viewPager)
        behavior.state = BottomSheetBehavior.STATE_EXPANDED
        behavior.isHideable = false
        behavior.addBottomSheetCallback(object : BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_COLLAPSED)
                    this@ChannelsNavigationFragment.dismiss()
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                if (isAdded) transitionBottomSheet(slideOffset)
            }
        })
    }

    private fun transitionBottomSheet(slideOffset: Float) {
        val offset: Float = if (slideOffset < MAX_SCALE_OUT) MAX_SCALE_OUT else slideOffset
        binding.viewPager.scaleX = offset
        binding.viewPager.scaleY = offset
    }
}

private class ChannelsAdapter(
    val contentAdapter: ChannelContentAdapter,
    fragment: Fragment,
    val onFinish: () -> Unit
): FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = contentAdapter.getChannelsCount()

    override fun createFragment(position: Int): Fragment = StoryChannelFragment().apply {
        arguments = Bundle().also {
            it.putInt(ARG_CHANNEL_POSITION, position)
        }
        this.onFinishAnimation = onFinish
    }
}

private class ScalePageTransformation : ViewPager2.PageTransformer {

    companion object {
        private const val MIN_SCALE: Float = 0.8f
    }

    override fun transformPage(page: View, position: Float) {
        if (position in 0.0..1.0) {
            page.scaleX = MIN_SCALE.coerceAtLeast(1f - abs(position))
            page.scaleY = MIN_SCALE.coerceAtLeast(1f - abs(position))
        }
    }
}