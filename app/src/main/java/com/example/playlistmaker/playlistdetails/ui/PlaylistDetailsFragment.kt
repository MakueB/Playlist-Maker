package com.example.playlistmaker.playlistdetails.ui

import android.content.res.Resources
import android.os.Bundle
import android.util.TypedValue
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.updateLayoutParams
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlaylistDetailsBinding
import com.example.playlistmaker.newplaylist.domain.models.Playlist
import com.example.playlistmaker.utils.CommonUtils
import com.google.android.material.bottomsheet.BottomSheetBehavior


class PlaylistDetailsFragment : Fragment() {
    private var _binding: FragmentPlaylistDetailsBinding? = null
    private val binding: FragmentPlaylistDetailsBinding get() = _binding!!

    private val args by navArgs<PlaylistDetailsFragmentArgs>()
    private var playlist: Playlist? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlaylistDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        playlist = args.playlist
        binding.apply {
            val tracksNumber = playlist?.trackList?.size ?: 0
            val totalDurationInSeconds = playlist?.trackList?.sumOf { it.trackDuration.toDurationInSeconds() } ?: 0
            val cornersInPx = CommonUtils.dpToPx(8f, requireContext())
            playlistName.text = playlist?.name
            playlistDescription.text = playlist?.description
            playlistDetails.text = "$tracksNumber ${CommonUtils.getTrackWordForm(tracksNumber)} • ${totalDurationInSeconds.toFormattedDuration()}"

            if (playlist?.imageUrl.isNullOrEmpty()) {
                playlistCoverImage.setImageResource(R.drawable.placeholder)
            } else {
                context?.let { context ->
                    Glide.with(context)
                        .load(playlist?.imageUrl)
                        .placeholder(R.drawable.placeholder)
                        .centerCrop()
                        .transform(RoundedCorners(cornersInPx))
                        .into(playlistCoverImage)
                }
            }

            bottomSheet.post {
                adjustTopLayoutHeight()
            }
        }
    }

    private fun adjustTopLayoutHeight() {
        binding.apply {
            // Находим координаты нижнего края LinearLayout (actionIcons)
            val screenHeight = Resources.getSystem().displayMetrics.heightPixels
            val bottomOfShareIcon = binding.actionIcons.bottom

            // Конвертируем отступ в пиксели
            val extraMarginPx = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                64f, // Дополнительный отступ
                resources.displayMetrics
            ).toInt()

            // Минимальная высота (в dp)
            val minHeightPx = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                100f, // Минимальная высота
                resources.displayMetrics
            ).toInt()

            val calculatedHeight = screenHeight - bottomOfShareIcon - extraMarginPx
            val finalHeight = calculatedHeight.coerceAtLeast(minHeightPx) // Учитываем минимальную высоту

            // Устанавливаем BottomSheetBehavior
            val behavior = BottomSheetBehavior.from(bottomSheet)
            behavior.peekHeight = if (calculatedHeight < finalHeight) finalHeight else calculatedHeight
            behavior.state = BottomSheetBehavior.STATE_COLLAPSED // Устанавливаем состояние
        }
    }
    // Конвертирует строку формата "ММ:СС" или "ЧЧ:ММ:СС" в секунды
    fun String.toDurationInSeconds(): Int {
        val parts = this.split(":").map { it.toIntOrNull() ?: 0 }
        return when (parts.size) {
            2 -> parts[0] * 60 + parts[1] // ММ:СС
            3 -> parts[0] * 3600 + parts[1] * 60 + parts[2] // ЧЧ:ММ:СС
            else -> 0
        }
    }

    // Конвертирует секунды в строку формата "ЧЧ:ММ:СС"
    fun Int.toFormattedDuration(): String {
        val hours = this / 3600
        val minutes = (this % 3600) / 60
        val seconds = this % 60
        return if (hours > 0) {
            String.format("%d:%02d:%02d", hours, minutes, seconds)
        } else {
            String.format("%02d:%02d", minutes, seconds)
        }
    }
}