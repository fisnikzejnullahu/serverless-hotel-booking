#!/bin/sh
echo "building booking service"
mvn clean package -f ./booking-service
echo "building payments service"
mvn clean package -f ./payments-service
echo "building invoice service"
mvn clean package -f ./payments-service
