#!/usr/bin/env bash
protoc -I=src/main/resources --proto_path=src/main/resources --java_out=src/main/java src/main/resources/*
protoc -I=src/test/resources --proto_path=src/test/resources --java_out=src/test/java src/test/resources/testing_model.proto
protoc -I=src/test/resources --proto_path=src/test/resources --java_out=src/test/java src/test/resources/testing_model_v2.proto
protoc -I=src/test/resources --proto_path=src/test/resources --java_out=src/test/java src/test/resources/testing_model_v3.proto
protoc -I=src/test/resources --proto_path=src/test/resources --java_out=src/test/java src/test/resources/nested_enum_test.proto
protoc -I=src/test/resources --proto_path=src/test/resources --java_out=src/test/java src/test/resources/complex_model.proto