$GATEWAY_URL = "http://localhost:8080/api/v1"

# 1. Login to get JWT
$login_payload = @{
    email = "globalhr@company.com"
    password = "password"
}
$response = Invoke-RestMethod -Uri "$GATEWAY_URL/auth/login" -Method Post -Body ($login_payload | ConvertTo-Json) -ContentType "application/json"
$token = $response.data.token

$headers = @{
    Authorization = "Bearer $token"
    "Content-Type" = "application/json"
}

$categories = @("WORK_LIFE_BALANCE", "LEADERSHIP", "GROWTH", "CULTURE", "BENEFITS", "MANAGER", "WORKLOAD")

Write-Host "Seeding Onboarding Questions (12)..."
$onboarding_questions = @(
    "How was your first day at the company?",
    "Did you receive all necessary equipment on time?",
    "Is your reporting manager communicating clearly?",
    "Do you understand the core values of the company?",
    "Are your team members supportive?",
    "Is the onboarding documentation helpful?",
    "Do you feel welcomed by the organization?",
    "How satisfied are you with the induction training?",
    "Is your current role aligned with what was discussed during interviews?",
    "Have you been introduced to your key stakeholders?",
    "What could we improve in our onboarding process?",
    "Rate your overall onboarding experience."
)

for ($i=0; $i -lt $onboarding_questions.Length; $i++) {
    $q_type = if ($i -eq 10) { "TEXT" } else { "LIKERT_SCALE" }
    $payload = @{
        questionText = $onboarding_questions[$i]
        questionType = $q_type
        category = $categories[(Get-Random -Maximum $categories.Length)]
        region = "GLOBAL"
        month = 7
        year = 2026
        surveyType = "ONBOARDING"
        remarks = "System Generated Seed"
    }
    try {
        Invoke-RestMethod -Uri "$GATEWAY_URL/questions" -Method Post -Body ($payload | ConvertTo-Json) -Headers $headers | Out-Null
    } catch {}
}

Write-Host "Seeding Monthly Pulse Questions (20)..."
$monthly_questions = @(
    "How would you rate your work-life balance this month?",
    "Do you feel your work is recognized by leadership?",
    "Are you satisfied with your career growth opportunities?",
    "How well does your team collaborate?",
    "Do you have the tools needed to do your job effectively?",
    "How often do you feel stressed at work?",
    "Is the company leadership transparent about business goals?",
    "Would you recommend this company to a friend?",
    "How supported do you feel by your direct manager?",
    "Are you able to take time off when needed?",
    "How clear are your performance expectations?",
    "Do you feel valued as a member of the team?",
    "Are there sufficient opportunities for training?",
    "How effectively are meetings run in your team?",
    "Is constructive feedback provided regularly?",
    "How aligned are your personal values with the company?",
    "Do you feel a sense of belonging at work?",
    "How comfortable are you sharing new ideas?",
    "What is the biggest blocker for you right now?",
    "Any other comments or feedback for this month?"
)

for ($i=0; $i -lt $monthly_questions.Length; $i++) {
    $q_type = if ($i -ge 18) { "TEXT" } else { "LIKERT_SCALE" }
    $payload = @{
        questionText = $monthly_questions[$i]
        questionType = $q_type
        category = $categories[(Get-Random -Maximum $categories.Length)]
        region = "GLOBAL"
        month = 7
        year = 2026
        surveyType = "MONTHLY_PULSE"
        remarks = "System Generated Seed"
    }
    try {
        Invoke-RestMethod -Uri "$GATEWAY_URL/questions" -Method Post -Body ($payload | ConvertTo-Json) -Headers $headers | Out-Null
    } catch {}
}

Write-Host "Data Seeding Complete!"
