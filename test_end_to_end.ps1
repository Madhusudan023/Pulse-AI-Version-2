Write-Host "Starting Kafka..."
Start-Process powershell -ArgumentList "-ExecutionPolicy Bypass -File start_kafka.ps1" -WindowStyle Hidden

Write-Host "Starting Eureka Server..."
$eureka = Start-Process "java" -ArgumentList "-Xmx256m -jar eureka-server\target\eureka-server-0.0.1-SNAPSHOT.jar" -WindowStyle Hidden -PassThru -RedirectStandardOutput "eureka.log" -RedirectStandardError "eureka_err.log"

Start-Sleep -Seconds 15

Write-Host "Starting Employee Service..."
$employee = Start-Process "java" -ArgumentList "-Xmx256m -jar employee-service\target\employee-service-0.0.1-SNAPSHOT.jar" -WindowStyle Hidden -PassThru -RedirectStandardOutput "employee.log" -RedirectStandardError "employee_err.log"

Write-Host "Starting Google Form Service..."
$gfs = Start-Process "java" -ArgumentList "-Xmx256m -jar google-form-service\target\google-form-service-0.0.1-SNAPSHOT.jar" -WindowStyle Hidden -PassThru -RedirectStandardOutput "google-form.log" -RedirectStandardError "google-form_err.log"

Write-Host "Waiting for services to boot and register with Eureka (40 seconds)..."
Start-Sleep -Seconds 40

Write-Host "Triggering Kafka Event (Survey Published in PUNE)..."
python trigger_kafka.py

Write-Host "Waiting 5 seconds for processing..."
Start-Sleep -Seconds 5

Write-Host "--- Google Form Service Logs ---"
Get-Content google-form.log -Tail 30

Write-Host "Cleaning up processes..."
Stop-Process -Id $eureka.Id -Force -ErrorAction SilentlyContinue
Stop-Process -Id $employee.Id -Force -ErrorAction SilentlyContinue
Stop-Process -Id $gfs.Id -Force -ErrorAction SilentlyContinue
