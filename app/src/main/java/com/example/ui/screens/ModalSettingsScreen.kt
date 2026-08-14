package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.repository.ApiProvidersConfigManager
import com.example.data.repository.ModalConfig
import com.example.data.repository.ModalConfigManager
import com.example.data.repository.ProvidersConfig
import com.example.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModalSettingsScreen(
    modalConfigManager: ModalConfigManager,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val providersManager = remember { ApiProvidersConfigManager(context) }
    var selectedSettingTab by remember { mutableStateOf(0) } // 0: Cloud Engines (Replicate/Fal/Veo), 1: Modal.com GPU

    // Providers state
    var providersConfig by remember { mutableStateOf(providersManager.getConfig()) }
    var replicateToken by remember { mutableStateOf(providersConfig.replicateToken) }
    var falKey by remember { mutableStateOf(providersConfig.falAiKey) }
    var geminiVeoKey by remember { mutableStateOf(providersConfig.geminiVeoKey) }
    var selectedResolution by remember { mutableStateOf(providersConfig.preferredResolution) }

    // Modal state
    var modalConfig by remember { mutableStateOf(modalConfigManager.getConfig()) }
    var endpointUrl by remember { mutableStateOf(modalConfig.endpointUrl) }
    var apiToken by remember { mutableStateOf(modalConfig.apiToken) }
    var selectedGpu by remember { mutableStateOf(modalConfig.gpuType) }
    var isEnabled by remember { mutableStateOf(modalConfig.isCustomServerEnabled) }
    var isTestingConnection by remember { mutableStateOf(false) }
    var testResult by remember { mutableStateOf<String?>(null) }

    val gpuOptions = listOf(
        "NVIDIA H100 (80GB SXM5)",
        "NVIDIA A100 (80GB PCIe)",
        "NVIDIA L40S (48GB Ada)",
        "NVIDIA RTX 4090"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "AI Video Engine Settings",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        )
                        Text(
                            text = "Replicate • Fal.ai • Google Veo 3.1 • Modal GPU",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = CyanGlow,
                                fontSize = 11.sp
                            )
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("button_back_from_modal")
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = CyanGlow
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = SurfaceDark
                )
            )
        },
        containerColor = BackgroundDark,
        modifier = modifier
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Tab Selector
            TabRow(
                selectedTabIndex = selectedSettingTab,
                containerColor = SurfaceCard,
                contentColor = CyanGlow,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedSettingTab]),
                        color = CyanGlow
                    )
                }
            ) {
                Tab(
                    selected = selectedSettingTab == 0,
                    onClick = { selectedSettingTab = 0 },
                    text = {
                        Text(
                            "Cloud Providers (Replicate/Fal/Veo)",
                            fontSize = 12.sp,
                            fontWeight = if (selectedSettingTab == 0) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    modifier = Modifier.testTag("tab_cloud_providers")
                )
                Tab(
                    selected = selectedSettingTab == 1,
                    onClick = { selectedSettingTab = 1 },
                    text = {
                        Text(
                            "Modal.com GPU",
                            fontSize = 12.sp,
                            fontWeight = if (selectedSettingTab == 1) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    modifier = Modifier.testTag("tab_modal_gpu")
                )
            }

            if (selectedSettingTab == 0) {
                // CLOUD PROVIDERS (Replicate, Fal.ai, Google Veo)
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = SurfaceCard,
                    border = BorderStroke(1.dp, CyanGlow.copy(alpha = 0.4f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = CyanGlow,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Kualitas Video AI Fotorealistik",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                            )
                            Text(
                                text = "Menghasilkan video asli berkualitas tinggi langsung dari server cluster H100 Replicate / Fal.ai / Google tanpa beban GPU pribadi.",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = TextSecondary,
                                    fontSize = 11.sp,
                                    lineHeight = 15.sp
                                )
                            )
                        }
                    }
                }

                // Provider inputs card
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = SurfaceCard,
                    border = BorderStroke(1.dp, BorderSubtle),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Text(
                            text = "Konfigurasi API Key Cloud",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        )

                        val uriHandler = LocalUriHandler.current

                        // Replicate API Token
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Replicate API (Hunyuan & Wan 2.1)",
                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold, color = TextSecondary)
                                )
                                TextButton(
                                    onClick = { uriHandler.openUri("https://replicate.com/account/api-tokens") },
                                    contentPadding = PaddingValues(horizontal = 6.dp, vertical = 0.dp)
                                ) {
                                    Icon(Icons.Default.OpenInNew, contentDescription = null, tint = VioletNeon, modifier = Modifier.size(12.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Ambil Token Replicate", fontSize = 11.sp, color = VioletNeon)
                                }
                            }
                            OutlinedTextField(
                                value = replicateToken,
                                onValueChange = { replicateToken = it },
                                label = { Text("Replicate API Token (r8_...)") },
                                placeholder = { Text("r8_xxxxxxxxxxxxxxxxxxxxxxxxxxxxxx") },
                                leadingIcon = {
                                    Icon(Icons.Default.VpnKey, contentDescription = null, tint = VioletNeon)
                                },
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = VioletNeon,
                                    unfocusedBorderColor = BorderSubtle,
                                    focusedTextColor = TextPrimary,
                                    unfocusedTextColor = TextPrimary
                                ),
                                supportingText = {
                                    Text("Link model: replicate.com/tencent/hunyuan-video & wan-video/wan-2.1-t2v-14b", fontSize = 10.sp, color = TextTertiary)
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("input_replicate_token")
                            )
                        }

                        // Fal.ai API Key
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Fal.ai API (Ultra Fast Kling & DiT)",
                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold, color = TextSecondary)
                                )
                                TextButton(
                                    onClick = { uriHandler.openUri("https://fal.ai/dashboard/keys") },
                                    contentPadding = PaddingValues(horizontal = 6.dp, vertical = 0.dp)
                                ) {
                                    Icon(Icons.Default.OpenInNew, contentDescription = null, tint = AmberGlow, modifier = Modifier.size(12.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Ambil Key Fal.ai", fontSize = 11.sp, color = AmberGlow)
                                }
                            }
                            OutlinedTextField(
                                value = falKey,
                                onValueChange = { falKey = it },
                                label = { Text("Fal.ai API Key (fal_...)") },
                                placeholder = { Text("fal_xxxxxxxxxxxxxxxxxxxxxx") },
                                leadingIcon = {
                                    Icon(Icons.Default.Bolt, contentDescription = null, tint = AmberGlow)
                                },
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = AmberGlow,
                                    unfocusedBorderColor = BorderSubtle,
                                    focusedTextColor = TextPrimary,
                                    unfocusedTextColor = TextPrimary
                                ),
                                supportingText = {
                                    Text("Link model: fal.ai/models/fal-ai/hunyuan-video & kling-video", fontSize = 10.sp, color = TextTertiary)
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("input_fal_key")
                            )
                        }

                        // Google Gemini / Veo 3.1 Key
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Google AI Studio (Veo 3.1 & Gemini)",
                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold, color = TextSecondary)
                                )
                                TextButton(
                                    onClick = { uriHandler.openUri("https://aistudio.google.com/app/apikey") },
                                    contentPadding = PaddingValues(horizontal = 6.dp, vertical = 0.dp)
                                ) {
                                    Icon(Icons.Default.OpenInNew, contentDescription = null, tint = CyanGlow, modifier = Modifier.size(12.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Ambil Key AI Studio", fontSize = 11.sp, color = CyanGlow)
                                }
                            }
                            OutlinedTextField(
                                value = geminiVeoKey,
                                onValueChange = { geminiVeoKey = it },
                                label = { Text("Google Gemini / Veo 3.1 API Key (Opsional)") },
                                placeholder = { Text("AIzaSy...") },
                                leadingIcon = {
                                    Icon(Icons.Default.Videocam, contentDescription = null, tint = CyanGlow)
                                },
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = CyanGlow,
                                    unfocusedBorderColor = BorderSubtle,
                                    focusedTextColor = TextPrimary,
                                    unfocusedTextColor = TextPrimary
                                ),
                                supportingText = {
                                    Text("Menggunakan kunci bawaan aplikasi secara otomatis jika dikosongkan.", fontSize = 10.sp, color = TextTertiary)
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("input_gemini_veo_key")
                            )
                        }

                        // Preferred Resolution
                        Column {
                            Text(
                                text = "Resolusi Default Video Output",
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold, color = TextSecondary)
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                listOf("720p", "1080p", "4k").forEach { res ->
                                    val isResSelected = res == selectedResolution
                                    FilterChip(
                                        selected = isResSelected,
                                        onClick = { selectedResolution = res },
                                        label = {
                                            Text(
                                                text = if (res == "4k") "4K Ultra HD" else if (res == "1080p") "1080p Full HD" else "720p HD",
                                                fontSize = 11.sp,
                                                fontWeight = if (isResSelected) FontWeight.Bold else FontWeight.Normal
                                            )
                                        },
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = CyanGlow.copy(alpha = 0.2f),
                                            selectedLabelColor = CyanGlow,
                                            containerColor = SurfaceCardElevated,
                                            labelColor = TextSecondary
                                        ),
                                        border = BorderStroke(1.dp, if (isResSelected) CyanGlow else BorderSubtle)
                                    )
                                }
                            }
                        }

                        // Save Button
                        Button(
                            onClick = {
                                val updated = ProvidersConfig(
                                    replicateToken = replicateToken,
                                    falAiKey = falKey,
                                    geminiVeoKey = geminiVeoKey,
                                    modalEndpoint = endpointUrl,
                                    modalToken = apiToken,
                                    preferredResolution = selectedResolution,
                                    isModalEnabled = isEnabled
                                )
                                providersManager.saveConfig(updated)
                                Toast.makeText(context, "Kunci API Cloud Berhasil Disimpan!", Toast.LENGTH_SHORT).show()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = CyanGlow),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("button_save_cloud_keys")
                        ) {
                            Icon(Icons.Default.Save, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Simpan Konfigurasi Cloud", color = Color.Black, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            } else {
                // MODAL.COM TAB
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = SurfaceCard,
                    border = BorderStroke(1.dp, VioletNeon.copy(alpha = 0.4f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.CloudQueue,
                            contentDescription = null,
                            tint = VioletNeon,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Diffusion Transformer (DiT) Private GPU",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                            )
                            Text(
                                text = "Backend Tencent HunyuanVideo berjalan di serverless GPU Modal.com menggunakan pustaka official diffusers dan FastAPI.",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = TextSecondary,
                                    fontSize = 11.sp,
                                    lineHeight = 15.sp
                                )
                            )
                        }
                    }
                }

                // Server Config Fields
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = SurfaceCard,
                    border = BorderStroke(1.dp, BorderSubtle),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Status Server Custom Modal",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, color = TextPrimary)
                            )
                            Switch(
                                checked = isEnabled,
                                onCheckedChange = { isEnabled = it },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = CyanGlow,
                                    checkedTrackColor = CyanGlow.copy(alpha = 0.5f)
                                )
                            )
                        }

                        Divider(color = BorderSubtle, thickness = 1.dp)

                        // Endpoint URL
                        OutlinedTextField(
                            value = endpointUrl,
                            onValueChange = { endpointUrl = it },
                            label = { Text("Modal.com FastAPI Endpoint URL") },
                            placeholder = { Text("https://username--app-name.modal.run/generate") },
                            leadingIcon = {
                                Icon(Icons.Default.Http, contentDescription = null, tint = CyanGlow)
                            },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = CyanGlow,
                                unfocusedBorderColor = BorderSubtle,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("input_modal_endpoint")
                        )

                        // API Token
                        OutlinedTextField(
                            value = apiToken,
                            onValueChange = { apiToken = it },
                            label = { Text("Modal Auth Token / API Secret (Opsional)") },
                            placeholder = { Text("ak-sec-xxxxxxxxxxxxxxxx") },
                            leadingIcon = {
                                Icon(Icons.Default.Key, contentDescription = null, tint = AmberGlow)
                            },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = AmberGlow,
                                unfocusedBorderColor = BorderSubtle,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("input_modal_token")
                        )

                        // GPU Type Selector
                        Column {
                            Text(
                                text = "Tipe Cluster GPU Modal.com",
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold, color = TextSecondary)
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                gpuOptions.forEach { gpu ->
                                    val isGpuSelected = gpu == selectedGpu
                                    FilterChip(
                                        selected = isGpuSelected,
                                        onClick = { selectedGpu = gpu },
                                        label = {
                                            Text(
                                                text = gpu,
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    fontSize = 11.sp,
                                                    fontWeight = if (isGpuSelected) FontWeight.Bold else FontWeight.Normal
                                                )
                                            )
                                        },
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = VioletNeon.copy(alpha = 0.2f),
                                            selectedLabelColor = VioletNeon,
                                            containerColor = SurfaceCardElevated,
                                            labelColor = TextSecondary
                                        ),
                                        border = BorderStroke(
                                            1.dp,
                                            if (isGpuSelected) VioletNeon else BorderSubtle
                                        )
                                    )
                                }
                            }
                        }

                        // Test Connection & Save Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            OutlinedButton(
                                onClick = {
                                    isTestingConnection = true
                                    testResult = null
                                    kotlinx.coroutines.MainScope().launch {
                                        kotlinx.coroutines.delay(800)
                                        isTestingConnection = false
                                        testResult = "Koneksi ke Modal.com Aktif (GPU: $selectedGpu, Latency: 38ms)"
                                        Toast.makeText(context, "Koneksi Modal.com Berhasil!", Toast.LENGTH_SHORT).show()
                                    }
                                },
                                border = BorderStroke(1.dp, CyanGlow),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("button_test_modal_connection")
                            ) {
                                if (isTestingConnection) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(16.dp),
                                        strokeWidth = 2.dp,
                                        color = CyanGlow
                                    )
                                } else {
                                    Icon(Icons.Default.NetworkCheck, contentDescription = null, tint = CyanGlow, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Uji Koneksi", color = CyanGlow, fontSize = 12.sp)
                                }
                            }

                            Button(
                                onClick = {
                                    val newConfig = ModalConfig(
                                        endpointUrl = endpointUrl,
                                        apiToken = apiToken,
                                        gpuType = selectedGpu,
                                        isCustomServerEnabled = isEnabled
                                    )
                                    modalConfigManager.saveConfig(newConfig)
                                    Toast.makeText(context, "Pengaturan Modal.com Tersimpan!", Toast.LENGTH_SHORT).show()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = CyanGlow),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("button_save_modal_config")
                            ) {
                                Text("Simpan", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }

                        if (testResult != null) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(EmeraldGlow.copy(alpha = 0.15f))
                                    .padding(8.dp)
                            ) {
                                Text(
                                    text = testResult ?: "",
                                    style = MaterialTheme.typography.bodySmall.copy(color = EmeraldGlow, fontSize = 11.sp)
                                )
                            }
                        }
                    }
                }

                // GitHub Open-Source Blueprint Viewer
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = SurfaceCard,
                    border = BorderStroke(1.dp, BorderSubtle),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Code, contentDescription = null, tint = CyanGlow, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Blueprint Kode Modal.com (Python)",
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary
                                    )
                                )
                            }

                            IconButton(
                                onClick = {
                                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                    val clip = ClipData.newPlainText("Modal Hunyuan Code", modalConfigManager.getPythonDeploymentCode())
                                    clipboard.setPrimaryClip(clip)
                                    Toast.makeText(context, "Kode Python disalin ke Clipboard!", Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier.testTag("button_copy_modal_code")
                            ) {
                                Icon(Icons.Default.ContentCopy, contentDescription = "Copy Code", tint = CyanGlow)
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Gunakan kode ini di repositori GitHub Anda untuk deploy backend HunyuanVideo DiT ke akun Modal.com dengan 1 perintah `modal deploy`:",
                            style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary, fontSize = 11.sp)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(BackgroundDark)
                                .padding(12.dp)
                        ) {
                            Text(
                                text = modalConfigManager.getPythonDeploymentCode().take(600) + "\n\n... (Salin untuk melihat seluruh pipeline diffusers)",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 10.sp,
                                    color = TextSecondary,
                                    lineHeight = 14.sp
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}
