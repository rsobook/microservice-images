#!/bin/bash

docker run -d --name rsobook_db_images -e POSTGRES_USER=postgres -e POSTGRES_PASSWORD=root -e POSTGRES_DB=rsobook-image -p 5432:5432 postgres:latest
