package com.makiev.playlistmaker.details.ui

sealed class DetailsUiEffect {
    data class Share(val text: String) : DetailsUiEffect()
    object ShowEmptyPlaylistToast : DetailsUiEffect()
    object NavigateBack : DetailsUiEffect()
}