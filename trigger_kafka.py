from kafka import KafkaProducer
import json
import time

producer = KafkaProducer(
    bootstrap_servers=['localhost:9092'],
    value_serializer=lambda v: json.dumps(v).encode('utf-8')
)

event = {
    "surveyId": 999,
    "title": "Pune Employee Satisfaction Survey",
    "targetAudience": "All Employees",
    "region": "PUNE",
    "endDate": "2026-07-31"
}

print("Sending SurveyPublishedEvent to Kafka topic 'survey-published'...")
producer.send('survey-published', event)
producer.flush()
print("Event sent successfully!")
