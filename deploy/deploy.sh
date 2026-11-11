#!/bin/sh
# Updates the server to the latest main of both repositories and restarts what changed.
# Run by GitHub Actions over SSH after a successful CI run.
# The braces make the shell read the whole script first: git pull may rewrite this file.
{
  set -eu
  exec 9>/tmp/curiolane-deploy.lock
  flock 9   # one deploy at a time

  cd "$HOME/curiolane/ecommerce"
  git pull --ff-only --quiet
  cd "$HOME/curiolane/ecommerce-frontend"
  git pull --ff-only --quiet

  cd "$HOME/curiolane/ecommerce"
  docker compose -f docker-compose.prod.yml up -d --build
  docker image prune -f >/dev/null

  echo "Online: backend $(git log -1 --format=%h), frontend $(git -C ../ecommerce-frontend log -1 --format=%h)"
  exit 0
}
