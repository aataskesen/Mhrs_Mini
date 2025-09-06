@echo off
echo MHRS Mini Servisleri Baslatiliyor...
echo =====================================

REM Auth Service'i baslat
echo [1/2] Auth Service baslatiliyor...
start "Auth Service" cmd /k "cd services\auth-service && mvn spring-boot:run"

REM 5 saniye bekle
timeout /t 5 /nobreak > nul

REM Appointment Service'i baslat  
echo [2/2] Appointment Service baslatiliyor...
start "Appointment Service" cmd /k "cd services\appointment-service && mvn spring-boot:run"

echo.
echo =====================================
echo Tum servisler baslatildi!
echo.
echo Auth Service: http://localhost:8080/api/auth/test
echo Appointment Service: http://localhost:8081/api/doctors/test
echo.
pause