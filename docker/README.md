# Starting the database

docker-compose up -d

# Accessing the database:

## From host's MariaDB client:

mariadb -h 127.0.0.1 -P 3307 -u root -proot --skip-ssl bookstore

## From docker containers:

docker exec -it bookstore-db mariadb -u root -proot bookstore
