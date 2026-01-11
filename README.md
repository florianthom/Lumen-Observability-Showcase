# Lumen-Observability-Showcase

## Zielbild & Grundprinzipien

###  Ziel

Ein einheitliches, governance-konformes Observability-Setup für Logs, Metriken und Traces, das:

- maschinenlesbar (Filter, Queries, Alerts, SIEM)
- menschenlesbar (verständliche Messages)
- team- und serviceübergreifend konsistent
- abteilungsexterne log-analyse Durchführbar (z.B. Rufbereitschaft)
- regulatorisch belastbar (NIS-2, ISO 27001, BSI)
ist.

### Grundprinzipien für Logging

- Structured Logging (JSON)
- Keine String-Konkatenation für dynamische Daten
- Logs sind Events
- Ein Event = eine logische Aussage
- Einheitliche Feldnamen
- Dot-Syntax als Standard
- Query-driven Design (Logs werden so entworfen, dass sinnvolle Queries möglich sind)
- Validierbar gegen Schema

## Log-Aufbau & Feldkonventionen

### Trennung: Message vs. strukturierte Felder

- Nicht Strukturierte Felder ("message" / "msg"") → Natural-language, für Menschen
- Strukturierte Felder ("event", "user_id") → für Maschinen, Filter, Aggregation, Alerting
- Begründung: Menschen sollen nicht in Key/Value-Feldern nach Bedeutung suchen, Maschinen sollen nicht Strings parsen müssen 
- Referenz:
  - https://github.com/uber-go/zap/blob/master/FAQ.md#why-do-the-structured-logging-apis-take-a-message-in-addition-to-fields
  - https://www.reddit.com/r/golang/comments/1kcmdy7/comment/mq43jto/

### Beispiel: ECS-konformes Event
```
{
    "message": "Invoice deleted",
    "event": {
        "action": "inovice.delete"
    },
    "invoice": {
        "id": 5
    }
}
```

- Dynamische Daten nicht im Message-String
- Bessere Filterbarkeit:
  - event.action=delete
  - invoice.id=5








------------------------------------------------------
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


Short answer (what you should do in 90% of cases)
Run Grafana Alloy as a DaemonSet — one instance per Kubernetes node



What should be prioritized?
🔴 Highest priority (application)
Type	Source
Business data	App
Errors	App
Decisions	App
Domain state	App
🟡 Medium priority (app OR Alloy)
Type	Source
service.name	App preferred
environment	App preferred
log level	App
🟢 Always Alloy
Type	Source
Kubernetes metadata	Alloy
Host metadata	Alloy
Timestamps	Alloy
Trace/span IDs (if not logged)	Alloy


alloy should enrich incoming log (not vice versa)

But: if your app already logs a correct, ECS-compatible timestamp, Alloy should not override it.


example with ecs: ECS encourages putting dynamic, structured data in dedicated fields, which allows for better searching, filtering, and visualization.
For your example, logging "invoice deleted with id 5":
{
"message": "Invoice deleted",
"event": {
"action": "delete"
},
"invoice": {
"id": 5
},



tracingid vs business correlation id
- should not be confused with each other

| Type                            | Purpose                                                              | Lifecycle                                                                           | Example                         |
| ------------------------------- | -------------------------------------------------------------------- | ----------------------------------------------------------------------------------- | ------------------------------- |
| **Tracing ID (trace/span IDs)** | Technical debugging, performance analysis, understanding call chains | Short-lived, per request; generated by tracing system (e.g., OpenTelemetry, Jaeger) | `trace-id: 4f7a8b`              |
| **Business correlation ID**     | Logical connection between business entities or flows                | Long-lived, persisted in DB, may outlive services/processes                         | `invoice-process-id: INV-12345` |



florianthom@Mac ~/Documents/git-ft/Lumen-Observability-Showcase $ ./gradlew dependencies --configuration compileClasspath | grep slf4j
|    |    +--- org.apache.logging.log4j:log4j-slf4j2-impl:2.25.3
|    |    |    +--- org.slf4j:slf4j-api:2.0.17



common logging scenarios

system.login
system.start
system.shutdown
secrutiy.login.error

Request and Response Data:

What: Log incoming requests and outgoing responses, including headers and payloads.
Why: This helps in tracking down issues related to API calls and understanding how data flows through your system.


System Events:
What: Log events like service starts/stops, configuration changes, or deployments.
Why: Keeping track of system events helps in understanding the state of your application at any given time and can aid in post-mortem analysis after incidents.

security:
what: Log authentication attempts, access control violations, and other security-related actions.
why: security logs are crucial for detecting unauthorized access attempts and ensuring compliance with security policies.

log event type, log type, log namespace, log category


logging context

human readable log message

warum einheitlich
-machine readable: filterbar
-indexing
-Log categories in Java logging libraries are hierarchical
-com.daysofwonder.ranking.ELORankingComputation
-Log categories in Java logging libraries are hierarchical, so for instance logging with category com.daysofwonder.ranking.ELORankingComputation: filter nach all logs von com.daysofwonder.ranking
-erlaubt alerting
-event=order_placed AND amount>100 - Large orders
-user_id=789 - All activity for this user
-group by z.B. error


Structured logging (JSON) enables efficient searching and analysis. Use consistent field names across services.

log tags
log topic

https://loggingsucks.com

Structured Logging: Logs emitted as key-value pairs (usually JSON) instead of plain strings. {"event": "payment_failed", "user_id": "123"} instead of "Payment failed for user 123". Structured logging is necessary but not sufficient.

Why String Search is Broken: The fundamental problem: logs are optimized for writing, not for querying.


"Structured logging is the same as wide events"

No. Structured logging means your logs are JSON instead of strings. That's table stakes. Wide events are a philosophy: one comprehensive event per request, with all context attached. You can have structured logs that are still useless (5 fields, no user context, scattered across 20 log lines).


Eigene Logger klasse

{
"timestamp": "2024-09-18T12:00:00Z",
"level": "INFO",
"message": "User login successful",
"user_id": "123456",
"session_id": "abcde12345"
}

"time": "2024-09-18T12:00:00Z", "duration_ms": "5000", "message": "User login authenticated", “user.credentials.verified”: true, "request_id": "req-789xyz", "user_id": "123456", "session_id": "abcde12345"
Copy to Clipboard
Logged by the service, this is sometimes called a canonical log because it fully represents one request. This example is also called a wide event because it describes one significant event with many fields.

https://www.honeycomb.io/blog/engineers-checklist-logging-best-practices

Set up alerts for ERROR or FATAL level logs or specific conditions, like repeated login failures, high memory usage,

wide events



https://www.reddit.com/r/golang/comments/1kcmdy7/comment/mq43jto/?utm_source=share&utm_medium=web3x&utm_name=web3xcss&utm_term=1&utm_content=share_button
-Log messages are for humans, don't make humans hunt in the kvs for the information they need (I refuse to use a logging library without a formatted printing mechanism)
-Key/value pairs are for filtering
-Request, operation, and trace IDs are critical for high concurrency, even if theyre entirely internal and just for log correlation (I try to use logging libraries with contextual key-value capabilities)

-Use metrics, traces, and logs properly, and don't try to use one for another




There is nothing stopping you from having the log file for each microservice record different types of events, or write log data in different ways. 

how to do consistent logs across teams and microservices, that means that specific log events have the same json keys

Add contextual data in logs

"message" = Natural-language




----------------
(log) event
* message (human summary)
* event (machine-readable event type)
*
* duration_ms (for operations)
* ✅ error_type (for failures)

Unterscheid event und error type unklar


Standard fields
Context fields
Domain specific fields


String Concatenation Instead of Fields
❌ Can't query:
logger.info(f"User {user_id} purchased {product_name} for ${amount}")
You can't easily filter "all purchases over $100" because the amount is buried in a string.


dot syntax ist Standard

7. Not Using Standard Field Names
   Use industry conventions when they exist:
   Concept	Standard Field	Not This
   HTTP status	http.status_code	httpStatus
   Request method	http.method	verb
   Duration	duration_ms	time, elapsed
   Error type	error.type	errorKind


Query-driven design

Validieren des Logs ueber Schema


why in additional field
https://github.com/uber-go/zap/blob/master/FAQ.md#why-do-the-structured-logging-apis-take-a-message-in-addition-to-fields


https://nemorize.com/roadmaps/production-observability-from-signals-to-root-cause-2026/lessons/structured-logging-strategy

class ValidatedLogger