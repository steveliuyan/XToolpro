@file:Suppress("ktlint:standard:function-naming")

package com.steveliuyan.xtoolpro

import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.steveliuyan.xtoolpro.ui.theme.XToolproTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            XToolproTheme {
                XToolproApp()
            }
        }
    }
}

private enum class ShellDestination(
    val route: String,
    val label: Int,
    val glyph: String,
) {
    HOME("home", R.string.nav_home, "⌂"),
    TOOLBOX("toolbox", R.string.nav_toolbox, "◇"),
    TASKS("tasks", R.string.nav_tasks, "✓"),
    SETTINGS("settings", R.string.nav_settings, "⚙"),
}

private data class ToolPreview(
    val title: Int,
    val description: Int,
    val glyph: String,
)

private val toolPreviews =
    listOf(
        ToolPreview(R.string.tool_proxy, R.string.tool_proxy_description, "↯"),
        ToolPreview(R.string.tool_cleaner, R.string.tool_cleaner_description, "⌫"),
        ToolPreview(R.string.tool_media, R.string.tool_media_description, "▶"),
        ToolPreview(R.string.tool_image, R.string.tool_image_description, "▧"),
    )

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Suppress("ktlint:standard:function-naming")
private fun XToolproApp() {
    var route by rememberSaveable { mutableStateOf(ShellDestination.HOME.route) }
    val destination = ShellDestination.entries.firstOrNull { it.route == route } ?: ShellDestination.HOME
    val reducedMotion = rememberReducedMotion()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val notifyUnavailable: (Int) -> Unit = { messageRes ->
        scope.launch { snackbarHostState.showSnackbar(context.getString(messageRes)) }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            ShellTopBar(
                title = stringResource(destination.label),
                onInfo = { notifyUnavailable(R.string.shell_info_unavailable) },
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 0.dp,
                modifier = Modifier.border(1.dp, MaterialTheme.colorScheme.outlineVariant),
            ) {
                ShellDestination.entries.forEach { item ->
                    NavigationBarItem(
                        selected = item == destination,
                        onClick = { route = item.route },
                        icon = {
                            Glyph(
                                value = item.glyph,
                                contentDescription = stringResource(item.label),
                                emphasized = item == destination,
                            )
                        },
                        label = { Text(text = stringResource(item.label), maxLines = 1) },
                        alwaysShowLabel = true,
                    )
                }
            }
        },
    ) { paddingValues ->
        AnimatedContent(
            targetState = destination,
            modifier = Modifier.fillMaxSize().padding(paddingValues),
            transitionSpec = {
                if (reducedMotion) {
                    EnterTransition.None togetherWith ExitTransition.None
                } else {
                    fadeIn(XToolproMotion.standard) togetherWith fadeOut(XToolproMotion.fast)
                }
            },
            label = "shell_destination_transition",
        ) { targetDestination ->
            when (targetDestination) {
                ShellDestination.HOME -> HomeScreen(onOpenToolbox = { route = ShellDestination.TOOLBOX.route })
                ShellDestination.TOOLBOX -> ToolboxScreen(onUnavailable = notifyUnavailable)
                ShellDestination.TASKS -> TasksScreen()
                ShellDestination.SETTINGS -> SettingsScreen(onUnavailable = notifyUnavailable)
            }
        }
    }
}

@Composable
private fun ShellTopBar(
    title: String,
    onInfo: () -> Unit,
) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        modifier = Modifier.fillMaxWidth().border(1.dp, MaterialTheme.colorScheme.outlineVariant),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().height(70.dp).padding(horizontal = 18.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Image(
                painter = painterResource(R.drawable.ic_xtoolpro_mark),
                contentDescription = stringResource(R.string.app_name),
                modifier = Modifier.size(32.dp).clip(RoundedCornerShape(8.dp)),
            )
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(R.string.app_name),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            IconButton(
                onClick = onInfo,
                modifier = Modifier.size(48.dp).focusable().semantics { role = Role.Button },
            ) {
                Glyph("ⓘ", stringResource(R.string.action_more_information))
            }
        }
    }
}

