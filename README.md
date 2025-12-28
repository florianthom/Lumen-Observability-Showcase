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