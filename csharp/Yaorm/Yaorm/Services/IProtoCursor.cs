using System;
using Org.Roylance.Yaorm.Models;

namespace Yaorm
{
	public interface IProtoCursor
	{
		Records GetRecords();
		void GetRecordsStreamer(IProtoStreamer streamer);
	}
}

