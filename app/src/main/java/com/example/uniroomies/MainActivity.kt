package com.example.uniroomies

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.uniroomies.presentation.AuthViewModel
import com.example.uniroomies.ui.theme.UniroomiesTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            UniroomiesTheme(dynamicColor = false) {
                UniroomiesApp()
            }
        }
    }
}

private enum class Screen {
    Auth,
    Listings,
    SearchRoomies,
    Profile,
    CreatePost,
    Detail,
    Chat
}

private data class UserProfile(
    val name: String = "",
    val age: String = "",
    val sex: String = "Prefiero no decir",
    val city: String = "Osorno",
    val university: String = "Universidad de Los Lagos"
)

private data class Listing(
    val id: Int,
    val type: String,
    val title: String,
    val description: String,
    val price: String,
    val rules: String,
    val sector: String,
    val spots: String,
    val owner: String,
    val imageColors: List<Color>
)

private data class RoomieRequest(
    val type: String,
    val title: String,
    val sector: String,
    val budget: String,
    val description: String
)

private data class ChatMessage(
    val text: String,
    val fromMe: Boolean
)

private val universities = listOf("Universidad de Los Lagos", "Inacap", "Santo Tomas")
private val sexOptions = listOf("Hombre", "Mujer", "Otro", "Prefiero no decir")
private val sectors = listOf("Centro", "Rahue", "Ovejeria", "Francke", "Oriente", "Pilauco", "Chuyaca", "Pampa Schilling")
private val listingTypes = listOf("Ofrezco pieza", "Ofrezco departamento", "Ofrezco casa", "Busco roomie", "Busco pieza")

