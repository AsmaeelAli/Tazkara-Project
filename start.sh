
#!/usr/bin/env bash
#chmod +x start.sh
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

COMPOSE=(
  docker compose
  --project-name tazkara
  --env-file "$ROOT_DIR/.env"
)

echo "Starting PostgreSQL..."
"${COMPOSE[@]}" -f "$ROOT_DIR/docker/postgres.compose.yaml" up -d

echo "Starting RabbitMQ..."
"${COMPOSE[@]}" -f "$ROOT_DIR/docker/rabbitmq.compose.yaml" up -d

echo "Infrastructure containers started."
docker ps --filter "name=tazkara-" \
  --format "table {{.Names}}\t{{.Image}}\t{{.Status}}\t{{.Ports}}"
