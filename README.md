project spezifisch (technisch)

http://localhost:8081/actuator/health

http://localhost:9090/query?g0.expr=jvm_memory_used_bytes&g0.show_tree=0&g0.tab=table&g0.range_input=1h&g0.res_type=auto&g0.res_density=medium&g0.display_mode=lines&g0.show_exemplars=0

http://localhost:3100/loki/api/v1/query_range?query={servicename=%22lumen%22}

backslash bei label notwendig in alloy

docker compose down && docker compose up -d




--------------------------------------

# Lumen-Observability-Showcase

## Zielbild & Grundprinzipien
mehr und mehr von monitoring (logs, metriken) zu observablity (traces)

im besten fall sollte fehler aus traces > monitoring > logs ersichtlich werden

###  Ziel / Anforderungen
Ein einheitliches, governance-konformes Observability-Setup für Logs, Metriken und Traces hat folgende Anforderungen:

- maschinenlesbar (Filter, Queries, Alerts, SIEM)
- menschenlesbar (verständliche Messages)
- team- und serviceübergreifend konsistent
- business logging möglich
- abteilungsexterne log-analyse Durchführbar (z.B. Rufbereitschaft)
- Observability regulatorisch belastbar und geschäftsfähig sein (NIS-2, ISO 27001, BSI)

### Grundprinzipien für Logging

- Structured Logging (JSON)
- Keine String-Konkatenation für dynamische Daten
- Logs sind Events (keine Debug-Ausgaben), Event = eine logische Aussage
- Einheitliche Feldnamen
- Dot-Syntax als Standard (flattened keys d.h. keine beliebigen Deep-Nesting-Strukturen))
- Query-driven Design (Logs werden so entworfen, dass sinnvolle Queries möglich sind)
- Validierbar gegen Schema

## Structed Logging

### what is structured logging / wide events bzw -logs / canonical logs
- "Structured logging is the same as wide events"
- No. Structured logging means your logs are JSON instead of strings. That's table stakes. Wide events are a philosophy: one comprehensive event per request, with all context attached. You can have structured logs that are still useless (5 fields, no user context, scattered across 20 log lines).
- Structured Logging: Logs emitted as key-value pairs (usually JSON) instead of plain strings. {"event": "payment_failed", "user_id": "123"} instead of "Payment failed for user 123". Structured logging is necessary but not sufficient.
- canonical log = fully represents one request. This example is also called a wide event because it describes one significant event with many fields.
- Structured logging (JSON) enables efficient searching and analysis. Use consistent field names across services.

### warum einheitlich structured logging
- Why String Search is Broken: The fundamental problem: logs are optimized for writing, not for querying.
- machine readable: filterbar
- indexing
- Log categories in Java logging libraries are hierarchical
- com.daysofwonder.ranking.ELORankingComputation
- Log categories in Java logging libraries are hierarchical, so for instance logging with category com.daysofwonder.ranking.ELORankingComputation: filter nach all logs von com.daysofwonder.ranking
- erlaubt alerting
- event=order_placed AND amount>100 - Large orders
- user_id=789 - All activity for this user
- group by z.B. error
- String Concatenation Instead of Fields: You can't easily query/filter "all purchases over $100" because the amount is buried in a string.


## Log-Aufbau & Feldkonventionen

### Trennung: Message vs. strukturierte Felder
Menschen sollen nicht in Key/Value-Feldern nach Bedeutung suchen. Maschinen sollen nicht Strings parsen müssen. Aus diesem Grund wird strukturiert gelogged in dem weiterhin ein Feld "message" existiert neben den tatsächlichen Key-Value-Paaren.

- Nicht Strukturierte Felder ("message" / "msg") → Natürliche-Sprache für Menschen
- Strukturierte Felder ("event", "user_id") → für Maschinen, Filter, Aggregation, Alerting

Referenz:
- https://github.com/uber-go/zap/blob/master/FAQ.md#why-do-the-structured-logging-apis-take-a-message-in-addition-to-fields
- https://www.reddit.com/r/golang/comments/1kcmdy7/comment/mq43jto/

### Beispiele für strukturuiertes Logging

Beispiel 1
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

Beispiel 2
```
{
    "timestamp": "2024-09-18T12:00:00Z",
    "level": "INFO",
    "message": "User login successful",
    "user_id": "123456",
    "session_id": "abcde12345"
}
```

- Dynamische Daten nicht im Message-String
- Bessere Filterbarkeit:
  - event.action=delete
  - invoice.id=5


### Namespaces & Reservierte Bereiche
Es existieren übergreifende gemeinsame Logging-Scenarien. Dazu zählen folgende:

- Request and Response Data:
  - What: Log incoming requests and outgoing responses, including headers and payloads.
  - Why: Debugging & Flow-Verständnis. This helps in tracking down issues related to API calls and understanding how data flows through your system.

- System Events:
  - What: Log events like service starts/stops, configuration changes, or deployments.
  - Why: Keeping track of system events helps in understanding the state of your application at any given time and can aid in post-mortem analysis after incidents.

