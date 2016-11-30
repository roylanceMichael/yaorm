"use strict";
var _root = dcodeIO.ProtoBuf.newBuilder({})['import']({
    "package": "org.roylance.yaorm",
    "messages": [
        {
            "name": "ProtoTypeToSqlType",
            "fields": [
                {
                    "rule": "optional",
                    "type": "ProtobufType",
                    "name": "proto_type",
                    "id": 1
                },
                {
                    "rule": "optional",
                    "type": "string",
                    "name": "sql_type",
                    "id": 2
                }
            ]
        },
        {
            "name": "ProtoTypeToSqlTypes",
            "fields": [
                {
                    "rule": "repeated",
                    "type": "ProtoTypeToSqlType",
                    "name": "results",
                    "id": 1
                }
            ]
        },
        {
            "name": "WhereClause",
            "fields": [
                {
                    "rule": "optional",
                    "type": "Column",
                    "name": "name_and_property",
                    "id": 1
                },
                {
                    "rule": "optional",
                    "type": "OperatorType",
                    "name": "operator_type",
                    "id": 2
                },
                {
                    "rule": "optional",
                    "type": "ConnectingAndOr",
                    "name": "connecting_and_or",
                    "id": 3
                },
                {
                    "rule": "optional",
                    "type": "WhereClause",
                    "name": "connecting_where_clause",
                    "id": 4
                },
                {
                    "rule": "repeated",
                    "type": "string",
                    "name": "in_items",
                    "id": 5
                }
            ],
            "enums": [
                {
                    "name": "OperatorType",
                    "values": [
                        {
                            "name": "EQUALS",
                            "id": 0
                        },
                        {
                            "name": "GREATER_THAN",
                            "id": 1
                        },
                        {
                            "name": "LESS_THAN",
                            "id": 2
                        },
                        {
                            "name": "NOT_EQUALS",
                            "id": 3
                        },
                        {
                            "name": "IN",
                            "id": 4
                        }
                    ]
                },
                {
                    "name": "ConnectingAndOr",
                    "values": [
                        {
                            "name": "NONE",
                            "id": 0
                        },
                        {
                            "name": "AND",
                            "id": 1
                        },
                        {
                            "name": "OR",
                            "id": 2
                        }
                    ]
                }
            ]
        },
        {
            "name": "ColumnDefinition",
            "fields": [
                {
                    "rule": "optional",
                    "type": "string",
                    "name": "name",
                    "id": 1
                },
                {
                    "rule": "optional",
                    "type": "ProtobufType",
                    "name": "type",
                    "id": 2
                },
                {
                    "rule": "optional",
                    "type": "bool",
                    "name": "is_key",
                    "id": 3
                },
                {
                    "rule": "optional",
                    "type": "ColumnType",
                    "name": "column_type",
                    "id": 4
                },
                {
                    "rule": "optional",
                    "type": "LinkerType",
                    "name": "linker_type",
                    "id": 5
                },
                {
                    "rule": "optional",
                    "type": "int32",
                    "name": "order",
                    "id": 6
                }
            ],
            "enums": [
                {
                    "name": "ColumnType",
                    "values": [
                        {
                            "name": "SCALAR",
                            "id": 0
                        },
                        {
                            "name": "ENUM_NAME",
                            "id": 1
                        },
                        {
                            "name": "ENUM_VALUE",
                            "id": 2
                        },
                        {
                            "name": "MESSAGE_KEY",
                            "id": 3
                        }
                    ]
                },
                {
                    "name": "LinkerType",
                    "values": [
                        {
                            "name": "NONE",
                            "id": 0
                        },
                        {
                            "name": "PARENT",
                            "id": 1
                        },
                        {
                            "name": "CHILD",
                            "id": 2
                        },
                        {
                            "name": "NEITHER",
                            "id": 3
                        }
                    ]
                }
            ]
        },
        {
            "name": "Index",
            "fields": [
                {
                    "rule": "repeated",
                    "type": "ColumnDefinition",
                    "name": "column_names",
                    "id": 1
                },
                {
                    "rule": "repeated",
                    "type": "ColumnDefinition",
                    "name": "include_names",
                    "id": 2
                }
            ]
        },
        {
            "name": "Difference",
            "fields": [
                {
                    "rule": "optional",
                    "type": "EntityType",
                    "name": "entity_type",
                    "id": 1
                },
                {
                    "rule": "optional",
                    "type": "Operation",
                    "name": "operation",
                    "id": 2
                },
                {
                    "rule": "optional",
                    "type": "string",
                    "name": "name",
                    "id": 3
                },
                {
                    "rule": "optional",
                    "type": "Index",
                    "name": "index",
                    "id": 4
                },
                {
                    "rule": "optional",
                    "type": "ColumnDefinition",
                    "name": "property_definition",
                    "id": 5
                },
                {
                    "rule": "optional",
                    "type": "TableDefinition",
                    "name": "table_definition",
                    "id": 6
                }
            ],
            "enums": [
                {
                    "name": "EntityType",
                    "values": [
                        {
                            "name": "INDEX",
                            "id": 0
                        },
                        {
                            "name": "COLUMN",
                            "id": 1
                        },
                        {
                            "name": "TABLE",
                            "id": 2
                        }
                    ]
                },
                {
                    "name": "Operation",
                    "values": [
                        {
                            "name": "CREATE",
                            "id": 0
                        },
                        {
                            "name": "DROP",
                            "id": 1
                        }
                    ]
                }
            ]
        },
        {
            "name": "TableDefinition",
            "fields": [
                {
                    "rule": "optional",
                    "type": "string",
                    "name": "name",
                    "id": 1
                },
                {
                    "rule": "repeated",
                    "type": "ColumnDefinition",
                    "name": "column_definitions",
                    "id": 2
                },
                {
                    "rule": "optional",
                    "type": "Index",
                    "name": "index",
                    "id": 3
                },
                {
                    "rule": "optional",
                    "type": "TableType",
                    "name": "table_type",
                    "id": 4
                }
            ],
            "enums": [
                {
                    "name": "TableType",
                    "values": [
                        {
                            "name": "NORMAL",
                            "id": 0
                        },
                        {
                            "name": "LINKER_MESSAGE",
                            "id": 1
                        },
                        {
                            "name": "LINKER_ENUM",
                            "id": 2
                        }
                    ]
                }
            ]
        },
        {
            "name": "TableDefinitions",
            "fields": [
                {
                    "rule": "repeated",
                    "type": "TableDefinition",
                    "name": "table_definitions",
                    "id": 1
                }
            ]
        },
        {
            "name": "DifferenceReport",
            "fields": [
                {
                    "rule": "optional",
                    "type": "bool",
                    "name": "migration_exists",
                    "id": 1
                },
                {
                    "rule": "repeated",
                    "type": "Difference",
                    "name": "differences",
                    "id": 2
                }
            ]
        },
        {
            "name": "Column",
            "fields": [
                {
                    "rule": "optional",
                    "type": "ColumnDefinition",
                    "name": "definition",
                    "id": 1
                },
                {
                    "rule": "optional",
                    "type": "double",
                    "name": "double_holder",
                    "id": 2
                },
                {
                    "rule": "optional",
                    "type": "float",
                    "name": "float_holder",
                    "id": 3
                },
                {
                    "rule": "optional",
                    "type": "int32",
                    "name": "int32_holder",
                    "id": 4
                },
                {
                    "rule": "optional",
                    "type": "int64",
                    "name": "int64_holder",
                    "id": 5
                },
                {
                    "rule": "optional",
                    "type": "uint32",
                    "name": "uint32_holder",
                    "id": 6
                },
                {
                    "rule": "optional",
                    "type": "uint64",
                    "name": "uint64_holder",
                    "id": 7
                },
                {
                    "rule": "optional",
                    "type": "sint32",
                    "name": "sint32_holder",
                    "id": 8
                },
                {
                    "rule": "optional",
                    "type": "sint64",
                    "name": "sint64_holder",
                    "id": 9
                },
                {
                    "rule": "optional",
                    "type": "fixed32",
                    "name": "fixed32_holder",
                    "id": 10
                },
                {
                    "rule": "optional",
                    "type": "fixed64",
                    "name": "fixed64_holder",
                    "id": 11
                },
                {
                    "rule": "optional",
                    "type": "sfixed32",
                    "name": "sfixed32_holder",
                    "id": 12
                },
                {
                    "rule": "optional",
                    "type": "sfixed64",
                    "name": "sfixed64_holder",
                    "id": 13
                },
                {
                    "rule": "optional",
                    "type": "bool",
                    "name": "bool_holder",
                    "id": 14
                },
                {
                    "rule": "optional",
                    "type": "string",
                    "name": "string_holder",
                    "id": 15
                },
                {
                    "rule": "optional",
                    "type": "bytes",
                    "name": "bytes_holder",
                    "id": 16
                }
            ]
        },
        {
            "name": "Record",
            "fields": [
                {
                    "rule": "repeated",
                    "type": "Column",
                    "name": "columns",
                    "id": 1
                }
            ]
        },
        {
            "name": "Records",
            "fields": [
                {
                    "rule": "repeated",
                    "type": "Record",
                    "name": "records",
                    "id": 1
                }
            ]
        },
        {
            "name": "TableRecords",
            "fields": [
                {
                    "rule": "optional",
                    "type": "TableDefinition",
                    "name": "table_definition",
                    "id": 1
                },
                {
                    "rule": "optional",
                    "type": "Records",
                    "name": "records",
                    "id": 2
                },
                {
                    "rule": "optional",
                    "type": "string",
                    "name": "table_name",
                    "id": 3
                }
            ]
        },
        {
            "name": "AllTableRecords",
            "fields": [
                {
                    "rule": "repeated",
                    "type": "TableRecords",
                    "name": "table_records",
                    "id": 1
                }
            ]
        },
        {
            "name": "DatabaseOperation",
            "fields": [
                {
                    "rule": "optional",
                    "type": "TableDefinition",
                    "name": "table_definition",
                    "id": 1
                },
                {
                    "rule": "optional",
                    "type": "Records",
                    "name": "records",
                    "id": 2
                },
                {
                    "rule": "optional",
                    "type": "Index",
                    "name": "index",
                    "id": 3
                },
                {
                    "rule": "optional",
                    "type": "ColumnDefinition",
                    "name": "column_definition",
                    "id": 4
                },
                {
                    "rule": "optional",
                    "type": "WhereClause",
                    "name": "where_clause",
                    "id": 5
                },
                {
                    "rule": "optional",
                    "type": "DatabaseOperationType",
                    "name": "database_operation_type",
                    "id": 6
                }
            ],
            "enums": [
                {
                    "name": "DatabaseOperationType",
                    "values": [
                        {
                            "name": "CREATE_TABLE",
                            "id": 0
                        },
                        {
                            "name": "DROP_TABLE",
                            "id": 1
                        },
                        {
                            "name": "CREATE_INDEX",
                            "id": 2
                        },
                        {
                            "name": "DROP_INDEX",
                            "id": 3
                        },
                        {
                            "name": "CREATE_COLUMN",
                            "id": 4
                        },
                        {
                            "name": "DROP_COLUMN",
                            "id": 5
                        },
                        {
                            "name": "GET_COUNT",
                            "id": 6
                        },
                        {
                            "name": "GET_CUSTOM",
                            "id": 7
                        },
                        {
                            "name": "GET",
                            "id": 8
                        },
                        {
                            "name": "GET_MANY",
                            "id": 9
                        },
                        {
                            "name": "GET_WHERE",
                            "id": 10
                        },
                        {
                            "name": "BULK_INSERT",
                            "id": 11
                        },
                        {
                            "name": "CREATE_OR_UPDATE",
                            "id": 12
                        },
                        {
                            "name": "CREATE",
                            "id": 13
                        },
                        {
                            "name": "UPDATE",
                            "id": 14
                        },
                        {
                            "name": "UPDATE_WITH_CRITERIA",
                            "id": 15
                        },
                        {
                            "name": "UPDATE_CUSTOM",
                            "id": 16
                        },
                        {
                            "name": "DELETE",
                            "id": 17
                        },
                        {
                            "name": "DELETE_ALL",
                            "id": 18
                        }
                    ]
                }
            ]
        },
        {
            "name": "DatabaseOperationResult",
            "fields": [
                {
                    "rule": "optional",
                    "type": "bool",
                    "name": "bool_result",
                    "id": 1
                },
                {
                    "rule": "optional",
                    "type": "int64",
                    "name": "count_result",
                    "id": 2
                },
                {
                    "rule": "optional",
                    "type": "Record",
                    "name": "record_result",
                    "id": 3
                },
                {
                    "rule": "optional",
                    "type": "Records",
                    "name": "records_result",
                    "id": 4
                },
                {
                    "rule": "optional",
                    "type": "string",
                    "name": "error_message",
                    "id": 5
                },
                {
                    "rule": "optional",
                    "type": "DatabaseOperation",
                    "name": "database_operation",
                    "id": 6
                }
            ]
        },
        {
            "name": "DatabaseDefinition",
            "fields": [
                {
                    "rule": "optional",
                    "type": "string",
                    "name": "schema",
                    "id": 1
                },
                {
                    "rule": "optional",
                    "type": "string",
                    "name": "name",
                    "id": 2
                },
                {
                    "rule": "optional",
                    "type": "TableDefinitions",
                    "name": "table_definitions",
                    "id": 3
                },
                {
                    "rule": "repeated",
                    "type": "TableDefinitionGraphs",
                    "name": "table_definition_graphs",
                    "id": 4
                }
            ]
        },
        {
            "name": "TableDefinitionGraph",
            "fields": [
                {
                    "rule": "optional",
                    "type": "TableDefinition",
                    "name": "main_table_definition",
                    "id": 1
                },
                {
                    "rule": "optional",
                    "type": "TableDefinition",
                    "name": "other_table_definition",
                    "id": 2
                },
                {
                    "rule": "optional",
                    "type": "TableDefinition",
                    "name": "linker_table_table",
                    "id": 3
                },
                {
                    "rule": "optional",
                    "type": "TableDefinitionGraphType",
                    "name": "definition_graph_type",
                    "id": 4
                },
                {
                    "rule": "optional",
                    "type": "string",
                    "name": "main_name",
                    "id": 5
                },
                {
                    "rule": "optional",
                    "type": "string",
                    "name": "other_name",
                    "id": 6
                },
                {
                    "rule": "optional",
                    "type": "string",
                    "name": "column_name",
                    "id": 7
                }
            ],
            "enums": [
                {
                    "name": "TableDefinitionGraphType",
                    "values": [
                        {
                            "name": "ENUM_TYPE",
                            "id": 0
                        },
                        {
                            "name": "MESSAGE_TYPE",
                            "id": 1
                        }
                    ]
                }
            ]
        },
        {
            "name": "TableDefinitionGraphs",
            "fields": [
                {
                    "rule": "optional",
                    "type": "TableDefinition",
                    "name": "main_table_definition",
                    "id": 1
                },
                {
                    "rule": "repeated",
                    "type": "TableDefinitionGraph",
                    "name": "table_definition_graphs",
                    "id": 2
                }
            ]
        },
        {
            "name": "ConnectionInfo",
            "fields": [
                {
                    "rule": "optional",
                    "type": "string",
                    "name": "host",
                    "id": 1
                },
                {
                    "rule": "optional",
                    "type": "string",
                    "name": "user",
                    "id": 2
                },
                {
                    "rule": "optional",
                    "type": "string",
                    "name": "password",
                    "id": 3
                },
                {
                    "rule": "optional",
                    "type": "string",
                    "name": "schema",
                    "id": 4
                },
                {
                    "rule": "optional",
                    "type": "bool",
                    "name": "should_create_schema",
                    "id": 5
                },
                {
                    "rule": "optional",
                    "type": "int32",
                    "name": "port",
                    "id": 6
                }
            ]
        },
        {
            "name": "Migration",
            "fields": [
                {
                    "rule": "optional",
                    "type": "string",
                    "name": "id",
                    "id": 1
                },
                {
                    "rule": "optional",
                    "type": "string",
                    "name": "context_name",
                    "id": 2
                },
                {
                    "rule": "optional",
                    "type": "string",
                    "name": "model_definition_base64",
                    "id": 3
                },
                {
                    "rule": "optional",
                    "type": "int64",
                    "name": "insert_date",
                    "id": 4
                }
            ]
        },
        {
            "name": "DatabaseExecutionReport",
            "fields": [
                {
                    "rule": "optional",
                    "type": "int64",
                    "name": "calls_to_database",
                    "id": 1
                }
            ]
        },
        {
            "name": "JoinKey",
            "fields": [
                {
                    "rule": "optional",
                    "type": "ColumnDefinition",
                    "name": "first",
                    "id": 1
                },
                {
                    "rule": "optional",
                    "type": "ColumnDefinition",
                    "name": "second",
                    "id": 2
                },
                {
                    "rule": "optional",
                    "type": "ColumnDefinition",
                    "name": "third",
                    "id": 3
                },
                {
                    "rule": "optional",
                    "type": "ColumnDefinition",
                    "name": "fourth",
                    "id": 4
                },
                {
                    "rule": "optional",
                    "type": "ColumnDefinition",
                    "name": "fifth",
                    "id": 5
                },
                {
                    "rule": "optional",
                    "type": "ColumnDefinition",
                    "name": "sixth",
                    "id": 6
                }
            ]
        },
        {
            "name": "JoinDefinition",
            "fields": [
                {
                    "rule": "optional",
                    "type": "TableDefinition",
                    "name": "table",
                    "id": 1
                },
                {
                    "rule": "optional",
                    "type": "JoinKey",
                    "name": "key",
                    "id": 2
                }
            ]
        },
        {
            "name": "Join",
            "fields": [
                {
                    "rule": "repeated",
                    "type": "JoinDefinition",
                    "name": "join_definitions",
                    "id": 1
                }
            ]
        },
        {
            "name": "UIYaormRequest",
            "fields": [
                {
                    "rule": "optional",
                    "type": "string",
                    "name": "token",
                    "id": 1
                },
                {
                    "rule": "optional",
                    "type": "TableDefinition",
                    "name": "table_definition",
                    "id": 2
                },
                {
                    "rule": "optional",
                    "type": "Records",
                    "name": "records",
                    "id": 3
                },
                {
                    "rule": "optional",
                    "type": "ConnectionInfo",
                    "name": "connection_info",
                    "id": 4
                },
                {
                    "rule": "optional",
                    "type": "WhereClause",
                    "name": "where_clause",
                    "id": 5
                },
                {
                    "rule": "optional",
                    "type": "int64",
                    "name": "limit",
                    "id": 6
                },
                {
                    "rule": "optional",
                    "type": "int64",
                    "name": "offset",
                    "id": 7
                },
                {
                    "rule": "optional",
                    "type": "bool",
                    "name": "insert_same_as_update",
                    "id": 8
                },
                {
                    "rule": "optional",
                    "type": "string",
                    "name": "schema_name",
                    "id": 9
                },
                {
                    "rule": "optional",
                    "type": "string",
                    "name": "table_name",
                    "id": 10
                },
                {
                    "rule": "optional",
                    "type": "string",
                    "name": "custom_sql",
                    "id": 11
                },
                {
                    "rule": "optional",
                    "type": "string",
                    "name": "file_descriptor",
                    "id": 13
                },
                {
                    "rule": "repeated",
                    "type": "string",
                    "name": "ids",
                    "id": 14
                }
            ]
        },
        {
            "name": "UIYaormResponse",
            "fields": [
                {
                    "rule": "optional",
                    "type": "Records",
                    "name": "records",
                    "id": 1
                },
                {
                    "rule": "repeated",
                    "type": "string",
                    "name": "schemas",
                    "id": 2
                },
                {
                    "rule": "repeated",
                    "type": "string",
                    "name": "tables",
                    "id": 3
                },
                {
                    "rule": "optional",
                    "type": "TableDefinition",
                    "name": "table_definition",
                    "id": 4
                },
                {
                    "rule": "optional",
                    "type": "TableDefinitions",
                    "name": "table_definitions",
                    "id": 5
                },
                {
                    "rule": "optional",
                    "type": "int64",
                    "name": "record_count",
                    "id": 6
                },
                {
                    "rule": "optional",
                    "type": "bool",
                    "name": "result",
                    "id": 7
                },
                {
                    "rule": "repeated",
                    "type": "ProtoTypeToSqlType",
                    "name": "proto_type_to_sql_types",
                    "id": 8
                },
                {
                    "rule": "optional",
                    "type": "string",
                    "name": "string_result",
                    "id": 9
                }
            ]
        },
        {
            "name": "SqlGeneratorRequestResponse",
            "fields": [
                {
                    "rule": "optional",
                    "type": "TableDefinition",
                    "name": "definition",
                    "id": 1
                },
                {
                    "rule": "optional",
                    "type": "ColumnDefinition",
                    "name": "column_definition",
                    "id": 2
                },
                {
                    "rule": "optional",
                    "type": "Index",
                    "name": "index",
                    "id": 3
                },
                {
                    "rule": "optional",
                    "type": "Column",
                    "name": "column",
                    "id": 4
                },
                {
                    "rule": "optional",
                    "type": "WhereClause",
                    "name": "where_clause",
                    "id": 5
                },
                {
                    "rule": "optional",
                    "type": "Records",
                    "name": "records",
                    "id": 6
                },
                {
                    "rule": "optional",
                    "type": "int64",
                    "name": "limit",
                    "id": 7
                },
                {
                    "rule": "optional",
                    "type": "int64",
                    "name": "offset",
                    "id": 8
                },
                {
                    "rule": "optional",
                    "type": "string",
                    "name": "schema_name",
                    "id": 9
                },
                {
                    "rule": "optional",
                    "type": "string",
                    "name": "table_name",
                    "id": 10
                },
                {
                    "rule": "optional",
                    "type": "string",
                    "name": "response",
                    "id": 11
                },
                {
                    "rule": "optional",
                    "type": "ProtoTypeToSqlTypes",
                    "name": "proto_type_to_sql_types",
                    "id": 12
                }
            ]
        },
        {
            "name": "CommonAction",
            "fields": [
                {
                    "rule": "optional",
                    "type": "UIYaormRequest",
                    "name": "request",
                    "id": 1
                },
                {
                    "rule": "optional",
                    "type": "UIYaormResponse",
                    "name": "response",
                    "id": 2
                }
            ]
        },
        {
            "name": "YaormMainController",
            "fields": [
                {
                    "rule": "optional",
                    "type": "CommonAction",
                    "name": "get_schemas",
                    "id": 1
                },
                {
                    "rule": "optional",
                    "type": "CommonAction",
                    "name": "get_tables",
                    "id": 2
                },
                {
                    "rule": "optional",
                    "type": "CommonAction",
                    "name": "get_table_definition",
                    "id": 3
                },
                {
                    "rule": "optional",
                    "type": "CommonAction",
                    "name": "get_table_definitions",
                    "id": 4
                },
                {
                    "rule": "optional",
                    "type": "CommonAction",
                    "name": "get_record_count",
                    "id": 5
                },
                {
                    "rule": "optional",
                    "type": "CommonAction",
                    "name": "get_records",
                    "id": 6
                }
            ]
        }
    ],
    "enums": [
        {
            "name": "ProtobufType",
            "values": [
                {
                    "name": "NONE",
                    "id": 0
                },
                {
                    "name": "DOUBLE",
                    "id": 1
                },
                {
                    "name": "FLOAT",
                    "id": 2
                },
                {
                    "name": "INT32",
                    "id": 3
                },
                {
                    "name": "INT64",
                    "id": 4
                },
                {
                    "name": "UINT32",
                    "id": 5
                },
                {
                    "name": "UINT64",
                    "id": 6
                },
                {
                    "name": "SINT32",
                    "id": 7
                },
                {
                    "name": "SINT64",
                    "id": 8
                },
                {
                    "name": "FIXED32",
                    "id": 9
                },
                {
                    "name": "FIXED64",
                    "id": 10
                },
                {
                    "name": "SFIXED32",
                    "id": 11
                },
                {
                    "name": "SFIXED64",
                    "id": 12
                },
                {
                    "name": "BOOL",
                    "id": 13
                },
                {
                    "name": "STRING",
                    "id": 14
                },
                {
                    "name": "BYTES",
                    "id": 15
                },
                {
                    "name": "PROTO",
                    "id": 16
                }
            ]
        }
    ]
}).build();
exports.YaormModel = _root;
//# sourceMappingURL=YaormModelFactory.js.map