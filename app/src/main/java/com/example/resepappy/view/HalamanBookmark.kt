package com.example.resepappy.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.resepappy.R
import com.example.resepappy.uicontroller.route.DestinasiBookmark
import com.example.resepappy.viewmodel.BookmarkViewModel
import com.example.resepappy.viewmodel.StatusUiBookmark
import com.example.resepappy.viewmodel.provider.PenyediaViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HalamanBookmark(
    idUserLogin: Int,
    navController: NavController,
    viewModel: BookmarkViewModel = viewModel(factory = PenyediaViewModel.Factory)
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    LaunchedEffect(Unit) {
        viewModel.loadBookmarks(idUserLogin)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(DestinasiBookmark.titleRes)) },
                scrollBehavior = scrollBehavior,
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
            when (val state = viewModel.statusUi) {
                is StatusUiBookmark.Loading -> CircularProgressIndicator(Modifier.align(Alignment.Center))
                is StatusUiBookmark.Error -> Text("Gagal memuat bookmark", Modifier.align(Alignment.Center))
                is StatusUiBookmark.Success -> {
                    if (state.resepList.isEmpty()) {
                        Text("Belum ada resep yang disimpan", Modifier.align(Alignment.Center))
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            contentPadding = PaddingValues(16.dp)
                        ) {
                            items(state.resepList) { resep ->
                                KomponenResep(
                                    resep = resep,
                                    onClick = { navController.navigate("detail_resep/${resep.id_resep}") },
                                    onBookmark = { viewModel.toggleBookmark(idUserLogin, resep.id_resep) },
                                    onComment = { navController.navigate("komentar/${resep.id_resep}") }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}