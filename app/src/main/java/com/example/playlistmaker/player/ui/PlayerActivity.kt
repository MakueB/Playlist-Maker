package com.example.playlistmaker.player.ui

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.navigation.fragment.findNavController
import androidx.navigation.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivityPlayerBinding
import com.example.playlistmaker.library.ui.playlists.PlaylistsState
import com.example.playlistmaker.newplaylist.domain.models.Playlist
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.BottomSheetCallback
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlayerActivity : AppCompatActivity(), BottomSheetListener {


    private val viewModel by viewModel<PlayerViewModel>()

    private lateinit var binding: ActivityPlayerBinding

    private lateinit var bottomSheetContainer: ConstraintLayout
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>

    private var adapter: PlayerAdapter? = null

    private val args by navArgs<PlayerActivityArgs>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val track = args.track

        if(savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container_view, PlayerFragment.newInstance(track))
                .commit()
        }
//        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragment_container_view) as NavHostFragment
//        val navController = navHostFragment.navController

        bottomSheetContainer = binding.bottomSheet
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetContainer).apply {
            state = BottomSheetBehavior.STATE_HIDDEN
        }
        bottomSheetBehavior.addBottomSheetCallback(object : BottomSheetCallback(){
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> {
                        binding.overlay.visibility = View.GONE
                    }
                    else -> {
                        binding.overlay.visibility = View.VISIBLE
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                val alpha = if (slideOffset < 0) 0f else slideOffset
                binding.overlay.alpha = alpha
            }
        })

        adapter = PlayerAdapter()
        binding.playlistsRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.playlistsRecyclerView.adapter = adapter

        binding.newPlaylistButton.setOnClickListener {
            navigateToNewPlaylist()
        }
    }

    private fun navigateToNewPlaylist() {
        val navController = supportFragmentManager.findFragmentById(R.id.fragment_container_view)?.findNavController()
        navController?.navigate(R.id.action_playerFragment_to_newPlaylistFragment)
    }

    private fun render(state: PlaylistsState) {
        when (state) {
            is PlaylistsState.Content -> showContent(state.playlists)
            is PlaylistsState.Empty -> showEmpty(state.message)
            else -> showEmpty(getString(R.string.library_is_empty))
        }
    }
    private fun showEmpty(message: String) {

    }

    private fun showContent(playlists: List<Playlist>) {
        adapter?.playlists?.clear()
        adapter?.playlists?.addAll(playlists)
        adapter?.notifyDataSetChanged()
    }

    override fun onPause() {
        super.onPause()
        viewModel.pausePlayer()
    }

    override fun showBottomSheet() {
        Log.d("PlayerFragment", "showBottomSheet called $bottomSheetBehavior")
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        viewModel.getPlaylistsAll()
        viewModel.state.observe(this) {
            render(it)
        }
    }
}