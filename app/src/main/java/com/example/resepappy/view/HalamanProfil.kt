package com.example.resepappy.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.resepappy.modeldata.User
import com.example.resepappy.modeldata.Resep
import com.example.resepappy.R
import com.example.resepappy.uicontroller.route.DestinasiCari
import com.example.resepappy.uicontroller.route.DestinasiHome
import com.example.resepappy.uicontroller.route.DestinasiLogin
import com.example.resepappy.uicontroller.route.DestinasiProfil
import com.example.resepappy.uicontroller.route.DestinasiWelcome
import com.example.resepappy.viewmodel.ProfilViewModel
import com.example.resepappy.viewmodel.SessionViewModel
import com.example.resepappy.viewmodel.StatusUiProfil
import com.example.resepappy.viewmodel.provider.PenyediaViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HalamanProfil(
    idUserLogin: Int,
    navController: NavController,
    onLogout: () -> Unit,
    sessionViewModel: SessionViewModel,
    viewModel: ProfilViewModel = viewModel(factory = PenyediaViewModel.Factory)
) {
    var showDeleteDialog by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val currentUserId = sessionViewModel.currentUserId ?: idUserLogin
    val scope = rememberCoroutineScope()

    LaunchedEffect(viewModel.errorMessage, viewModel.successMessage) {
        if (viewModel.errorMessage.isNotEmpty()) {
            snackbarHostState.showSnackbar(viewModel.errorMessage)
            viewModel.clearMessages()
        }
        if (viewModel.successMessage.isNotEmpty()) {
            snackbarHostState.showSnackbar(viewModel.successMessage)
            viewModel.clearMessages()
        }
    }

    LaunchedEffect(Unit) {
        if (currentUserId > 0) {
            viewModel.loadProfil(currentUserId)
        } else {
            navController.navigate(DestinasiLogin.route)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text(stringResource(DestinasiProfil.titleRes))},
                scrollBehavior = scrollBehavior
            )
        },
        bottomBar = {
            BottomNav(
                onNavigateToDashboard = { navController.navigate(DestinasiHome.route) },
                onNavigateToCari = { navController.navigate(DestinasiCari.route) },
                onNavigateToBuatResep = {
                    navController.navigate("buat_resep/$currentUserId")
                },
                onNavigateToProfil = {
                    navController.navigate("profil/$currentUserId")
                }
            )
        }
    ) { innerPadding ->
        if (showDeleteDialog) {
            DeleteConfirmationDialog(
                onConfirm = {
                    showDeleteDialog = false
                    scope.launch {
                        val success = viewModel.deleteAccount(currentUserId)
                        if (success) {
                            sessionViewModel.clearSession()
                            navController.navigate(DestinasiWelcome.route) {
                                popUpTo(0) { inclusive = true }
                            }
                        }
                    }
                },
                onDismiss = { showDeleteDialog = false }
            )
        }

        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            when (val status = viewModel.statusUi) {
                is StatusUiProfil.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }

                is StatusUiProfil.ProfilLoaded, is StatusUiProfil.Success -> {
                    val userProfil =
                        if (status is StatusUiProfil.ProfilLoaded) status.profil else viewModel.currentProfil
                    userProfil?.let {
                        ProfilContent(
                            profil = it,
                            viewModel = viewModel,
                            sessionViewModel = sessionViewModel,
                            navController = navController,
                            onLogout = {
                                sessionViewModel.clearSession()
                                navController.navigate(DestinasiWelcome.route) {
                                    popUpTo(0) { inclusive = true }
                                }
                            },
                            onDeleteAccount = { showDeleteDialog = true }
                        )
                    }
                }

                is StatusUiProfil.Error -> {

                    ErrorScreen(status.message) {
                        viewModel.loadProfil(currentUserId)
                    }
                }
            }
        }
    }
}

