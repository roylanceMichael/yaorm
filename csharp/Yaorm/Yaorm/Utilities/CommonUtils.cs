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
				case ProtobufType.String:
					returnColumn.StringHolder = notNullValueAsString;
					break;
				case ProtobufType.Bool:
					returnColumn.BoolHolder = notNullValueAsString == "1" || bool.TrueString.Equals(notNullValueAsString) ? true : false;
					break;
				case ProtobufType.Int32:
					returnColumn.Int32Holder = int.Parse(notNullValueAsString);
					break;
				case ProtobufType.Fixed32:
					returnColumn.Fixed32Holder = uint.Parse(notNullValueAsString);
					break;
				case ProtobufType.Sfixed32:
					returnColumn.Sfixed32Holder = int.Parse(notNullValueAsString);
					break;
				case ProtobufType.Uint32:
					returnColumn.Uint32Holder = uint.Parse(notNullValueAsString);
					break;
				case ProtobufType.Sint32:
					returnColumn.Sint32Holder = int.Parse(notNullValueAsString);
					break;
				case ProtobufType.Int64:
					returnColumn.Int64Holder = long.Parse(notNullValueAsString);
					break;
				case ProtobufType.Fixed64:
					returnColumn.Fixed64Holder = ulong.Parse(notNullValueAsString);
					break;
				case ProtobufType.Sfixed64:
					returnColumn.Sfixed64Holder = long.Parse(notNullValueAsString);
					break;
				case ProtobufType.Double:
					returnColumn.DoubleHolder = double.Parse(notNullValueAsString);
					break;
				case ProtobufType.Float:
					returnColumn.FloatHolder = float.Parse(notNullValueAsString);
					break;
				case ProtobufType.Bytes:
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
				case ProtobufType.String:
					return column.StringHolder;
				case ProtobufType.Bool:
					return column.BoolHolder;
				case ProtobufType.Int32:
					return column.Int32Holder;
				case ProtobufType.Fixed32:
					return column.Fixed32Holder;
				case ProtobufType.Sfixed32:
					return column.Sfixed32Holder;
				case ProtobufType.Uint32:
					return column.Uint32Holder;
				case ProtobufType.Sint32:
					return column.Sint32Holder;
				case ProtobufType.Int64:
					return column.Int64Holder;
				case ProtobufType.Fixed64:
					return column.Fixed64Holder;
				case ProtobufType.Sfixed64:
					return column.Sfixed64Holder;
				case ProtobufType.Double:
					return column.DoubleHolder;
				case ProtobufType.Float:
					return column.FloatHolder;
				case ProtobufType.Bytes:
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
				case ProtobufType.String:
					return SingleQuote + column.StringHolder.Replace(SingleQuote, DoubleSingleQuote) + SingleQuote;
				case ProtobufType.Bool:
					return column.BoolHolder ? "1" : "0";
				case ProtobufType.Int32:
					return column.Int32Holder.ToString();
				case ProtobufType.Fixed32:
					return column.Fixed32Holder.ToString();
				case ProtobufType.Sfixed32:
					return column.Sfixed32Holder.ToString();
				case ProtobufType.Uint32:
					return column.Uint32Holder.ToString();
				case ProtobufType.Sint32:
					return column.Sint32Holder.ToString();
				case ProtobufType.Int64:
					return column.Int64Holder.ToString();
				case ProtobufType.Fixed64:
					return column.Fixed64Holder.ToString();
				case ProtobufType.Sfixed64:
					return column.Sfixed64Holder.ToString();
				case ProtobufType.Double:
					return column.DoubleHolder.ToString();
				case ProtobufType.Float:
					return column.FloatHolder.ToString();
				case ProtobufType.Bytes:
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

