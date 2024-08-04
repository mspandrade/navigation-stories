package br.com.mspandrade.navigationstories.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import br.com.mspandrade.navigation_stories_kit.ui.framents.FragmentBaseContent
import br.com.mspandrade.navigationstories.R
import br.com.mspandrade.navigationstories.databinding.FragmentRenderPhotoBinding
import br.com.mspandrade.navigationstories.di.ARG_SRC_IMAGE
import com.bumptech.glide.Glide

class RenderPhotoFragment: FragmentBaseContent() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = FragmentRenderPhotoBinding.inflate(inflater).root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        FragmentRenderPhotoBinding.bind(view).apply {
            val srcImageContent = arguments?.getString(ARG_SRC_IMAGE) ?: ""
            Log.d(RenderPhotoFragment::onViewCreated.name, "rendering image: $srcImageContent")
            Glide.with(imgContent)
                .load(srcImageContent)
                .placeholder(R.drawable.shape_placeholder)
                .into(imgContent)
                .clearOnDetach()

            btn.setOnClickListener {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.example_action_message),
                    Toast.LENGTH_LONG
                ).show()
            }

            next.setOnClickListener { navigation.next() }
            previous.setOnClickListener { navigation.previous() }
        }
    }
}