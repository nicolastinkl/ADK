using UnityEngine;

public static class VungleLog
{
	public enum Level
	{
		None = 0,
		Debug = 10000,
		Info,
		Warning,
		Error,
	}

	public static class Context
	{
		public const string SDKInitialization = "SDK Initialization";
		public const string AdLifecycle = "Ad Lifecycle";
		public const string ConsentStatus = "Consent Status";
		public const string LogEvent = "Log Event";
	}

	public static void Log(Level logLevel, string logContext, string callerMethod, string logMessage)
	{
		if (Vungle.logLevel >= logLevel || logLevel == Level.None)
		{
			return;
		}

		var finalLogMessage = string.Format("[Vungle Unity Plugin][{0}]: [{1}] {2}", logContext, callerMethod, logMessage);
		switch(Vungle.logLevel)
		{
			case Level.Error:
				Debug.LogError(finalLogMessage);
				break;
			case Level.Warning:
				Debug.LogWarning(finalLogMessage);
				break;
			case Level.Debug:
			case Level.Info:
			case Level.None:
			default:
				Debug.Log(finalLogMessage);
				break;
		}
	}
}
