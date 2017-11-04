#!/bin/bash

docker run -d --name rsobook_db_images -e POSTGRES_USER=dbuser -e POSTGRES_PASSWORD=postgres -e POSTGRES_DB=images -p 5432:5432 postgres:latest
