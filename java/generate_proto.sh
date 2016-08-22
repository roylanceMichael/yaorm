#!/usr/bin/env bash
~/.nuget/packages/Google.Protobuf.Tools/3.0.0/tools/macosx_x64/protoc -I=src/main/resources --proto_path=src/main/resources --java_out=src/main/java src/main/resources/*
~/.nuget/packages/Google.Protobuf.Tools/3.0.0/tools/macosx_x64/protoc -I=src/test/resources --proto_path=src/test/resources --java_out=src/test/java src/test/resources/testing_model.proto
~/.nuget/packages/Google.Protobuf.Tools/3.0.0/tools/macosx_x64/protoc -I=src/test/resources --proto_path=src/test/resources --java_out=src/test/java src/test/resources/testing_model_v2.proto
~/.nuget/packages/Google.Protobuf.Tools/3.0.0/tools/macosx_x64/protoc -I=src/test/resources --proto_path=src/test/resources --java_out=src/test/java src/test/resources/testing_model_v3.proto
~/.nuget/packages/Google.Protobuf.Tools/3.0.0/tools/macosx_x64/protoc -I=src/test/resources --proto_path=src/test/resources --java_out=src/test/java src/test/resources/nested_enum_test.proto
~/.nuget/packages/Google.Protobuf.Tools/3.0.0/tools/macosx_x64/protoc -I=src/test/resources --proto_path=src/test/resources --java_out=src/test/java src/test/resources/complex_model.proto

#protoc -I=src/main/proto --proto_path=src/main/proto --csharp_out="../csharp/Yaorm/Yaorm" src/main/proto/*