# Lab 9 Solution

This repository now includes a complete Lab 9 setup for observability and alerting.

## Selected metrics

`availability`
- Metric: `up{job="spring-prod-eng-app"}`
- Meaning: Prometheus can successfully scrape the Spring Boot service.
- Alert threshold: value equals `0` for more than `30s`

`performance`
- Metric: `rate(prod_eng_appointment_create_duration_seconds_sum[5m]) / rate(prod_eng_appointment_create_duration_seconds_count[5m])`
- Meaning: average latency for creating appointments, based on the existing Micrometer timer `prod.eng.appointment.create.duration`.
- Alert threshold: average latency is above `0.20` seconds for more than `1m`

`quality`
- Metric: `max_over_time(prod_eng_appointments_rejected_total[5m])`
- Meaning: invalid appointment requests rejected by the service in the last 5 minutes, grouped by rejection reason.
- Alert threshold: counter above `0` within the last `5m`

## Grafana dashboard

The dashboard is provisioned automatically from:

- `infrastructure/grafana/dashboards/app/lab-9-observability.json`

Open Grafana at `http://localhost:3000` and access the dashboard named `Lab 9 Observability`.

## Prometheus alerts

The alerts are defined in:

- `infrastructure/prometheus/app-alerts.yml`

Implemented alerts:

- `ProdEngServiceDown`
- `AppointmentCreationLatencyHigh`
- `AppointmentValidationErrorsDetected`

Open Prometheus at `http://localhost:9090/alerts` to inspect their state.

## Email alert delivery

For local demo purposes, the monitoring profile now starts a `Mailpit` SMTP server and inbox UI.

Mailpit endpoints:

- Web inbox: `http://localhost:8025`
- SMTP server: `mailpit:1025`

Alertmanager is configured to send alerts to Mailpit using:

- `infrastructure/alertmanager/alertmanager.yml`

This avoids the need for a Gmail App Password during the lab demo while still generating real emails.

## How to run

1. Start the stack with `./start_with_monitoring.sh`
2. Open:
   - Grafana: `http://localhost:3000`
   - Prometheus: `http://localhost:9090`
   - Alertmanager: `http://localhost:9093`
   - Mailpit: `http://localhost:8025`

## How to trigger an alert

The easiest quality alert demo is an invalid appointment request:

1. Start the application with monitoring enabled.
2. Send the invalid request from `requests.http`:
   - `Test laborator - appointment invalid pentru eroare`
3. Wait for Prometheus to evaluate the rule.
4. Confirm that:
   - the `AppointmentValidationErrorsDetected` alert becomes `Firing` in Prometheus / Alertmanager
   - an email appears in Mailpit

You can also trigger:

- `ProdEngServiceDown` by stopping the `prod-eng` container
- `AppointmentCreationLatencyHigh` by generating sustained traffic with the existing performance injectors
