# Script para eliminar configuraciones duplicadas
# Mantiene solo las configuraciones en common.config

$ErrorActionPreference = "Continue"

Write-Host "`n==========================================" -ForegroundColor Cyan
Write-Host "Eliminando Configuraciones Duplicadas" -ForegroundColor Cyan
Write-Host "==========================================" -ForegroundColor Cyan

$baseDir = "src\main\java\com\udipsai\backend"
$configFiles = @("SecurityConfig.java", "JwtUtil.java", "JwtFilter.java", "CorsConfig.java", "LogFilter.java")

$deletedCount = 0

# Eliminar de AUTH
Write-Host "`n[1/3] Procesando módulo AUTH..." -ForegroundColor Yellow
foreach ($file in $configFiles) {
    $filePath = "$baseDir\auth\web\config\$file"
    if (Test-Path $filePath) {
        Remove-Item $filePath -Force
        Write-Host "  ✓ Eliminado: $file" -ForegroundColor Green
        $deletedCount++
    }
}

# Eliminar de USUARIOS
Write-Host "`n[2/3] Procesando módulo USUARIOS..." -ForegroundColor Yellow
foreach ($file in $configFiles) {
    $filePath = "$baseDir\usuarios\web\config\$file"
    if (Test-Path $filePath) {
        Remove-Item $filePath -Force
        Write-Host "  ✓ Eliminado: $file" -ForegroundColor Green
        $deletedCount++
    }
}

# Eliminar de CITAS
Write-Host "`n[3/3] Procesando módulo CITAS..." -ForegroundColor Yellow
foreach ($file in $configFiles) {
    $filePath = "$baseDir\citas\web\config\$file"
    if (Test-Path $filePath) {
        Remove-Item $filePath -Force
        Write-Host "  ✓ Eliminado: $file" -ForegroundColor Green
        $deletedCount++
    }
}

Write-Host "`n==========================================" -ForegroundColor Cyan
Write-Host "Actualizando Imports..." -ForegroundColor Cyan
Write-Host "==========================================" -ForegroundColor Cyan

$updatedCount = 0

Get-ChildItem "$baseDir" -Filter *.java -Recurse | ForEach-Object {
    $filePath = $_.FullName
    $content = Get-Content $filePath -Raw -Encoding UTF8
    $originalContent = $content
    
    # Actualizar imports de configuración
    $content = $content -replace 'import com\.udipsai\.backend\.auth\.web\.config\.(JwtUtil|JwtFilter|CorsConfig|LogFilter|SecurityConfig);', 'import com.udipsai.backend.common.config.$1;'
    $content = $content -replace 'import com\.udipsai\.backend\.usuarios\.web\.config\.(JwtUtil|JwtFilter|CorsConfig|LogFilter|SecurityConfig);', 'import com.udipsai.backend.common.config.$1;'
    $content = $content -replace 'import com\.udipsai\.backend\.citas\.web\.config\.(JwtUtil|JwtFilter|CorsConfig|LogFilter|SecurityConfig);', 'import com.udipsai.backend.common.config.$1;'
    
    if ($content -ne $originalContent) {
        Set-Content $filePath -Value $content -Encoding UTF8 -NoNewline
        $updatedCount++
    }
}

Write-Host "  ✓ $updatedCount archivos con imports actualizados" -ForegroundColor Green

Write-Host "`n==========================================" -ForegroundColor Green
Write-Host "¡Limpieza Completada!" -ForegroundColor Green
Write-Host "==========================================" -ForegroundColor Green
Write-Host "`nResumen:" -ForegroundColor Cyan
Write-Host "  - Archivos eliminados: $deletedCount" -ForegroundColor White
Write-Host "  - Imports actualizados: $updatedCount" -ForegroundColor White
Write-Host "`n✅ El backend ahora usa configuraciones unificadas en common.config" -ForegroundColor Green
