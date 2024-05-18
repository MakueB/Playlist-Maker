package com.example.playlistmaker.library.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentFavoritesBinding
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf


class FavoritesFragment : Fragment() {
    companion object {
        private const val TRACK_ID = "track_id"
        @JvmStatic
        fun newInstance(trackId: Int) =
            FavoritesFragment().apply {
                arguments = Bundle().apply {
                    putInt(TRACK_ID, trackId)
                }
            }
    }

    private lateinit var binding: FragmentFavoritesBinding

    private val favoritesViewModel: FavoritesViewModel by viewModel {
        parametersOf(requireArguments().getInt(TRACK_ID))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFavoritesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        favoritesViewModel.trackLiveData.observe(viewLifecycleOwner) {
            //в следующем спринте?
        }
    }


}