package com.example.resepappy.view


import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.resepappy.R
import com.example.resepappy.modeldata.Resep
import com.example.resepappy.uicontroller.route.DestinasiCari
import com.example.resepappy.viewmodel.HomeViewModel
import com.example.resepappy.viewmodel.StatusUiHome
import com.example.resepappy.viewmodel.provider.PenyediaViewModel
import com.example.resepappy.uicontroller.route.DestinasiHome
import com.example.resepappy.uicontroller.route.DestinasiLogin
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

    LaunchedEffect(idUserLogin) {
        viewModel.loadByKategori(idUserLogin)
    }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
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
                    navController.navigate("buat_resep/$idUserLogin")
                },
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
        Box(modifier = Modifier.padding(innerPadding)) {
            when (val state = uiState) {
                is StatusUiHome.Loading -> LoadingScreen()
                is StatusUiHome.Error -> ErrorScreen(retryAction = { viewModel.loadByKategori(idUserLogin) })
                is StatusUiHome.Success -> {
                    val listDitampilkan = remember(state) {
                        when (state.selectedCategory) {
                            "Makanan Berat" -> state.makananBerat
                            "Cemilan" -> state.cemilan
                            "Minuman" -> state.minuman
                            else -> state.allResep
                        }
                    }

                    HomeContent(
                        resepList = listDitampilkan,
                        idUserLogin = idUserLogin,
                        viewModel = viewModel,
                        navController = navController,
                        onResepClick = { idResep ->
                            navController.navigate("detail_resep/$idResep")
                        }
                    )
                }
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

    CenterAlignedTopAppBar(
        title = {
            Box {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier
                        .clickable { expanded = true }
                        .padding(8.dp)
                ) {
                    Text(
                        text = "$title: $currentSelected",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                }

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    categories.forEach { category ->
                        DropdownMenuItem(
                            text = { Text(category) },
                            onClick = {
                                onCategorySelected(category)
                                expanded = false
                            },
                            trailingIcon = if (category == currentSelected) {
                                { Icon(Icons.Default.Check, contentDescription = null) }
                            } else null
                        )
                    }
                }
            }
        },
        navigationIcon = {
            IconButton(onClick = navigateUp) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
            }
        },
        scrollBehavior = scrollBehavior
    )
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
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(16.dp)
    ) {
        if (resepList.isEmpty()) {
            item {
                Text(
                    text = "Belum ada resep tersedia",
                    modifier = Modifier.fillMaxWidth().padding(32.dp),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        } else {
            items(
                items = resepList,
                key = { it.id_resep },
                contentType = { "resep_card" } // Optimasi Recycle Memori
            ) { resep ->
                KomponenResep(
                    resep = resep,
                    onClick = { onResepClick(resep.id_resep) },
                    onBookmark = { viewModel.toggleBookmark(idUserLogin, resep.id_resep) }
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
    modifier: Modifier = Modifier
) {
    val bahanDisplay = remember(resep.bahan) {
        if (resep.bahan.isEmpty()) ""
        else resep.bahan.take(2).joinToString("\n") { "• ${it.nama_bahan} (${it.takaran})" }
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = resep.judul,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold
            )

            Text(
                text = "Oleh: ${resep.username}",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary
            )

            if (bahanDisplay.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Column(modifier = Modifier.padding(8.dp).fillMaxWidth()) {
                        Text(text = "Bahan utama:", style = MaterialTheme.typography.labelSmall)
                        Text(text = bahanDisplay, style = MaterialTheme.typography.bodySmall)
                        if (resep.bahan.size > 2) {
                            Text(
                                text = "+ ${resep.bahan.size - 2} bahan lainnya",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        }
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(onClick = onBookmark) {
                    Icon(
                        imageVector = if (resep.is_bookmarked) Icons.Filled.Star else Icons.Filled.Star,
                        contentDescription = "Bookmark",
                        tint = if (resep.is_bookmarked) colorResource(R.color.pastelbrown) else MaterialTheme.colorScheme.outline
                    )
                }
            }
        }
    }
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