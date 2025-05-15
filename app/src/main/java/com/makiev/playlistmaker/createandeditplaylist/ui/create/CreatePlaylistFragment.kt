package com.makiev.playlistmaker.createandeditplaylist.ui.create

import androidx.navigation.fragment.findNavController
import com.makiev.playlistmaker.createandeditplaylist.ui.base.BaseCreateEditPlaylistFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class CreatePlaylistFragment : BaseCreateEditPlaylistFragment() {

    override val viewModel by viewModel<CreatePlaylistViewModel>()

    override fun onSubmitAction() {
        viewModel.savePlaylist()
        findNavController().navigateUp()
    }
}