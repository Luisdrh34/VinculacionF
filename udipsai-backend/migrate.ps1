# Script para migrar archivos de los microservicios al backend unificado
# Este script copia los archivos y actualiza los paquetes

$ErrorActionPreference = "Stop"

$baseOld = "c:\Users\Usuario\Downloads\vinculacion\sistema-web-turnos-udipsai\sistema-web-turnos-udipsai\backend"
$baseNew = "$baseOld\udipsai-backend\src\main\java\com\udipsai\backend"

Write-Host "===============================================" -ForegroundColor Cyan
Write-Host "Migrando archivos al backend unificado..." -ForegroundColor Cyan
Write-Host "===============================================" -ForegroundColor Cyan

# Función para actualizar paquetes en archivos Java
function Update-JavaPackage {
    param(
        [string]$FilePath,
        [string]$OldPackageBase,
        [string]$NewPackageBase
    )
    
    if (Test-Path $FilePath) {
        $content = Get-Content $FilePath -Raw -Encoding UTF8
        $content = $content -replace "package $OldPackageBase", "package $NewPackageBase"
        $content = $content -replace "import $OldPackageBase", "import com.udipsai.backend"
        
        # Actualizar referencias a excepciones comunes
        $content = $content -replace "import com\.udipsai\.ms_\w+\.exception\.(ResourceNotFoundException|DataConflictException|InvalidRequestBodyException|UnauthorizedAccessException|ExceptionResponse)", 'import com.udipsai.backend.common.exception.$1'
        
        Set-Content $FilePath -Value $content -Encoding UTF8 -NoNewline
    }
}

# Migrar servicio de AUTENTICACION
Write-Host "`n[1/3] Migrando servicio de Autenticacion..." -ForegroundColor Yellow
$authSource = "$baseOld\servicio_autentificacion_udipsai\src\main\java\com\udipsai\ms_autentificacion"

if (Test-Path $authSource) {
    # Copiar persistence
    if (Test-Path "$authSource\persistence") {
        Copy-Item -Path "$authSource\persistence\*" -Destination "$baseNew\auth\persistence\" -Recurse -Force
        Write-Host "  - Persistence copiado" -ForegroundColor Green
    }
    
    # Copiar service
    if (Test-Path "$authSource\service") {
        Copy-Item -Path "$authSource\service\*" -Destination "$baseNew\auth\service\" -Recurse -Force
        Write-Host "  - Services copiados" -ForegroundColor Green
    }
    
    # Copiar web (controllers y config)
    if (Test-Path "$authSource\web") {
        Copy-Item -Path "$authSource\web\*" -Destination "$baseNew\auth\web\" -Recurse -Force
        Write-Host "  - Web (controllers/config) copiados" -ForegroundColor Green
    }
}

# Migrar servicio de USUARIOS
Write-Host "`n[2/3] Migrando servicio de Usuarios..." -ForegroundColor Yellow
$userSource = "$baseOld\servicio_usuarios_udipsai\src\main\java\com\udipsai\ms_usuarios"

if (Test-Path $userSource) {
    # Copiar persistence
    if (Test-Path "$userSource\persistence") {
        Copy-Item -Path "$userSource\persistence\*" -Destination "$baseNew\usuarios\persistence\" -Recurse -Force
        Write-Host "  - Persistence copiado" -ForegroundColor Green
    }
    
    # Copiar service
    if (Test-Path "$userSource\service") {
        Copy-Item -Path "$userSource\service\*" -Destination "$baseNew\usuarios\service\" -Recurse -Force
        Write-Host "  - Services copiados" -ForegroundColor Green
    }
    
    # Copiar web
    if (Test-Path "$userSource\web") {
        Copy-Item -Path "$userSource\web\*" -Destination "$baseNew\usuarios\web\" -Recurse -Force
        Write-Host "  - Web (controllers/config) copiados" -ForegroundColor Green
    }
}

# Migrar servicio de CITAS
Write-Host "`n[3/3] Migrando servicio de Citas..." -ForegroundColor Yellow
$citasSource = "$baseOld\servicio_citas_udipsai\src\main\java\com\udipsai\ms_citas"

if (Test-Path $citasSource) {
    # Copiar persistence
    if (Test-Path "$citasSource\persistence") {
        Copy-Item -Path "$citasSource\persistence\*" -Destination "$baseNew\citas\persistence\" -Recurse -Force
        Write-Host "  - Persistence copiado" -ForegroundColor Green
    }
    
    # Copiar service
    if (Test-Path "$citasSource\service") {
        Copy-Item -Path "$citasSource\service\*" -Destination "$baseNew\citas\service\" -Recurse -Force
        Write-Host "  - Services copiados" -ForegroundColor Green
    }
    
    # Copiar web
    if (Test-Path "$citasSource\web") {
        Copy-Item -Path "$citasSource\web\*" -Destination "$baseNew\citas\web\" -Recurse -Force
        Write-Host "  - Web (controllers/config) copiados" -ForegroundColor Green
    }
    
    # Copiar config si existe
    if (Test-Path "$citasSource\config") {
        Copy-Item -Path "$citasSource\config\*" -Destination "$baseNew\citas\config\" -Recurse -Force
        Write-Host "  - Config adicional copiado" -ForegroundColor Green
    }
}

Write-Host "`n===============================================" -ForegroundColor Cyan
Write-Host "Actualizando paquetes Java..." -ForegroundColor Cyan
Write-Host "===============================================" -ForegroundColor Cyan

# Actualizar paquetes en archivos de AUTH
Get-ChildItem "$baseNew\auth" -Filter *.java -Recurse | ForEach-Object {
    Update-JavaPackage -FilePath $_.FullName -OldPackageBase "com.udipsai.ms_autentificacion" -NewPackageBase "com.udipsai.backend.auth"
}
Write-Host "Paquetes de Auth actualizados" -ForegroundColor Green

# Actualizar paquetes en archivos de USUARIOS
Get-ChildItem "$baseNew\usuarios" -Filter *.java -Recurse | ForEach-Object {
    Update-JavaPackage -FilePath $_.FullName -OldPackageBase "com.udipsai.ms_usuarios" -NewPackageBase "com.udipsai.backend.usuarios"
}
Write-Host "Paquetes de Usuarios actualizados" -ForegroundColor Green

# Actualizar paquetes en archivos de CITAS
Get-ChildItem "$baseNew\citas" -Filter *.java -Recurse | ForEach-Object {
    Update-JavaPackage -FilePath $_.FullName -OldPackageBase "com.udipsai.ms_citas" -NewPackageBase "com.udipsai.backend.citas"
}
Write-Host "Paquetes de Citas actualizados" -ForegroundColor Green

Write-Host "`n===============================================" -ForegroundColor Green
Write-Host "Migración completada exitosamente!" -ForegroundColor Green
Write-Host "===============================================" -ForegroundColor Green

Write-Host "`nResumen:" -ForegroundColor Cyan
Write-Host "- Backend unificado: $baseNew"
Write-Host "- Módulos migrados: auth, usuarios, citas"
Write-Host "- Paquetes actualizados automáticamente"