@Composable
private fun HomeScreen(onOpenToolbox: () -> Unit) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 18.dp, vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            Text(
                text = stringResource(R.string.home_greeting),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = stringResource(R.string.home_subtitle),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        item {
            StatusNotice(
                title = stringResource(R.string.home_foundation_title),
                message = stringResource(R.string.home_foundation_message),
            )
        }
        item { SectionHeading(text = stringResource(R.string.home_quick_tools)) }
        item {
            BoxWithConstraints {
                val columns = if (maxWidth >= 600.dp) 4 else 2
                LazyVerticalGrid(
                    columns = GridCells.Fixed(columns),
                    userScrollEnabled = false,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.height(if (columns == 2) 228.dp else 108.dp),
                ) {
                    items(toolPreviews) { tool -> ToolCard(tool = tool, onClick = onOpenToolbox) }
                }
            }
        }
        item {
            OutlinedAction(
                label = stringResource(R.string.home_open_toolbox),
                onClick = onOpenToolbox,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
private fun ToolboxScreen(onUnavailable: (Int) -> Unit) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 18.dp, vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            Text(
                text = stringResource(R.string.toolbox_title),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = stringResource(R.string.toolbox_subtitle),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        item { SectionHeading(text = stringResource(R.string.toolbox_available_later)) }
        item {
            BoxWithConstraints {
                val columns =
                    when {
                        maxWidth >= 840.dp -> 4
                        maxWidth >= 600.dp -> 3
                        else -> 2
                    }
                LazyVerticalGrid(
                    columns = GridCells.Fixed(columns),
                    userScrollEnabled = false,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.height(if (columns <= 2) 228.dp else 114.dp),
                ) {
                    items(toolPreviews) { tool ->
                        ToolCard(tool = tool, onClick = { onUnavailable(R.string.feature_unavailable) })
                    }
                }
            }
        }
        item {
            StatusNotice(
                title = stringResource(R.string.toolbox_unavailable_title),
                message = stringResource(R.string.toolbox_unavailable_message),
            )
        }
    }
}

@Composable
private fun TasksScreen() {
    EmptyState(
        glyph = "✓",
        title = stringResource(R.string.tasks_empty_title),
        message = stringResource(R.string.tasks_empty_message),
    )
}

@Composable
private fun SettingsScreen(onUnavailable: (Int) -> Unit) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 18.dp, vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            Text(
                text = stringResource(R.string.settings_title),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = stringResource(R.string.settings_subtitle),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        item { HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant) }
        items(
            listOf(
                R.string.settings_language to "文",
                R.string.settings_theme to "◐",
                R.string.settings_about to "ⓘ",
            ),
        ) { (label, glyph) ->
            SettingsRow(
                label = stringResource(label),
                glyph = glyph,
                onClick = { onUnavailable(R.string.settings_unavailable) },
            )
        }
        item {
            StatusNotice(
                title = stringResource(R.string.settings_unavailable_title),
                message = stringResource(R.string.settings_unavailable_message),
            )
        }
    }
}

@Composable
private fun ToolCard(
    tool: ToolPreview,
    onClick: () -> Unit,
) {
    OutlinedCard(
        onClick = onClick,
        shape = RoundedCornerShape(8.dp),
        border = CardDefaults.outlinedCardBorder(),
        colors = CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth().height(104.dp).semantics { role = Role.Button },
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(34.dp).clip(RoundedCornerShape(8.dp)).background(MaterialTheme.colorScheme.secondaryContainer),
            ) {
                Text(
                    text = tool.glyph,
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
            Column {
                Text(
                    text = stringResource(tool.title),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = stringResource(tool.description),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

@Composable
private fun StatusNotice(
    title: String,
    message: String,
) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant,
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.fillMaxWidth().border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(8.dp)),
    ) {
        Row(modifier = Modifier.fillMaxWidth().padding(14.dp), verticalAlignment = Alignment.Top) {
            Text(
                text = "•",
                color = MaterialTheme.colorScheme.primary,
                fontSize = 20.sp,
                lineHeight = 20.sp,
                modifier = Modifier.padding(end = 10.dp),
            )
            Column {
                Text(text = title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(3.dp))
                Text(text = message, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
private fun EmptyState(
    glyph: String,
    title: String,
    message: String,
) {
    Box(modifier = Modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
            Text(text = glyph, color = MaterialTheme.colorScheme.primary, fontSize = 42.sp)
            Spacer(Modifier.height(12.dp))
            Text(text = title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(6.dp))
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
private fun SettingsRow(
    label: String,
    glyph: String,
    onClick: () -> Unit,
) {
    Surface(
        onClick = onClick,
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.fillMaxWidth().height(64.dp).semantics { role = Role.Button },
    ) {
        Row(
            modifier = Modifier.fillMaxSize().padding(horizontal = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(35.dp).clip(RoundedCornerShape(8.dp)).background(MaterialTheme.colorScheme.secondaryContainer),
            ) { Text(text = glyph, color = MaterialTheme.colorScheme.primary, fontSize = 18.sp) }
            Spacer(Modifier.width(12.dp))
            Text(text = label, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f))
            Text(text = "›", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 24.sp)
        }
    }
}

@Composable
private fun SectionHeading(text: String) {
    Text(text = text, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
}

@Composable
private fun OutlinedAction(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        onClick = onClick,
        color = Color.Transparent,
        shape = RoundedCornerShape(8.dp),
        modifier = modifier.height(48.dp).border(1.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(8.dp)),
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
            Text(text = label, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
private fun Glyph(
    value: String,
    contentDescription: String?,
    emphasized: Boolean = false,
) {
    Text(
        text = value,
        color = if (emphasized) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
        fontSize = 21.sp,
        fontWeight = if (emphasized) FontWeight.Bold else FontWeight.Normal,
        modifier =
            Modifier.semantics {
                if (contentDescription != null) this.contentDescription = contentDescription
            },
    )
}

@Composable
private fun rememberReducedMotion(): Boolean {
    val context = LocalContext.current
    return remember(context) {
        Settings.Global.getFloat(
            context.contentResolver,
            Settings.Global.TRANSITION_ANIMATION_SCALE,
            1f,
        ) == 0f
    }
}

private object XToolproMotion {
    val fast = androidx.compose.animation.core.tween<Float>(150)
    val standard = androidx.compose.animation.core.tween<Float>(220)
}
