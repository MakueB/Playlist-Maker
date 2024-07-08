package com.example.playlistmaker.newplaylist.ui

import android.net.Uri
import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import com.example.playlistmaker.R
import com.markodevcic.peko.PermissionRequester

class NewPlaylistFragment : Fragment() {

    companion object {
        fun newInstance() = NewPlaylistFragment()
    }

    private val viewModel: NewPlaylistViewModel by viewModels()

    val requester = PermissionRequester.instance()

    private val photoPickerLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri ->
        uri?.let {

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO: Use the ViewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_new_playlist, container, false)
    }
}