// THIS FILE WAS AUTO-GENERATED. DO NOT ALTER!
package org.roylance.yaorm.services;

public class YaormMainJNIBridge {
	private native byte[] get_schemasJNI(byte[] request);
	private native byte[] get_tablesJNI(byte[] request);
	private native byte[] get_table_definitionJNI(byte[] request);
	private native byte[] get_table_definitionsJNI(byte[] request);
	private native byte[] get_record_countJNI(byte[] request);
	private native byte[] get_recordsJNI(byte[] request);
	public byte[] get_schemas(byte[] request) {
		return get_schemasJNI(request);
	}
	public byte[] get_tables(byte[] request) {
		return get_tablesJNI(request);
	}
	public byte[] get_table_definition(byte[] request) {
		return get_table_definitionJNI(request);
	}
	public byte[] get_table_definitions(byte[] request) {
		return get_table_definitionsJNI(request);
	}
	public byte[] get_record_count(byte[] request) {
		return get_record_countJNI(request);
	}
	public byte[] get_records(byte[] request) {
		return get_recordsJNI(request);
	}
}
