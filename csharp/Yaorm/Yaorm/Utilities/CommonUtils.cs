using System.Collections.Generic;
using System.Linq;
using Org.Roylance.Yaorm.Models;

namespace Yaorm
{
	public static class CommonUtils
	{
		public const string SingleQuote = "'";
		public const string DoubleSingleQuote = "''";
		public const string IdName = "Id";
		public const string Null = "null";
		public const string Space = " ";
		public const string Comma = ",";
		public const string EqualsName = "=";
		public const string SemiColon = ";";
		public const string CarriageReturn = "\n";
		public const string SpacedUnion = CarriageReturn + "union";
		public const string SpacedAnd = " and ";
		public const string And = "and";
		public const string Or = "or";
		public const string Underscore = "_";
		public const string Is = "is";
		public const string LeftParen = "(";
		public const string RightParen = ")";

		public static Column BuildColumn(this ColumnDefinition columnDefintion, object value)
		{
			var returnColumn = new Column();

			returnColumn.Definition = columnDefintion;

			var notNullValueAsString = value == null ? string.Empty : value.ToString();

			switch (columnDefintion.Type)
			{
				case ProtobufType.STRING:
					returnColumn.StringHolder = notNullValueAsString;
					break;
				case ProtobufType.BOOL:
					returnColumn.BoolHolder = notNullValueAsString == "1" || bool.TrueString.Equals(notNullValueAsString) ? true : false;
					break;
				case ProtobufType.INT32:
					returnColumn.Int32Holder = int.Parse(notNullValueAsString);
					break;
				case ProtobufType.FIXED32:
					returnColumn.Fixed32Holder = uint.Parse(notNullValueAsString);
					break;
				case ProtobufType.SFIXED32:
					returnColumn.Sfixed32Holder = int.Parse(notNullValueAsString);
					break;
				case ProtobufType.UINT32:
					returnColumn.Uint32Holder = uint.Parse(notNullValueAsString);
					break;
				case ProtobufType.SINT32:
					returnColumn.Sint32Holder = int.Parse(notNullValueAsString);
					break;
				case ProtobufType.INT64:
					returnColumn.Int64Holder = long.Parse(notNullValueAsString);
					break;
				case ProtobufType.FIXED64:
					returnColumn.Fixed64Holder = ulong.Parse(notNullValueAsString);
					break;
				case ProtobufType.SFIXED64:
					returnColumn.Sfixed64Holder = long.Parse(notNullValueAsString);
					break;
				case ProtobufType.DOUBLE:
					returnColumn.DoubleHolder = double.Parse(notNullValueAsString);
					break;
				case ProtobufType.FLOAT:
					returnColumn.FloatHolder = float.Parse(notNullValueAsString);
					break;
				case ProtobufType.BYTES:
					if (value is Google.Protobuf.ByteString)
					{
						returnColumn.BytesHolder = value as Google.Protobuf.ByteString;
					}
					else if (value is string)
					{
						returnColumn.BytesHolder = Google.Protobuf.ByteString.CopyFromUtf8(notNullValueAsString);
					}
					else
					{
						returnColumn.BytesHolder = Google.Protobuf.ByteString.Empty;
					}
					break;
			}

			return returnColumn;
		}

		public static object GetAnyObject(this Column column)
		{
			switch (column.Definition.Type)
			{
				case ProtobufType.STRING:
					return column.StringHolder;
				case ProtobufType.BOOL:
					return column.BoolHolder;
				case ProtobufType.INT32:
					return column.Int32Holder;
				case ProtobufType.FIXED32:
					return column.Fixed32Holder;
				case ProtobufType.SFIXED32:
					return column.Sfixed32Holder;
				case ProtobufType.UINT32:
					return column.Uint32Holder;
				case ProtobufType.SINT32:
					return column.Sint32Holder;
				case ProtobufType.INT64:
					return column.Int64Holder;
				case ProtobufType.FIXED64:
					return column.Fixed64Holder;
				case ProtobufType.SFIXED64:
					return column.Sfixed64Holder;
				case ProtobufType.DOUBLE:
					return column.DoubleHolder;
				case ProtobufType.FLOAT:
					return column.FloatHolder;
				case ProtobufType.BYTES:
					return column.BytesHolder;
			}
			return null;
		}

		public static string GetFormattedString(this Column column)
		{
			if (column.Definition == null)
			{
				return Null;
			}
			switch (column.Definition.Type)
			{
				case ProtobufType.STRING:
					return SingleQuote + column.StringHolder.Replace(SingleQuote, DoubleSingleQuote) + SingleQuote;
				case ProtobufType.BOOL:
					return column.BoolHolder ? "1" : "0";
				case ProtobufType.INT32:
					return column.Int32Holder.ToString();
				case ProtobufType.FIXED32:
					return column.Fixed32Holder.ToString();
				case ProtobufType.SFIXED32:
					return column.Sfixed32Holder.ToString();
				case ProtobufType.UINT32:
					return column.Uint32Holder.ToString();
				case ProtobufType.SINT32:
					return column.Sint32Holder.ToString();
				case ProtobufType.INT64:
					return column.Int64Holder.ToString();
				case ProtobufType.FIXED64:
					return column.Fixed64Holder.ToString();
				case ProtobufType.SFIXED64:
					return column.Sfixed64Holder.ToString();
				case ProtobufType.DOUBLE:
					return column.DoubleHolder.ToString();
				case ProtobufType.FLOAT:
					return column.FloatHolder.ToString();
				case ProtobufType.BYTES:
					return SingleQuote + column.BytesHolder.ToStringUtf8() + SingleQuote;
			}
			return Null;
		}

		public static string GetIndexName(this IEnumerable<string> names)
		{
			var sortedNames = names.ToList().OrderBy(p => p);
			return string.Join("_", sortedNames) + "idx";
		}
	}
}

