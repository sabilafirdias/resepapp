package com.example.resepappy.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
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
                    viewModel.viewModelScope.launch {
                        if (viewModel.deleteAccount(currentUserId)) {
                            sessionViewModel.clearSession()
                            onLogout()
                            navController.navigate(DestinasiLogin.route) {
                                popUpTo(0) { inclusive = true }
                                launchSingleTop = true
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
                                viewModel.viewModelScope.launch {
                                    sessionViewModel.clearSession()
                                    onLogout()
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

    Column(modifier = Modifier.fillMaxSize()) {
        // Sticky TabRow di bagian atas
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

        // Konten yang berubah sesuai Tab
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(16.dp)
        ) {
            when (selectedTab) {
                0 -> { // TAB RESEP SAYA
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

                1 -> { // TAB BOOKMARK
                    if (viewModel.listBookmarkUser.isEmpty()) {
                        item { EmptyState("Belum ada resep yang disimpan.") }
                    } else {
                        items(viewModel.listBookmarkUser) { resep ->
                            ItemResepProfil(resep = resep, onClick = {
                                navController.navigate("detail_resep/${resep.id_resep}")
                            })
                        }
                    }
                }

                2 -> { // TAB PENGATURAN AKUN
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                if (viewModel.isEditing) {
                                    EditProfilForm(viewModel = viewModel)
                                } else {
                                    ProfilInfo(profil = profil)
                                }
                            }
                        }
                    }

                    item {
                        // Memanggil tombol aksi (Edit, Simpan, Batal)
                        ActionButtons(
                            viewModel = viewModel,
                            profil = profil,
                            onLogout = onLogout,
                            onDeleteAccount = onDeleteAccount
                        )
                    }
                }
            }

            // Spacer bawah agar tidak tertutup BottomNav
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
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Anda bisa menambahkan AsyncImage di sini jika ada foto resep
            Column {
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
    onLogout: () -> Unit,
    onDeleteAccount: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        if (viewModel.isEditing) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = { viewModel.saveChanges(profil.id_user) },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = colorResource(R.color.pastelbrown))
                ) { Text("Simpan") }

                OutlinedButton(
                    onClick = { viewModel.cancelEdit() },
                    modifier = Modifier.weight(1f)
                ) { Text("Batal") }
            }
        } else {
            Button(
                onClick = { viewModel.startEdit() },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = colorResource(R.color.pastelbrown))
            ) { Text("Edit Profil") }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = onLogout,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
                ) { Text("Logout") }

                Button(
                    onClick = onDeleteAccount,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = colorResource(R.color.merah))
                ) { Text("Hapus Akun") }
            }
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

@Composable
private fun EditProfilForm(viewModel: ProfilViewModel) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        OutlinedTextField(
            value = viewModel.editUsername,
            onValueChange = { viewModel.editUsername = it },
            label = { Text("Username") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = viewModel.editEmail,
            onValueChange = { viewModel.editEmail = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )

        val passwordMismatch = viewModel.newPassword.isNotEmpty() &&
                viewModel.newPassword != viewModel.confirmPassword

        OutlinedTextField(
            value = viewModel.oldPassword,
            onValueChange = { viewModel.oldPassword = it },
            label = { Text("Password Lama (opsional)") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = viewModel.newPassword,
            onValueChange = { viewModel.newPassword = it },
            label = { Text("Password Baru (opsional)") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = viewModel.confirmPassword,
            onValueChange = { viewModel.confirmPassword = it },
            label = { Text("Konfirmasi Password Baru") },
            visualTransformation = PasswordVisualTransformation(),
            isError = passwordMismatch,
            supportingText = {
                if (passwordMismatch) Text("Password tidak cocok", color = MaterialTheme.colorScheme.error)
            },
            modifier = Modifier.fillMaxWidth()
        )
    }
}
