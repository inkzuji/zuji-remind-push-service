#!/bin/bash

mvn -T 4 -U clean package -DskipTests -pl ./zuji-remind-push-biz -am

#cp ./target/zuji-remind-push-service-*.jar ./docker