@Composable
private fun UniroomiesApp() {
    val authViewModel: AuthViewModel = viewModel()
    var screen by rememberSaveable { mutableStateOf(Screen.Auth) }
    var selectedListingId by rememberSaveable { mutableStateOf(1) }
    var activeChatTitle by rememberSaveable { mutableStateOf("Consulta") }
    var profile by remember { mutableStateOf(UserProfile()) }
    val listings = remember { mutableStateListOf<Listing>().apply { addAll(sampleListings()) } }
    val roomieRequests = remember { mutableStateListOf<RoomieRequest>().apply { addAll(sampleRoomieRequests()) } }
    val chatMessages = remember {
        mutableStateListOf(
            ChatMessage("Hola, me interesa la publicacion. Sigue disponible?", true),
            ChatMessage("Hola! Si, aun tenemos un cupo disponible para estudiante.", false)
        )
    }
    val selectedListing = listings.firstOrNull { it.id == selectedListingId } ?: listings.first()
    val signedIn = screen != Screen.Auth

    Scaffold(
        topBar = {
            if (signedIn) {
                UniroomiesTopBar(
                    title = when (screen) {
                        Screen.Listings -> "UNIROOMIES"
                        Screen.SearchRoomies -> "Buscar roomies"
                        Screen.Profile -> "Perfil"
                        Screen.CreatePost -> "Crear publicacion"
                        Screen.Detail -> selectedListing.type
                        Screen.Chat -> "Chat privado"
                        Screen.Auth -> "UNIROOMIES"
                    },
                    canGoBack = screen !in listOf(Screen.Listings, Screen.Auth),
                    onBack = { screen = Screen.Listings },
                    onMenu = { screen = it },
                    onSignOut = {
                        authViewModel.signOut()
                        profile = UserProfile()
                        selectedListingId = 1
                        activeChatTitle = "Consulta"
                        screen = Screen.Auth
                    }
                )
            }
        },
        floatingActionButton = {
            if (screen == Screen.Listings) {
                Button(
                    onClick = { screen = Screen.CreatePost },
                    shape = RoundedCornerShape(16.dp),
                    contentPadding = PaddingValues(horizontal = 18.dp, vertical = 14.dp)
                ) {
                    Text("Crear publicacion")
                }
            }
        }
    ) { padding ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            color = Color(0xFFF6F8F7)
        ) {
            when (screen) {
                Screen.Auth -> AuthScreen(
                    authViewModel = authViewModel,
                    onEnter = { createdProfile ->
                        profile = createdProfile
                        screen = Screen.Listings
                    }
                )

                Screen.Listings -> ListingsScreen(
                    listings = listings,
                    onOpen = {
                        selectedListingId = it.id
                        screen = Screen.Detail
                    }
                )

                Screen.SearchRoomies -> SearchRoomiesScreen(
                    requests = roomieRequests,
                    onCreate = { roomieRequests.add(0, it) }
                )

                Screen.Profile -> ProfileScreen(
                    profile = profile,
                    onSave = { profile = it }
                )

                Screen.CreatePost -> CreatePostScreen(
                    onCreate = {
                        listings.add(0, it.copy(id = (listings.maxOfOrNull { listing -> listing.id } ?: 0) + 1))
                        screen = Screen.Listings
                    }
                )

                Screen.Detail -> DetailScreen(
                    listing = selectedListing,
                    onChat = {
                        activeChatTitle = selectedListing.title
                        screen = Screen.Chat
                    }
                )

                Screen.Chat -> ChatScreen(
                    title = activeChatTitle,
                    messages = chatMessages,
                    onSend = { chatMessages.add(ChatMessage(it, true)) }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun UniroomiesTopBar(
    title: String,
    canGoBack: Boolean,
    onBack: () -> Unit,
    onMenu: (Screen) -> Unit,
    onSignOut: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    TopAppBar(
        title = {
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        navigationIcon = {
            if (canGoBack) {
                TextButton(onClick = onBack) {
                    Text("Atras")
                }
            }
        },
        actions = {
            Box {
                TextButton(onClick = { expanded = true }) {
                    Text("Menu")
                }
                DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    DropdownMenuItem(text = { Text("Buscar roomies") }, onClick = {
                        expanded = false
                        onMenu(Screen.SearchRoomies)
                    })
                    DropdownMenuItem(text = { Text("Perfil") }, onClick = {
                        expanded = false
                        onMenu(Screen.Profile)
                    })
                    DropdownMenuItem(text = { Text("Listados de arriendos") }, onClick = {
                        expanded = false
                        onMenu(Screen.Listings)
                    })
                    DropdownMenuItem(text = { Text("Cerrar sesion") }, onClick = {
                        expanded = false
                        onSignOut()
                    })
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color(0xFFF6F8F7),
            titleContentColor = Color(0xFF10201B)
        )
    )
}

@Composable
private fun AuthScreen(
    authViewModel: AuthViewModel,
    onEnter: (UserProfile) -> Unit
) {
    var loginMode by rememberSaveable { mutableStateOf(true) }
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var name by rememberSaveable { mutableStateOf("") }
    var age by rememberSaveable { mutableStateOf("") }
    var sex by rememberSaveable { mutableStateOf(sexOptions.last()) }
    var city by rememberSaveable { mutableStateOf("Osorno") }
    var university by rememberSaveable { mutableStateOf(universities.first()) }
    var error by rememberSaveable { mutableStateOf("") }
    var loading by rememberSaveable { mutableStateOf(false) }
    val scroll = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scroll)
            .padding(22.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Spacer(modifier = Modifier.height(18.dp))
        Text("UNIROOMIES", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.ExtraBold, color = Color(0xFF10201B))
        Text("Arriendos y roomies para estudiantes universitarios en Osorno.", color = Color(0xFF52645E))

        ElevatedCard(shape = RoundedCornerShape(28.dp), colors = CardDefaults.elevatedCardColors(containerColor = Color.White)) {
            Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChipButton("Iniciar sesion", selected = loginMode) { loginMode = true }
                    FilterChipButton("Registrarme", selected = !loginMode) { loginMode = false }
                }

                OutlinedTextField(value = email, onValueChange = { email = it.trim() }, label = { Text("Correo Gmail") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Contrasena") },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth()
                )

                if (!loginMode) {
                    OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Nombre") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = age, onValueChange = { age = it.filter(Char::isDigit) }, label = { Text("Edad") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = city, onValueChange = { city = it }, label = { Text("Ciudad") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                    DropdownField("Sexo", sex, sexOptions) { sex = it }
                    DropdownField("Universidad", university, universities) { university = it }
                }

                if (error.isNotBlank()) {
                    Text(error, color = Color(0xFFB3261E), style = MaterialTheme.typography.bodyMedium)
                }

                Button(
                    onClick = {
                        error = when {
                            !email.endsWith("@gmail.com", ignoreCase = true) -> "Usa un correo terminado en @gmail.com."
                            password.length < 6 -> "La contrasena debe tener al menos 6 caracteres."
                            !loginMode && name.isBlank() -> "Ingresa tu nombre."
                            !loginMode && age.isBlank() -> "Ingresa tu edad."
                            else -> ""
                        }
                        if (error.isBlank()) {
                            loading = true
                            val resolvedName = if (name.isBlank()) "Estudiante UNIROOMIES" else name
                            val resolvedAge = if (age.isBlank()) "20" else age
                            val resolvedCity = city.ifBlank { "Osorno" }
                            val handleResult: (Result<com.example.uniroomies.data.model.UserProfileDto>) -> Unit = { result ->
                                loading = false
                                result
                                    .onSuccess { firebaseProfile ->
                                        onEnter(
                                            UserProfile(
                                                name = firebaseProfile.name.ifBlank { resolvedName },
                                                age = firebaseProfile.age.ifBlank { resolvedAge },
                                                sex = firebaseProfile.sex.ifBlank { sex },
                                                city = firebaseProfile.city.ifBlank { resolvedCity },
                                                university = firebaseProfile.university.ifBlank { university }
                                            )
                                        )
                                    }
                                    .onFailure { exception ->
                                        error = exception.localizedMessage ?: "No se pudo autenticar con Firebase."
                                    }
                            }

                            if (loginMode) {
                                authViewModel.signIn(email, password, handleResult)
                            } else {
                                authViewModel.register(
                                    email = email,
                                    password = password,
                                    name = resolvedName,
                                    age = resolvedAge,
                                    sex = sex,
                                    city = resolvedCity,
                                    university = university,
                                    onResult = handleResult
                                )
                            }
                        }
                    },
                    enabled = !loading,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        when {
                            loading -> "Conectando..."
                            loginMode -> "Entrar"
                            else -> "Crear cuenta"
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun ListingsScreen(listings: List<Listing>, onOpen: (Listing) -> Unit) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 16.dp, top = 10.dp, end = 16.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text("Publicaciones destacadas", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = Color(0xFF10201B))
            Text("Espacios compartidos, piezas y busquedas activas para estudiantes.", color = Color(0xFF52645E))
        }
        items(listings) { listing ->
            ListingCard(listing = listing, onClick = { onOpen(listing) })
        }
    }
}

@Composable
private fun ListingCard(listing: Listing, onClick: () -> Unit) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = Color.White)
    ) {
        Column {
            ImagePlaceholder(
                colors = listing.imageColors,
                label = listing.sector,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
            )
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Tag(text = listing.type)
                    Tag(text = listing.sector, soft = true)
                }
                Text(listing.title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, maxLines = 2, overflow = TextOverflow.Ellipsis)
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text(if (listing.price.isBlank()) "Precio a conversar" else "$${listing.price} CLP", fontWeight = FontWeight.SemiBold, color = Color(0xFF005B4F))
                    Text("${listing.spots} cupos", color = Color(0xFF52645E))
                }
            }
        }
    }
}

@Composable
private fun DetailScreen(listing: Listing, onChat: () -> Unit) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                listing.imageColors.take(5).forEachIndexed { index, color ->
                    ImagePlaceholder(
                        colors = listOf(color, Color(0xFFFFD166), Color(0xFF6CC2BD)),
                        label = "Foto ${index + 1}",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(if (index == 0) 210.dp else 126.dp)
                    )
                }
            }
        }
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Tag(listing.type)
                Text(listing.title, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                InfoRow("Precio mensual", if (listing.price.isBlank()) "A conversar" else "$${listing.price} CLP")
                InfoRow("Sector", listing.sector)
                InfoRow("Cupos disponibles", listing.spots)
                InfoBlock("Descripcion", listing.description)
                InfoBlock("Reglas", listing.rules)
                Button(onClick = onChat, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
                    Text("Chatear con ${listing.owner}")
                }
            }
        }
    }
}

