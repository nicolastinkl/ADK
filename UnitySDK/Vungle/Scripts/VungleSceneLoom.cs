using UnityEngine;
using System.Collections.Generic;
using System;
using System.Runtime.InteropServices;

public class VungleSceneLoom : MonoBehaviour
{
	public interface ILoom
	{
		void QueueOnMainThread(Action action);
	}

	private static NullLoom nullLoom = new NullLoom();
	private static LoomDispatcher loom;
	private static VungleSceneLoom instance;
	private static bool initialized = false;

#if ENABLE_WINMD_SUPPORT
	[DllImport("__Internal")]
    extern static int GetPageContent([MarshalAs(UnmanagedType.IInspectable)]object frame, [MarshalAs(UnmanagedType.IInspectable)]out object pageContent);
#endif

	public static ILoom Loom
	{
		get
		{
			if (loom != null)
			{
				return loom as ILoom;
			}
			return nullLoom as ILoom;
		}
	}

	void Awake()
	{
		if (instance != null && instance != this)
		{
			Destroy(gameObject);
			return;
		}
		instance = this;
		DontDestroyOnLoad(gameObject);
		loom = new LoomDispatcher();
	}

	public static void Initialize()
	{
		if (!initialized)
		{
			var g = new GameObject("VungleSceneLoom");
			instance = g.AddComponent<VungleSceneLoom>();
			initialized = true;
		}
	}

	void OnDestroy()
	{
		loom = null;
		initialized = false;
	}

	void Update()
	{
		if (Application.isPlaying)
		{
			loom.Update();
		}
	}

	public static VungleSceneLoom Instance
	{
		get
		{
			if (!initialized)
			{
				Initialize();
			}
			return instance;
		}
	}

	public void GetSwapChainPanel(Action<object> callback)
	{
		if (callback == null) { return; }

#if ENABLE_WINMD_SUPPORT
		UnityEngine.WSA.Application.InvokeOnUIThread(() =>
		{
			try
			{
				var content = Windows.UI.Xaml.Window.Current.Content as Windows.UI.Xaml.Controls.Frame;
				var page = content.Content as Windows.UI.Xaml.Controls.Page;
				Windows.UI.Xaml.Controls.Grid dxSwapChainPanel;
				if (page != null)
				{
					// Unity 2017 uses .NET and does not need to use C++ to get the swap chain panel
					dxSwapChainPanel = page.Content as Windows.UI.Xaml.Controls.Grid;
				}
				else
				{
					// The class gets scrambled in 2018+ so need to use the C++ script to get the panel
					object pageContent;
					var result = GetPageContent(Windows.UI.Xaml.Window.Current.Content, out pageContent);
					if (result < 0)
					{
						Marshal.ThrowExceptionForHR(result);
					}

					dxSwapChainPanel = pageContent as Windows.UI.Xaml.Controls.SwapChainPanel;
				}
				callback(dxSwapChainPanel);
			}
			catch (Exception e)
			{
				VungleLog.Log(VungleLog.Level.Error, "GetSwapChainPanel", "VungleSceneLoom.GetSwapChainPanel", e.ToString());
				callback(null);
			}
		}, false);
#endif
	}

	private class NullLoom : ILoom
	{
		public void QueueOnMainThread(Action action) { }
	}

	private class LoomDispatcher : ILoom
	{
		private readonly List<Action> actions = new List<Action>();

		public void QueueOnMainThread(Action action)
		{
			lock (actions)
			{
				actions.Add(action);
			}
		}

		public void Update()
		{
			// Pop the actions from the synchronized list
			Action[] actionsToRun = null;
			lock (actions)
			{
				actionsToRun = actions.ToArray();
				actions.Clear();
			}

			// Run each action
			foreach (Action action in actionsToRun)
			{
				action();
			}
		}
	}
}
