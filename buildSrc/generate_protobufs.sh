#!/usr/bin/env bash
. ~/.bashrc
. ~/.bash_profile
protoc -I=src/main/resources --proto_path=src/main/resources --java_out=src/main/java src/main/resources/*.proto