@Composable
private fun CreatePostScreen(onCreate: (Listing) -> Unit) {
    var type by rememberSaveable { mutableStateOf(listingTypes.first()) }
    var title by rememberSaveable { mutableStateOf("") }
    var description by rememberSaveable { mutableStateOf("") }
    var price by rememberSaveable { mutableStateOf("") }
    var rules by rememberSaveable { mutableStateOf("") }
    var sector by rememberSaveable { mutableStateOf(sectors.first()) }
    var spots by rememberSaveable { mutableStateOf("1") }
    var error by rememberSaveable { mutableStateOf("") }

    FormColumn {
        Text("Nueva publicacion", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        DropdownField("Tipo", type, listingTypes) { type = it }
        OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Titulo") }, modifier = Modifier.fillMaxWidth())
        DropdownField("Sector", sector, sectors) { sector = it }
        OutlinedTextField(value = price, onValueChange = { price = it.filter(Char::isDigit) }, label = { Text("Precio mensual CLP") }, singleLine = true, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = spots, onValueChange = { spots = it.filter(Char::isDigit).ifBlank { "1" } }, label = { Text("Cupos disponibles") }, singleLine = true, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Descripcion") }, minLines = 4, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = rules, onValueChange = { rules = it }, label = { Text("Reglas") }, minLines = 3, modifier = Modifier.fillMaxWidth())
        Text("Fotos: en esta version se simulan hasta 5 imagenes de la publicacion.", color = Color(0xFF52645E))
        if (error.isNotBlank()) Text(error, color = Color(0xFFB3261E))
        Button(
            onClick = {
                error = when {
                    title.isBlank() -> "Agrega un titulo."
                    description.isBlank() -> "Agrega una descripcion."
                    else -> ""
                }
                if (error.isBlank()) {
                    onCreate(
                        Listing(
                            id = 0,
                            type = type,
                            title = title,
                            description = description,
                            price = price,
                            rules = rules.ifBlank { "Reglas a conversar con los interesados." },
                            sector = sector,
                            spots = spots,
                            owner = "Tu",
                            imageColors = listOf(Color(0xFF2A9D8F), Color(0xFFE9C46A), Color(0xFFE76F51), Color(0xFF457B9D), Color(0xFFA8DADC))
                        )
                    )
                }
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text("Publicar")
        }
    }
}

@Composable
private fun SearchRoomiesScreen(requests: List<RoomieRequest>, onCreate: (RoomieRequest) -> Unit) {
    var showForm by rememberSaveable { mutableStateOf(false) }
    var type by rememberSaveable { mutableStateOf("Busco roomie") }
    var title by rememberSaveable { mutableStateOf("") }
    var sector by rememberSaveable { mutableStateOf(sectors.first()) }
    var budget by rememberSaveable { mutableStateOf("") }
    var description by rememberSaveable { mutableStateOf("") }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Roomies y busquedas", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                    Text("Estudiantes buscando pieza o companeros de piso en Osorno.", color = Color(0xFF52645E))
                }
                Button(
                    onClick = { showForm = !showForm },
                    shape = RoundedCornerShape(16.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 10.dp)
                ) {
                    Text(if (showForm) "Cerrar" else "Rellenar formulario")
                }
            }
        }
        if (showForm) {
            item {
                ElevatedCard(shape = RoundedCornerShape(24.dp), colors = CardDefaults.elevatedCardColors(containerColor = Color.White)) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text("Publica tu busqueda", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        DropdownField("Tipo", type, listOf("Busco roomie", "Busco pieza")) { type = it }
                        OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Titulo") }, modifier = Modifier.fillMaxWidth())
                        DropdownField("Sector preferido", sector, sectors) { sector = it }
                        OutlinedTextField(value = budget, onValueChange = { budget = it.filter(Char::isDigit) }, label = { Text("Presupuesto CLP") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                        OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Descripcion") }, minLines = 3, modifier = Modifier.fillMaxWidth())
                        Button(
                            onClick = {
                                if (title.isNotBlank() && description.isNotBlank()) {
                                    onCreate(RoomieRequest(type, title, sector, budget, description))
                                    title = ""
                                    budget = ""
                                    description = ""
                                    showForm = false
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Text("Publicar busqueda")
                        }
                    }
                }
            }
        }
        items(requests) { request ->
            Card(shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Tag(request.type)
                    Text(request.title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                    Text("${request.sector} · Presupuesto ${if (request.budget.isBlank()) "a conversar" else "$${request.budget} CLP"}", color = Color(0xFF52645E))
                    Text(request.description)
                }
            }
        }
    }
}

@Composable
private fun ProfileScreen(profile: UserProfile, onSave: (UserProfile) -> Unit) {
    var name by rememberSaveable(profile) { mutableStateOf(profile.name) }
    var age by rememberSaveable(profile) { mutableStateOf(profile.age) }
    var sex by rememberSaveable(profile) { mutableStateOf(profile.sex) }
    var city by rememberSaveable(profile) { mutableStateOf(profile.city) }
    var university by rememberSaveable(profile) { mutableStateOf(profile.university) }
    var saved by rememberSaveable { mutableStateOf(false) }

    FormColumn {
        Text("Datos personales", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
                .background(Brush.linearGradient(listOf(Color(0xFF2A9D8F), Color(0xFFFFD166)))),
            contentAlignment = Alignment.Center
        ) {
            Text(profile.name.take(1).ifBlank { "U" }, color = Color.White, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        }
        OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Nombre") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = age, onValueChange = { age = it.filter(Char::isDigit) }, label = { Text("Edad") }, singleLine = true, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = city, onValueChange = { city = it }, label = { Text("Ciudad") }, singleLine = true, modifier = Modifier.fillMaxWidth())
        DropdownField("Sexo", sex, sexOptions) { sex = it }
        DropdownField("Universidad", university, universities) { university = it }
        Button(
            onClick = {
                onSave(UserProfile(name, age, sex, city, university))
                saved = true
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text("Guardar cambios")
        }
        if (saved) Text("Perfil actualizado en esta sesion.", color = Color(0xFF005B4F))
    }
}

@Composable
private fun ChatScreen(title: String, messages: List<ChatMessage>, onSend: (String) -> Unit) {
    var draft by rememberSaveable { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = title,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(messages) { message ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = if (message.fromMe) Arrangement.End else Arrangement.Start
                ) {
                    Surface(
                        shape = RoundedCornerShape(18.dp),
                        color = if (message.fromMe) Color(0xFF005B4F) else Color.White,
                        tonalElevation = 1.dp,
                        modifier = Modifier.fillMaxWidth(0.78f)
                    ) {
                        Text(
                            text = message.text,
                            color = if (message.fromMe) Color.White else Color(0xFF10201B),
                            modifier = Modifier.padding(14.dp)
                        )
                    }
                }
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = draft,
                onValueChange = { draft = it },
                label = { Text("Mensaje") },
                modifier = Modifier.weight(1f),
                singleLine = true
            )
            Button(onClick = {
                if (draft.isNotBlank()) {
                    onSend(draft)
                    draft = ""
                }
            }) {
                Text("Enviar")
            }
        }
    }
}

@Composable
private fun FormColumn(content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        content = content
    )
}

@Composable
private fun DropdownField(label: String, value: String, options: List<String>, onSelect: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(label, style = MaterialTheme.typography.labelLarge, color = Color(0xFF52645E))
        Box {
            OutlinedButton(
                onClick = { expanded = true },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 14.dp)
            ) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(value, color = Color(0xFF10201B))
                    Text("v", color = Color(0xFF52645E))
                }
            }
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = {
                            expanded = false
                            onSelect(option)
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun FilterChipButton(text: String, selected: Boolean, onClick: () -> Unit) {
    OutlinedButton(
        onClick = onClick,
        shape = RoundedCornerShape(50),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = if (selected) Color(0xFF005B4F) else Color.Transparent,
            contentColor = if (selected) Color.White else Color(0xFF005B4F)
        )
    ) {
        Text(text)
    }
}