@Composable
private fun ProfilContent(
    profil: User,
    viewModel: ProfilViewModel,
    sessionViewModel: SessionViewModel,
    navController: NavController,
    onLogout: () -> Unit,
    onDeleteAccount: () -> Unit,
) {
    var selectedTab by remember { mutableStateOf(0) }

    LaunchedEffect(profil.id_user) {
        viewModel.loadResepUser(profil.id_user)
        viewModel.loadBookmarkUser(profil.id_user)
    }

    LaunchedEffect(selectedTab) {
        if (selectedTab == 1) {
            viewModel.loadBookmarkUser(profil.id_user)
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        TabRow(selectedTabIndex = selectedTab) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = { Text("Resep") }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = { Text("Bookmark") }
            )
            Tab(
                selected = selectedTab == 2,
                onClick = { selectedTab = 2 },
                text = { Text("Akun") }
            )
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(16.dp)
        ) {
            when (selectedTab) {
                0 -> { // resep saya
                    if (viewModel.listResepUser.isEmpty()) {
                        item { EmptyState("Belum ada resep yang dibuat.") }
                    } else {
                        items(viewModel.listResepUser) { resep ->
                            ItemResepProfil(resep = resep, onClick = {
                                navController.navigate("detail_resep/${resep.id_resep}")
                            })
                        }
                    }
                }

                1 -> { // bookmark
                    if (viewModel.listBookmarkUser.isEmpty()) {
                        item { EmptyState("Belum ada resep yang disimpan.") }
                    } else {
                        items(viewModel.listBookmarkUser) { resep ->
                            KomponenResep(
                                resep = resep,
                                onClick = { navController.navigate("detail_resep/${resep.id_resep}") },
                                onBookmark = { viewModel.toggleBookmark(profil.id_user, resep.id_resep) },
                            )
                        }
                    }
                }

                2 -> { // akun
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                ProfilInfo(profil = profil)
                            }
                        }
                    }

                    item {
                        ActionButtons(
                            viewModel = viewModel,
                            profil = profil,
                            navController = navController,
                            idUserLogin = profil.id_user,
                            onLogout = onLogout,
                            onDeleteAccount = onDeleteAccount
                        )
                    }
                }
            }
            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
    }
}

@Composable
fun ItemResepProfil(resep: Resep, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = resep.judul, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(text = "Kategori: ${resep.kategori}", fontSize = 12.sp, color = Color.Gray)
            }
        }
    }
}

@Composable
fun ActionButtons(
    viewModel: ProfilViewModel,
    profil: User,
    navController: NavController,
    idUserLogin: Int,
    onLogout: () -> Unit,
    onDeleteAccount: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Button(
            onClick = {
                val routeWithId = "edit_profil/${idUserLogin}"
                navController.navigate(routeWithId)
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = colorResource(R.color.pastelbrown))
        ) {
            Text("Edit Profil",
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold)
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedButton(
                onClick = onLogout,
                shape = RoundedCornerShape(50),
                modifier = Modifier.weight(1f),
            ) { Text("Logout", color = colorResource(id = R.color.pastelbrown),
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold
                )
            }

            Button(
                onClick = onDeleteAccount,
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = colorResource(R.color.merah))
            ) { Text("Hapus Akun",
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold) }
        }
    }
}

@Composable
fun EmptyState(pesan: String) {
    Box(modifier = Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
        Text(pesan, color = Color.Gray, fontSize = 14.sp)
    }
}

@Composable
private fun ProfilInfo(profil: User) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("Username: ${profil.username}",
            fontSize = 20.sp,
            fontFamily = FontFamily.Monospace)
        Text("Email: ${profil.email}",
            fontSize = 20.sp,
            fontFamily = FontFamily.Monospace)
    }
}

@Composable
private fun DeleteConfirmationDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Konfirmasi Hapus Akun") },
        text = {
            Text("Tindakan ini tidak dapat dikembalikan! Semua data pribadi dan resep Anda akan dihapus permanen.")
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(colorResource(id = R.color.merah))
            ) {
                Text("Hapus Akun")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Batal")
            }
        }
    )
}