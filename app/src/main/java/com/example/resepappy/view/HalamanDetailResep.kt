package com.example.resepappy.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.resepappy.R
import com.example.resepappy.modeldata.Resep
import com.example.resepappy.uicontroller.route.DestinasiDetailResep
import com.example.resepappy.uicontroller.route.DestinasiKomentar
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
    onNavigateBack: () -> Unit,
    onEditClick: (Int) -> Unit,
    viewModel: DetailResepViewModel = viewModel(factory = PenyediaViewModel.Factory)
) {
    var showDeleteDialog by remember { mutableStateOf(false) }
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    LaunchedEffect(idResep) {
        viewModel.getDetailResep(idResep)
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Hapus Resep") },
            text = { Text("Apakah Anda yakin ingin menghapus resep ini?") },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteDialog = false
                        viewModel.viewModelScope.launch {
                            if (viewModel.hapusResep(idResep, idUserLogin)) {
                                navController.popBackStack()
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Text("Hapus")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Batal")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = stringResource(DestinasiDetailResep.titleRes),
                canNavigateBack = true,
                scrollBehavior = scrollBehavior,
                navigateUp = onNavigateBack
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
                    onDelete = { showDeleteDialog = true },
                    onCommentClick = {
                        navController.navigate("${DestinasiKomentar.route}/$idResep")
                    },
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
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = resep.judul,
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Oleh: ${resep.username}",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.secondary
                )
                Spacer(Modifier.height(8.dp))
                Surface(
                    shape = MaterialTheme.shapes.small,
                    color = MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier.wrapContentSize()
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Icon(Icons.Default.Star, contentDescription = null, tint = colorResource(id = R.color.pastelbrown), modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = "$jumlahBookmark orang menyimpan ini",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text(
                        text = "Bahan-bahan",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    HorizontalDivider(Modifier.padding(vertical = 12.dp))
                    resep.bahan.forEach { bahan ->
                        Row(Modifier.padding(vertical = 4.dp)) {
                            Text("•", Modifier.width(20.dp))
                            Text(
                                text = "${bahan.nama_bahan}",
                                modifier = Modifier.weight(1f),
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Text(
                                text = bahan.takaran,
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                }
            }
        }

        item {
            Text(
                text = "Langkah Memasak",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(8.dp))
            val langkahList = resep.langkah
                .split("|", "\n")
                .map { it.trim() }
                .filter { it.isNotBlank() }

            if (langkahList.isEmpty()) {
                Text(text = resep.langkah, style = MaterialTheme.typography.bodyLarge)
            } else {
                langkahList.forEachIndexed { index, langkah ->
                    Row(modifier = Modifier.padding(vertical = 8.dp)) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(28.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = "${index + 1}",
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    style = MaterialTheme.typography.labelLarge
                                )
                            }
                        }
                        Spacer(Modifier.width(12.dp))
                        Text(
                            text = langkah,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.align(Alignment.CenterVertically)
                        )
                    }
                }
            }
        }

        if (!resep.catatan.isNullOrBlank()) {
            item {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.tertiaryContainer
                    ),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Row(Modifier.padding(16.dp), verticalAlignment = Alignment.Top) {
                        Icon(Icons.Default.Info, contentDescription = null)
                        Spacer(Modifier.width(12.dp))
                        Column {
                            Text("Tips/Catatan:", fontWeight = FontWeight.ExtraBold)
                            Text(resep.catatan, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }
        }

        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = onCommentClick,
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Text("Lihat Komentar")
                }

                if (resep.id_user == idUserLogin) {
                    OutlinedButton(
                        onClick = onDelete,
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
                        modifier = Modifier.fillMaxWidth(),
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Text("Hapus Resep")
                    }
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