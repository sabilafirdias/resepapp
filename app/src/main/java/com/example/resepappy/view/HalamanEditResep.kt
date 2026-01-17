package com.example.resepappy.view

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.resepappy.modeldata.Bahan
import com.example.resepappy.uicontroller.route.DestinasiEditResep
import com.example.resepappy.viewmodel.EditResepViewModel
import com.example.resepappy.viewmodel.provider.PenyediaViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HalamanEditResep(
    idResep: Int,
    idUser: Int,
    navController: NavController,
    onNavigateBack: () -> Unit,
    viewModel: EditResepViewModel = viewModel(factory = PenyediaViewModel.Factory)
) {
    var expanded by remember { mutableStateOf(false) }
    var namaBahanBaru by remember { mutableStateOf("") }
    var takaranBaru by remember { mutableStateOf("") }
    var stepBaru by remember { mutableStateOf("") }
    var isEditingCatatan by remember { mutableStateOf(false) }
    val context = LocalContext.current

    LaunchedEffect(idResep) {
        viewModel.loadResepData(idResep)
    }

    LaunchedEffect(viewModel.isUpdateSuccess) {
        if (viewModel.isUpdateSuccess) {
            Toast.makeText(context, "Resep berhasil diperbarui!", Toast.LENGTH_SHORT).show()
            navController.popBackStack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(DestinasiEditResep.titleRes)) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                }
            )
        }
    ) { innerPadding ->
        if (viewModel.isLoading && viewModel.judul.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
            ) {
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        OutlinedTextField(
                            value = viewModel.judul,
                            onValueChange = { viewModel.judul = it },
                            label = { Text("Judul Resep") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    item {
                        ExposedDropdownMenuBox(
                            expanded = expanded,
                            onExpandedChange = { expanded = !expanded }
                        ) {
                            OutlinedTextField(
                                value = viewModel.kategori,
                                onValueChange = {},
                                label = { Text("Kategori") },
                                readOnly = true,
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                                modifier = Modifier.menuAnchor().fillMaxWidth()
                            )
                            ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                                listOf("Makanan Berat", "Cemilan", "Minuman").forEach { kat ->
                                    DropdownMenuItem(
                                        text = { Text(kat) },
                                        onClick = {
                                            viewModel.kategori = kat
                                            expanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    item { Text("Bahan-Bahan", style = MaterialTheme.typography.titleMedium) }

                    itemsIndexed(viewModel.bahanList) { index, bahan ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = bahan.nama_bahan,
                                onValueChange = { newValue ->
                                    viewModel.bahanList[index] = bahan.copy(nama_bahan = newValue)
                                },
                                modifier = Modifier.weight(1f),
                                label = { Text("Nama Bahan") }
                            )
                            OutlinedTextField(
                                value = bahan.takaran,
                                onValueChange = { newValue ->
                                    viewModel.bahanList[index] = bahan.copy(takaran = newValue)
                                },
                                modifier = Modifier.weight(0.6f),
                                label = { Text("Takaran") }
                            )
                            IconButton(onClick = { viewModel.bahanList.removeAt(index) }) {
                                Icon(Icons.Default.Delete, contentDescription = "Hapus")
                            }
                        }
                    }

                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                        ) {
                            Column(modifier = Modifier.padding(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text("Tambah Bahan Baru", style = MaterialTheme.typography.labelLarge)
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
                                    modifier = Modifier.fillMaxWidth()
                                )
                                Button(
                                    onClick = {
                                        viewModel.bahanList.add(Bahan(nama_bahan = namaBahanBaru, takaran = takaranBaru))
                                        namaBahanBaru = ""; takaranBaru = ""
                                    },
                                    enabled = namaBahanBaru.isNotBlank() && takaranBaru.isNotBlank(),
                                    modifier = Modifier.align(Alignment.End)
                                ) { Text("Tambah") }
                            }
                        }
                    }

                    item { Text("Langkah Pembuatan", style = MaterialTheme.typography.titleMedium) }

                    val langkahList = if (viewModel.langkah.isBlank()) emptyList() else viewModel.langkah.split("\n")

                    itemsIndexed(langkahList) { index, langkah ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = langkah,
                                onValueChange = { newValue ->
                                    val mutableLangkah = langkahList.toMutableList()
                                    mutableLangkah[index] = newValue
                                    viewModel.langkah = mutableLangkah.joinToString("\n")
                                },
                                modifier = Modifier.weight(1f),
                                label = { Text("Langkah ${index + 1}") }
                            )
                            IconButton(onClick = {
                                val mutableLangkah = langkahList.toMutableList()
                                mutableLangkah.removeAt(index)
                                viewModel.langkah = mutableLangkah.joinToString("\n")
                            }) {
                                Icon(Icons.Default.Delete, contentDescription = "Hapus")
                            }
                        }
                    }

                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = stepBaru,
                                onValueChange = { stepBaru = it },
                                label = { Text("Deskripsi Langkah Baru") },
                                modifier = Modifier.weight(1f)
                            )
                            Button(
                                onClick = {
                                    viewModel.langkah = if (viewModel.langkah.isEmpty()) stepBaru else "${viewModel.langkah}\n$stepBaru"
                                    stepBaru = ""
                                },
                                enabled = stepBaru.isNotBlank()
                            ) { Text("Tambah") }
                        }
                    }

                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("Catatan (Opsional)", style = MaterialTheme.typography.titleSmall)
                                    IconButton(onClick = { isEditingCatatan = !isEditingCatatan }) {
                                        Icon(Icons.Default.Edit, contentDescription = "Edit")
                                    }
                                }
                                if (isEditingCatatan) {
                                    OutlinedTextField(
                                        value = viewModel.catatan,
                                        onValueChange = { viewModel.catatan = it },
                                        placeholder = { Text("Tambahkan tips...") },
                                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                                    )
                                } else {
                                    Text(
                                        text = viewModel.catatan.ifBlank { "Belum ada catatan." },
                                        style = MaterialTheme.typography.bodyMedium,
                                        modifier = Modifier.padding(top = 4.dp)
                                    )
                                }
                            }
                        }
                    }

                    item { Spacer(modifier = Modifier.height(16.dp)) }
                }

                Column(modifier = Modifier.padding(16.dp)) {
                    if (viewModel.errorMessage != null) {
                        Text(viewModel.errorMessage!!, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(bottom = 8.dp))
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(onClick = { navController.popBackStack() }, modifier = Modifier.weight(1f)) { Text("Batal") }
                        Button(
                            onClick = { viewModel.updateResep(idResep, idUser) },
                            modifier = Modifier.weight(1f),
                            enabled = !viewModel.isLoading && viewModel.judul.isNotBlank() && viewModel.bahanList.isNotEmpty()
                        ) {
                            if (viewModel.isLoading) CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White)
                            else Text("Simpan")
                        }
                    }
                }
            }
        }
    }
}