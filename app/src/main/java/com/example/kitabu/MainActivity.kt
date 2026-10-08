package com.example.kitabu

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.kitabu.data.local.BookingEntity
import com.example.kitabu.data.local.BookingStatus
import com.example.kitabu.data.local.KitabuDatabase
import com.example.kitabu.data.repository.BookRepository
import com.example.kitabu.data.repository.BookingRepository
import com.example.kitabu.ui.theme.KitabuTheme
import com.example.kitabu.viewmodel.BookViewModel
import com.example.kitabu.viewmodel.BookingViewModel
import com.example.kitabu.viewmodel.ViewModelFactory

data class Book(
    val title: String,
    val author: String,
    val category: String,
    val isAvailable: Boolean
)

data class Reservation(
    val bookTitle: String,
    val reservationDate: String,
    val returnDeadline: String,
    val daysRemaining: Int
)

class MainActivity : ComponentActivity() {

    private val database by lazy {
        KitabuDatabase.getDatabase(applicationContext)
    }

    private val viewModelFactory by lazy {
        ViewModelFactory(
            bookRepository = BookRepository(database.bookDao()),
            bookingRepository = BookingRepository(database.bookingDao())
        )
    }

    private val bookViewModel: BookViewModel by viewModels {
        viewModelFactory
    }

    private val bookingViewModel: BookingViewModel by viewModels {
        viewModelFactory
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            KitabuTheme {
                KitabuApp(
                    bookViewModel = bookViewModel,
                    bookingViewModel = bookingViewModel
                )
            }
        }
    }
}

@Composable
fun KitabuApp(
    bookViewModel: BookViewModel,
    bookingViewModel: BookingViewModel
) {
    val navController = rememberNavController()
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            NavigationBar(
                containerColor = Color(0xFF5C8FC9)
            ) {
                NavigationBarItem(
                    selected = currentRoute == "books",
                    onClick = {
                        navController.navigate("books") {
                            launchSingleTop = true
                            restoreState = true
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                        }
                    },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Book,
                            contentDescription = "Books"
                        )
                    },
                    label = {
                        Text("Books")
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.White,
                        selectedTextColor = Color.White,
                        unselectedIconColor = Color(0xFFD6E6F5),
                        unselectedTextColor = Color(0xFFD6E6F5),
                        indicatorColor = Color(0xFF487FB8)
                    )
                )

                NavigationBarItem(
                    selected = currentRoute == "reservations",
                    onClick = {
                        navController.navigate("reservations") {
                            launchSingleTop = true
                            restoreState = true
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                        }
                    },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Bookmark,
                            contentDescription = "Reservations"
                        )
                    },
                    label = {
                        Text("Reservations")
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.White,
                        selectedTextColor = Color.White,
                        unselectedIconColor = Color(0xFFD6E6F5),
                        unselectedTextColor = Color(0xFFD6E6F5),
                        indicatorColor = Color(0xFF487FB8)
                    )
                )
            }
        }
    ) { paddingValues ->

        NavHost(
            navController = navController,
            startDestination = "books",
            modifier = Modifier.padding(paddingValues),
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None },
            popEnterTransition = { EnterTransition.None },
            popExitTransition = { ExitTransition.None }
        ) {
            composable("books") {
                CatalogScreen(
                    bookViewModel = bookViewModel,
                    bookingViewModel = bookingViewModel
                )
            }

            composable("reservations") {
                ReservationsScreen(
                    bookViewModel = bookViewModel,
                    bookingViewModel = bookingViewModel
                )
            }
        }
    }
}

