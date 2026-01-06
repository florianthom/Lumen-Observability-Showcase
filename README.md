# Lumen-Observability-Showcase



Put all context attribute under context field in json so that one can just object.toString with all keys indie that object
- Events: invoice.create.success


Central log4j.xml müsste später angepasst werden für App Business Logs



Always string or numbers (ggf. flattened keys) no nested object

Event type for machines log message for humans (but fields under separate fields and no magic strings) (“event.type”; “invoice.validated”)

mdc only for tech/infratsructure e.g. spanid


shared field naming conventions
field naming rules


L1(catalog)vsL2vsL3free2use

Dashboards&alertsonly for l1

reserved (event) namespaces
- system
- Job
- Security (auth)
- http
- Db (database) (inkl. migration)
- messaging
- camunda
- (validation)
- (other: business namespaces)

Weitere Logs:
System.startup, system.shutdown



governance:
- nis-2
- -cra
- isms
- 27001
- sdlc
- SIEM-Zwang, kein 24/7 SOC-Zwang

„Wie analysieren Sie die Auswirkungen?“
→ „Log- + Trace-Korrelation“

Log Catalog (welche Events gibt es?)


Konkrete Stellen
ISO 27001 A.8.15 / A.8.16
ISO 27002 Kapitel 8.15 – Logging
iso27001 Wird vom BSI als „Stand der Technik“ akzeptiert 

ENISA & EU-Empfehlungen (NIS-2-nah)

„Wie erfüllen Sie Art. 21 NIS-2 ohne diese Information?“

Elastic Common Schema (ECS)
Wird von vielen SIEMs genutzt

Nationale Konkretisierung: BSI & Stand der Technik
In Deutschland wird NIS-2 über:
NIS2UmsuCG
BSI-Gesetz
BSI-Leitlinien / Orientierungshilfen
konkretisiert.
📄 BSI-Grundschutz (z. B. OPS.1.1.5, OPS.1.2.5)



grafana agent
- monitoring (alle actuator endpunkte) inkl. metriken
- loggs
grafana alloy
- = unified observablity collector/agent, integrations for logs, metrics, traces, and profiles
- replaces the need to run separate collectors for metrics (Prometheus agent), logs (Promtail), traces (OpenTelemetry Collector), and more
- änderung der urls die metriken, logs traces empfangen
grafana tempo
-  traces
loki
- log aggregation system
prometheus
- metrics database + scraper
grafana ui
- visualization/dashboards


-------------------
otel

1️⃣ OpenTelemetry Operator injection ✅ Top choice
Modern, declarative, and automated
No changes to app image
Auto-upgrades and consistent rollout
Widely adopted in production Kubernetes environments
2️⃣ Sidecar / volume mount agent
Production-ready, works reliably
Keeps app image separate from agent
Manual configuration of volumes and environment variables
Very common when the operator is not used
3️⃣ Init-container download
Works for controlled CI/CD deployments
Allows agent version to be updated independently of app image
More complex Kubernetes specs, slightly more brittle than operator/sidecar
4️⃣ Bake agent into image (COPY jar) ❌ Legacy / bottom
Only suitable for local dev, PoC, or small-scale apps
Upgrading agent requires rebuilding images
No separation of concerns
Rarely seen in modern cloud-native production environments




https://grafana.com/docs/alloy/latest/tutorials/processing-logs/

https://grafana.com/grot/

https://grafana.com/docs/alloy/latest/monitor/monitor-structured-logs/#understand-the-alloy-configuration

https://github.com/grafana/alloy-scenarios?tab=readme-ov-file




http://localhost:8081/actuator/health

http://localhost:9090/query?g0.expr=jvm_memory_used_bytes&g0.show_tree=0&g0.tab=table&g0.range_input=1h&g0.res_type=auto&g0.res_density=medium&g0.display_mode=lines&g0.show_exemplars=0

http://localhost:3100/loki/api/v1/query_range?query={servicename=%22lumen%22}

backslash bei label notwendig in alloy

docker compose down && docker compose up -d