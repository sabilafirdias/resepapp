package com.example.resepappy.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.resepappy.R
import com.example.resepappy.uicontroller.route.DestinasiEditProfil
import com.example.resepappy.viewmodel.ProfilViewModel
import com.example.resepappy.viewmodel.provider.PenyediaViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HalamanEditProfil(
    idUser: Int,
    onNavigateBack: () -> Unit,
    viewModel: ProfilViewModel = viewModel(factory = PenyediaViewModel.Factory)
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()


    LaunchedEffect(idUser) {
        viewModel.loadProfil(idUser)
        viewModel.startEdit()
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(
                title = stringResource(DestinasiEditProfil.titleRes),
                canNavigateBack = true,
                scrollBehavior = scrollBehavior,
                navigateUp = onNavigateBack
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            EditProfilForm(viewModel = viewModel)

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    val validationError = validatePassword(viewModel.newPassword)

                    coroutineScope.launch {
                        if (viewModel.newPassword.isNotEmpty() && validationError != null) {
                            snackbarHostState.showSnackbar(validationError)
                        } else if (viewModel.newPassword != viewModel.confirmPassword) {
                            snackbarHostState.showSnackbar("Konfirmasi password tidak cocok")
                        } else {
                            val success = viewModel.saveChanges(idUser)
                            if (success) {
                                snackbarHostState.showSnackbar("Profil berhasil diperbarui!")
                                delay(300)
                                onNavigateBack()
                            } else {
                                val friendlyError = when {
                                    viewModel.errorMessage.contains("password", ignoreCase = true) ->
                                        "Password lama yang Anda masukkan salah. Silakan coba lagi."
                                    else -> viewModel.errorMessage.ifEmpty { "Gagal menyimpan perubahan." }
                                }
                                snackbarHostState.showSnackbar(friendlyError)
                            }
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = colorResource(R.color.pastelbrown))
            ) {
                Text("Simpan Perubahan")
            }
        }
    }
}

fun validatePassword(password: String): String? {
    return when {
        password.length < 8 -> "Password minimal harus 8 karakter"
        !password.any { it.isUpperCase() } -> "Password harus mengandung minimal satu huruf besar"
        !password.any { it.isDigit() } -> "Password harus mengandung minimal satu angka"
        else -> null
    }
}

@Composable
fun EditProfilForm(viewModel: ProfilViewModel) {
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