@Composable
fun CatalogScreen(
    bookViewModel: BookViewModel,
    bookingViewModel: BookingViewModel
) {
    val searchText by bookViewModel.searchQuery.collectAsState()
    val databaseBooks by bookViewModel.books.collectAsState()

    var selectedBook by remember {
        mutableStateOf<Book?>(null)
    }

    val books = databaseBooks.map { book ->
        Book(
            title = book.title,
            author = book.author,
            category = book.category,
            isAvailable = book.isAvailable
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Kitabu",
            style = MaterialTheme.typography.headlineMedium
        )

        Text(
            text = "Library Book Rental & Reservation System",
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = searchText,
            onValueChange = { bookViewModel.onSearchQueryChanged(it) },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Search books") },
            placeholder = { Text("Search by title or author") },
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (books.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (searchText.isBlank()) {
                        "No books available in library."
                    } else {
                        "No books found matching \"$searchText\"."
                    },
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                itemsIndexed(books) { _, book ->
                    val cardBackgroundColor = Color(0xFFD6E6F5)

                    BookCard(
                        book = book,
                        backgroundColor = cardBackgroundColor,
                        onReserveClick = { selectedBook = book }
                    )
                }
            }
        }
    }

    if (selectedBook != null) {
        ReservationDialog(
            book = selectedBook!!,
            onDismiss = { selectedBook = null },
            onReserve = { selectedDuration ->
                val bookingDate = System.currentTimeMillis()
                val returnDeadline = bookingDate + (selectedDuration * 24L * 60L * 60L * 1000L)

                val selectedBookEntity = databaseBooks.find {
                    it.title == selectedBook!!.title
                }

                if (selectedBookEntity != null) {
                    bookingViewModel.addBooking(
                        BookingEntity(
                            bookOwnerId = selectedBookEntity.bookId,
                            userName = "Current User",
                            bookingDate = bookingDate,
                            returnDeadline = returnDeadline,
                            status = BookingStatus.PENDING
                        )
                    )
                }

                selectedBook = null
            }
        )
    }
}

@Composable
fun BookCard(
    book: Book,
    backgroundColor: Color,
    onReserveClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = book.title,
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Author: ${book.author}",
                style = MaterialTheme.typography.bodyMedium
            )

            Text(
                text = "Category: ${book.category}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = if (book.isAvailable) Color(0xFFC8E6C9) else Color(0xFFFFCDD2)
                ) {
                    Text(
                        text = if (book.isAvailable) "Available" else "Borrowed",
                        color = if (book.isAvailable) Color(0xFF1B5E20) else Color(0xFFB71C1C),
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        style = MaterialTheme.typography.labelMedium
                    )
                }

                if (book.isAvailable) {
                    Button(onClick = onReserveClick) {
                        Text("Reserve")
                    }
                }
            }
        }
    }
}

