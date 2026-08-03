import requests
import random
import time

GATEWAY_URL = "http://localhost:8080/api/v1"

# 1. Login to get JWT
login_payload = {
    "email": "globalhr@company.com",
    "password": "password"
}
response = requests.post(f"{GATEWAY_URL}/auth/login", json=login_payload)
if response.status_code != 200:
    print("Login failed!", response.text)
    exit(1)

token = response.json()["data"]["token"]
headers = {
    "Authorization": f"Bearer {token}",
    "Content-Type": "application/json"
}

categories = ["WORK_ENVIRONMENT", "LEADERSHIP", "CAREER_GROWTH", "TEAM_DYNAMICS", "WORK_LIFE_BALANCE"]

print("Seeding Onboarding Questions (12)...")
onboarding_questions = [
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
]

for i, text in enumerate(onboarding_questions):
    q_type = "TEXT" if i == 10 else "LIKERT_SCALE"
    payload = {
        "questionText": text,
        "questionType": q_type,
        "category": random.choice(categories),
        "region": "GLOBAL",
        "month": 7,
        "year": 2026,
        "surveyType": "ONBOARDING",
        "remarks": "System Generated Seed"
    }
    r = requests.post(f"{GATEWAY_URL}/questions", json=payload, headers=headers)
    if r.status_code != 200:
        print(f"Failed to create question {i}: {r.text}")

print("Seeding Monthly Pulse Questions (20)...")
monthly_questions = [
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
]

for i, text in enumerate(monthly_questions):
    q_type = "TEXT" if i >= 18 else "LIKERT_SCALE"
    payload = {
        "questionText": text,
        "questionType": q_type,
        "category": random.choice(categories),
        "region": "GLOBAL",
        "month": 7,
        "year": 2026,
        "surveyType": "MONTHLY_PULSE",
        "remarks": "System Generated Seed"
    }
    r = requests.post(f"{GATEWAY_URL}/questions", json=payload, headers=headers)
    if r.status_code != 200:
        print(f"Failed to create question {i}: {r.text}")

print("Data Seeding Complete!")
