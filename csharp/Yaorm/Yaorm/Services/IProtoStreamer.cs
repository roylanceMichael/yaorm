using System;
using Org.Roylance.Yaorm.Models;

namespace Yaorm
{
	public interface IProtoStreamer
	{
		void Stream(Record record);
	}
}

