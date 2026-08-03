cd C:\kafka\kafka_2.13-4.2.0

$env:KAFKA_HEAP_OPTS = "-Xmx1G -Xms1G"

Write-Host "Formatting KRaft Storage (Safely ignores if already formatted)..."
.\bin\windows\kafka-storage.bat format -t ZaQazwz6TaCedJ5cn04egw -c .\config\server.properties --standalone

Write-Host "Starting Kafka Server..."
.\bin\windows\kafka-server-start.bat .\config\server.properties
