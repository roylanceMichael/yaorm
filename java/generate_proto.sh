#!/usr/bin/env bash
protoc -I=src/main/proto --proto_path=src/main/proto --java_out=src/main/java src/main/proto/*
protoc -I=src/test/proto --proto_path=src/test/proto --java_out=src/test/java src/test/proto/*

#protoc -I=src/main/proto --proto_path=src/main/proto --csharp_out="../csharp/Yaorm/Yaorm" src/main/proto/*