package com.mcg.financemaster

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.room.Room
import com.mcg.financemaster.data.AppDatabase
import com.mcg.financemaster.data.FinanceRepository
import com.mcg.financemaster.ui.AddTransactionRoute
import com.mcg.financemaster.ui.AddTransactionScreen
import com.mcg.financemaster.ui.DashboardRoute
import com.mcg.financemaster.ui.DashboardScreen
import com.mcg.financemaster.ui.FinanceViewModel
import com.mcg.financemaster.ui.FinanceViewModelFactory
import com.mcg.financemaster.ui.TransactionListScreen
import com.mcg.financemaster.ui.TransactionsRoute
import com.mcg.financemaster.ui.theme.FinanceMasterTheme

class MainActivity : ComponentActivity() {
    private lateinit var viewModel: FinanceViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "finance-db"
        ).build()
        val repository = FinanceRepository(db.transactionDao())
        val factory = FinanceViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[FinanceViewModel::class.java]

        setContent {
            FinanceMasterTheme {
                MainAppScreen(viewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppScreen(viewModel: FinanceViewModel) {
    val navController = rememberNavController()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        floatingActionButton = {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentDestination = navBackStackEntry?.destination
            // Show FAB only on Dashboard or Transactions list
            val isMainScreen = currentDestination?.route?.contains("DashboardRoute") == true ||
                               currentDestination?.route?.contains("TransactionsRoute") == true
            
            if (isMainScreen) {
                FloatingActionButton(onClick = { navController.navigate(AddTransactionRoute) }) {
                    Icon(Icons.Default.Add, contentDescription = "Adicionar Transação")
                }
            }
        },
        bottomBar = {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentDestination = navBackStackEntry?.destination
            val isMainScreen = currentDestination?.route?.contains("DashboardRoute") == true ||
                               currentDestination?.route?.contains("TransactionsRoute") == true

            if (isMainScreen) {
                NavigationBar {
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Home, contentDescription = "Início") },
                        label = { Text("Início") },
                        selected = currentDestination?.hierarchy?.any { it.route?.contains("DashboardRoute") == true } == true,
                        onClick = {
                            navController.navigate(DashboardRoute) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.List, contentDescription = "Transações") },
                        label = { Text("Transações") },
                        selected = currentDestination?.hierarchy?.any { it.route?.contains("TransactionsRoute") == true } == true,
                        onClick = {
                            navController.navigate(TransactionsRoute) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = DashboardRoute,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable<DashboardRoute> { DashboardScreen(viewModel) }
            composable<TransactionsRoute> { TransactionListScreen(viewModel) }
            composable<AddTransactionRoute> { 
                AddTransactionScreen(viewModel, onNavigateBack = { navController.popBackStack() }) 
            }
        }
    }
}

