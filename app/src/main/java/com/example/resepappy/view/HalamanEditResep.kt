package com.example.resepappy.view

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.resepappy.viewmodel.EditResepViewModel
import com.example.resepappy.viewmodel.provider.PenyediaViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HalamanEditResep(
    idResep: Int,
    idUser: Int,
    navController: NavController,
    viewModel: EditResepViewModel = viewModel(factory = PenyediaViewModel.Factory)
) {
    val context = LocalContext.current

    // Load data lama hanya sekali saat halaman dibuka
    LaunchedEffect(idResep) {
        viewModel.loadResepData(idResep)
    }

    // Navigasi balik jika sukses
    LaunchedEffect(viewModel.isUpdateSuccess) {
        if (viewModel.isUpdateSuccess) {
            Toast.makeText(context, "Resep berhasil diperbarui!", Toast.LENGTH_SHORT).show()
            navController.popBackStack() // Kembali ke detail
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Resep") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Batal")
                    }
                }
            )
        }
    ) { innerPadding ->
        if (viewModel.isLoading && viewModel.judul.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
                    .fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = viewModel.judul,
                    onValueChange = { viewModel.judul = it },
                    label = { Text("Judul Resep") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = viewModel.kategori,
                    onValueChange = { viewModel.kategori = it },
                    label = { Text("Kategori") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = viewModel.langkah,
                    onValueChange = { viewModel.langkah = it },
                    label = { Text("Langkah-langkah") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 5
                )

                OutlinedTextField(
                    value = viewModel.catatan,
                    onValueChange = { viewModel.catatan = it },
                    label = { Text("Catatan (Opsional)") },
                    modifier = Modifier.fillMaxWidth()
                )

                if (viewModel.errorMessage != null) {
                    Text(viewModel.errorMessage!!, color = MaterialTheme.colorScheme.error)
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    // Tombol Batal
                    OutlinedButton(
                        onClick = { navController.popBackStack() },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Batal")
                    }

                    Button(
                        onClick = { viewModel.updateResep(idResep, idUser) },
                        modifier = Modifier.weight(1f),
                        enabled = !viewModel.isLoading
                    ) {
                        if (viewModel.isLoading) CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White)
                        else Text("Simpan Perubahan")
                    }
                }
            }
        }
    }
}