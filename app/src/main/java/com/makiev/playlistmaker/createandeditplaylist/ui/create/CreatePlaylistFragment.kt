package com.makiev.playlistmaker.createandeditplaylist.ui.create

import androidx.navigation.fragment.findNavController
import com.google.firebase.analytics.*
import com.makiev.playlistmaker.createandeditplaylist.ui.base.BaseCreateEditPlaylistFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class CreatePlaylistFragment : BaseCreateEditPlaylistFragment() {

    override val viewModel by viewModel<CreatePlaylistViewModel>()

    override fun onSubmitAction() {
        val analytics = FirebaseAnalytics.getInstance(requireContext())

        viewModel.savePlaylist()
        analytics.logEvent("create_playlist") {
            param("name", viewModel.playlistName.toString())
        }
        findNavController().navigateUp()
    }
}