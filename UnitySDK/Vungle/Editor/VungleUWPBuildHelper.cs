using UnityEditor;
using UnityEngine;

class VungleBuildHelper
{
	// Sets the correct import settings for the Windows plugins
	[UnityEditor.MenuItem("Tools/Vungle/Prepare Windows Plugins")]
	static void PrepareWin10()
	{
		PluginImporter pi;
		pi = (PluginImporter)PluginImporter.GetAtPath("Assets/Vungle/Plugins/metro/VungleSDKProxy.dll");
		pi.SetCompatibleWithAnyPlatform(false);
		pi.SetCompatibleWithEditor(true);
		pi.SaveAndReimport();
		pi = (PluginImporter)PluginImporter.GetAtPath("Assets/Vungle/Plugins/metro/VungleSDKProxy.winmd");
		pi.SetPlatformData(BuildTarget.WSAPlayer, "PlaceholderPath", "Assets/Vungle/Plugins/metro/VungleSDKProxy.dll");
		pi.SaveAndReimport();
		pi = (PluginImporter)PluginImporter.GetAtPath("Assets/Vungle/Plugins/metro/VungleSDK.winmd");
		pi.SetPlatformData(BuildTarget.WSAPlayer, "SDK", "UWP");
		pi.SetCompatibleWithPlatform(BuildTarget.WSAPlayer, true);
		pi.SaveAndReimport();
	}
}
