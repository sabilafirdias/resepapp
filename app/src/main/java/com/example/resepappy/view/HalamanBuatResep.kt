package com.example.resepappy.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.resepappy.R
import com.example.resepappy.modeldata.DetailBahan
import com.example.resepappy.uicontroller.route.DestinasiHome
import com.example.resepappy.viewmodel.ResepViewModel
import com.example.resepappy.viewmodel.StatusUiResep
import com.example.resepappy.viewmodel.provider.PenyediaViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HalamanBuatResep(
    idUser: Int,
    navController: NavController,
    viewModel: ResepViewModel = viewModel(factory = PenyediaViewModel.Factory),
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    val uiState = when (val state = viewModel.statusUi) {
        is StatusUiResep.FormInput -> state.uiState
        else -> null
    }

    LaunchedEffect(viewModel.statusUi) {
        when (viewModel.statusUi) {
            is StatusUiResep.OperationSuccess -> {
                navController.navigate(DestinasiHome.route) {
                    popUpTo(DestinasiHome.route) { inclusive = false }
                }
            }
            is StatusUiResep.OperationError -> {
                // Tampilkan snackbar error (opsional)
            }
            else -> {}
        }
    }

    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.buat_resep_baru)) },
                scrollBehavior = scrollBehavior,
                        navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                }
            )
        },
        floatingActionButton = {
            if (uiState?.isEntryValid == true) {
                FloatingActionButton(
                    onClick = {
                        coroutineScope.launch {
                            viewModel.createResep(idUser)
                        }
                    }
                ) {
                    Icon(Icons.Default.Check, contentDescription = "Simpan")
                }
            }
        }
    ) { innerPadding ->
        if (uiState != null) {
            FormBuatResepLengkap(
                detailResep = uiState.detailResep,
                onValueChange = viewModel::updateFormState,
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxWidth()
            )
        } else {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormBuatResepLengkap(
    detailResep: com.example.resepappy.modeldata.DetailResep,
    onValueChange: (com.example.resepappy.modeldata.DetailResep) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    var namaBahanBaru by remember { mutableStateOf("") }
    var takaranBaru by remember { mutableStateOf("") }

    LazyColumn(
        modifier = modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            OutlinedTextField(
                value = detailResep.judul,
                onValueChange = { onValueChange(detailResep.copy(judul = it)) },
                label = { Text("Judul Resep") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
        }

        item {
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    value = detailResep.kategori,
                    onValueChange = {},
                    label = { Text("Kategori") },
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier.exposedDropdownSize()
                ) {
                    listOf("Makanan Berat", "Cemilan", "Minuman").forEach { kategori ->
                        DropdownMenuItem(
                            text = { Text(kategori) },
                            onClick = {
                                onValueChange(detailResep.copy(kategori = kategori))
                                expanded = false
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }

        item {
            Text("Langkah Pembuatan", style = MaterialTheme.typography.titleMedium)
            OutlinedTextField(
                value = detailResep.langkah,
                onValueChange = { onValueChange(detailResep.copy(langkah = it)) },
                label = { Text("Tulis langkah-langkah secara lengkap") },
                minLines = 5,
                maxLines = 10,
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("Catatan (Opsional)", style = MaterialTheme.typography.labelMedium)
                    Text(
                        text = detailResep.catatan.ifEmpty { "Tambahkan catatan pribadi, tips, atau variasi..." },
                        style = MaterialTheme.typography.bodySmall,
                        color = if (detailResep.catatan.isEmpty()) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface
                    )
                }
            }
            OutlinedTextField(
                value = detailResep.catatan,
                onValueChange = { onValueChange(detailResep.copy(catatan = it)) },
                label = { Text("Catatan") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                enabled = false // Biar tidak bisa diedit langsung di sini
            )
        }

        item {
            Text("Bahan-Bahan", style = MaterialTheme.typography.titleMedium)
        }

        itemsIndexed(detailResep.bahan) { index, bahan ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(bahan.nama_bahan, style = MaterialTheme.typography.bodyMedium)
                    Text(bahan.takaran, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                IconButton(
                    onClick = {
                        val newBahan = detailResep.bahan.toMutableList().apply { removeAt(index) }
                        onValueChange(detailResep.copy(bahan = newBahan))
                    }
                ) {
                    Icon(Icons.Default.Delete, contentDescription = "Hapus bahan")
                }
            }
        }

        item {
            OutlinedTextField(
                value = namaBahanBaru,
                onValueChange = { namaBahanBaru = it },
                label = { Text("Nama Bahan") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = takaranBaru,
                onValueChange = { takaranBaru = it },
                label = { Text("Takaran") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                modifier = Modifier.fillMaxWidth()
            )
            Button(
                onClick = {
                    if (namaBahanBaru.isNotBlank() && takaranBaru.isNotBlank()) {
                        val newBahan = detailResep.bahan + DetailBahan(namaBahanBaru, takaranBaru)
                        onValueChange(detailResep.copy(bahan = newBahan))
                        namaBahanBaru = ""
                        takaranBaru = ""
                    }
                },
                enabled = namaBahanBaru.isNotBlank() && takaranBaru.isNotBlank(),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Tambah Bahan")
            }
        }

        item {
            Text("* Judul dan minimal satu bahan wajib diisi", fontSize = MaterialTheme.typography.labelSmall.fontSize)
        }
    }
}