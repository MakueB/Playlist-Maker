package com.makiev.playlistmaker.createandeditplaylist.ui.edit

import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.makiev.playlistmaker.R
import com.makiev.playlistmaker.createandeditplaylist.ui.base.BaseCreateEditPlaylistFragment
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf


class EditPlaylistFragment : BaseCreateEditPlaylistFragment() {

    private val args: EditPlaylistFragmentArgs by navArgs()

    override val viewModel: EditPlaylistViewModel by viewModel {
        parametersOf(args.playlist)
    }

    override fun setupInitialUi() {
        super.setupInitialUi()

        viewModel.playlistName.value?.let {
            binding.playlistNameEditText.setText(it)
        }

        viewModel.playlistDescription.value?.let {
            binding.playlistDescriptionEditText.setText(it)
        }

        binding.createButton.text = getString(R.string.save)

        binding.toolbar.title = getString(R.string.edit_playlist)
    }

    override fun onSubmitAction() {
        viewModel.savePlaylist()
        findNavController().navigateUp()
    }

    override fun handleBackPressed() {
        findNavController().navigateUp()
    }
}
