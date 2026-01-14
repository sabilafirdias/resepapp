package com.example.resepappy.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch
import com.example.resepappy.R
import com.example.resepappy.modeldata.UIStateUser
import com.example.resepappy.uicontroller.route.DestinasiRegister
import com.example.resepappy.viewmodel.AuthViewModel
import com.example.resepappy.viewmodel.provider.PenyediaViewModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HalamanRegister(
    onNavigateBack: () -> Unit,
    onNavigateLogin: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AuthViewModel = viewModel(factory = PenyediaViewModel.Factory)
) {
    val coroutineScope = rememberCoroutineScope()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = stringResource(DestinasiRegister.titleRes),
                canNavigateBack = true,
                scrollBehavior = scrollBehavior,
                navigateUp = onNavigateBack
            )
        }
    ) { innerPadding ->
        RegisterBody(
            uiStateUser = viewModel.uiStateUser,
            onRegisterClick = {
                coroutineScope.launch {
                    if (viewModel.register()) {
                        onNavigateBack()
                    }
                }
            },
            onNavigateLogin = onNavigateLogin,
            modifier = Modifier.padding(innerPadding)
        )
    }
}

@Composable
fun RegisterBody(
    uiStateUser: UIStateUser,
    onRegisterClick: () -> Unit,
    onNavigateLogin: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier.padding(16.dp)
    ) {
        FormRegister(
            uiStateUser = uiStateUser,
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = onRegisterClick,
            enabled = uiStateUser.isEntryValid,
            shape = RoundedCornerShape(50),
            colors = ButtonDefaults.buttonColors(
                containerColor = colorResource(id = R.color.pastelbrown)
            )
        ) {
            Text(
                "Register",
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold
            )
        }

        TextButton(
            onClick = onNavigateLogin,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Sudah punya akun? Masuk",
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                color = colorResource(id = R.color.pastelbrown))
        }

        uiStateUser.errorMessage?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error,
                fontSize = 14.sp
            )
        }
    }
}

@Composable
fun FormRegister(
    uiStateUser: UIStateUser,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    val viewModel: AuthViewModel = viewModel()
    val coroutineScope = rememberCoroutineScope()

    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = modifier
    ) {
        OutlinedTextField(
            value = uiStateUser.detailUser.username,
            onValueChange = { newUsername ->
                viewModel.updateUsername(newUsername)
                coroutineScope.launch {
                    delay(500)
                    viewModel.checkUsernameAvailability(newUsername)
                }
            },
            label = { Text("Username") },
            isError = uiStateUser.usernameError != null,
            modifier = Modifier.fillMaxWidth(),
            enabled = enabled,
            singleLine = true
        )
        uiStateUser.usernameError?.let {
            Text(it, color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
        }

        OutlinedTextField(
            value = uiStateUser.detailUser.email,
            onValueChange = { newEmail ->
                viewModel.updateEmail(newEmail)
            },
            label = { Text("Email") },
            isError = uiStateUser.emailError != null,
            modifier = Modifier.fillMaxWidth(),
            enabled = enabled,
            singleLine = true
        )
        uiStateUser.emailError?.let {
            Text(it, color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
        }

        OutlinedTextField(
            value = uiStateUser.detailUser.password,
            onValueChange = { newPassword ->
                viewModel.updatePassword(newPassword)
            },
            label = { Text("Password") },
            isError = uiStateUser.passwordError != null,
            modifier = Modifier.fillMaxWidth(),
            enabled = enabled,
            singleLine = true,
            visualTransformation = PasswordVisualTransformation()
        )
        uiStateUser.passwordError?.let {
            Text(it, color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
        }
    }
}