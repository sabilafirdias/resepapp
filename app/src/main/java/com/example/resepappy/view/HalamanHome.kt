package com.example.resepappy.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.resepappy.modeldata.Resep
import com.example.resepappy.uicontroller.route.DestinasiBuatResep
import com.example.resepappy.uicontroller.route.DestinasiCari
import com.example.resepappy.uicontroller.route.DestinasiProfil
import com.example.resepappy.viewmodel.HomeViewModel
import com.example.resepappy.viewmodel.StatusUiHome
import com.example.resepappy.viewmodel.provider.PenyediaViewModel
import com.example.resepappy.uicontroller.route.DestinasiHome
import com.example.resepappy.uicontroller.route.DestinasiLogin
import com.example.resepappy.uicontroller.route.DestinasiRegister
import com.example.resepappy.viewmodel.SessionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HalamanHome(
    idUserLogin: Int,
    navController: NavController,
    onNavigateBack: () -> Unit,
    sessionViewModel: SessionViewModel,
    viewModel: HomeViewModel = viewModel(factory = PenyediaViewModel.Factory)
) {
    val uiState = viewModel.statusUi
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    LaunchedEffect(Unit) {
        viewModel.loadByKategori()
    }

    Scaffold(
        topBar = {
            val currentCategory = if (uiState is StatusUiHome.Success) uiState.selectedCategory else "Semua"

            TopBarWithDropdown(
                title = stringResource(DestinasiHome.titleRes),
                categories = listOf("Semua", "Makanan Berat", "Cemilan", "Minuman"),
                currentSelected = currentCategory,
                onCategorySelected = { category ->
                    viewModel.filterByCategory(category)
                },
                scrollBehavior = scrollBehavior,
                navigateUp = onNavigateBack
            )
        },
        bottomBar = {
            BottomNav(
                onNavigateToDashboard = { navController.navigate(DestinasiHome.route) },
                onNavigateToCari = { navController.navigate(DestinasiCari.route) },
                onNavigateToBuatResep = {
                    navController.navigate("buat_resep/$idUserLogin")},
                onNavigateToProfil = {
                    val currentUserId = sessionViewModel.currentUserId
                    if (currentUserId != null) {
                        navController.navigate("profil/$currentUserId")
                    } else {
                        navController.navigate(DestinasiLogin.route)
                    }
                }
            )
        }
    ) { innerPadding ->
        when (val state = uiState) {
            is StatusUiHome.Loading -> LoadingScreen()
            is StatusUiHome.Error -> ErrorScreen(retryAction = { viewModel.loadByKategori() })
            is StatusUiHome.Success -> {
                val listDitampilkan = when (state.selectedCategory) {
                    "Makanan Berat" -> state.makananBerat
                    "Cemilan" -> state.cemilan
                    "Minuman" -> state.minuman
                    else -> state.allResep // Untuk "Semua"
                }

                HomeContent(
                    resepList = listDitampilkan,
                    idUserLogin = idUserLogin,
                    viewModel = viewModel,
                    navController = navController,
                    onResepClick = { idResep ->
                        navController.navigate("detail_resep/$idResep")
                    },
                    modifier = Modifier.padding(innerPadding)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBarWithDropdown(
    title: String,
    categories: List<String>,
    currentSelected: String,
    onCategorySelected: (String) -> Unit,
    scrollBehavior: TopAppBarScrollBehavior,
    navigateUp: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var selectedCategory by remember { mutableStateOf("Semua") }

    TopAppBar(
        title = {
            Box {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier
                        .clickable { expanded = true }
                        .padding(8.dp) // Memberi sedikit ruang klik agar lebih nyaman
                ) {
                    Text(
                        text = "$title: $currentSelected",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "Pilih kategori"
                    )
                }

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    categories.forEach { category ->
                        val isSelected = (category == currentSelected)
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = category,
                                    fontWeight = if (category == currentSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            onClick = {
                                onCategorySelected(category)
                                expanded = false
                            },
                            // Memberi tanda jika kategori sedang dipilih
                            leadingIcon = if (isSelected) {
                                {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            } else null
                        )
                    }
                }
            }
        },
        scrollBehavior = scrollBehavior
    )
}

@Composable
fun LoadingScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator()
        Spacer(modifier = Modifier.height(16.dp))
        Text("Memuat resep...")
    }
}

@Composable
fun ErrorScreen(retryAction: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Gagal memuat data")
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = retryAction) {
            Text("Coba Lagi")
        }
    }
}

@Composable
fun HomeContent(
    resepList: List<Resep>,
    idUserLogin: Int,
    viewModel: HomeViewModel,
    navController: NavController,
    onResepClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        if (resepList.isEmpty()) {
            item {
                Text(
                    text = "Belum ada resep tersedia",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    textAlign = TextAlign.Center
                )
            }
        } else {
            items(resepList) { resep ->
                KomponenResep(
                    resep = resep,
                    onClick = { onResepClick(resep.id_resep) },
                    onBookmark = {
                        viewModel.toggleBookmark(idUserLogin, resep.id_resep)
                    },
                    onComment = {
                        navController.navigate("komentar/${resep.id_resep}")
                    }
                )
            }
        }
    }
}

@Composable
fun KomponenResep(
    resep: Resep,
    onClick: () -> Unit,
    onBookmark: () -> Unit,
    onComment: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = resep.judul,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Oleh: ${resep.username}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.secondary
            )

            Spacer(modifier = Modifier.height(12.dp))

            if (resep.bahan.isNotEmpty()) {
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp)
                    ) {
                        Text(
                            text = "Bahan:",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        // Tampilkan maksimal 3 bahan pertama
                        resep.bahan.take(3).forEach { bahan ->
                            Text(
                                text = "• ${bahan.nama_bahan} ${bahan.takaran}",
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(vertical = 2.dp)
                            )
                        }

                        // Jika ada lebih dari 3 bahan, tampilkan "..."
                        if (resep.bahan.size > 3) {
                            Text(
                                text = "... dan ${resep.bahan.size - 3} bahan lainnya",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Aksi (Bookmark & Komentar)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBookmark) {
                    Icon(
                        imageVector = if (resep.is_bookmarked) Icons.Filled.Check else Icons.Filled.FavoriteBorder,
                        contentDescription = "Bookmark",
                        tint = if (resep.is_bookmarked) Color.Red else LocalContentColor.current
                    )
                }
                IconButton(onClick = onComment) {
                    BadgedBox(badge = {
                        if(resep.jumlah_komentar > 0) Badge { Text(resep.jumlah_komentar.toString()) }
                    }) {
                        Icon(Icons.Filled.Email, contentDescription = "Komentar")
                    }
                }
            }
        }
    }
}