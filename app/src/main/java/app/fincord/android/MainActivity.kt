package app.fincord.android

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val AppBg = Color(0xFF232428)
private val Rail = Color(0xFF1E1F22)
private val Panel = Color(0xFF2B2D31)
private val Muted = Color(0xFFB5BAC1)
private val Blurple = Color(0xFF5865F2)
private val DeletedRed = Color(0xFFED4245)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { FinCordApp() }
    }
}

private data class Plugin(val title: String, val description: String, val category: String, val initial: Boolean = false)

@Composable
fun FinCordApp() {
    var page by remember { mutableStateOf("Chat") }
    var channel by remember { mutableStateOf("general") }
    var wallpaper by remember { mutableStateOf<Uri?>(null) }
    var audioFiles by remember { mutableStateOf(listOf<Uri>()) }
    var audioNames by remember { mutableStateOf(listOf<String>()) }
    val pluginStates = remember {
        mutableStateMapOf(
            "Deleted Message Log" to true,
            "Custom Background" to false,
            "Badge Collection" to true,
            "Music Integrations" to false,
            "Voice Studio" to true,
            "Account Integrations" to false,
            "Plugin Marketplace" to true
        )
    }
    val wallpaperPicker = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri != null) wallpaper = uri
    }
    val audioPicker = rememberLauncherForActivityResult(ActivityResultContracts.OpenMultipleDocuments()) { uris ->
        if (uris.isNotEmpty()) {
            audioFiles = audioFiles + uris
            audioNames = audioNames + uris.map { it.lastPathSegment?.substringAfterLast('/') ?: "audio.mp3" }
        }
    }

    MaterialTheme(colorScheme = darkColorScheme(background = AppBg, surface = Panel, primary = Blurple, onBackground = Color.White, onSurface = Color.White)) {
        Row(Modifier.fillMaxSize().background(AppBg)) {
            NavigationRail(page) { page = it }
            Column(Modifier.weight(1f).fillMaxHeight()) {
                when (page) {
                    "Chat" -> ChatPage(channel) { channel = it }
                    "FinCord" -> SettingsPage(pluginStates, { wallpaperPicker.launch(arrayOf("image/*")) }, wallpaper != null)
                    "Music" -> MusicPage(audioNames, { audioPicker.launch(arrayOf("audio/*")) })
                    "Badges" -> BadgePage()
                    else -> ProfilePage()
                }
            }
        }
    }
}

