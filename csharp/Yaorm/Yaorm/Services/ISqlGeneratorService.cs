using System;
using System.Collections.Generic;
using Org.Roylance.Yaorm.Models;

namespace Yaorm
{
	public interface ISqlGeneratorService
	{
		string IdName { get; }
		IDictionary<ProtobufType, String> ProtoTypeToSqlType { get; }
		int BulkInsertSize { get; }

		string BuildCountSQL(TableDefinition definition);

		string BuildCreateColumn(TableDefinition definition, ColumnDefinition propertyDefinition);
		string BuildDropColumn(TableDefinition definition, ColumnDefinition columnDefinition);

		string BuildCreateIndex(TableDefinition definition,
								IDictionary<string, ColumnDefinition> properties,
								IDictionary<string, ColumnDefinition> includes);
		string BuildDropIndex(TableDefinition definition,
							  IDictionary<string, ColumnDefinition> columns);

		string BuildDropTable(TableDefinition definition);
		string BuildCreateTable(TableDefinition definition);

		string BuildDeleteAll(TableDefinition definition);
		string BuildDeleteTable(TableDefinition definition);
		string BuildDeleteWithCriteria(TableDefinition definition, WhereClause whereClause);

		string BuildBulkInsert(TableDefinition definition, Records records);
		string BuildInsertIntoTable(TableDefinition definition, Record record);

		string BuildUpdateTable(TableDefinition definition, Record record);
		string BuildWithCriteria(TableDefinition definition, Record record);

		string BuildSelectAll(TableDefinition definition, int n);
		string BuildWhereClause(TableDefinition definition, WhereClause whereClause);
	}
}

