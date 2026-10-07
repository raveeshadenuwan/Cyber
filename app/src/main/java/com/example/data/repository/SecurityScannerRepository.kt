package com.example.data.repository

import android.content.Context
import com.example.data.local.AppDatabase
import com.example.data.local.FindingEntity
import com.example.data.local.ScanDao
import com.example.data.local.ScanEntity
import com.example.data.local.ScanWithFindings
import com.example.domain.model.AppRiskLevel
import com.example.domain.model.SecurityCheck
import com.example.domain.model.SecurityFinding
import com.example.domain.model.SecurityScanResult
import com.example.domain.model.SecuritySeverity
import com.example.domain.security.SecurityScanner
import com.example.domain.security.SecurityScoreCalculator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class SecurityScannerRepository(
    private val context: Context,
    private val scanDao: ScanDao = AppDatabase.getInstance(context).scanDao(),
    private val appRepository: AppRepository = AppRepository(context),
    private val networkRepository: NetworkRepository = NetworkRepository(context),
    private val scanner: SecurityScanner = SecurityScanner(context),
    private val scoreCalculator: SecurityScoreCalculator = SecurityScoreCalculator()
) {

    fun getScanHistory(): Flow<List<ScanEntity>> = scanDao.getAllScans()

    fun getLatestScan(): Flow<ScanEntity?> = scanDao.getLatestScan()

    fun getScanDetails(scanId: Long): Flow<ScanWithFindings?> = scanDao.getScanWithFindings(scanId)

    suspend fun runFullSecurityScan(
        onProgressUpdate: suspend (phase: String, progress: Float) -> Unit = { _, _ -> }
    ): SecurityScanResult = withContext(Dispatchers.IO) {
        onProgressUpdate("Checking device & hardware...", 0.15f)
        delay(250)

        onProgressUpdate("Checking system security settings...", 0.35f)
        val (systemChecks, systemFindings) = scanner.performSystemChecks()
        delay(250)

        onProgressUpdate("Auditing applications & permissions...", 0.60f)
        val installedApps = appRepository.getInstalledApps()
        val highRiskApps = installedApps.filter { it.riskLevel == AppRiskLevel.HIGH }
        delay(250)

        onProgressUpdate("Evaluating network security...", 0.80f)
        val networkInfo = networkRepository.getNetworkInfo()
        val isInsecureNetwork = !networkInfo.isConnected || (networkInfo.warnings.isNotEmpty() && !networkInfo.isVpnActive)
        delay(200)

        onProgressUpdate("Calculating security score...", 0.95f)
        val allChecks = ArrayList(systemChecks)
        val allFindings = ArrayList(systemFindings)

        // Add app check
        if (highRiskApps.isNotEmpty()) {
            allChecks.add(
                SecurityCheck(
                    id = "app_permissions",
                    title = "App Permissions Audit",
                    subtitle = "${highRiskApps.size} app(s) request high-risk permission combinations",
                    severity = SecuritySeverity.WARNING,
                    category = "Applications",
                    description = "${highRiskApps.size} installed application(s) have access to sensitive permissions such as SMS, microphone, or background location."
                )
            )
            allFindings.add(
                SecurityFinding(
                    id = "finding_apps",
                    title = "Apps with Elevated Permission Combinations",
                    description = "Found ${highRiskApps.size} apps (${highRiskApps.take(3).joinToString { it.appName }}) requesting multiple sensitive permissions.",
                    severity = SecuritySeverity.WARNING,
                    category = "Applications",
                    recommendation = "Review installed applications in CyberGuard and uninstall any unfamiliar or unused apps."
                )
            )
        } else {
            allChecks.add(
                SecurityCheck(
                    id = "app_permissions",
                    title = "App Permissions Audit",
                    subtitle = "No abnormal permission anomalies detected",
                    severity = SecuritySeverity.SECURE,
                    category = "Applications",
                    description = "Installed applications do not show uncharacteristic sensitive permission combinations."
                )
            )
        }

        // Add network check
        allChecks.add(
            SecurityCheck(
                id = "network_indicator",
                title = "Network Connection Security",
                subtitle = "${networkInfo.networkType.displayName} connected",
                severity = if (networkInfo.isConnected) SecuritySeverity.SECURE else SecuritySeverity.INFO,
                category = "Network",
                description = "IP: ${networkInfo.localIpAddress} | Metered: ${if (networkInfo.isMetered) "Yes" else "No"} | VPN: ${if (networkInfo.isVpnActive) "Active" else "None"}"
            )
        )

        val score = scoreCalculator.calculate(
            checks = allChecks,
            highRiskAppCount = highRiskApps.size,
            isInsecureNetwork = isInsecureNetwork
        )

        val scanResult = SecurityScanResult(
            scanId = System.currentTimeMillis(),
            timestamp = System.currentTimeMillis(),
            score = score,
            checks = allChecks,
            findings = allFindings
        )

        // Save to Room
        val scanEntity = ScanEntity(
            timestamp = scanResult.timestamp,
            score = score.score,
            status = score.status.displayName,
            criticalCount = score.criticalCount,
            warningCount = score.warningCount,
            passedCount = score.passedCount,
            summary = "${score.status.displayName} (${score.score}/100)"
        )
        val insertedId = scanDao.insertScan(scanEntity)

        val findingEntities = allFindings.map { finding ->
            FindingEntity(
                scanId = insertedId,
                title = finding.title,
                description = finding.description,
                severity = finding.severity.name,
                category = finding.category,
                recommendation = finding.recommendation
            )
        }
        if (findingEntities.isNotEmpty()) {
            scanDao.insertFindings(findingEntities)
        }

        onProgressUpdate("Scan Complete", 1.0f)
        scanResult
    }

    suspend fun deleteScan(scanId: Long) = withContext(Dispatchers.IO) {
        scanDao.deleteScanById(scanId)
    }

    suspend fun clearAllHistory() = withContext(Dispatchers.IO) {
        scanDao.deleteAllScans()
    }
}
