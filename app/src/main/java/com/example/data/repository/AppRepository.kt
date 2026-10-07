package com.example.data.repository

import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build
import com.example.domain.model.AppRiskLevel
import com.example.domain.model.InstalledAppInfo
import com.example.domain.model.PermissionDetail
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AppRepository(private val context: Context) {

    private val sensitivePermissionMap = mapOf(
        android.Manifest.permission.CAMERA to ("Camera" to "Allows taking pictures and recording video."),
        android.Manifest.permission.RECORD_AUDIO to ("Microphone" to "Allows recording audio directly from the device."),
        android.Manifest.permission.ACCESS_FINE_LOCATION to ("Precise Location" to "Accesses exact GPS coordinates."),
        android.Manifest.permission.ACCESS_COARSE_LOCATION to ("Approximate Location" to "Accesses cell/Wi-Fi network based location."),
        android.Manifest.permission.READ_CONTACTS to ("Read Contacts" to "Accesses stored contacts and address book."),
        android.Manifest.permission.READ_SMS to ("Read SMS" to "Accesses text messages."),
        android.Manifest.permission.SEND_SMS to ("Send SMS" to "Allows sending SMS without user confirmation."),
        android.Manifest.permission.READ_CALL_LOG to ("Read Call Log" to "Accesses incoming and outgoing phone calls."),
        android.Manifest.permission.READ_PHONE_STATE to ("Read Phone State" to "Accesses phone identity and network status.")
    )

    private val storagePermissions = setOf(
        "android.permission.READ_EXTERNAL_STORAGE",
        "android.permission.WRITE_EXTERNAL_STORAGE",
        "android.permission.READ_MEDIA_IMAGES",
        "android.permission.READ_MEDIA_VIDEO"
    )

    suspend fun getInstalledApps(): List<InstalledAppInfo> = withContext(Dispatchers.IO) {
        val pm = context.packageManager
        val appList = mutableListOf<InstalledAppInfo>()

        try {
            // First attempt to query launcher intent packages (works on Android 11+ with <queries>)
            val launcherIntent = Intent(Intent.ACTION_MAIN, null).apply {
                addCategory(Intent.CATEGORY_LAUNCHER)
            }
            val resolveInfos = pm.queryIntentActivities(launcherIntent, 0)
            val packageNames = resolveInfos.map { it.activityInfo.packageName }.distinct().toMutableSet()

            // Also add self
            packageNames.add(context.packageName)

            for (pkgName in packageNames) {
                try {
                    val packageInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        pm.getPackageInfo(pkgName, PackageManager.PackageInfoFlags.of(PackageManager.GET_PERMISSIONS.toLong()))
                    } else {
                        @Suppress("DEPRECATION")
                        pm.getPackageInfo(pkgName, PackageManager.GET_PERMISSIONS)
                    }

                    val appInfo = packageInfo.applicationInfo ?: continue
                    val appName = pm.getApplicationLabel(appInfo).toString()
                    val isSystem = (appInfo.flags and ApplicationInfo.FLAG_SYSTEM) != 0

                    val permissionsList = mutableListOf<PermissionDetail>()
                    val requested = packageInfo.requestedPermissions ?: emptyArray()
                    val flags = packageInfo.requestedPermissionsFlags ?: IntArray(0)

                    for (i in requested.indices) {
                        val perm = requested[i]
                        val isGranted = if (i < flags.size) {
                            (flags[i] and PackageInfo.REQUESTED_PERMISSION_GRANTED) != 0
                        } else false

                        val sensitiveEntry = sensitivePermissionMap[perm]
                        val isSensitive = sensitiveEntry != null || storagePermissions.contains(perm)
                        val simpleName = when {
                            sensitiveEntry != null -> sensitiveEntry.first
                            storagePermissions.contains(perm) -> "Storage / Media"
                            else -> perm.substringAfterLast('.')
                        }
                        val desc = sensitiveEntry?.second ?: "Application standard permission."

                        permissionsList.add(
                            PermissionDetail(
                                permission = perm,
                                simpleName = simpleName,
                                description = desc,
                                isGranted = isGranted,
                                isSensitive = isSensitive
                            )
                        )
                    }

                    // Risk calculation based on factual permissions
                    val grantedSensitives = permissionsList.filter { it.isSensitive && it.isGranted }
                    val reasons = mutableListOf<String>()

                    val hasLocation = grantedSensitives.any { it.permission.contains("LOCATION") }
                    val hasMicOrCam = grantedSensitives.any { it.permission.contains("CAMERA") || it.permission.contains("RECORD_AUDIO") }
                    val hasSmsOrContacts = grantedSensitives.any { it.permission.contains("SMS") || it.permission.contains("CONTACTS") }
                    val hasStorage = grantedSensitives.any { storagePermissions.contains(it.permission) }

                    if (hasMicOrCam) reasons.add("Accesses camera or microphone")
                    if (hasLocation) reasons.add("Accesses device location")
                    if (hasSmsOrContacts) reasons.add("Accesses SMS or contact details")
                    if (hasStorage) reasons.add("Accesses shared media or storage")

                    val riskLevel = when {
                        !isSystem && (hasSmsOrContacts && (hasMicOrCam || hasLocation)) -> AppRiskLevel.HIGH
                        !isSystem && grantedSensitives.size >= 4 -> AppRiskLevel.HIGH
                        !isSystem && (hasLocation || hasMicOrCam || hasSmsOrContacts) -> AppRiskLevel.MEDIUM
                        grantedSensitives.size >= 2 -> AppRiskLevel.MEDIUM
                        else -> AppRiskLevel.LOW
                    }

                    appList.add(
                        InstalledAppInfo(
                            appName = appName,
                            packageName = pkgName,
                            versionName = packageInfo.versionName ?: "1.0",
                            targetSdk = appInfo.targetSdkVersion,
                            isSystemApp = isSystem,
                            requestedPermissions = permissionsList,
                            riskLevel = riskLevel,
                            riskReasons = reasons,
                            installTime = packageInfo.firstInstallTime,
                            updateTime = packageInfo.lastUpdateTime
                        )
                    )
                } catch (e: Exception) {
                    // Skip any package that fails to parse
                }
            }
        } catch (e: Exception) {
            // Graceful fallback
        }

        // Sort by risk (High, Medium, Low) then appName
        appList.sortedWith(
            compareBy<InstalledAppInfo> {
                when (it.riskLevel) {
                    AppRiskLevel.HIGH -> 0
                    AppRiskLevel.MEDIUM -> 1
                    AppRiskLevel.LOW -> 2
                }
            }.thenBy { it.appName.lowercase() }
        )
    }
}
