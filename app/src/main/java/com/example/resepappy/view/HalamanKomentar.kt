package com.example.resepappy.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.resepappy.modeldata.Komentar
import com.example.resepappy.viewmodel.KomentarViewModel
import com.example.resepappy.viewmodel.provider.PenyediaViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HalamanKomentar(
    idResep: Int,
    idUserLogin: Int,
    onNavigateBack: () -> Unit,
    viewModel: KomentarViewModel = viewModel(factory = PenyediaViewModel.Factory)
) {
    var teksKomentar by remember { mutableStateOf("") }

    LaunchedEffect(idResep) {
        viewModel.getKomentar(idResep)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Komentar", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Kembali"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        bottomBar = {
            // Input Komentar yang menempel di bawah
            Surface(
                tonalElevation = 8.dp,
                shadowElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier
                        .padding(12.dp)
                        .fillMaxWidth()
                        .navigationBarsPadding() // Agar tidak tertutup navigasi sistem
                        .imePadding(), // Agar naik saat keyboard muncul
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = teksKomentar,
                        onValueChange = { teksKomentar = it },
                        placeholder = { Text("Tulis komentar Anda...") },
                        modifier = Modifier.weight(1f),
                        shape = MaterialTheme.shapes.medium,
                        maxLines = 4
                    )
                    Spacer(Modifier.width(8.dp))
                    FilledIconButton(
                        onClick = {
                            viewModel.kirimKomentar(idResep, idUserLogin, teksKomentar) {
                                teksKomentar = "" // Reset teks setelah terkirim
                            }
                        },
                        enabled = teksKomentar.isNotBlank(),
                        modifier = Modifier.size(50.dp)
                    ) {
                        Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Kirim")
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
            if (viewModel.isLoading && viewModel.listKomentar.isEmpty()) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (viewModel.listKomentar.isEmpty()) {
                Text(
                    text = "Belum ada komentar. Jadilah yang pertama!",
                    modifier = Modifier.align(Alignment.Center),
                    color = Color.Gray
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(viewModel.listKomentar) { item ->
                        ItemKomentar(komentar = item)
                    }
                }
            }
        }
    }
}

@Composable
fun ItemKomentar(komentar: Komentar) {
    Row(modifier = Modifier.fillMaxWidth()) {
        // Avatar Inisial Sederhana
        Surface(
            shape = CircleShape,
            color = MaterialTheme.colorScheme.secondaryContainer,
            modifier = Modifier.size(40.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = komentar.username.take(1).uppercase(),
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
        }

        Spacer(Modifier.width(12.dp))

        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = komentar.username,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = komentar.created_at, // Bisa diformat jika perlu
                    fontSize = 10.sp,
                    color = Color.Gray
                )
            }
            Spacer(Modifier.height(4.dp))
            Text(
                text = komentar.isi_komentar,
                fontSize = 15.sp,
                lineHeight = 20.sp
            )
        }
    }
}