package com.example.compustoree.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.compustoree.model.UserSession

// Warna Tema (Konsisten)
private val BluePrimary = Color(0xFF2563EB)
private val IconSelectedColor = Color.White
private val IconUnselectedColor = Color.Gray

// --- DATA CLASS UNTUK ITEM BOTTOM BAR (DENGAN ICON SELECTED & UNSELECTED) ---
sealed class BottomBarScreen(
    val route: String,
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    object Home : BottomBarScreen(
        route = "home",
        title = "Beranda",
        selectedIcon = Icons.Filled.Home,
        unselectedIcon = Icons.Outlined.Home
    )
    object Riwayat : BottomBarScreen(
        route = "riwayat",
        title = "Riwayat",
        selectedIcon = Icons.Filled.ReceiptLong, // Icon Struk Belanja
        unselectedIcon = Icons.Outlined.ReceiptLong
    )
    object Profile : BottomBarScreen(
        route = "profile",
        title = "Akun Saya",
        selectedIcon = Icons.Filled.Person,
        unselectedIcon = Icons.Outlined.Person
    )
}

@Composable
fun PengelolaHalaman(
    navController: NavHostController = rememberNavController()
) {
    // Daftar halaman yang WAJIB menampilkan Bottom Bar
    val screensWithBottomBar = listOf("home", "riwayat", "profile")

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
            if (currentRoute in screensWithBottomBar) {
                // --- BOTTOM BAR MODERN ---
                Surface(
                    shadowElevation = 16.dp, // Shadow agar mengambang
                    color = Color.White,
                    shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp) // Sudut atas melengkung
                ) {
                    NavigationBar(
                        containerColor = Color.White,
                        tonalElevation = 0.dp // Kita pakai shadow Surface, jadi ini 0
                    ) {
                        val items = listOf(
                            BottomBarScreen.Home,
                            BottomBarScreen.Riwayat,
                            BottomBarScreen.Profile
                        )
                        items.forEach { screen ->
                            val isSelected = currentRoute == screen.route

                            NavigationBarItem(
                                icon = {
                                    Icon(
                                        imageVector = if (isSelected) screen.selectedIcon else screen.unselectedIcon,
                                        contentDescription = screen.title
                                    )
                                },
                                label = {
                                    Text(
                                        text = screen.title,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        fontSize = 11.sp
                                    )
                                },
                                selected = isSelected,
                                onClick = {
                                    navController.navigate(screen.route) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                },
                                // --- WARNA CUSTOM ---
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = IconSelectedColor, // Icon jadi Putih
                                    selectedTextColor = BluePrimary,       // Teks jadi Biru
                                    indicatorColor = BluePrimary,          // Background pill jadi Biru
                                    unselectedIconColor = IconUnselectedColor,
                                    unselectedTextColor = IconUnselectedColor
                                )
                            )
                        }
                    }
                }
            }
        }
    ) { innerPadding ->

        // --- NAV HOST (LOGIKA TETAP SAMA) ---
        NavHost(
            navController = navController,
            startDestination = "splash",
            modifier = Modifier.padding(innerPadding)
        ) {

            // 0. SPLASH
            composable("splash") {
                SplashScreen(onTimeout = {
                    navController.navigate("home") { popUpTo("splash") { inclusive = true } }
                })
            }

            // 1. HOME
            composable("home") {
                HomeScreen(
                    onProdukClick = { id -> navController.navigate("detail/$id") },
                    onRiwayatClick = { navController.navigate("riwayat") },
                    onProfileClick = { navController.navigate("profile") },
                    onAddProductClick = { navController.navigate("add_product") },
                    onEditProductClick = { id -> navController.navigate("edit_product/$id") }
                )
            }

// 2. LOGIN SCREEN
            composable("login") {
                LoginScreen(
                    onLoginSuccess = {
                        navController.popBackStack()
                    },
                    onRegisterClick = { navController.navigate("register") },

                    // ✅ UBAH BAGIAN INI: Arahkan ke "home"
                    onBackClick = {
                        navController.navigate("home") {
                            // Hapus tumpukan layar agar tidak bisa back ke login lagi
                            popUpTo("home") { inclusive = true }
                        }
                    }
                )
            }

            // 3. REGISTER
            composable("register") {
                RegisterScreen(
                    onRegisterSuccess = { navController.popBackStack() },
                    onLoginClick = { navController.popBackStack() }
                )
            }

            // 4. DETAIL
            composable(
                route = "detail/{produkId}",
                arguments = listOf(navArgument("produkId") { type = NavType.IntType })
            ) { backStackEntry ->
                val produkId = backStackEntry.arguments?.getInt("produkId") ?: 0
                DetailScreen(
                    produkId = produkId,
                    onBackClick = { navController.popBackStack() },
                    onBuyClick = { id ->
                        if (UserSession.currentUser != null) {
                            navController.navigate("checkout/$id")
                        } else {
                            navController.navigate("login")
                        }
                    },
                    onEditClick = { id -> navController.navigate("edit_product/$id") }
                )
            }

            // 5. CHECKOUT
            composable("checkout/{produkId}") { backStackEntry ->
                val produkId = backStackEntry.arguments?.getString("produkId")?.toIntOrNull() ?: 0
                CheckoutScreen(
                    produkId = produkId,
                    onBackClick = { navController.popBackStack() },
                    onSuccess = {
                        navController.navigate("riwayat") { popUpTo("home") }
                    }
                )
            }

            // 6. RIWAYAT
            composable("riwayat") {
                if (UserSession.currentUser != null) {
                    RiwayatScreen()
                } else {
                    LaunchedEffect(Unit) { navController.navigate("login") }
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
            }

            // 7. PROFILE
            composable("profile") {
                if (UserSession.currentUser != null) {
                    ProfileScreen(
                        onLogout = {
                            UserSession.logout() // Panggil fungsi logout di object
                            navController.navigate("home") { popUpTo(0) { inclusive = true } }
                        }
                    )
                } else {
                    GuestScreen(onLoginClick = { navController.navigate("login") })
                }
            }

            // 8. ADMIN PAGES
            composable("add_product") {
                AddProductScreen(onBackClick = { navController.popBackStack() })
            }
            composable("edit_product/{produkId}") { backStackEntry ->
                val produkId = backStackEntry.arguments?.getString("produkId")?.toIntOrNull() ?: 0
                EditProductScreen(produkId = produkId, onBackClick = { navController.popBackStack() })
            }
        }
    }
}

// --- KOMPONEN TAMBAHAN: TAMPILAN GUEST (Jika belum login) ---
@Composable
fun GuestScreen(onLoginClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(Icons.Default.LockPerson, null, modifier = Modifier.size(100.dp), tint = Color.LightGray)
        Spacer(modifier = Modifier.height(24.dp))
        Text("Anda belum login", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Text("Silakan login untuk mengakses fitur ini.", color = Color.Gray)
        Spacer(modifier = Modifier.height(32.dp))
        Button(
            onClick = onLoginClick,
            colors = ButtonDefaults.buttonColors(containerColor = BluePrimary),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth().height(50.dp)
        ) {
            Text("Login Sekarang", fontWeight = FontWeight.Bold)
        }
    }
}