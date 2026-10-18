# Spring PetClinic Microservices (experimento)

Versão em **microsserviços** do PetClinic usada no experimento controlado de comparação de desempenho com o monolito modular.

## Arquitetura enxuta

| Componente | Função |
|---|---|
| `api-gateway` | Porta única (`8080`); rotas estáticas |
| `customers-service` | Owners e Pets |
| `vets-service` | Veterinários |
| `postgres` | Bancos `customers` e `vets` |

Removidos do experimento: Config Server, Eureka, Visits, GenAI, Admin, Zipkin, Grafana/Prometheus e front-end Vue.

## Escopo do experimento

Mesmo fluxo JMeter do monolito:

- `GET /api/vet/vets`
- `POST /api/customer/owners`
- `POST /api/customer/owners/{ownerId}/pets`

## Como executar (Docker)

Na pasta do experimento:

```bash
python OrquestradorMicrosservicos.py
```

O orquestrador constrói as imagens locais (`-PbuildDocker`) e sobe a stack do `docker-compose.yml`.

## Métricas

Throughput (fluxos/s via JMeter) e consumo de CPU/RAM (`docker stats` de gateway + customers + vets + postgres), analisados por `AnalisarResultados.py`.
