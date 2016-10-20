// THIS FILE WAS AUTO-GENERATED. DO NOT ALTER!
package org.roylance.yaorm.services;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface IYaormMainRest {

    @POST("/rest/yaormmain/get-schemas")
    Call<String> get_schemas(@Body String request);

    @POST("/rest/yaormmain/get-tables")
    Call<String> get_tables(@Body String request);

    @POST("/rest/yaormmain/get-table-definition")
    Call<String> get_table_definition(@Body String request);

    @POST("/rest/yaormmain/get-table-definitions")
    Call<String> get_table_definitions(@Body String request);

    @POST("/rest/yaormmain/get-record-count")
    Call<String> get_record_count(@Body String request);

    @POST("/rest/yaormmain/get-records")
    Call<String> get_records(@Body String request);
}