package org.roylance.yaorm

import org.junit.Test
import java.util.*

class ModelClassBuilderTests {
    @Test
    fun simpleRunThrough() {
        // arrange
        val allTexts = HashMap<String, String>()
        allTexts["yaas_model.proto"] = YaasModelFile

        // act
        val actualKotlinFile = ModelClassBuilder.build("org.roylance.yaorm", "test", allTexts)

        // assert
        assert(actualKotlinFile.contains("YaasModel.UIYaasResponse"))
        assert(actualKotlinFile.contains("YaasModel.UIAuthentications"))
    }

    @Test
    fun simpleRunThroughSeveralFiles() {
        // arrange
        val allTexts = HashMap<String, String>()
        allTexts["yaas_model.proto"] = YaasModelFile
        allTexts["yadel_model.proto"] = YadelModelFile

        // act
        val actualKotlinFile = ModelClassBuilder.build("org.roylance.yaorm", "test", allTexts)

        // assert
        assert(actualKotlinFile.contains("YadelModel.WorkerConfiguration"))
        assert(actualKotlinFile.contains("YadelModel.Log"))
        assert(actualKotlinFile.contains("YadelModel.Log"))
        assert(actualKotlinFile.contains("YaasModel.UIYaasResponse"))
        assert(actualKotlinFile.contains("YaasModel.UIAuthentications"))
    }

    companion object {

        private const val YadelModelFile ="""syntax = "proto3";
package org.roylance.yadel;

message WorkerConfiguration {
    string id = 1;
    string ip = 2;
    string port = 3;
    string host = 4;
    string initialized_time = 5;
    WorkerState state = 6;
    Task task = 7;
    Dag dag = 8;
    uint64 minutes_before_task_reset = 9;
    string task_start_time = 10;
}

enum WorkerState {
    WORKING = 0;
    IDLE = 1;
}

enum WorkerToManagerMessageType {
    REGISTRATION = 0;
}

enum ManagerToManagerMessageType {
    ENSURE_WORKERS_WORKING = 0;
}

enum ActorRole {
    MANAGER = 0;
    WORKER = 1;
}

message Log {
    string id = 1;
    string message = 2;
}

message Dag {
    string id = 1;
    string display = 2;
    repeated Task flattened_tasks = 3;
    int64 execution_date = 4;
    int64 start_date = 5;
    int64 end_date = 6;
    int64 duration = 7;
    repeated Task uncompleted_tasks = 8;
    repeated Task processing_tasks = 9;
    repeated Task errored_tasks = 10;
    repeated Task completed_tasks = 11;
    Dag parent = 12;
}

message Task {
    string id = 1;
    string display = 2;
    repeated TaskDependency dependencies = 3;
    string dag_id = 4;
    repeated Log logs = 5;
    int64 execution_date = 6;
    int64 start_date = 7;
    int64 end_date = 8;
    int64 duration = 9;
    string first_context_base_64 = 10;
    string second_context_base_64 = 11;
    string third_context_base_64 = 12;
    bool is_waiting_for_another_dag_task = 13;
}

message TaskDependency {
    string id = 1;
    string parent_task_id = 2;
}

message AddTaskToDag {
    string id = 1;
    Task parent_task = 2;
    Task new_task = 3;
    string first_context_base_64 = 4;
    string second_context_base_64 = 5;
    string third_context_base_64 = 6;
}

message CompleteTask {
    string id = 1;
    Task task = 2;
    WorkerConfiguration worker_configuration = 3;
    bool is_error = 5;
}

message AllDags {
    repeated Dag dags = 1;
    repeated WorkerConfiguration workers = 2;
    bool include_unprocessed = 3;
    bool include_file_saved = 4;
}
"""

        private const val YaasModelFile = """syntax = "proto3";
package org.roylance.yaas;

enum UserDeviceType {
    ANDROID = 0;
    IOS = 1;
    JAVASCRIPT = 2;
}

enum UserRole {
    NORMAL = 0;
    ADMIN = 1;
}

message UserDevice {
    string id = 1;
    UserDeviceType user_device_type = 2;
    string user_device_token = 3;
    int64 last_updated = 4;
    User user = 5;
}

message Image {
    string id = 1;
    string name = 2;
    string actual_image = 3;
}

message User {
    string id = 1;
    string user_name = 2;
    string password = 3;
    string display = 4;
    string first_name = 5;
    string last_name = 6;
    Image image = 7;
    repeated UserRole roles = 8;
}

message Token {
    string id = 1;
    string user_id = 2;
    int64 issued = 3;
    int64 expiration = 4;
}

message UIAuthentication {
    bool authenticated = 1;
    string token = 2;
    string user_name = 3;
    string display = 4;
    bool is_admin = 5;
}

message UIChangePassword {
    string user_name = 1;
    string old_password = 2;
    string new_password = 3;
}

message UIYaasRequest {
    string token = 1;
    string content = 2;
    User user = 3;
    UserDevice user_device = 4;
    Image image = 5;
    int32 offset = 6;
    int32 limit = 7;
    UIChangePassword change_password = 8;
}

message UIYaasResponse {
    bool authenticated = 1;
    string error_message = 2;
    bool successful = 3;
    string content = 4;
    bool is_admin = 5;
    UIAuthentication user = 6;
    UIAuthentications users = 7;
    repeated UserDevice user_devices = 8;
}

message UIAuthentications {
    repeated UIAuthentication users = 1;
}
"""
    }
}