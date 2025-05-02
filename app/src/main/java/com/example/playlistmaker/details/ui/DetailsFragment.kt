package com.example.playlistmaker.details.ui

import android.app.AlertDialog
import android.content.Intent
import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentDetailsBinding
import com.example.playlistmaker.main.ui.MainActivity
import com.example.playlistmaker.newplaylist.domain.models.Playlist
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.search.ui.TrackActionListener
import com.example.playlistmaker.search.ui.TrackAdapter
import com.example.playlistmaker.utils.CommonUtils
import com.example.playlistmaker.utils.debounce
import com.google.android.material.bottomsheet.BottomSheetBehavior
import org.koin.androidx.viewmodel.ext.android.viewModel


class DetailsFragment : Fragment() {
    companion object {
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }

    private var _binding: FragmentDetailsBinding? = null
    private val binding: FragmentDetailsBinding get() = _binding!!

    private val viewModel: DetailsViewModel by viewModel()

    private val args by navArgs<DetailsFragmentArgs>()
    private var playlist: Playlist? = null

    private var adapter: TrackAdapter? = null
    private lateinit var onTrackClickDebounce: (Track) -> Unit

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        playlist = args.playlist
        setupUi(playlist)
        setupShareBottomSheetHeight()
        setupListeners()
        setupObservers()
        setupAdapter()
        setupDebounce()
    }

    private fun setupDebounce() {
        binding.apply {
            onTrackClickDebounce = debounce<Track>(
                CLICK_DEBOUNCE_DELAY,
                viewLifecycleOwner.lifecycleScope,
                false
            ) { track: Track ->
                val action =
                    DetailsFragmentDirections.actionPlaylistDetailsFragmentToPlayerFragment(
                        track
                    )
                findNavController().navigate(action)
            }
        }
    }

    private fun setupAdapter() {
        adapter = TrackAdapter(object : TrackActionListener {
            override fun onTrackClick(track: Track) {
                (activity as MainActivity).animateBottomNavigationView()
                onTrackClickDebounce(track)
            }

            override fun onTrackLongClick(track: Track): Boolean {
                showRemoveTrackDialog(track)
                return true
            }
        })
        binding.tracksRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter?.tracks = playlist?.trackList?.toMutableList() ?: mutableListOf()
        binding.tracksRecyclerView.adapter = adapter
    }

    private fun setupObservers() {
        viewModel.shareCommand.observe(viewLifecycleOwner) { command ->
            when (command) {
                is ShareCommand.ShowEmptyPlaylistMessage -> {
                    Toast.makeText(
                        requireContext(),
                        "В этом плейлисте нет списка треков, которым можно поделиться",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                is ShareCommand.SharePlaylist -> {
                    val shareIntent = Intent(Intent.ACTION_SEND).apply {
                        putExtra(Intent.EXTRA_TEXT, command.text)
                        type = "text/plain"
                    }
                    startActivity(Intent.createChooser(shareIntent, "Поделиться через:"))
                }
            }
        }
    }

    private fun setupUi(playlist: Playlist?) {
        binding.apply {
            val tracksNumber = playlist?.trackList?.size ?: 0
            val totalDurationInSeconds =
                playlist?.trackList?.sumOf { CommonUtils.toDurationInSeconds(it.trackDuration) }
                    ?: 0
            val cornersInPx = CommonUtils.dpToPx(8f, requireContext())
            playlistName.text = playlist?.name
            playlistDescription.text = playlist?.description
            playlistDetails.text = getString(
                R.string.playlist_details,
                tracksNumber,
                CommonUtils.getTrackWordForm(tracksNumber),
                CommonUtils.toFormattedDuration(totalDurationInSeconds)
            )

            if (playlist?.imageUrl.isNullOrEmpty()) {
                playlistCoverImage.setImageResource(R.drawable.placeholder)
            } else {
                context?.let { context ->
                    Glide.with(context)
                        .load(playlist.imageUrl)
                        .placeholder(R.drawable.placeholder)
                        .centerCrop()
                        .transform(RoundedCorners(cornersInPx))
                        .into(playlistCoverImage)
                }
            }
        }
    }

    private fun setupListeners() {
        binding.apply {
            backArrow.setOnClickListener {
                requireActivity().onBackPressedDispatcher.onBackPressed()
            }

            shareIcon.setOnClickListener {
                viewModel.onShareClicked(playlist)
            }

            menuIcon.setOnClickListener {
                menuBottomSheet.visibility = View.VISIBLE
                playlist?.let {
                    setupMenuBottomSheet(it)
                }
            }
        }
    }

    private fun showRemoveTrackDialog(track: Track) {
        val dialog = AlertDialog.Builder(requireContext())
            .setMessage("Хотите удалить трек?")
            .setNegativeButton("Нет") { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton("Да") { dialog, _ ->
                removeTrackFromPlaylist(track)
                dialog.dismiss()
            }
            .create()

        dialog.show()
    }

    private fun removeTrackFromPlaylist(track: Track) {
        val position = adapter?.tracks?.indexOf(track) ?: return
        if (position != -1) {
            adapter?.tracks?.removeAt(position)
            adapter?.notifyItemRemoved(position)
            adapter?.notifyItemRangeChanged(position, adapter?.itemCount ?: 0)
        }

        viewModel.removeTrack(track, playlist!!.id)
    }

    // метод ограничивает высоту вью в процентах от экрана. Позже выберу лучшее решение
    private fun adjustTopLayoutHeight(bottomSheet: View, percent: Float) {
        binding.apply {
            val screenHeight = Resources.getSystem().displayMetrics.heightPixels

            val peekHeightPercent = percent

            val peekHeight = (screenHeight * peekHeightPercent).toInt()

            val behavior = BottomSheetBehavior.from(bottomSheet)
            behavior.peekHeight = peekHeight
            behavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }
    }

    private fun setupShareBottomSheetHeight() {
        binding.actionIcons.post {
            val actionIconsBottom = binding.actionIcons.bottom

            val parentHeight = (binding.root.parent as View).height

            val extraOffsetInDp = 16
            val extraOffsetInPx = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                extraOffsetInDp.toFloat(),
                resources.displayMetrics
            ).toInt()

            val desiredPeekHeight = parentHeight - actionIconsBottom - extraOffsetInPx

            val bottomSheet = binding.bottomSheet
            val bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)

            bottomSheetBehavior.peekHeight = desiredPeekHeight

            bottomSheetBehavior.isFitToContents = true

            var lastSlideOffset = 0f
            bottomSheetBehavior.addBottomSheetCallback(object :
                BottomSheetBehavior.BottomSheetCallback() {
                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    if (newState == BottomSheetBehavior.STATE_SETTLING) {
                        if (lastSlideOffset < 0.5f) {
                            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                        } else {
                            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
                        }
                    }
                }

                override fun onSlide(bottomSheet: View, slideOffset: Float) {
                    lastSlideOffset = slideOffset
                }
            })
        }
    }

    private fun setupMenuBottomSheet(playlist: Playlist) {
        val cornersInPx = CommonUtils.dpToPx(8f, requireContext())
        val menuBottomSheet = binding.menuBottomSheet
        context?.let { Glide.with(it).clear(binding.menuImage) }

        if (!playlist.imageUrl.isNullOrEmpty()) {
            context?.let { context ->
                Glide.with(context)
                    .load(playlist.imageUrl)
                    .placeholder(R.drawable.placeholder)
                    .centerCrop()
                    .transform(RoundedCorners(cornersInPx))
                    .into(binding.menuImage)
            }
        } else {
            binding.menuImage.setImageResource(R.drawable.placeholder)
        }

        val bottomSheetBehavior = BottomSheetBehavior.from(menuBottomSheet)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED

        adjustTopLayoutHeight(binding.menuBottomSheet, 0.45f)

        bottomSheetBehavior.isFitToContents = false
        bottomSheetBehavior.isHideable = true
    }

    override fun onResume() {
        super.onResume()
        setupShareBottomSheetHeight()
    }
}