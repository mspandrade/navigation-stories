package br.com.mspandrade.navigationstories.di

import android.os.Bundle
import br.com.mspandrade.navigation_stories_kit.data.ChannelContentData
import br.com.mspandrade.navigation_stories_kit.service.ChannelContentAdapter
import br.com.mspandrade.navigation_stories_kit.ui.framents.FragmentBaseContent
import br.com.mspandrade.navigationstories.ui.RenderPhotoFragment
import org.koin.dsl.module

const val ARG_SRC_IMAGE = "ARG_SRC_IMAGE"

val appModule = module {

    single<ChannelContentAdapter> { object : ChannelContentAdapter {

        override fun getChannelsCount(): Int = 6

        override fun getChannelContent(position: Int): ChannelContentData = ChannelContentData(
            id = position.toString(),
            beginAt = 0
        )

        override fun getStoryBundles(channel: ChannelContentData): List<Bundle> {
            val list = mutableListOf<Bundle>()
            repeat(8) { position ->
                list.add(instanceBundle(position + 1))
            }
            return list
        }

        override fun onStoryFragmentCreate(): FragmentBaseContent                                   = RenderPhotoFragment()

        private fun instanceBundle(position: Int): Bundle {

            val imageSrc = when {
                position.rem(2) == 0 ->
                    "https://s2-techtudo.glbimg.com/SSAPhiaAy_zLTOu3Tr3ZKu2H5vg=/0x0:1024x609/888x0/smart/filters:strip_icc()/i.s3.glbimg.com/v1/AUTH_08fbf48bc0524877943fe86e43087e7a/internal_photos/bs/2022/c/u/15eppqSmeTdHkoAKM0Uw/dall-e-2.jpg"
                else ->
                    "https://cdn.pixabay.com/photo/2024/02/26/19/39/monochrome-image-8598798_640.jpg"
            }


            return Bundle().also {
                it.putString("id", "story-$position")
                it.putString(ARG_SRC_IMAGE, imageSrc)
            }
        }

    }}
}