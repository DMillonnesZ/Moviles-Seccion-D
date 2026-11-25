package com.millones.murocampus.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.millones.murocampus.data.PostRepositoryRemote
import com.millones.murocampus.data.remote.RetrofitClient

class PostsViewModelFactory : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return PostsViewModel(PostRepositoryRemote(RetrofitClient.api)) as T
    }
}