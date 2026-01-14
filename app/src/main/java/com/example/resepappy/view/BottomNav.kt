package com.example.resepappy.view

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomNav(
    onNavigateToDashboard: () -> Unit,
    onNavigateToCari: () -> Unit,
    onNavigateToBuatResep: () -> Unit,
    onNavigateToProfil: () -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        NavigationBarItem(
            icon = { Icon(Icons.Default.Home, contentDescription = "Dashboard") },
            label = { Text("Dashboard") },
            selected = false,
            onClick = onNavigateToDashboard
        )

        NavigationBarItem(
            icon = { Icon(Icons.Default.Search, contentDescription = "Cari") },
            label = { Text("Cari") },
            selected = false,
            onClick = onNavigateToCari
        )

        NavigationBarItem(
            icon = { Icon(Icons.Default.Add, contentDescription = "Buat Resep") },
            label = { Text("Buat") },
            selected = false,
            onClick = onNavigateToBuatResep
        )

        NavigationBarItem(
            icon = { Icon(Icons.Default.Person, contentDescription = "Profil") },
            label = { Text("Profil") },
            selected = false,
            onClick = onNavigateToProfil
        )
    }
}