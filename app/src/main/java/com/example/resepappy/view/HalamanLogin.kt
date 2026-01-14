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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.resepappy.R
import com.example.resepappy.modeldata.DetailUser
import com.example.resepappy.modeldata.UIStateUser
import com.example.resepappy.uicontroller.route.DestinasiLogin
import com.example.resepappy.uicontroller.route.DestinasiRegister
import com.example.resepappy.viewmodel.AuthViewModel
import com.example.resepappy.viewmodel.SessionViewModel
import com.example.resepappy.viewmodel.provider.PenyediaViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HalamanLogin(
    onNavigateRegister: () -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AuthViewModel = viewModel(factory = PenyediaViewModel.Factory),
    sessionViewModel: SessionViewModel
) {
    LaunchedEffect(Unit) {
        viewModel.setLoginMode()
    }

    val coroutineScope = rememberCoroutineScope()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = stringResource(DestinasiLogin.titleRes),
                canNavigateBack = true,
                scrollBehavior = scrollBehavior,
                navigateUp = onNavigateBack
            )
        }
    ) { innerPadding ->
        LoginBody(
            uiStateUser = viewModel.uiStateUser,
            isLoading = viewModel.isLoading,
            loginError = viewModel.loginError,
            onUserValueChange = { detailUser ->
                if (detailUser.email != viewModel.uiStateUser.detailUser.email) {
                    viewModel.updateEmail(detailUser.email)
                }
                if (detailUser.password != viewModel.uiStateUser.detailUser.password) {
                    viewModel.updatePassword(detailUser.password)
                }
            },
            onLoginClick = {
                coroutineScope.launch {
                    val user = viewModel.login()
                    if (user != null) {
                        sessionViewModel.setUserId(user.id_user)
                        onNavigateBack()
                    }
                }
            },
            onNavigateRegister = onNavigateRegister,
            modifier = Modifier.padding(innerPadding)
        )
    }
}

@Composable
fun LoginBody(
    uiStateUser: UIStateUser,
    isLoading: Boolean,
    loginError: String,
    onUserValueChange: (DetailUser) -> Unit,
    onLoginClick: () -> Unit,
    onNavigateRegister: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier
            .padding(16.dp)
            .fillMaxSize(),
    ) {
        FormLogin(
            detailUser = uiStateUser.detailUser,
            onValueChange = onUserValueChange,
            modifier = Modifier.fillMaxWidth()
        )

        if (loginError.isNotEmpty()) {
            Text(
                text = loginError,
                color = MaterialTheme.colorScheme.error,
                fontSize = 14.sp
            )
        }
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier
                    .fillMaxWidth()
                    .size(32.dp),
                strokeWidth = 4.dp
            )
        } else {
            Button(
                onClick = onLoginClick,
                enabled = uiStateUser.isEntryValid,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorResource(id = R.color.pastelbrown)
                )
            ) {
                Text("Login")
            }
        }

        TextButton(
            onClick = onNavigateRegister,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Belum punya akun? Daftar",
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                color = colorResource(id = R.color.pastelbrown))
        }
    }
}

@Composable
fun FormLogin(
    detailUser: DetailUser,
    onValueChange: (DetailUser) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = modifier
    ) {
        OutlinedTextField(
            value = detailUser.email,
            onValueChange = {
                onValueChange(detailUser.copy(email = it))
            },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth(),
            enabled = enabled,
            singleLine = true
        )

        OutlinedTextField(
            value = detailUser.password,
            onValueChange = {
                onValueChange(detailUser.copy(password = it))
            },
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth(),
            enabled = enabled,
            singleLine = true
        )
    }
}
