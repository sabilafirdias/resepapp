package com.example.resepappy.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.resepappy.modeldata.Resep
import com.example.resepappy.viewmodel.DetailResepViewModel
import com.example.resepappy.viewmodel.DetailUiState
import com.example.resepappy.viewmodel.provider.PenyediaViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HalamanDetailResep(
    idResep: Int,
    idUserLogin: Int,
    navController: NavController,
    onEditClick: (Int) -> Unit,
    viewModel: DetailResepViewModel = viewModel(factory = PenyediaViewModel.Factory)
) {
    LaunchedEffect(idResep) {
        viewModel.getDetailResep(idResep)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detail Resep") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                }
            )
        },
        floatingActionButton = {
            val state = viewModel.uiState
            if (state is DetailUiState.Success && state.resep.id_user == idUserLogin) {
                FloatingActionButton(onClick = { onEditClick(idResep) }) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit Resep")
                }
            }
        }
    ) { innerPadding ->
        when (val state = viewModel.uiState) {
            is DetailUiState.Loading -> LoadingScreen()
            is DetailUiState.Error -> ErrorScreen(message = state.message, onRetry = { viewModel.getDetailResep(idResep) })
            is DetailUiState.Success -> {
                DetailContent(
                    resep = state.resep,
                    idUserLogin = idUserLogin,
                    jumlahBookmark = viewModel.jumlahBookmark,
                    onEditClick = { onEditClick(idResep) },
                    onDelete = {
                        viewModel.viewModelScope.launch {
                            if (viewModel.hapusResep(idResep)) {
                                navController.popBackStack()
                            }
                        }
                    },
                    onCommentClick = { navController.navigate("komentar/$idResep") },
                    modifier = Modifier.padding(innerPadding)
                )
            }
        }
    }
}

@Composable
fun DetailContent(
    resep: Resep,
    idUserLogin: Int,
    jumlahBookmark: Int,
    onEditClick: () -> Unit,
    onDelete: () -> Unit,
    onCommentClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(text = resep.judul, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            Text(text = "Oleh: ${resep.username}", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Favorite, contentDescription = null, tint = Color.Red, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(4.dp))
                Text(text = "$jumlahBookmark orang menyimpan resep ini", style = MaterialTheme.typography.labelSmall)
            }
            HorizontalDivider(Modifier.padding(vertical = 8.dp))
        }

        item {
            Text(text = "Bahan-bahan", style = MaterialTheme.typography.titleLarge)
            resep.bahan.forEach { bahan ->
                Text("• ${bahan.nama_bahan} (${bahan.takaran})")
            }
        }

        item {
            Text(text = "Langkah Memasak", style = MaterialTheme.typography.titleLarge)
            Text(text = resep.langkah)
        }

        if (resep.catatan != null) {
            item {
                Card(colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF9C4))) {
                    Column(Modifier.padding(12.dp)) {
                        Text("Catatan:", fontWeight = FontWeight.Bold)
                        Text(resep.catatan)
                    }
                }
            }
        }

        item {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = onCommentClick,
                    modifier = Modifier.weight(1f)
                ) {
                    Spacer(Modifier.width(8.dp))
                    Text("Komentar")
                }

                if (resep.id_user == idUserLogin) {
                    Button(
                        onClick = onEditClick,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFA000)),
                        modifier = Modifier.weight(1f)
                    ) {
                        Spacer(Modifier.width(8.dp))
                        Text("Edit")
                    }
                }
            }

                if (resep.id_user == idUserLogin) {
                    Button(
                        onClick = onDelete,
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Hapus Resep", color = Color.White)
                    }
                }
            }
        }
    }

@Composable
fun ErrorScreen(message: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Terjadi Kesalahan: $message", textAlign = TextAlign.Center)
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onRetry) {
            Text("Coba Lagi")
        }
    }
}