- security:
  what: Log authentication attempts, access control violations, and other security-related actions.
  why: security logs are crucial for detecting unauthorized access attempts and ensuring compliance with security policies.

Diese Szenarien begründen `Namespaces` für logs events (-types). Diese werden folgend definiert.

### Reservierte Event-Namespaces
- system
- job
- security (auth)
- http
- db (inkl. migration)
- messaging
- camunda
- validation
- other (Business-spezifisch)

Beispiele für events (types):
- invoice.create.success
- invoice.validated
- system.login
- system.startup
- system.shutdown
- secrutiy.login.error

### Log-Kategorien & Hierarchie (Java)
Logger-Namen sind hierarchisch z.B. "com.daysofwonder.ranking.ELORankingComputation"

Vorteile:
- Filter nach Subsystemen
- Alerting auf Namespace-Ebene

## Log-Datenmodell
Logs sind Events (keine Debug-Ausgaben)

### ECS
Es wird sich an ECS orientiert. ECS beinhaltet Log-Struktur mit empfohlene Feldnamen und defaults.
ECS wird von vielen SIEMs (Security Information and Event Management) genutzt.
ECS ist De-facto-Standard in Industrie
Beispiel:

| Konzept     | Standard           | Nicht             |
| ----------- | ------------------ | ----------------- |
| HTTP Status | `http.status_code` | `httpStatus`      |
| HTTP Method | `http.method`      | `verb`            |
| Duration    | `duration_ms`      | `time`, `elapsed` |
| Error Type  | `error.type`       | `errorKind`       |

### Eingrenzung auf ausgewählte Felder
Folgend findet eine Eingrenzung der Felder statt. Dabei wird unterschieden in Standard fields, Context fields und Domain specific fields.

- message (human summary)
- event (machine-readable event type)
- duration_ms (for operations)
- error_type (for failures) (?)
- Request, operation, and trace IDs: critical for high concurrency, even if theyre entirely internal and just for log correlation (I try to use logging libraries with contextual key-value capabilities)


WIP:

- welches feld für events: event.type / event.action / event / event.category / event.kind / log tag / log topic / log event type, log type, log namespace, log category
> 2️⃣ Error Events
Du kannst alles über Event-Typen abbilden
Industrie-Best-Practice ist:
Event bleibt gleich
Error ist ein Attribut
>
> 3️⃣ event.action vs event.type
event.type = Was für ein Ding
event.action = Was wurde getan
event.outcome = Wie ging es aus
>
> Substantiv → event.type
Verb → event.action
Adjektiv → event.outcome

### Event-Typ vs. Error-Typ
Unklarheit explizit festgehalten:
- Event beschreibt was passiert ist
- Error Type beschreibt warum es fehlgeschlagen ist (? brauch man das überhaupt - liest man das nicht aus dem event+stacktrace aus - sonst müsste man den error doch direkt klassifizieren zusätzlich zum event oder)

## Log-Katalog / Event-Katalog
Log-Katalog (legacy) / Event-Katalog (State of the Art)
service-übergreifende Dokumentation aller existierenden Events , Zweck:
- Governance
- Konsistenz über Teams
- Audits
- Alert-Definitionen

unterscheidung von log catalogen und dashbaord implizit in SIEM-, SRE- und Audit-Prozessen angelehnt:

- L1 – Governed Events (Catalog Level)
  - Stabil, Versioniert
  - Alertfähig
  - Auditiert
  - Schema-validiert
  - Beispiel: security.login.failed

- L2 – Observational Events + L3 - Explorative Logs
  - Nicht versioniert, Temporäre Dashboards erlaubt, Keine harten SLAs, kein Audit-Zwang
  - Entwicklergetrieben, Debug / Investigation, Keine Alerts, Keine Stabilitätsgarantie, Kann jederzeit verschwinden

### Unterschiede zwischen Event Catalog und Log Catalog
|             | Event Catalog                     | Log Catalog        |
| ----------- | --------------------------------- | ------------------ |
| Fokus       | Fachliche & technische Ereignisse | Konkrete Logzeilen |
| Abstraktion | Hoch                              | Niedrig            |
| Stabilität  | Hoch                              | Mittel             |
| Governance  | Ja                                | Optional           |
| Audits      | Sehr gut                          | Mittel             |


## Governance & Compliance (RULES)
### Governance & Compliance: Relevante Standards & Gesetze
- ISO 27001: A.8.15 / A.8.16
- ISO 27002: Kapitel 8.15 – Logging
- NIS-2: Art. 21
- ENISA & EU-Empfehlungen (NIS-2-nah)
- CRA
- ISMS
- SDLC
- SIEM-Zwang
- Kein 24/7 SOC-Zwang

Zitat-/Audit-Fragen:
- „Wie analysieren Sie die Auswirkungen?“: Log- + Trace-Korrelation
- „Wie erfüllen Sie Art. 21 NIS-2 ohne diese Information?“

