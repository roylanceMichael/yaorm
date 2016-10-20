// THIS FILE WAS AUTO-GENERATED. DO NOT ALTER!
using System.Threading.Tasks;

namespace Org.Roylance.Yaorm.Api
{
    public interface IYaormMainService
    {
		Task<Org.Roylance.Yaorm.UIYaormResponse> get_schemas(Org.Roylance.Yaorm.UIYaormRequest request);
		Task<Org.Roylance.Yaorm.UIYaormResponse> get_tables(Org.Roylance.Yaorm.UIYaormRequest request);
		Task<Org.Roylance.Yaorm.UIYaormResponse> get_table_definition(Org.Roylance.Yaorm.UIYaormRequest request);
		Task<Org.Roylance.Yaorm.UIYaormResponse> get_table_definitions(Org.Roylance.Yaorm.UIYaormRequest request);
		Task<Org.Roylance.Yaorm.UIYaormResponse> get_record_count(Org.Roylance.Yaorm.UIYaormRequest request);
		Task<Org.Roylance.Yaorm.UIYaormResponse> get_records(Org.Roylance.Yaorm.UIYaormRequest request);
	}
}
