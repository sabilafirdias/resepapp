package com.example.resepappy.view
//
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.foundation.lazy.itemsIndexed
//import androidx.compose.material3.*
//import androidx.compose.runtime.*
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.unit.dp
//import androidx.lifecycle.viewmodel.compose.viewModel
//import androidx.navigation.NavController
//import com.example.resepappy.modeldata.Bahan
//import com.example.resepappy.modeldata.Resep
//import com.example.resepappy.uicontroller.route.DestinasiListResep
//import com.example.resepappy.viewmodel.ResepViewModel
//import kotlinx.serialization.json.buildJsonObject
//import kotlinx.serialization.json.put
//
//@Composable
//fun HalamanEditResep(
//    navController: NavController,
//    idResep: Int,
//    idUserLogin: Int,
//    viewModel: ResepViewModel = viewModel { ResepViewModel() }
//) {
//    var currentResep by remember { mutableStateOf<Resep?>(null) }
//    var judul by remember { mutableStateOf("") }
//    var langkah by remember { mutableStateOf("") }
//    var catatan by remember { mutableStateOf("") }
//    var kategori by remember { mutableStateOf("Makanan Berat") }
//    var bahanList by remember { mutableStateOf<MutableList<Bahan>>(mutableListOf()) }
//    var isValid by remember { mutableStateOf(false) }
//    var isLoading by remember { mutableStateOf(true) }
//
//    // Load data resep saat pertama kali masuk
//    LaunchedEffect(idResep) {
//        viewModel.getResepById(idResep)
//        viewModel.detailResep.collect { resep ->
//            if (resep != null) {
//                currentResep = resep
//                judul = resep.judul
//                langkah = resep.langkah
//                catatan = resep.catatan ?: ""
//                kategori = resep.kategori
//                bahanList = resep.bahan.toMutableList()
//                if (bahanList.isEmpty()) bahanList.add(Bahan())
//                isLoading = false
//            }
//        }
//    }
//
//    // Validasi form
//    LaunchedEffect(judul, langkah, bahanList) {
//        isValid = judul.isNotBlank() && langkah.isNotBlank() &&
//                bahanList.any { it.nama_bahan.isNotBlank() }
//    }
//
//    if (isLoading) {
//        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
//            CircularProgressIndicator()
//        }
//        return
//    }
//
//    Scaffold(
//        topBar = { TopAppBarResep(title = "Edit Resep") },
//        floatingActionButton = {
//            if (isValid) {
//                FloatingActionButton(onClick = {
//                    val updatedResep = Resep(
//                        id_resep = idResep,
//                        id_user = idUserLogin,
//                        judul = judul,
//                        langkah = langkah,
//                        catatan = if (catatan.isBlank()) null else catatan,
//                        kategori = kategori
//                    )
//                    viewModel.updateResep(updatedResep, bahanList.filter { it.nama_bahan.isNotBlank() })
//                    navController.popBackStack()
//                }) {
//                    Icon(Icons.Default.Check, "Simpan")
//                }
//            }
//        }
//    ) { padding ->
//        LazyColumn(modifier = Modifier.padding(padding)) {
//            item {
//                OutlinedTextField(
//                    value = judul,
//                    onValueChange = { judul = it },
//                    label = { Text("Judul Resep") },
//                    modifier = Modifier.padding(16.dp)
//                )
//            }
//
//            item {
//                DropdownMenuKategori(kategori) { kategori = it }
//            }
//
//            item {
//                Text("Bahan", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(16.dp))
//            }
//            itemsIndexed(bahanList) { index, bahan ->
//                Row(modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)) {
//                    OutlinedTextField(
//                        value = bahan.nama_bahan,
//                        onValueChange = { nama ->
//                            bahanList[index] = bahan.copy(nama_bahan = nama)
//                        },
//                        placeholder = { Text("Nama bahan") },
//                        modifier = Modifier.weight(1f)
//                    )
//                    Spacer(modifier = Modifier.width(8.dp))
//                    OutlinedTextField(
//                        value = bahan.takaran,
//                        onValueChange = { takaran ->
//                            bahanList[index] = bahan.copy(takaran = takaran)
//                        },
//                        placeholder = { Text("Takaran") },
//                        modifier = Modifier.width(120.dp)
//                    )
//                }
//            }
//
//            item {
//                Row(modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)) {
//                    Button(
//                        onClick = { bahanList.add(Bahan()) },
//                        modifier = Modifier.weight(1f)
//                    ) {
//                        Text("+ Tambah Bahan")
//                    }
//                    Spacer(modifier = Modifier.width(8.dp))
//                    if (bahanList.size > 1) {
//                        Button(
//                            onClick = {
//                                if (bahanList.isNotEmpty()) {
//                                    bahanList.removeAt(bahanList.size - 1)
//                                }
//                            },
//                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
//                            modifier = Modifier.weight(1f)
//                        ) {
//                            Text("Hapus Terakhir", color = MaterialTheme.colorScheme.onError)
//                        }
//                    }
//                }
//            }
//
//            item {
//                OutlinedTextField(
//                    value = langkah,
//                    onValueChange = { langkah = it },
//                    label = { Text("Langkah-langkah (pisahkan dengan enter)") },
//                    placeholder = { Text("1. Potong bawang\n2. Tumis...") },
//                    modifier = Modifier
//                        .padding(16.dp)
//                        .fillMaxWidth(),
//                    maxLines = 10
//                )
//            }
//
//            item {
//                OutlinedTextField(
//                    value = catatan,
//                    onValueChange = { catatan = it },
//                    label = { Text("Catatan (Opsional)") },
//                    modifier = Modifier.padding(16.dp)
//                )
//            }
//        }
//    }
//}
//
//// Gunakan ulang dari HalamanBuatResep.kt
//@Composable
//fun DropdownMenuKategori(
//    selected: String,
//    onSelected: (String) -> Unit
//) {
//    var expanded by remember { mutableStateOf(false) }
//    Box(modifier = Modifier.padding(16.dp)) {
//        OutlinedTextField(
//            value = selected,
//            onValueChange = {},
//            readOnly = true,
//            label = { Text("Kategori") },
//            trailingIcon = {
//                IconButton(onClick = { expanded = !expanded }) {
//                    Icon(Icons.Default.ArrowDropDown, "Kategori")
//                }
//            },
//            modifier = Modifier.menuAnchor()
//        )
//        DropdownMenu(
//            expanded = expanded,
//            onDismissRequest = { expanded = false }
//        ) {
//            listOf("Makanan Berat", "Cemilan", "Minuman").forEach { kategori ->
//                DropdownMenuItem(
//                    text = { Text(kategori) },
//                    onClick = {
//                        onSelected(kategori)
//                        expanded = false
//                    }
//                )
//            }
//        }
//    }
//}