package com.example.playlistmaker.player.ui

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlayerBinding
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.utils.CommonUtils
import com.google.android.material.bottomnavigation.BottomNavigationView
import org.koin.androidx.viewmodel.ext.android.viewModel


class PlayerFragment : Fragment() {
    companion object {
        private const val ARG_TRACK = "track"

        fun newInstance(track: Track): PlayerFragment {
            val fragment = PlayerFragment()
            val args = Bundle()
            args.putParcelable(ARG_TRACK, track)
            fragment.arguments = args
            return fragment
        }
    }

    private var _binding: FragmentPlayerBinding? = null
    private val binding: FragmentPlayerBinding get() = _binding!!

    private val viewModel by viewModel<PlayerViewModel>()

    private var track: Track? = null

    //private val args by navArgs<PlayerFragmentArgs>()

    private var bottomSheetListener: BottomSheetListener? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlayerBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            track = it.getParcelable(ARG_TRACK, Track::class.java)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //val track = args.track

        track?.let { setFavoriteButton(it) }

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
                    // Обработка других состояний по необходимости
                }
            }
        }
        viewModel.elapsedTime.observe(viewLifecycleOwner) { time ->
            binding.elapsedTime.text = time
        }
        viewModel.preparePlayer(track)

        binding.favoriteButton.setOnClickListener {
            track?.let { it1 -> viewModel.onFavoriteClicked(it1) }
        }

        binding.addButton.setOnClickListener {
            bottomSheetListener?.showBottomSheet()
        }

        viewModel.isFavorite.observe(viewLifecycleOwner) { isFavorite ->
            if (isFavorite) {
                binding.favoriteButton.setImageResource(R.drawable.favorite_active)
            } else {
                binding.favoriteButton.setImageResource(R.drawable.favorite_inactive)
            }
        }
    }

    private fun setFavoriteButton(track: Track) {
        if (track.isFavorite) {
            binding.favoriteButton.setImageResource(R.drawable.favorite_active)
        } else {
            binding.favoriteButton.setImageResource(R.drawable.favorite_inactive)
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Log.d("PlayerFragment", "onAttach called")
        if (context is BottomSheetListener) {
            bottomSheetListener = context
        }  else {
            throw RuntimeException("$context must implement BottomSheetListener")
        }
        if (activity is PlayerActivity) {
            requireActivity().findViewById<BottomNavigationView>(R.id.bottom_navigation_view)
                .isVisible = false
        }
    }

    override fun onDetach() {
        super.onDetach()
        bottomSheetListener = null
        if (activity is PlayerActivity) {
            requireActivity().findViewById<BottomNavigationView>(R.id.bottom_navigation_view)
                .isVisible = true
        }

    }
}