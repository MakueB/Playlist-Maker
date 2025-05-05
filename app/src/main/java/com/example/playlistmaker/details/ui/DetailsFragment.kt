package com.example.playlistmaker.details.ui

import android.app.*
import android.content.*
import android.content.res.*
import android.os.*
import android.util.*
import android.view.*
import android.widget.*
import androidx.core.view.doOnLayout
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.*
import androidx.navigation.fragment.*
import androidx.recyclerview.widget.*
import com.bumptech.glide.*
import com.bumptech.glide.load.resource.bitmap.*
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.*
import com.example.playlistmaker.main.ui.*
import com.example.playlistmaker.createandeditplaylist.domain.models.*
import com.example.playlistmaker.search.domain.models.*
import com.example.playlistmaker.search.ui.*
import com.example.playlistmaker.utils.*
import com.google.android.material.bottomsheet.*
import org.koin.androidx.viewmodel.ext.android.*


class DetailsFragment : Fragment() {
    companion object {
        private const val CLICK_DEBOUNCE_DELAY = 1000L
        private const val MENU_BOTTOM_SHEET_HEIGHT_PERCENT = 0.45f
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
                menuBottomSheet.isVisible = true
                playlist?.let {
                    setupMenuBottomSheet(it)
                }
            }

            menuShare.setOnClickListener {
                viewModel.onShareClicked(playlist)
            }

            menuDeletePlaylist.setOnClickListener {
                val dialog = AlertDialog.Builder(requireContext())
                    .setMessage("Хотите удалить плейлист ${playlist?.name}?")
                    .setNegativeButton(getString(R.string.no)) { dialog, _ ->
                        dialog.dismiss()
                    }
                    .setPositiveButton(getString(R.string.yes)) { dialog, _ ->
                        playlist?.let { playlist -> viewModel.deletePlaylist(playlist) }
                        findNavController().popBackStack()
                    }
                    .create()
                dialog.show()
            }

            menuEditInfo.setOnClickListener {
                val action = DetailsFragmentDirections.actionDetailsFragmentToEditPlaylistFragment(playlist!!)
                findNavController().navigate(action)
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

    private fun adjustTopLayoutHeight(bottomSheet: View, percent: Float) {
        binding.apply {
            val screenHeight = Resources.getSystem().displayMetrics.heightPixels

            val peekHeight = (screenHeight * percent).toInt()

            val behavior = BottomSheetBehavior.from(bottomSheet)
            behavior.peekHeight = peekHeight
            behavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }
    }

    private fun setupShareBottomSheetHeight() {
        binding.actionIcons.doOnLayout {
            val actionIconsBottom = binding.actionIcons.bottom

            val parentHeight = (binding.root.parent as View).height

            val extraOffsetInDp = 16
            val extraOffsetInPx = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                extraOffsetInDp.toFloat(),
                resources.displayMetrics
            ).toInt()

            val desiredPeekHeight = parentHeight - actionIconsBottom - extraOffsetInPx //не забыть поковыряться тут, странное поведение

            val bottomSheet = binding.bottomSheet
            val bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED

            bottomSheetBehavior.peekHeight = desiredPeekHeight

            bottomSheetBehavior.isFitToContents = true
        }
    }

    private fun setupMenuBottomSheet(playlist: Playlist) {
        val cornersInPx = CommonUtils.dpToPx(8f, requireContext())

        binding.apply {
            val menuBottomSheet = menuBottomSheet
            context?.let { Glide.with(it).clear(menuImage) }

            if (!playlist.imageUrl.isNullOrEmpty()) {
                context?.let { context ->
                    Glide.with(context)
                        .load(playlist.imageUrl)
                        .placeholder(R.drawable.placeholder)
                        .centerCrop()
                        .transform(RoundedCorners(cornersInPx))
                        .into(menuImage)
                }
            } else {
                menuImage.setImageResource(R.drawable.placeholder)
            }

            menuPlaylistName.text = playlist.name

            val tracksNumber = playlist.trackList.size
            val trackCountText = getString(
                R.string.track_count,
                tracksNumber,
                CommonUtils.getTrackWordForm(tracksNumber)
            )
            menuNumberOfTracks.text = trackCountText

            val bottomSheetBehavior = BottomSheetBehavior.from(menuBottomSheet)
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED

            adjustTopLayoutHeight(menuBottomSheet, MENU_BOTTOM_SHEET_HEIGHT_PERCENT)

            bottomSheetBehavior.isFitToContents = false
            bottomSheetBehavior.isHideable = true
        }
    }

    override fun onResume() {
        super.onResume()
        binding.actionIcons.doOnLayout {
            setupShareBottomSheetHeight()
        }
    }
}