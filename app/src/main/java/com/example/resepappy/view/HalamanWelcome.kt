package com.example.resepappy.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.resepappy.R
import com.example.resepappy.viewmodel.WelcomeUiState
import com.example.resepappy.viewmodel.WelcomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HalamanWelcome(
    onNavigateLogin: () -> Unit,
    onNavigateRegister: () -> Unit,
    viewModel: WelcomeViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    Scaffold { innerPadding ->
        WelcomeBody(
            uiState = viewModel.uiState.collectAsState().value,
            onNavigateLogin = onNavigateLogin,
            onNavigateRegister = onNavigateRegister,
            modifier = modifier.padding(innerPadding)
        )
    }
}

@Composable
fun WelcomeBody(
    uiState: WelcomeUiState,
    onNavigateLogin: () -> Unit,
    onNavigateRegister: () -> Unit,
    modifier: Modifier = Modifier
)
{
    when (uiState) {
        is WelcomeUiState.Success ->
            {
            Box(
                modifier = modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier.padding(70.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Resep",
                        style = MaterialTheme.typography.headlineMedium,
                        color = colorResource(id = R.color.pastelbrown),
                        textAlign = TextAlign.Center,
                        fontSize = 50.sp,
                        fontFamily = FontFamily.Monospace
                    )

                    Spacer(modifier = Modifier.height(80.dp))

                    Image(
                        painter = painterResource(R.drawable.logoresep),
                        contentDescription = null,
                        modifier = Modifier.size(250.dp)
                    )
                    Spacer(modifier = Modifier.height(45.dp))

                    Button(
                        onClick = onNavigateLogin,
                        shape = RoundedCornerShape(50),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colorResource(id = R.color.pastelbrown)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Masuk", color = Color.White,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedButton(
                        onClick = onNavigateRegister,
                        shape = RoundedCornerShape(50),
                        modifier = Modifier.fillMaxWidth()
                        ) {
                        Text("Daftar", color = colorResource(id = R.color.pastelbrown),
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}