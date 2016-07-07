using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Org.Roylance.Yaorm.Models;

namespace Yaorm
{
	public class SQLiteGeneratorService: ISqlGeneratorService
	{
		const string CreateInitialTableTemplate = "create table if not exists %s (%s);";
		const string InsertIntoTableSingleTemplate = "insert into %s (%s) values (%s);";
		const string UpdateTableSingleTemplate = "update %s set %s where id=%s;";
		const string DeleteTableTemplate = "delete from %s where id=%s;";
		const string WhereClauseTemplate = "select * from %s where %s;";
		const string SelectAllTemplate = "select * from %s limit %s;";
		const string PrimaryKey = "primary key";

		const string SqlIntegerName = "integer";
		const string SqlTextName = "text";
		const string SqlRealName = "real";
		const string SqlBlobName = "text";

		readonly int bulkInsertSize;
		readonly IDictionary<ProtobufType, string> protoTypeToSqlType = new Dictionary<ProtobufType, string>
		{
			{ProtobufType.STRING, SqlTextName},
			{ProtobufType.INT32, SqlIntegerName},
			{ProtobufType.INT64, SqlIntegerName},
			{ProtobufType.UINT32, SqlIntegerName},
			{ProtobufType.UINT64, SqlIntegerName},
			{ProtobufType.SINT32, SqlIntegerName},
			{ProtobufType.SINT64, SqlIntegerName},
			{ProtobufType.FIXED32, SqlIntegerName},
			{ProtobufType.FIXED64, SqlIntegerName},
			{ProtobufType.SFIXED32, SqlIntegerName},
			{ProtobufType.SFIXED64, SqlIntegerName},
			{ProtobufType.BOOL, SqlIntegerName},
			{ProtobufType.BYTES, SqlBlobName},
			{ProtobufType.DOUBLE, SqlRealName},
			{ProtobufType.FLOAT, SqlRealName}
		};

		public SQLiteGeneratorService(int bulkInsertSize)
		{
			
			this.bulkInsertSize = bulkInsertSize;
		}

		public int BulkInsertSize { get { return this.bulkInsertSize; } }
		public string IdName { get { return CommonUtils.IdName; } }

		public IDictionary<ProtobufType, string> ProtoTypeToSqlType
		{
			get
			{
				return this.protoTypeToSqlType;
			}
		}

		public string BuildBulkInsert(TableDefinition definition, Records records)
		{
			var tableName = definition.Name;
			var columnNames = definition.ColumnDefinitions.Values.Select(item => item.Name).OrderBy(item => item);
			var commaSeparatedName = string.Join(",", columnNames);

			var workspace = new StringBuilder();
			workspace.AppendLine("insert into " + tableName + "(" + commaSeparatedName + ") ");

			var selectStatements = new List<string>();
			foreach(var record in records.Records_)
			{
				var valueColumnPairs = new StringBuilder();
				foreach (var column in record.Columns.Values.OrderBy(item => item.Definition.Name))
				{
					var formattedValue = column.GetFormattedString();
					if (valueColumnPairs.Length == 0)
					{
						valueColumnPairs.Append("select " + formattedValue + " as " + column.Definition.Name);
					}
					else {
						valueColumnPairs.Append(", " + formattedValue + " as " + column.Definition.Name);

					}
				}
				selectStatements.Add(valueColumnPairs.ToString());
			}

			workspace.Append(string.Join(CommonUtils.SpacedUnion, selectStatements));
			workspace.Append(CommonUtils.SemiColon);
			return workspace.ToString();
		}

		public string BuildCountSQL(TableDefinition definition)
		{
			return "select count(1) as longVal from " + definition.Name;
		}

		public string BuildCreateColumn(TableDefinition definition, ColumnDefinition propertyDefinition)
		{
			throw new NotImplementedException();
		}

		public string BuildCreateIndex(TableDefinition definition, IDictionary<string, ColumnDefinition> properties, IDictionary<string, ColumnDefinition> includes)
		{
			throw new NotImplementedException();
		}

		public string BuildCreateTable(TableDefinition definition)
		{
			throw new NotImplementedException();
		}

		public string BuildDeleteAll(TableDefinition definition)
		{
			throw new NotImplementedException();
		}

		public string BuildDeleteTable(TableDefinition definition)
		{
			throw new NotImplementedException();
		}

		public string BuildDeleteWithCriteria(TableDefinition definition, WhereClause whereClause)
		{
			throw new NotImplementedException();
		}

		public string BuildDropColumn(TableDefinition definition, ColumnDefinition columnDefinition)
		{
			throw new NotImplementedException();
		}

		public string BuildDropIndex(TableDefinition definition, IDictionary<string, ColumnDefinition> columns)
		{
			throw new NotImplementedException();
		}

		public string BuildDropTable(TableDefinition definition)
		{
			throw new NotImplementedException();
		}

		public string BuildInsertIntoTable(TableDefinition definition, Record record)
		{
			throw new NotImplementedException();
		}

		public string BuildSelectAll(TableDefinition definition, int n)
		{
			throw new NotImplementedException();
		}

		public string BuildUpdateTable(TableDefinition definition, Record record)
		{
			throw new NotImplementedException();
		}

		public string BuildWhereClause(TableDefinition definition, WhereClause whereClause)
		{
			throw new NotImplementedException();
		}

		public string BuildWithCriteria(TableDefinition definition, Record record)
		{
			throw new NotImplementedException();
		}
	}
}

