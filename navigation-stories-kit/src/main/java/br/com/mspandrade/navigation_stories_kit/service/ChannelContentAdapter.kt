package br.com.mspandrade.navigation_stories_kit.service

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import br.com.mspandrade.navigation_stories_kit.data.ChannelContentData
import br.com.mspandrade.navigation_stories_kit.ui.framents.FragmentBaseContent

interface ChannelContentAdapter {

    fun getChannelsCount(): Int

    fun getChannelContent(position: Int): ChannelContentData

    fun getStoryBundles(channel: ChannelContentData): List<Bundle>

    fun onStoryFragmentCreate(): FragmentBaseContent

}