@Composable
private fun Tag(text: String, soft: Boolean = false) {
    Surface(
        color = if (soft) Color(0xFFE8F0EE) else Color(0xFFFFD166),
        contentColor = Color(0xFF10201B),
        shape = RoundedCornerShape(50)
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, color = Color(0xFF52645E))
        Spacer(modifier = Modifier.width(12.dp))
        Text(value, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun InfoBlock(label: String, value: String) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(label, color = Color(0xFF52645E), style = MaterialTheme.typography.labelLarge)
        Text(value, color = Color(0xFF10201B))
    }
}

@Composable
private fun ImagePlaceholder(colors: List<Color>, label: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(22.dp))
            .background(Brush.linearGradient(colors)),
        contentAlignment = Alignment.BottomStart
    ) {
        Surface(
            color = Color.Black.copy(alpha = 0.35f),
            contentColor = Color.White,
            shape = RoundedCornerShape(topEnd = 14.dp)
        ) {
            Text(label, modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp), fontWeight = FontWeight.Bold)
        }
    }
}

private fun sampleListings() = listOf(
    Listing(
        id = 1,
        type = "Ofrezco pieza",
        title = "Pieza luminosa cerca de la Universidad de Los Lagos",
        description = "Casa compartida con estudiantes. Incluye internet, cocina equipada y espacios comunes tranquilos para estudiar.",
        price = "180000",
        rules = "No fumar dentro de la casa. Mantener limpieza semanal y avisar visitas con anticipacion.",
        sector = "Chuyaca",
        spots = "1",
        owner = "Camila",
        imageColors = listOf(Color(0xFF2A9D8F), Color(0xFF80CED7), Color(0xFFFFD166), Color(0xFFEF476F), Color(0xFF118AB2))
    ),
    Listing(
        id = 2,
        type = "Ofrezco departamento",
        title = "Departamento compartido en sector Centro",
        description = "Departamento de dos dormitorios, cercano a locomocion, supermercado y bibliotecas. Ideal para dos estudiantes.",
        price = "260000",
        rules = "Gastos comunes se dividen por partes iguales. No mascotas por reglamento del edificio.",
        sector = "Centro",
        spots = "2",
        owner = "Ignacio",
        imageColors = listOf(Color(0xFF457B9D), Color(0xFFA8DADC), Color(0xFFF1FAEE), Color(0xFFE63946), Color(0xFF1D3557))
    ),
    Listing(
        id = 3,
        type = "Busco roomie",
        title = "Busco companera para arrendar casa en Rahue",
        description = "Estoy armando grupo para arrendar una casa tranquila. Busco alguien responsable, estudiante y ordenada.",
        price = "200000",
        rules = "Ambiente de estudio, limpieza compartida y respeto por horarios de descanso.",
        sector = "Rahue",
        spots = "1",
        owner = "Valentina",
        imageColors = listOf(Color(0xFFE76F51), Color(0xFFF4A261), Color(0xFFE9C46A), Color(0xFF2A9D8F), Color(0xFF264653))
    )
)

private fun sampleRoomieRequests() = listOf(
    RoomieRequest(
        type = "Busco roomie",
        title = "Busco companero para compartir depto cerca del Centro",
        sector = "Centro",
        budget = "220000",
        description = "Soy estudiante de Inacap, ordenado y con horarios tranquilos. Me interesa compartir gastos con alguien responsable."
    ),
    RoomieRequest(
        type = "Busco pieza",
        title = "Necesito pieza cerca de locomocion hacia Chuyaca",
        sector = "Rahue",
        budget = "170000",
        description = "Busco pieza para el semestre. Idealmente con internet incluido y ambiente de estudio."
    ),
    RoomieRequest(
        type = "Busco roomie",
        title = "Armando grupo para arrendar casa en Ovejeria",
        sector = "Ovejeria",
        budget = "200000",
        description = "Somos dos estudiantes y buscamos una tercera persona para arrendar casa con espacios comunes."
    )
)

@Preview(showBackground = true)
@Composable
private fun UniroomiesPreview() {
    UniroomiesTheme(dynamicColor = false) {
        UniroomiesApp()
    }
}
