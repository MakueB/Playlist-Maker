package com.example.playlistmaker.newplaylist.ui

import android.content.ContentResolver
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentNewPlaylistBinding
import com.markodevcic.peko.PermissionRequester
import java.io.File
import java.io.FileOutputStream

class NewPlaylistFragment : Fragment() {

    companion object {
        fun newInstance() = NewPlaylistFragment()
    }

    private val viewModel: NewPlaylistViewModel by viewModels()

    private var _binding: FragmentNewPlaylistBinding? = null
    private val binding: FragmentNewPlaylistBinding get() = _binding!!

    private val requester = PermissionRequester.instance()
    private lateinit var contentResolver: ContentResolver


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNewPlaylistBinding.inflate(inflater, container, false)

        contentResolver = requireContext().contentResolver
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val photoPicker = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri: Uri? ->
            binding.playlistImage.setImageURI(uri)
            saveImageToPrivateStorage(uri)
            viewModel.updateImageUri(uri)
        }

        binding.playlistImage.setOnClickListener {
            photoPicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }
    }


    private fun saveImageToPrivateStorage(uri: Uri) {
        val filePath = File(requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES),
            getString(R.string.app_name))
        if (!filePath.exists()) {
            filePath.mkdirs()
        }
        val file = File(filePath, "${viewModel.playlistName.value}_cover.jpg")
        val inputStream = contentResolver.openInputStream(uri)
        val outputStream = FileOutputStream(file)
        BitmapFactory
            .decodeFile(inputStream)
            .compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
    }
}