Deutschland:
- NIS2UmsuCG
- BSI-Gesetz
- BSI-Leitlinien / Orientierungshilfen
- BSI-Grundschutz (OPS.1.1.5, OPS.1.2.5)


## Technische Umsetzung

### MDC (Java)
- Sofern möglich auf sl4j v2+ Fluent-Api setzen statt MDC
- oft für technische / Infrastruktur-Daten z.B. span_id, Request-ID, Correlation-ID, Tenant-ID
- Seltener für Business-Daten


## Tracing vs. Business Correlation
Dürfen nicht verwechselt werden oder das eine für das andere instrumentatlisiert werden

| Typ                     | Zweck                 | Lifecycle                  | Beispiel                        |
| ----------------------- | --------------------- | -------------------------- | ------------------------------- |
| Tracing ID              | Technisches Debugging | Kurzlebig, pro Request     | `trace-id: 4f7a8b`              |
| Business Correlation ID | Fachliche Verbindung  | Lang lebig, DB-persistiert | `invoice-process-id: INV-12345` |


Tracing ID: Technical debugging, performance analysis, understanding call chains
Business correlation ID: Logical connection between business entities or flows

## Observability Plattform

### Observability Stack
- Grafana Alloy: Unified Observability Collector
- Prometheus: Metrics DB & Scraper
- Loki: Log Aggregation
- Tempo: Traces
- Grafana UI: Dashboards

### Grafana Alloy: Überblick
- Collect und Enrichment für Logs, Metrics, Traces, Profiles
- Ersetzt: Grafana Agent, Prometheus Agent, Promtail, OpenTelemetry Collector
- Grund für Abstraktion z.B. Abstraktion z.B. URLs ändern sich für Empfang, externe apps (z.B. gotenberg) loggen eventuell kein timestamp - wird aber trotzdem benötigt
- Welche Log-Felder kommen von App und Alloy - App liefert Business-Daten, Alloy enriched Logs:
  - Applikation: Business Data, Errors, Decisions, Domain State
  - Applikation oder Alloy: service.name (App bevorzugt), environment, Log Level
  - Alloy: Kubernetes Metadata, Host Metadata, Timestamps  (Wenn App bereits korrekten ECS-Timestamp loggt nicht überschreiben), Trace/Span IDs


### OpenTelemetry Integration
Folgend werden die Varianten sortiert nach Empfehlung (absteigend) dargestellt.
Besonders bei selbst gehosteten extern-entwickelten Services wird die eigene otel integration schwierig weshalb besonders die letzte option wenig trägt.

- OpenTelemetry Operator Injection
  - Deklarativ
  - Keine Image-Änderung
  - Auto-Upgrades
  - Production-Standard
- Sidecar / Volume Mount Agent
  - Sehr verbreitet
  - Manuell konfiguriert
- Init-Container Download
  - CI/CD kontrolliert
  - Komplexer
- In-Process Instrumentation
  - OpenTelemetry SDK / Spring Boot Starter
  - OTEL SDK läuft im selben Prozess
  - häufig in regulierten Umgebungen, On-Prem, Legacy Kubernetes, Weniger „Cloud-native“, aber nicht falsch
- Agent (jar) ins Image backen
  - Legacy
  - Nur Dev / PoC


### Tooling & Sonstiges
- Eigene Logger-Klasse (z. B. ValidatedLogger): Log-Validierung gegen Schema
- SLF4J / Log4j2 Setup: `log4j-slf4j2-impl:2.25.3`, `slf4j-api:2.0.17` (bietet fluent-api)
- Deployment Grafana Alloy: 1 DaemonSet instance per Kubernetes node (90% of cases)
- Use metrics, traces, and logs properly, and don't try to use one for another
- Eigene Logger klasse abgeleitet von base logger oder base logger als composition/field (siehe nemorize link)


## Referenzen & Links
- Grafana Alloy:
  - https://grafana.com/docs/alloy/latest/tutorials/processing-logs/
  - https://grafana.com/docs/alloy/latest/monitor/monitor-structured-logs/
  - https://github.com/grafana/alloy-scenarios
- Observability Roadmap:
  - https://nemorize.com/roadmaps/production-observability-from-signals-to-root-cause-2026/
- Logging Philosophy:
  - https://loggingsucks.com
- grafana alloy:
  - https://grafana.com/docs/alloy/latest/tutorials/processing-logs/
  - https://grafana.com/docs/alloy/latest/monitor/monitor-structured-logs/#understand-the-alloy-configuration
  - https://github.com/grafana/alloy-scenarios?tab=readme-ov-file
- grafana ai
  - https://grafana.com/grot/
- Strukturiertes Logging aus Developer-Sicht
  - https://www.reddit.com/r/golang/comments/1kcmdy7/comment/mq43jto/?utm_source=share&utm_medium=web3x&utm_name=web3xcss&utm_term=1&utm_content=share_button
- https://www.honeycomb.io/blog/engineers-checklist-logging-best-practices