#!/bin/bash

GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
RED='\033[0;31m'
NC='\033[0m'

wait_for_db() {
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
}

case "$1" in
  "build")
    echo -e "${YELLOW} 🔧 Building application...${NC}"
    if ! docker exec bookstore-db mariadb -u bookstore_user -pBookStore@777 -e "SELECT 1;" bookstore &>/dev/null; then
      echo -e "${RED}❌ Database is not up. Please start the database first using './docker.sh start-db'${NC}"
      exit 1
    fi
    mvn clean install
    ;;
  "clean")
    mvn clean
    ;;
  "run")
    echo -e "${YELLOW}🚀 Running application...${NC}"
    if ! docker exec bookstore-db mariadb -u bookstore_user -pBookStore@777 -e "SELECT 1;" bookstore &>/dev/null; then
      echo -e "${RED}❌ Database is not up. Please start the database first using './docker.sh start-db'${NC}"
      exit 1
    fi
    mvn exec:java -pl app
    ;;
  "test")
    echo -e "${YELLOW}🧪 Running tests...${NC}"
    if ! docker exec bookstore-db mariadb -u bookstore_user -pBookStore@777 -e "SELECT 1;" bookstore &>/dev/null; then
      echo -e "${RED}❌ Database is not up. Please start the database first using './docker.sh start-db'${NC}"
      exit 1
    fi
    mvn test
    ;;
  "kickstart")
    echo -e "${YELLOW}🚀 Starting application...${NC}"
    if ! docker exec bookstore-db mariadb -u bookstore_user -pBookStore@777 -e "SELECT 1;" bookstore &>/dev/null; then
      echo -e "${RED}❌ Database is not up. Please start the database first using './docker.sh start-db'${NC}"
      exit 1
    fi
    mvn clean install && mvn exec:java -pl app
    ;;
    
  "start-db"|"up")
    echo -e "${YELLOW}🚀 Starting database...${NC}"
    docker compose up -d
    wait_for_db
    ;;
  "stop-db"|"down")
    echo -e "${YELLOW}🛑 Stopping database...${NC}"
    docker compose down
    ;;
  "clean-db")
    echo -e "${YELLOW}🧹 Cleaning everything...${NC}"
    docker compose down -v --rmi all
    docker system prune -f
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

      wait_for_db
    else
      echo -e "${RED}❌ Operation canceled${NC}"
    fi
    ;;
  "logs")
    docker compose logs -f
    ;;
  "status")
    docker compose ps
    ;;
  "clean-vol")
    echo -e "${YELLOW}🧹 Cleaning volumes...${NC}"
    docker compose down -v
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
    echo -e "${YELLOW}Application commands:${NC}"
    echo -e "  ${GREEN}build${NC}         - Build application"
    echo -e "  ${GREEN}clean${NC}         - Clean application"
    echo -e "  ${GREEN}run${NC}           - Run application"
    echo -e "  ${GREEN}test${NC}          - Run tests"
    echo -e "  ${GREEN}kickstart${NC}     - Build and run application"
    echo
    echo -e "${YELLOW}Database commands:${NC}"
    echo -e "  ${GREEN}start-db/up${NC}   - Start database"
    echo -e "  ${GREEN}stop-db/down${NC}  - Stop database"
    echo -e "  ${GREEN}clean-db${NC}      - Clean database"
    echo -e "  ${GREEN}reset-db${NC}      - Reset database (all data will be lost)"
    echo -e "  ${GREEN}fix${NC}           - Fix container conflicts"
    echo
    echo -e "${YELLOW}Utils:${NC}"
    echo -e "  ${GREEN}logs${NC}          - View logs"
    echo -e "  ${GREEN}status${NC}        - Container status"
    echo -e "  ${GREEN}clean-vol${NC}     - Clean volumes"
    echo
    exit 1
    ;;
esac
