package com.example.playlistmaker.player.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlayerBinding
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.utils.CommonUtils
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.BottomSheetCallback
import org.koin.androidx.viewmodel.ext.android.viewModel


class PlayerFragment : Fragment() {
    private var _binding: FragmentPlayerBinding? = null
    private val binding: FragmentPlayerBinding get() = _binding!!

    private val viewModel by viewModel<PlayerViewModel>()

    private lateinit var bottomSheetContainer: ConstraintLayout
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>

    private val args by navArgs<PlayerFragmentArgs>()
    private var adapter: PlayerAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlayerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val track = args.track

        setFavoriteButton(track)

        binding.apply {
            country.text = track.country
            genre.text = track.primaryGenreName
            releaseDate.text = track.releaseDate?.substring(0, 4)

            if (track.collectionName.isNullOrEmpty()) {
                albumGroup.visibility = View.GONE
            } else {
                albumGroup.isVisible = true
                collectionName.text = track.collectionName
            }

            duration.text = track.trackDuration
            collectionNameTextView.text = track.collectionName
            trackName.text = track.trackName
        }

        val cornersInPx = CommonUtils.dpToPx(8f, requireContext())

        binding.apply {
            if (track.artworkUrl100.isEmpty())
                cover.setImageResource(R.drawable.placeholder)
            else
                context?.let { context ->
                    Glide.with(context)
                        .load(track.getCoverArtwork())
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
                    // Обработка других состояний по необходимости
                }
            }
        }
        viewModel.elapsedTime.observe(viewLifecycleOwner) { time ->
            binding.elapsedTime.text = time
        }
        viewModel.preparePlayer(track)

        binding.favoriteButton.setOnClickListener {
            track.let { it1 -> viewModel.onFavoriteClicked(it1) }
        }

        binding.addButton.setOnClickListener {
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
        binding.playlistsRecyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.playlistsRecyclerView.adapter = adapter

        binding.newPlaylistButton.setOnClickListener {
            navigateToNewPlaylist()
        }
    }

    private fun navigateToNewPlaylist() {
        val action = PlayerFragmentDirections.actionPlayerFragmentToNewPlaylistFragment()
        findNavController().navigate(action)
    }

    private fun setFavoriteButton(track: Track) {
        if (track.isFavorite) {
            binding.favoriteButton.setImageResource(R.drawable.favorite_active)
        } else {
            binding.favoriteButton.setImageResource(R.drawable.favorite_inactive)
        }
    }
}