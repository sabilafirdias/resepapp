package com.example.resepappy.uicontroller

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.resepappy.uicontroller.route.DestinasiHome
import com.example.resepappy.uicontroller.route.DestinasiLogin
import com.example.resepappy.uicontroller.route.DestinasiRegister
import com.example.resepappy.uicontroller.route.DestinasiWelcome
import com.example.resepappy.uicontroller.route.*
import com.example.resepappy.view.HalamanBuatResep
import com.example.resepappy.view.HalamanCari
import com.example.resepappy.view.HalamanDetailResep
import com.example.resepappy.view.HalamanEditProfil
import com.example.resepappy.view.HalamanEditResep
import com.example.resepappy.view.HalamanHome
import com.example.resepappy.view.HalamanLogin
import com.example.resepappy.view.HalamanProfil
import com.example.resepappy.view.HalamanRegister
import com.example.resepappy.view.HalamanWelcome
import com.example.resepappy.viewmodel.SessionViewModel
import com.example.resepappy.viewmodel.provider.PenyediaViewModel


@Composable
fun ResepApp(navController: NavHostController = rememberNavController(), modifier: Modifier){
    HostNavigasi(navController = navController)
}

@Composable
fun HostNavigasi(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val sessionViewModel: SessionViewModel = viewModel(factory = PenyediaViewModel.Factory)

    NavHost(
        navController = navController,
        startDestination = DestinasiWelcome.route, modifier = Modifier.fillMaxSize()
    ) {
        composable(DestinasiWelcome.route) {
            HalamanWelcome(
                onNavigateLogin = { navController.navigate(DestinasiLogin.route) },
                onNavigateRegister = { navController.navigate(DestinasiRegister.route) }
            )
        }

        composable(DestinasiRegister.route) {
            HalamanRegister(
                onNavigateLogin = {
                    navController.navigate(DestinasiLogin.route) },
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(DestinasiLogin.route) {
            HalamanLogin(
                onNavigateRegister = {
                    navController.navigate(DestinasiRegister.route)
                },
                onNavigateBack = {
                    navController.navigate(DestinasiHome.route) {
                        popUpTo(DestinasiWelcome.route) { inclusive = true }
                    }
                },
                sessionViewModel = sessionViewModel
            )
        }

        composable(DestinasiHome.route) {
            val idUserLogin = sessionViewModel.currentUserId

            if (idUserLogin == null) {
                navController.navigate(DestinasiLogin.route)
                return@composable
            }
            HalamanHome(
                idUserLogin = idUserLogin,
                navController = navController,
                onNavigateBack = { navController.popBackStack() },
                sessionViewModel = sessionViewModel
            )
        }

        composable(DestinasiCari.route) {
            HalamanCari(
                navController = navController,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(
            route = DestinasiProfil.route,
            arguments = listOf(navArgument("idUser") { type = NavType.IntType })
        ) { backStackEntry ->
            val idUserLogin = sessionViewModel.currentUserId

            if (idUserLogin == null) {
                navController.navigate(DestinasiLogin.route) {
                    popUpTo(0) { inclusive = true }
                }
                return@composable
            }
            HalamanProfil(
                idUserLogin = idUserLogin,
                navController = navController,
                sessionViewModel = sessionViewModel,
                onLogout = {
                    sessionViewModel.clearSession()
                    navController.navigate(DestinasiLogin.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(
            route = DestinasiEditProfil.route,
            arguments = listOf(navArgument("idUser") { type = NavType.IntType })
        ) { backStackEntry ->
            val idUser = backStackEntry.arguments?.getInt("idUser") ?: 0
            HalamanEditProfil(
                idUser = idUser,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(
            route = DestinasiBuatResep.route,
            arguments = listOf(navArgument("idUser") { type = NavType.IntType })
        ) { backStackEntry ->
            val idUser = backStackEntry.arguments?.getInt("idUser") ?: 1
            HalamanBuatResep(
                idUser = idUser,
                navController = navController,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(
            route = DestinasiDetailResep.route,
            arguments = listOf(
                navArgument("idResep") { type = NavType.IntType }            )
        ) { backStackEntry ->
            val idResep = backStackEntry.arguments?.getInt("idResep") ?: 0
            val idUserLogin = sessionViewModel.currentUserId ?: 0

            HalamanDetailResep(
                idResep = idResep,
                idUserLogin = idUserLogin,
                navController = navController,
                onEditClick = { id ->
                    navController.navigate("edit_resep/$id")
                },
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(
            route = DestinasiEditResep.route,
            arguments = listOf(navArgument("idResep") { type = NavType.IntType })
        ) { backStackEntry ->
            val idResep = backStackEntry.arguments?.getInt("idResep") ?: 0
            val idUser = sessionViewModel.currentUserId ?: 0

            HalamanEditResep(
                idResep = idResep,
                idUser = idUser,
                navController = navController,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
