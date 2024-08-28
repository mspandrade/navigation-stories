package br.com.mspandrade.navigationstories

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import br.com.mspandrade.navigation_stories_kit.data.StoryIndicatorTheme
import br.com.mspandrade.navigation_stories_kit.data.viewmodel.StoryViewModel
import br.com.mspandrade.navigation_stories_kit.service.ChannelContentAdapter
import br.com.mspandrade.navigation_stories_kit.ui.framents.ChannelsNavigationFragment
import br.com.mspandrade.navigationstories.databinding.ActivityMainBinding
import org.koin.android.ext.android.get

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        ViewModelProvider(this)[StoryViewModel::class.java].indicatorTheme = StoryIndicatorTheme(
            indicatorCorner = 0f,
            gapSize = 10f,
            indicatorEnabledColor = android.R.color.holo_red_light,
            indicatorDisabledColor = android.R.color.holo_red_dark,
            height = 10
        )

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.btn.setOnClickListener {
            ChannelsNavigationFragment().apply {
                    setContent(get<ChannelContentAdapter>())
                    setInitialPosition(2)
                }
                .show(supportFragmentManager, "stories")
        }
    }
}