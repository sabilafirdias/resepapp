package com.example.resepappy.viewmodel.provider

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.resepappy.repositori.AplikasiResep
import com.example.resepappy.viewmodel.AuthViewModel
import com.example.resepappy.viewmodel.CariResepViewModel
import com.example.resepappy.viewmodel.DetailResepViewModel
import com.example.resepappy.viewmodel.EditResepViewModel
import com.example.resepappy.viewmodel.HomeViewModel
import com.example.resepappy.viewmodel.ProfilViewModel
import com.example.resepappy.viewmodel.ResepViewModel
import com.example.resepappy.viewmodel.SessionViewModel

fun CreationExtras.aplikasiResep(): AplikasiResep = (
        this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as AplikasiResep
        )

object PenyediaViewModel {
    val Factory = viewModelFactory {
        initializer {
            SessionViewModel(aplikasiResep())
        }

        initializer {
            AuthViewModel(aplikasiResep().container.repositoryResep)
        }

        initializer {
            HomeViewModel(aplikasiResep().container.repositoryResep)
        }

        initializer {
            ProfilViewModel(aplikasiResep().container.repositoryResep)
        }

        initializer {
            ResepViewModel(aplikasiResep().container.repositoryResep)
        }

        initializer {
            DetailResepViewModel(aplikasiResep().container.repositoryResep)
        }

        initializer {
            EditResepViewModel(aplikasiResep().container.repositoryResep)
        }

        initializer {
            CariResepViewModel(aplikasiResep().container.repositoryResep)
        }

//        initializer {
//            WelcomeViewModel(aplikasiResep())
//        }
    }
}