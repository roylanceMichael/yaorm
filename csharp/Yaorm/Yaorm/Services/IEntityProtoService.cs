using System;
using Org.Roylance.Yaorm.Models;

namespace Yaorm
{
	public interface IEntityProtoService
	{
		Index Index { get; }

		bool CreateTable(TableDefinition tableDefinition);
		bool DropTable(TableDefinition tableDefinition);

		bool CreateIndex(TableDefinition tableDefinition, Index index);
		bool DropIndex(TableDefinition tableDefinition, Index index);

		bool CreateColumn(TableDefinition tableDefinition, ColumnDefinition columnDefinition);
		bool DropColumn(TableDefinition tableDefinition, ColumnDefinition columnDefinition);

		bool BulkInsert(Records records, TableDefinition tableDefinition);
		bool CreateOrUpdate(Record record, TableDefinition tableDefinition);
		bool Create(Record record, TableDefinition tableDefinition);
		bool Update(Record record, TableDefinition tableDefinition);

		bool UpdateWithCriteria(Record record, TableDefinition tableDefinition, WhereClause whereClause);
		bool UpdateCustom(string customerSql);
		bool Delete(string id, TableDefinition tableDefinition);
		bool DeleteAll(TableDefinition tableDefinition);

		long GetCount(TableDefinition tableDefinition);

		void GetManyStream(int n, TableDefinition tableDefinition, IProtoStreamer streamer);

		Record Get(string id, TableDefinition tableDefinition);
		Records GetCustom(string customSql, TableDefinition tableDefinition);
		Records GetMany(int n, TableDefinition tableDefinition);
		Records Where(WhereClause whereClause, TableDefinition tableDefinition);
	}
}

