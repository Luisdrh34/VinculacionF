# Script para corregir imports en el backend unificado

$ErrorActionPreference = "Continue"

$baseNew = "c:\Users\Usuario\Downloads\vinculacion\sistema-web-turnos-udipsai\sistema-web-turnos-udipsai\backend\udipsai-backend\src\main\java\com\udipsai\backend"

Write-Host "`n=== Corrigiendo imports en backend unificado ===" -ForegroundColor Cyan

function Fix-Imports {
    param(
        [string]$ModulePath,
        [string]$ModuleName
    )
    
    Write-Host "`nProcesando módulo: $ModuleName" -ForegroundColor Yellow
    $count = 0
    
    Get-ChildItem "$ModulePath" -Filter *.java -Recurse | ForEach-Object {
        $filePath = $_.FullName
        $content = Get-Content $filePath -Raw -Encoding UTF8
        $originalContent = $content
        
        # Determinar si el archivo está en auth, usuarios o citas basado en la ruta
        $fileModule = ""
        if ($filePath -like "*\auth\*") { $fileModule = "auth" }
        elseif ($filePath -like "*\usuarios\*") { $fileModule = "usuarios" }
        elseif ($filePath -like "*\citas\*") { $fileModule = "citas" }
        
        # Corregir imports de excepciones
        $content = $content -replace 'import com\.udipsai\.backend\.(auth|usuarios|citas)\.exception\.', 'import com.udipsai.backend.common.exception.'
        $content = $content -replace 'import com\.udipsai\.backend\.exception\.', 'import com.udipsai.backend.common.exception.'
        
        # Corregir imports según el módulo actual
        if ($fileModule -eq "usuarios") {
            # Para archivos en el módulo usuarios
            $content = $content -replace 'import com\.udipsai\.backend\.persistence\.entity\.', "import com.udipsai.backend.usuarios.persistence.entity."
            $content = $content -replace 'import com\.udipsai\.backend\.persistence\.repository\.', "import com.udipsai.backend.usuarios.persistence.repository."
            $content = $content -replace 'import com\.udipsai\.backend\.persistence\.view\.', "import com.udipsai.backend.usuarios.persistence.view."
            $content = $content -replace 'import com\.udipsai\.backend\.service\.dto\.', "import com.udipsai.backend.usuarios.service.dto."
            $content = $content -replace 'import com\.udipsai\.backend\.service\.', "import com.udipsai.backend.usuarios.service."
            $content = $content -replace 'import com\.udipsai\.backend\.web\.config\.', "import com.udipsai.backend.usuarios.web.config."
            $content = $content -replace 'import com\.udipsai\.backend\.web\.controller\.', "import com.udipsai.backend.usuarios.web.controller."
        }
        elseif ($fileModule -eq "auth") {
            # Para archivos en el módulo auth
            $content = $content -replace 'import com\.udipsai\.backend\.persistence\.entity\.', "import com.udipsai.backend.auth.persistence.entity."
            $content = $content -replace 'import com\.udipsai\.backend\.persistence\.repository\.', "import com.udipsai.backend.auth.persistence.repository."
            $content = $content -replace 'import com\.udipsai\.backend\.service\.dto\.', "import com.udipsai.backend.auth.service.dto."
            $content = $content -replace 'import com\.udipsai\.backend\.service\.', "import com.udipsai.backend.auth.service."
            $content = $content -replace 'import com\.udipsai\.backend\.web\.config\.', "import com.udipsai.backend.auth.web.config."
            $content = $content -replace 'import com\.udipsai\.backend\.web\.controller\.', "import com.udipsai.backend.auth.web.controller."
        }
        elseif ($fileModule -eq "citas") {
            # Para archivos en el módulo citas
            $content = $content -replace 'import com\.udipsai\.backend\.persistence\.mysql\.entity\.', "import com.udipsai.backend.citas.persistence.mysql.entity."
            $content = $content -replace 'import com\.udipsai\.backend\.persistence\.mysql\.repository\.', "import com.udipsai.backend.citas.persistence.mysql.repository."
            $content = $content -replace 'import com\.udipsai\.backend\.persistence\.postgresql\.entity\.', "import com.udipsai.backend.citas.persistence.postgresql.entity."
            $content = $content -replace 'import com\.udipsai\.backend\.persistence\.postgresql\.repository\.', "import com.udipsai.backend.citas.persistence.postgresql.repository."
            $content = $content -replace 'import com\.udipsai\.backend\.service\.dto\.', "import com.udipsai.backend.citas.service.dto."
            $content = $content -replace 'import com\.udipsai\.backend\.service\.', "import com.udipsai.backend.citas.service."
            $content = $content -replace 'import com\.udipsai\.backend\.web\.config\.', "import com.udipsai.backend.citas.web.config."
            $content = $content -replace 'import com\.udipsai\.backend\.web\.controller\.', "import com.udipsai.backend.citas.web.controller."
        }
        
        # Si hubo cambios, guardar el archivo
        if ($content -ne $originalContent) {
            Set-Content $filePath -Value $content -Encoding UTF8 -NoNewline
            $count++
        }
    }
    
    Write-Host "  - $count archivos actualizados" -ForegroundColor Green
}

# Procesar cada módulo
Fix-Imports -ModulePath "$baseNew\auth" -ModuleName "AUTH"
Fix-Imports -ModulePath "$baseNew\usuarios" -ModuleName "USUARIOS"
Fix-Imports -ModulePath "$baseNew\citas" -ModuleName "CITAS"

Write-Host "`n=== Corrección de imports completada ===" -ForegroundColor Green
