package com.example.playlistmaker.details.ui

import android.app.AlertDialog
import android.content.Intent
import android.content.res.Resources
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.doOnLayout
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
import com.example.playlistmaker.databinding.FragmentDetailsBinding
import com.example.playlistmaker.details.ui.models.PlaylistUiModel
import com.example.playlistmaker.main.ui.MainActivity
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.search.ui.TrackActionListener
import com.example.playlistmaker.search.ui.TrackAdapter
import com.example.playlistmaker.utils.CommonUtils
import com.example.playlistmaker.utils.debounce
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.BottomSheetCallback
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel


class DetailsFragment : Fragment() {
    companion object {
        private const val CLICK_DEBOUNCE_DELAY = 1000L
        private const val MENU_BOTTOM_SHEET_HEIGHT_PERCENT = 0.45f
    }

    private var _binding: FragmentDetailsBinding? = null
    private val binding: FragmentDetailsBinding get() = _binding!!

    private val viewModel: DetailsViewModel by viewModel()

    private val args by navArgs<DetailsFragmentArgs>()

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

        viewModel.onEvent(DetailsUiEvent.LoadPlaylist(args.playlist.id))

        setupAdapter()
        setupListeners()
        setupDebounce()
        setupObservers()
        setupShareBottomSheetHeight()
    }

    private fun setupDebounce() {
        binding.apply {
            onTrackClickDebounce = debounce<Track>(
                CLICK_DEBOUNCE_DELAY,
                viewLifecycleOwner.lifecycleScope,
                false
            ) { track: Track ->
                val action =
                    DetailsFragmentDirections.actionPlaylistDetailsFragmentToPlayerFragment(track)
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
        binding.tracksRecyclerView.adapter = adapter
    }

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.uiState.collect { state ->
                        when (state) {
                            is DetailsUiState.Content -> renderUi(state.playlist)
                            is DetailsUiState.Loading -> {  } //  не забыть установить лоадер
                        }
                    }
                }

                launch {
                    viewModel.uiEffect.collect { effect ->
                        when (effect) {
                            is DetailsUiEffect.Share -> {
                                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                    putExtra(Intent.EXTRA_TEXT, effect.text)
                                    type = "text/plain"
                                }
                                startActivity(
                                    Intent.createChooser(
                                        shareIntent,
                                        "Поделиться через:"
                                    )
                                )
                            }

                            DetailsUiEffect.NavigateBack -> {
                                findNavController().popBackStack()
                            }

                            DetailsUiEffect.ShowEmptyPlaylistToast -> {
                                Toast.makeText(
                                    requireContext(),
                                    "В этом плейлисте нет списка треков, которым можно поделиться",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                }
            }
        }
    }

    private fun renderUi(model: PlaylistUiModel) = binding.apply {
        playlistName.text = model.name
        playlistDescription.text = model.description
        playlistDetails.text = getString(
            R.string.playlist_details,
            model.totalDuration,
            model.trackCount,
            model.trackWordForm
        )

        val corners = CommonUtils.dpToPx(8f, requireContext())
        Glide.with(requireContext())
            .load(model.imageUrl)
            .placeholder(R.drawable.placeholder)
            .centerCrop()
            .transform(RoundedCorners(corners))
            .into(playlistCoverImage)

        adapter?.tracks = model.tracks.toMutableList()
        adapter?.notifyDataSetChanged()
    }

    private fun setupListeners() {
        binding.apply {
            backArrow.setOnClickListener {
                //requireActivity().onBackPressedDispatcher.onBackPressed()
                findNavController().popBackStack()
            }

            shareIcon.setOnClickListener {
                viewModel.onEvent(DetailsUiEvent.ShareClicked)
            }

            menuIcon.setOnClickListener {
                menuBottomSheet.isVisible = true
                setupMenuBottomSheet((viewModel.uiState.value as DetailsUiState.Content).playlist)
            }

            menuShare.setOnClickListener {
                menuBottomSheet.isVisible = false
                viewModel.onEvent(DetailsUiEvent.ShareClicked)
            }

            binding.menuDeletePlaylist.setOnClickListener {
                AlertDialog.Builder(requireContext(), R.style.CustomAlertDialog)
                    .setMessage("Удалить плейлист?")
                    .setNegativeButton("Нет", null)
                    .setPositiveButton("Да") { _, _ ->
                        viewModel.deletePlaylist()
                        findNavController().popBackStack()
                    }
                    .show()
            }

            menuEditInfo.setOnClickListener {
                val action =
                    DetailsFragmentDirections.actionDetailsFragmentToEditPlaylistFragment(args.playlist)
                findNavController().navigate(action)
            }
        }
    }

    private fun showRemoveTrackDialog(track: Track) {
        AlertDialog.Builder(requireContext(), R.style.CustomAlertDialog)
            .setMessage("Хотите удалить трек?")
            .setNegativeButton("Нет") { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton("Да") { dialog, _ ->
                viewModel.removeTrack(track)
                dialog.dismiss()
            }
            .create().show()
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
            val offsetDp = 16
            val offsetPx = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                offsetDp.toFloat(),
                resources.displayMetrics
            ).toInt()

            val peekHeight =
                parentHeight - actionIconsBottom - offsetPx

            val bottomSheet = binding.bottomSheet
            val behavior = BottomSheetBehavior.from(bottomSheet).apply {
                state = BottomSheetBehavior.STATE_COLLAPSED
                isFitToContents = true
            }
            behavior.peekHeight = peekHeight

            behavior.addBottomSheetCallback(object : BottomSheetCallback() {
                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    binding.overlay.isVisible = newState != BottomSheetBehavior.STATE_HIDDEN
                }

                override fun onSlide(bottomSheet: View, slideOffset: Float) {
                    binding.overlay.alpha = slideOffset.coerceAtLeast(0f)
                }
            })
        }
    }

    private fun setupMenuBottomSheet(playlist: PlaylistUiModel) {
        binding.apply {
            val corners = CommonUtils.dpToPx(8f, requireContext())

            context?.let { Glide.with(it).clear(menuImage) }
            if (!playlist.imageUrl.isNullOrEmpty()) {
                Glide.with(requireContext())
                    .load(playlist.imageUrl)
                    .placeholder(R.drawable.placeholder)
                    .centerCrop()
                    .transform(RoundedCorners(corners))
                    .into(menuImage)
            } else {
                menuImage.setImageResource(R.drawable.placeholder)
            }

            menuPlaylistName.text = playlist.name
            menuNumberOfTracks.text = getString(
                R.string.track_count,
                playlist.tracks.size,
                CommonUtils.getTrackWordForm(playlist.tracks.size)
            )

            adjustTopLayoutHeight(menuBottomSheet, MENU_BOTTOM_SHEET_HEIGHT_PERCENT)

            BottomSheetBehavior.from(menuBottomSheet).apply {
                state = BottomSheetBehavior.STATE_COLLAPSED
                isFitToContents = false
                isHideable = true

                addBottomSheetCallback(object : BottomSheetCallback() {
                    override fun onStateChanged(bottomSheet: View, newState: Int) {
                        overlay.isVisible = newState != BottomSheetBehavior.STATE_HIDDEN
                    }

                    override fun onSlide(bottomSheet: View, slideOffset: Float) {
                        overlay.alpha = slideOffset.coerceAtLeast(0f)
                    }
                })
            }
        }
    }

    override fun onResume() {
        super.onResume()
        binding.actionIcons.doOnLayout {
            setupShareBottomSheetHeight()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}