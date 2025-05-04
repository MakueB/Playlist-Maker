package com.example.playlistmaker.player.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlayerBinding
import com.example.playlistmaker.library.ui.playlists.PlaylistsState
import com.example.playlistmaker.createplaylist.domain.models.Playlist
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.utils.CommonUtils
import com.example.playlistmaker.utils.debounce
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.BottomSheetCallback
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.activityViewModel


class PlayerFragment : Fragment() {
    companion object {
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }

    private var _binding: FragmentPlayerBinding? = null
    private val binding: FragmentPlayerBinding get() = _binding!!

    private var peekHeight: Int = 440

    private val viewModel by activityViewModel<PlayerViewModel>()

    private var track: Track? = null

    private lateinit var bottomSheetContainer: ConstraintLayout
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>

    private val args by navArgs<PlayerFragmentArgs>()
    private var adapter: PlayerAdapter? = null
    private lateinit var onPlaylistClickDebounce: (Playlist) -> Unit

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlayerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        peekHeight = resources.getDimensionPixelSize(R.dimen.bottom_sheet_peek_height)

        onPlaylistClickDebounce = debounce<Playlist>(
            CLICK_DEBOUNCE_DELAY,
            viewLifecycleOwner.lifecycleScope,
            false
        ) {
            viewModel.addTrackToPlaylist(track!!, it.id)
        }

        track = arguments?.let { args.track } ?: viewModel.track.value

        track?.let { viewModel.setTrack(it) }

        track?.let { setFavoriteButton(it) }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.trackToPlaylistResultMessage.collect { result ->
                    val (message, success) = result
                    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                    if (success) {
                        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                        bottomSheetBehavior.peekHeight = peekHeight
                    }
                }
            }
        }

        binding.apply {
            country.text = track?.country
            genre.text = track?.primaryGenreName
            releaseDate.text = track?.releaseDate?.substring(0, 4)

            if (track?.collectionName.isNullOrEmpty()) {
                albumGroup.visibility = View.GONE
            } else {
                albumGroup.isVisible = true
                collectionName.text = track?.collectionName
            }

            duration.text = track?.trackDuration
            collectionNameTextView.text = track?.collectionName
            trackName.text = track?.trackName
        }

        val cornersInPx = CommonUtils.dpToPx(8f, requireContext())

        binding.apply {
            if (track?.artworkUrl100?.isEmpty() == true)
                cover.setImageResource(R.drawable.placeholder)
            else
                context?.let { context ->
                    Glide.with(context)
                        .load(track?.getCoverArtwork())
                        .placeholder(R.drawable.placeholder)
                        .centerCrop()
                        .transform(RoundedCorners(cornersInPx))
                        .into(cover)
                }

            backArrow.setOnClickListener {
                requireActivity().onBackPressedDispatcher.onBackPressed()
            }
        }

        binding.playButton.isEnabled = false

        binding.playButton.setOnClickListener {
            viewModel.playBackControl()
        }

        viewModel.playerState.observe(viewLifecycleOwner) { state ->
            when (state) {
                PlayerState.PREPARED -> {
                    binding.playButton.isEnabled = true
                    binding.playButton.setImageResource(R.drawable.button_play)
                }

                PlayerState.PLAYING -> {
                    binding.playButton.setImageResource(R.drawable.button_pause)
                }

                PlayerState.PAUSED -> {
                    binding.playButton.setImageResource(R.drawable.button_play)
                }

                else -> {
                }
            }
        }
        viewModel.elapsedTime.observe(viewLifecycleOwner) { time ->
            binding.elapsedTime.text = time
        }
        viewModel.preparePlayer(track)

        binding.favoriteButton.setOnClickListener {
            track.let { it1 ->
                if (it1 != null) {
                    viewModel.onFavoriteClicked(it1)
                }
            }
        }

        binding.addButton.setOnClickListener {
            bottomSheetBehavior.peekHeight = peekHeight
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            viewModel.getPlaylistsAll()
            viewModel.state.observe(viewLifecycleOwner) {
                render(it)
            }
        }

        viewModel.isFavorite.observe(viewLifecycleOwner) { isFavorite ->
            if (isFavorite) {
                binding.favoriteButton.setImageResource(R.drawable.favorite_active)
            } else {
                binding.favoriteButton.setImageResource(R.drawable.favorite_inactive)
            }
        }

        bottomSheetContainer = binding.bottomSheet
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetContainer).apply {
            state = BottomSheetBehavior.STATE_HIDDEN
        }
        bottomSheetBehavior.addBottomSheetCallback(object : BottomSheetCallback() {
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
        adapter = PlayerAdapter { playlist: Playlist ->
            onPlaylistClickDebounce(playlist)
        }

        binding.playlistsRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.playlistsRecyclerView.adapter = adapter

        binding.newPlaylistButton.setOnClickListener {
            navigateToNewPlaylist()
        }
    }

    private fun render(state: PlaylistsState) {
        when (state) {
            is PlaylistsState.Content -> showContent(state.playlists)
            is PlaylistsState.Empty -> showEmpty(state.message)
            else -> showEmpty(getString(R.string.library_is_empty))
        }
    }

    private fun showEmpty(message: String) {
        //на будущее
    }

    private fun showContent(playlists: List<Playlist>) {
        adapter?.playlists?.clear()
        adapter?.playlists?.addAll(playlists)
        adapter?.notifyDataSetChanged()
    }

    private fun navigateToNewPlaylist() {
        val action = PlayerFragmentDirections.actionPlayerFragmentToNewPlaylistFragment(null)
        findNavController().navigate(action)
    }

    private fun setFavoriteButton(track: Track) {
        if (track.isFavorite) {
            binding.favoriteButton.setImageResource(R.drawable.favorite_active)
        } else {
            binding.favoriteButton.setImageResource(R.drawable.favorite_inactive)
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.pausePlayer()
        track = arguments?.let { args.track } ?: viewModel.track.value
        viewModel.preparePlayer(track)
        track?.let { setFavoriteButton(it) }
    }
}