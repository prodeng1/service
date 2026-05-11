# Task Plan - Lab 9 Alerting with Prometheus and AlertManager

## Scope
- Implement Lab 9 alerting requirements in the current project setup.
- Keep credentials as placeholders (no real secrets committed).

## Checklist
- [x] Add custom Prometheus alert rules for all three categories:
  - [x] Availability (service health / target up)
  - [x] Performance (high request latency)
  - [x] Quality (high 5xx error rate)
- [x] Add critical variants for custom alerts with stricter thresholds.
- [x] Register the new `custom-alerts.yml` in `infrastructure/prometheus/prometheus.yml`.
- [x] Update `infrastructure/alertmanager/alertmanager.yml` with:
  - [x] Severity-based routing (`critical` vs `warning`)
  - [x] Grouping strategy (`alertname`, `severity`)
  - [x] Inhibition rule to suppress warning noise when related critical alerts fire
  - [x] Email placeholders for receivers
- [x] Add shared alert grouping labels on paired warning/critical rules where needed for inhibition.
- [x] Validate syntax and consistency by checking monitoring config files.
- [x] Document how to test the full lifecycle (Inactive -> Pending -> Firing -> Resolved).

## Validation Commands
- `./stop_with_monitoring.sh && ./start_with_monitoring.sh`
- `docker exec service-prometheus-1 promtool check rules /etc/prometheus/custom-alerts.yml`
- `curl -s http://localhost:9093/api/v2/alerts`

## Validation Notes
- YAML syntax is valid for all updated monitoring files.
- Runtime validation (`promtool` in container, alert firing scenarios, email delivery) must be executed with local Docker stack running.

## Full Lifecycle Test Steps
1. Restart stack: `./stop_with_monitoring.sh && ./start_with_monitoring.sh`
2. Open:
   - `http://localhost:9090/alerts`
   - `http://localhost:9093`
3. Trigger availability alert:
   - `docker stop service_prod-eng_1`
   - Wait for warning and critical transitions to `Firing`
4. Resolve:
   - `docker start service_prod-eng_1`
   - Verify transition to `Resolved` then `Inactive`
5. Trigger performance/quality alerts:
   - Generate load to `/info` and optionally force 5xx responses
   - Watch `CustomAlerts` and `AppAlerts` state transitions