@Composable
fun ReservationDialog(
    book: Book,
    onDismiss: () -> Unit,
    onReserve: (Int) -> Unit
) {
    var selectedDuration by remember { mutableStateOf(7) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Reserve Book") },
        text = {
            Column {
                Text(
                    text = book.title,
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text("Choose rental duration:")

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = { selectedDuration = 7 },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("7 Days")
                    }

                    OutlinedButton(
                        onClick = { selectedDuration = 14 },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("14 Days")
                    }

                    OutlinedButton(
                        onClick = { selectedDuration = 21 },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("21 Days")
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(text = "Selected: $selectedDuration days")
            }
        },
        confirmButton = {
            Button(onClick = { onReserve(selectedDuration) }) {
                Text("Reserve")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun ReservationsScreen(
    bookViewModel: BookViewModel,
    bookingViewModel: BookingViewModel
) {
    val bookings by bookingViewModel.currentUserBookings.collectAsState()
    val books by bookViewModel.books.collectAsState()

    var bookingToCancel by remember { mutableStateOf<BookingEntity?>(null) }

    val activeBookings = bookings.filter { it.status != BookingStatus.RETURNED }

    val reservations = activeBookings.map { booking ->
        booking to Reservation(
            bookTitle = books.firstOrNull {
                it.bookId == booking.bookOwnerId
            }?.title ?: "Unknown Book",

            reservationDate = java.text.SimpleDateFormat(
                "dd MMMM yyyy",
                java.util.Locale.getDefault()
            ).format(java.util.Date(booking.bookingDate)),

            returnDeadline = java.text.SimpleDateFormat(
                "dd MMMM yyyy",
                java.util.Locale.getDefault()
            ).format(java.util.Date(booking.returnDeadline)),

            daysRemaining = maxOf(
                0,
                ((booking.returnDeadline - System.currentTimeMillis()) / (24L * 60L * 60L * 1000L)).toInt()
            )
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "My Reservations",
            style = MaterialTheme.typography.headlineMedium
        )

        Text(
            text = "Active rentals",
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (reservations.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No active reservations.\nReserve a book from the catalog!",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                itemsIndexed(reservations) { _, (booking, reservation) ->
                    ReservationCard(
                        reservation = reservation,
                        status = booking.status,
                        backgroundColor = Color(0xFF5C8FC9),
                        onCancel = { bookingToCancel = booking },
                        onActivate = {
                            bookingViewModel.updateBooking(
                                booking.copy(status = BookingStatus.ACTIVE)
                            )
                            books.firstOrNull {
                                it.bookId == booking.bookOwnerId
                            }?.let { book ->
                                bookViewModel.setBookAvailability(book = book, isAvailable = false)
                            }
                        },
                        onReturn = {
                            bookingViewModel.updateBooking(
                                booking.copy(status = BookingStatus.RETURNED)
                            )
                            books.firstOrNull {
                                it.bookId == booking.bookOwnerId
                            }?.let { book ->
                                bookViewModel.setBookAvailability(book = book, isAvailable = true)
                            }
                        },
                        onRenew = {
                            bookingViewModel.renewBooking(booking)
                        }
                    )
                }
            }
        }

        if (bookingToCancel != null) {
            AlertDialog(
                onDismissRequest = { bookingToCancel = null },
                title = { Text("Cancel Reservation") },
                text = { Text("Are you sure you want to cancel this reservation?") },
                confirmButton = {
                    Button(
                        onClick = {
                            bookingToCancel?.let { booking ->
                                bookingViewModel.deleteBooking(booking)
                                books.firstOrNull {
                                    it.bookId == booking.bookOwnerId
                                }?.let { book ->
                                    bookViewModel.setBookAvailability(book = book, isAvailable = true)
                                }
                            }
                            bookingToCancel = null
                        }
                    ) {
                        Text("Cancel Reservation")
                    }
                },
                dismissButton = {
                    OutlinedButton(onClick = { bookingToCancel = null }) {
                        Text("Keep Reservation")
                    }
                }
            )
        }
    }
}

@Composable
fun ReservationCard(
    reservation: Reservation,
    status: BookingStatus,
    backgroundColor: Color,
    onCancel: () -> Unit,
    onActivate: () -> Unit,
    onReturn: () -> Unit,
    onRenew: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = reservation.bookTitle,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.weight(1f),
                    color = Color.White
                )

                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = when (status) {
                        BookingStatus.PENDING -> Color(0xFFFFF3E0)
                        BookingStatus.ACTIVE -> Color(0xFFC8E6C9)
                        BookingStatus.RETURNED -> Color(0xFFE0E0E0)
                    }
                ) {
                    Text(
                        text = status.name,
                        color = when (status) {
                            BookingStatus.PENDING -> Color(0xFFE65100)
                            BookingStatus.ACTIVE -> Color(0xFF1B5E20)
                            BookingStatus.RETURNED -> Color.Gray
                        },
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Reservation date: ${reservation.reservationDate}",
                color = Color.White
            )

            Text(
                text = "Return deadline: ${reservation.returnDeadline}",
                color = Color.White
            )

            Text(
                text = if (reservation.daysRemaining <= 0 && status == BookingStatus.ACTIVE) {
                    "Overdue"
                } else {
                    "Days remaining: ${reservation.daysRemaining}"
                },
                color = if (reservation.daysRemaining <= 0 && status == BookingStatus.ACTIVE) {
                    Color(0xFFFFCDD2)
                } else {
                    Color.White
                }
            )

            Spacer(modifier = Modifier.height(12.dp))

            if (status != BookingStatus.RETURNED) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (status == BookingStatus.PENDING) {
                        Button(
                            onClick = onActivate,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Activate")
                        }

                        OutlinedButton(
                            onClick = onCancel,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Cancel")
                        }
                    } else if (status == BookingStatus.ACTIVE) {
                        Button(
                            onClick = onRenew,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Renew")
                        }

                        OutlinedButton(
                            onClick = onReturn,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Return")
                        }
                    }
                }
            }
        }
    }
}