@Composable
private fun NavigationRail(page: String, onPage: (String) -> Unit) {
    Column(Modifier.width(64.dp).fillMaxHeight().background(Rail).padding(vertical = 12.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(13.dp)) {
        listOf("Chat", "FinCord", "Music", "Badges", "Profile").forEach { item ->
            Box(Modifier.size(44.dp).background(if (page == item) Blurple else Panel, CircleShape).clickable { onPage(item) }, contentAlignment = Alignment.Center) {
                Icon(when (item) {
                    "Chat" -> Icons.Default.Chat
                    "FinCord" -> Icons.Default.Tune
                    "Music" -> Icons.Default.Headphones
                    "Badges" -> Icons.Default.EmojiEvents
                    else -> Icons.Default.Person
                }, contentDescription = item, tint = Color.White)
            }
        }
        Spacer(Modifier.weight(1f))
        Text("FC", color = Blurple, fontWeight = FontWeight.Black)
    }
}

@Composable
private fun ChatPage(channel: String, onChannel: (String) -> Unit) {
    Column(Modifier.fillMaxSize()) {
        Row(Modifier.fillMaxWidth().background(Panel).padding(15.dp), verticalAlignment = Alignment.CenterVertically) {
            Text("#  $channel", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Spacer(Modifier.weight(1f))
            Icon(Icons.Default.Search, null, tint = Muted)
        }
        Row(Modifier.weight(1f)) {
            Column(Modifier.width(122.dp).fillMaxHeight().background(Panel).padding(8.dp), verticalArrangement = Arrangement.spacedBy(7.dp)) {
                Text("MY SERVER", color = Muted, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                listOf("general", "media", "gaming", "off-topic").forEach {
                    Text("#  $it", Modifier.fillMaxWidth().background(if (channel == it) Color(0xFF404249) else Color.Transparent, RoundedCornerShape(7.dp)).clickable { onChannel(it) }.padding(8.dp), color = if (channel == it) Color.White else Muted, fontSize = 12.sp)
                }
            }
            Column(Modifier.weight(1f).fillMaxHeight().padding(12.dp)) {
                Spacer(Modifier.weight(1f))
                Text("Bun venit în #$channel", fontWeight = FontWeight.Bold)
                Text("Ecran demonstrativ. Integrarea Discord reală și sincronizarea chatului urmează.", color = Muted, fontSize = 12.sp)
                Spacer(Modifier.height(12.dp))
                Text("Mesajele șterse capturate de un bot autorizat vor putea apărea aici cu roșu.", color = DeletedRed, fontSize = 11.sp)
            }
        }
    }
}

@Composable
private fun SettingsPage(states: MutableMap<String, Boolean>, onWallpaper: () -> Unit, wallpaperChosen: Boolean) {
    val groups = listOf(
        "CHAT" to listOf(Plugin("Deleted Message Log", "Log roșu pentru mesaje capturate înainte de ștergere", "CHAT", true)),
        "APPEARANCE" to listOf(Plugin("Custom Background", "Alege imagine din galerie", "APPEARANCE")),
        "BADGES" to listOf(Plugin("Badge Collection", "Catalog vizual Hypersquad și badge-uri; fără acordare pe profilul Discord", "BADGES", true)),
        "MUSIC" to listOf(Plugin("Music Integrations", "Conectori OAuth/API unde platforma permite", "MUSIC")),
        "VOICE" to listOf(Plugin("Voice Studio", "Bibliotecă audio și pregătire fișiere pentru atașare", "VOICE", true)),
        "ACCOUNTS" to listOf(Plugin("Account Integrations", "Conectare prin OAuth; nu introduce tokenuri personale", "ACCOUNTS")),
        "SYSTEM" to listOf(Plugin("Plugin Marketplace", "Bibliotecă extensibilă pentru module FinCord", "SYSTEM", true))
    )
    LazyColumn(Modifier.fillMaxSize().background(AppBg).padding(15.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        item {
            Text("FinCord Settings", fontSize = 24.sp, fontWeight = FontWeight.ExtraBold)
            Text("Pluginuri și personalizare", color = Muted, fontSize = 12.sp)
        }
        item {
            Card(colors = CardDefaults.cardColors(containerColor = Panel)) {
                Column(Modifier.padding(13.dp), verticalArrangement = Arrangement.spacedBy(7.dp)) {
                    Text("DISCORD ACCOUNT", color = Blurple, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    Text("OAuth2 oficial — în lucru", fontWeight = FontWeight.SemiBold)
                    Text("FinCord nu solicită și nu extrage tokenuri de utilizator.", color = Muted, fontSize = 11.sp)
                }
            }
        }
        groups.forEach { (heading, plugins) ->
            item { Text(heading, color = Blurple, fontSize = 10.sp, fontWeight = FontWeight.Bold) }
            items(plugins) { plugin ->
                Card(colors = CardDefaults.cardColors(containerColor = Panel)) {
                    Row(Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Column(Modifier.weight(1f)) {
                            Text(plugin.title, fontWeight = FontWeight.SemiBold)
                            Text(plugin.description, color = Muted, fontSize = 10.sp)
                        }
                        Switch(checked = states[plugin.title] ?: plugin.initial, onCheckedChange = { states[plugin.title] = it })
                    }
                }
            }
        }
        item {
            Card(colors = CardDefaults.cardColors(containerColor = Panel)) {
                Column(Modifier.padding(13.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("WALLPAPER", color = Blurple, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    Text(if (wallpaperChosen) "Imagine selectată" else "Alege un fundal")
                    Button(onClick = onWallpaper) { Text("Choose image") }
                }
            }
        }
    }
}

@Composable
private fun MusicPage(audioNames: List<String>, onAdd: () -> Unit) {
    LazyColumn(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            Text("Music & Voice Studio", fontSize = 22.sp, fontWeight = FontWeight.ExtraBold)
            Text("Conectări muzicale și biblioteca audio", color = Muted, fontSize = 12.sp)
        }
        item {
            Card(colors = CardDefaults.cardColors(containerColor = Panel)) {
                Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("MUSIC INTEGRATIONS", color = Blurple, fontWeight = FontWeight.Bold, fontSize = 10.sp)
                    listOf("Spotify", "YouTube Music", "Apple Music", "SoundCloud", "Other services").forEach {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.MusicNote, null, tint = Muted)
                            Spacer(Modifier.width(9.dp))
                            Text(it, Modifier.weight(1f))
                            Text("OAuth/API", color = Muted, fontSize = 10.sp)
                        }
                    }
                    Text("Afișarea ca Discord activity depinde de suportul oficial al platformei și de integrarea disponibilă.", color = Muted, fontSize = 10.sp)
                }
            }
        }
        item {
            Card(colors = CardDefaults.cardColors(containerColor = Panel)) {
                Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("VOICE STUDIO", color = Blurple, fontWeight = FontWeight.Bold, fontSize = 10.sp)
                    Text("Adaugă fișiere audio din telefon")
                    Button(onClick = onAdd) { Icon(Icons.Default.Add, null); Spacer(Modifier.width(6.dp)); Text("Import audio files") }
                    Text("Fișierele pot fi pregătite ca atașamente. Trimiterea vocală nativă depinde de API-ul oficial.", color = Muted, fontSize = 10.sp)
                }
            }
        }
        if (audioNames.isNotEmpty()) {
            item { Text("YOUR AUDIO FILES", color = Blurple, fontWeight = FontWeight.Bold, fontSize = 10.sp) }
            items(audioNames) { name ->
                Card(colors = CardDefaults.cardColors(containerColor = Panel)) {
                    Row(Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.AudioFile, null, tint = Blurple)
                        Spacer(Modifier.width(10.dp))
                        Text(name, fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun BadgePage() {
    val badges = listOf("House of Bravery", "House of Brilliance", "House of Balance", "Early Supporter", "Active Developer", "Nitro", "Server Booster")
    LazyColumn(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        item {
            Text("Badge Collection", fontSize = 22.sp, fontWeight = FontWeight.ExtraBold)
            Text("Catalog informativ și previzualizări locale", color = Muted, fontSize = 12.sp)
        }
        items(badges) { badge ->
            Card(colors = CardDefaults.cardColors(containerColor = Panel)) {
                Row(Modifier.fillMaxWidth().padding(13.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.EmojiEvents, null, tint = Blurple)
                    Spacer(Modifier.width(12.dp))
                    Text(badge, Modifier.weight(1f), fontWeight = FontWeight.SemiBold)
                    Text("Preview", color = Muted, fontSize = 10.sp)
                }
            }
        }
        item { Text("Previzualizările nu acordă badge-uri oficiale și nu modifică profilul Discord.", color = Muted, fontSize = 10.sp) }
    }
}

@Composable
private fun ProfilePage() {
    Column(Modifier.fillMaxSize().padding(20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text("Profile", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Text("Profilul Discord va apărea după integrarea OAuth2.", color = Muted)
    }
}
