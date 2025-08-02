#!/bin/bash

GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
RED='\033[0;31m'
NC='\033[0m'

case "$1" in
  "start"|"up")
    echo -e "${YELLOW}🚀 Starting application...${NC}"
    docker compose up -d
    ;;
  "stop"|"down")
    echo -e "${YELLOW}🛑 Stopping application...${NC}"
    docker compose down
    ;;
  "restart")
    echo -e "${YELLOW}🔄 Restarting...${NC}"
    docker compose restart
    ;;
  "rebuild")
    echo -e "${YELLOW}🔨 Rebuilding images...${NC}"
    docker compose down
    docker compose up --build -d
    ;;
  "logs")
    docker compose logs -f
    ;;
  "status")
    docker compose ps
    ;;
  "clean")
    echo -e "${YELLOW}🧹 Cleaning everything...${NC}"
    docker compose down -v --rmi all
    docker system prune -f
    ;;
  "clean-vol")
    echo -e "${YELLOW}🧹 Cleaning volumes...${NC}"
    docker compose down -v
    ;;
  "reset-db")
    echo -e "${YELLOW}🗄️  Resetting database...${NC}"
    echo "⚠️  All data in the database will be lost!"
    read -p "Continue? (y/N): " -n 1 -r
    echo
    if [[ $REPLY =~ ^[Yy]$ ]]; then
      docker compose down -v
      docker compose up -d
      echo -e "${GREEN}✅ Database reset! Waiting for initialization...${NC}"

      echo -e "${YELLOW}⏳ Waiting for database to be ready...${NC}"
      for i in {1..30}; do
        if docker exec bookstore-db mariadb -u bookstore_user -pBookStore@777 -e "SELECT 1;" bookstore &>/dev/null; then
          echo -e "${GREEN}✅ Database is ready!${NC}"
          break
        fi
        echo -e "${YELLOW}⏳ Attempt $i/30 - Database not ready yet...${NC}"
        sleep 2
      done
      
      if [ $i -eq 30 ]; then
        echo -e "${RED}⚠️  Database may not be fully ready. Try waiting a bit more before starting the application.${NC}"
      fi
    else
      echo -e "${RED}❌ Operation canceled${NC}"
    fi
    ;;
  "fix")
    echo -e "${YELLOW}🔧 Fixing container conflicts...${NC}"
    docker compose down --remove-orphans
    docker container prune -f
    docker compose up -d
    ;;
  *)
    echo -e "${BLUE}BookStore Blackbird - Docker Manager${NC}"
    echo
    echo -e "${YELLOW}Main commands:${NC}"
    echo -e "  ${GREEN}start/up${NC}    - Start application"
    echo -e "  ${GREEN}stop/down${NC}   - Stop application"
    echo -e "  ${GREEN}restart${NC}     - Restart containers"
    echo -e "  ${GREEN}rebuild${NC}     - Rebuild everything"
    echo
    echo -e "${YELLOW}Utilitários:${NC}"
    echo -e "  ${GREEN}logs${NC}        - View logs"
    echo -e "  ${GREEN}status${NC}      - Container status"
    echo -e "  ${GREEN}clean${NC}       - Clean everything"
    echo -e "  ${GREEN}clean-vol${NC}   - Clean volumes"
    echo
    echo -e "${YELLOW}Database:${NC}"
    echo -e "  ${GREEN}reset-db${NC}    - Reset database (all data will be lost)"
    echo -e "  ${GREEN}fix${NC}         - Fix container conflicts"
    echo
    exit 1
    ;;
esac
