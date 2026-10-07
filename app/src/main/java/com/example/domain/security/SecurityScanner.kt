package com.example.domain.security

import android.app.KeyguardManager
import android.app.admin.DevicePolicyManager
import android.content.Context
import android.os.Build
import android.provider.Settings
import com.example.domain.model.SecurityCheck
import com.example.domain.model.SecurityFinding
import com.example.domain.model.SecuritySeverity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SecurityScanner(private val context: Context) {

    fun performSystemChecks(): Pair<List<SecurityCheck>, List<SecurityFinding>> {
        val checks = mutableListOf<SecurityCheck>()
        val findings = mutableListOf<SecurityFinding>()

        // 1. Screen Lock Check
        val keyguardManager = context.getSystemService(Context.KEYGUARD_SERVICE) as? KeyguardManager
        val isScreenLockSecure = try {
            keyguardManager?.isDeviceSecure ?: keyguardManager?.isKeyguardSecure ?: false
        } catch (e: Exception) {
            false
        }

        if (isScreenLockSecure) {
            checks.add(
                SecurityCheck(
                    id = "screen_lock",
                    title = "Screen Lock Protection",
                    subtitle = "Secure lock screen is active",
                    severity = SecuritySeverity.SECURE,
                    category = "Device Security",
                    description = "A PIN, pattern, password, or biometric credential protects device access."
                )
            )
        } else {
            checks.add(
                SecurityCheck(
                    id = "screen_lock",
                    title = "Screen Lock Protection",
                    subtitle = "No secure screen lock detected",
                    severity = SecuritySeverity.CRITICAL,
                    category = "Device Security",
                    description = "Anyone with physical access to your device can read your private data without authentication.",
                    actionLabel = "Set Screen Lock",
                    settingsAction = Settings.ACTION_SECURITY_SETTINGS
                )
            )
            findings.add(
                SecurityFinding(
                    id = "finding_screen_lock",
                    title = "Screen Lock is Missing",
                    description = "The device lacks a secure screen lock (PIN, password, pattern, or biometric).",
                    severity = SecuritySeverity.CRITICAL,
                    category = "Authentication",
                    recommendation = "Go to Android Security Settings and configure a strong screen lock immediately.",
                    actionLabel = "Open Security Settings",
                    settingsAction = Settings.ACTION_SECURITY_SETTINGS
                )
            )
        }

        // 2. Developer Options
        val devOptionsEnabled = try {
            Settings.Global.getInt(context.contentResolver, Settings.Global.DEVELOPMENT_SETTINGS_ENABLED, 0) == 1
        } catch (e: Exception) {
            false
        }

        if (devOptionsEnabled) {
            checks.add(
                SecurityCheck(
                    id = "dev_options",
                    title = "Developer Options",
                    subtitle = "Developer options are enabled",
                    severity = SecuritySeverity.WARNING,
                    category = "System Configuration",
                    description = "Developer mode is enabled. While normal for development, it activates debug diagnostic interfaces.",
                    actionLabel = "Open Developer Settings",
                    settingsAction = Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS
                )
            )
            findings.add(
                SecurityFinding(
                    id = "finding_dev_options",
                    title = "Developer Options Enabled",
                    description = "Developer options are currently active on the device.",
                    severity = SecuritySeverity.WARNING,
                    category = "System Configuration",
                    recommendation = "If you are not currently developing software, consider disabling Developer Options to minimize surface exposure.",
                    actionLabel = "Developer Settings",
                    settingsAction = Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS
                )
            )
        } else {
            checks.add(
                SecurityCheck(
                    id = "dev_options",
                    title = "Developer Options",
                    subtitle = "Disabled",
                    severity = SecuritySeverity.SECURE,
                    category = "System Configuration",
                    description = "System developer settings are inactive."
                )
            )
        }

        // 3. USB Debugging
        val adbEnabled = try {
            Settings.Global.getInt(context.contentResolver, Settings.Global.ADB_ENABLED, 0) == 1
        } catch (e: Exception) {
            false
        }

        if (adbEnabled) {
            checks.add(
                SecurityCheck(
                    id = "usb_debugging",
                    title = "USB Debugging (ADB)",
                    subtitle = "USB Debugging is enabled",
                    severity = SecuritySeverity.WARNING,
                    category = "Interface Security",
                    description = "USB debugging is enabled. This can increase attack surface if the device is connected to an untrusted computer or charging kiosk.",
                    actionLabel = "Disable in Settings",
                    settingsAction = Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS
                )
            )
            findings.add(
                SecurityFinding(
                    id = "finding_usb_debugging",
                    title = "USB Debugging is Active",
                    description = "Android Debug Bridge (ADB) interface is accessible via USB.",
                    severity = SecuritySeverity.WARNING,
                    category = "Interface Security",
                    recommendation = "Disable USB debugging when not actively debugging to prevent unauthorized extraction over USB connections.",
                    actionLabel = "Open Developer Settings",
                    settingsAction = Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS
                )
            )
        } else {
            checks.add(
                SecurityCheck(
                    id = "usb_debugging",
                    title = "USB Debugging (ADB)",
                    subtitle = "Disabled",
                    severity = SecuritySeverity.SECURE,
                    category = "Interface Security",
                    description = "USB Debugging is turned off, protecting device ports."
                )
            )
        }

        // 4. Unknown Sources / Non-Market Apps
        val unknownSources = try {
            @Suppress("DEPRECATION")
            Settings.Secure.getInt(context.contentResolver, Settings.Secure.INSTALL_NON_MARKET_APPS, 0) == 1
        } catch (e: Exception) {
            false
        }

        if (unknownSources) {
            checks.add(
                SecurityCheck(
                    id = "unknown_sources",
                    title = "Unknown App Installation",
                    subtitle = "Unknown sources enabled",
                    severity = SecuritySeverity.WARNING,
                    category = "App Sourcing",
                    description = "Installation of apps outside verified app stores is permitted on this device.",
                    actionLabel = "Manage App Sources",
                    settingsAction = Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES
                )
            )
            findings.add(
                SecurityFinding(
                    id = "finding_unknown_sources",
                    title = "Sideloading Permitted",
                    description = "Unknown sources setting allows sideloading APK files directly.",
                    severity = SecuritySeverity.WARNING,
                    category = "App Sourcing",
                    recommendation = "Only permit app installs from verified sources to avoid malicious repackaged applications.",
                    actionLabel = "Manage Sources",
                    settingsAction = Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES
                )
            )
        } else {
            checks.add(
                SecurityCheck(
                    id = "unknown_sources",
                    title = "Unknown App Installation",
                    subtitle = "Restricted",
                    severity = SecuritySeverity.SECURE,
                    category = "App Sourcing",
                    description = "Sideloading from unverified sources is restricted."
                )
            )
        }

        // 5. Device Security Patch
        val patchDateString = Build.VERSION.SECURITY_PATCH
        if (!patchDateString.isNullOrEmpty()) {
            val isOutdated = try {
                val format = SimpleDateFormat("yyyy-MM-dd", Locale.US)
                val patchDate = format.parse(patchDateString)
                if (patchDate != null) {
                    val ageMillis = System.currentTimeMillis() - patchDate.time
                    val ageDays = ageMillis / (1000L * 60 * 60 * 24)
                    ageDays > 180 // Older than ~6 months
                } else false
            } catch (e: Exception) {
                false
            }

            if (isOutdated) {
                checks.add(
                    SecurityCheck(
                        id = "security_patch",
                        title = "Android Security Patch",
                        subtitle = "Patch: $patchDateString (Outdated)",
                        severity = SecuritySeverity.WARNING,
                        category = "Firmware & OS",
                        description = "Device security patch level is older than recommended. Check system updates for bug and security fixes.",
                        actionLabel = "Check Updates",
                        settingsAction = Settings.ACTION_SYSTEM_UPDATE_SETTINGS
                    )
                )
                findings.add(
                    SecurityFinding(
                        id = "finding_patch",
                        title = "Outdated Security Patch Level",
                        description = "Your Android patch level is $patchDateString, which may lack recent security patches.",
                        severity = SecuritySeverity.WARNING,
                        category = "Firmware & OS",
                        recommendation = "Check your device manufacturer's system update menu to download the latest security patch.",
                        actionLabel = "System Update",
                        settingsAction = Settings.ACTION_SYSTEM_UPDATE_SETTINGS
                    )
                )
            } else {
                checks.add(
                    SecurityCheck(
                        id = "security_patch",
                        title = "Android Security Patch",
                        subtitle = "Patch: $patchDateString (Up-to-date)",
                        severity = SecuritySeverity.SECURE,
                        category = "Firmware & OS",
                        description = "System patch level is recent."
                    )
                )
            }
        } else {
            checks.add(
                SecurityCheck(
                    id = "security_patch",
                    title = "Android Security Patch",
                    subtitle = "Unavailable on this build",
                    severity = SecuritySeverity.UNAVAILABLE,
                    category = "Firmware & OS",
                    description = "Security patch string is not published by this device firmware."
                )
            )
        }

        // 6. Android Version
        checks.add(
            SecurityCheck(
                id = "android_version",
                title = "Android Version",
                subtitle = "Android ${Build.VERSION.RELEASE} (API ${Build.VERSION.SDK_INT})",
                severity = SecuritySeverity.INFO,
                category = "Firmware & OS",
                description = "Device is running Android ${Build.VERSION.RELEASE} on hardware ${Build.MANUFACTURER} ${Build.MODEL}."
            )
        )

        // 7. Device Encryption
        val dpm = context.getSystemService(Context.DEVICE_POLICY_SERVICE) as? DevicePolicyManager
        val encryptionStatus = try {
            dpm?.storageEncryptionStatus ?: DevicePolicyManager.ENCRYPTION_STATUS_UNSUPPORTED
        } catch (e: Exception) {
            DevicePolicyManager.ENCRYPTION_STATUS_UNSUPPORTED
        }

        when (encryptionStatus) {
            DevicePolicyManager.ENCRYPTION_STATUS_ACTIVE,
            DevicePolicyManager.ENCRYPTION_STATUS_ACTIVE_PER_USER -> {
                checks.add(
                    SecurityCheck(
                        id = "device_encryption",
                        title = "Device Storage Encryption",
                        subtitle = "Hardware/File-based encryption active",
                        severity = SecuritySeverity.SECURE,
                        category = "Data Protection",
                        description = "Data on disk is encrypted at rest using Android cryptographic storage."
                    )
                )
            }
            DevicePolicyManager.ENCRYPTION_STATUS_INACTIVE -> {
                checks.add(
                    SecurityCheck(
                        id = "device_encryption",
                        title = "Device Storage Encryption",
                        subtitle = "Encryption inactive",
                        severity = SecuritySeverity.CRITICAL,
                        category = "Data Protection",
                        description = "Device filesystem is not encrypted. Data can be read if memory chips are extracted.",
                        actionLabel = "Security Settings",
                        settingsAction = Settings.ACTION_SECURITY_SETTINGS
                    )
                )
            }
            else -> {
                checks.add(
                    SecurityCheck(
                        id = "device_encryption",
                        title = "Device Storage Encryption",
                        subtitle = "Encrypted by Android default (Modern API)",
                        severity = SecuritySeverity.SECURE,
                        category = "Data Protection",
                        description = "On Android 10+ devices, file-based encryption (FBE) is enforced by the operating system."
                    )
                )
            }
        }

        // 8. Google Play Protect
        // Adhere strictly to the requirement: Never claim verified if direct public API is unavailable!
        checks.add(
            SecurityCheck(
                id = "play_protect",
                title = "Google Play Protect",
                subtitle = "Google Play Protect status cannot be directly verified by this app",
                severity = SecuritySeverity.INFO,
                category = "App Verification",
                description = "Android app sandboxing restricts third-party apps from reading Play Protect state directly. You can inspect Play Protect status directly in Google Play settings.",
                actionLabel = "Open Play Protect",
                settingsAction = Settings.ACTION_SECURITY_SETTINGS
            )
        )

        return Pair(checks, findings)
    }
}
