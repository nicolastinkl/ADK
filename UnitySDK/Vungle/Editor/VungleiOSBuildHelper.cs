using UnityEngine;
using UnityEditor;
using UnityEditor.Callbacks;
using System.Collections.Generic;

public class VungleiOSPostBuilder
{
	// Message to show the user to get permission for IDFA (targeted ads)
	private const string NSUserTrackingUsageDescription = "To ensure the best possible ad experience";

#if UNITY_IOS
	// XCode project constants
	private static string[] SKAdNetworks = new string[]
	{
		// Updated 03/18/2021
		"gta9lk7p23.skadnetwork", // <!--  Vungle  -->
		"p78axxw29g.skadnetwork", // <!--  A9 Inc  -->
		"4fzdc2evr5.skadnetwork", // <!--  Aarki  -->
		"4pfyvq9l8r.skadnetwork", // <!--  AdColony  -->
		"ydx93a7ass.skadnetwork", // <!--  Adikteev  -->
		"cstr6suwn9.skadnetwork", // <!--  AdMob  -->
		"v72qych5uu.skadnetwork", // <!--  Appier  -->
		"mlmmfzh3r3.skadnetwork", // <!--  Appreciate/Pubnative  -->
		"c6k4g5qg8m.skadnetwork", // <!--  Beeswax/Pubnative  -->
		"wg4vff78zm.skadnetwork", // <!--  Bidmachine -->
		"mls7yz5dvl.skadnetwork", // <!--  Bucksense Inc  -->
		"22mmun2rn5.skadnetwork", // <!--  Bytedance  -->
		"3sh42y64q3.skadnetwork", // <!--  Centro Inc.  -->
		"f38h382jlk.skadnetwork", // <!--  Chartboost Inc.  -->
		"hs6bdukanm.skadnetwork", // <!--  Criteo SA  -->
		"prcb7njmu6.skadnetwork", // <!--  CrossInstall Inc.  -->
		"m8dbw4sv7c.skadnetwork", // <!--  Dataset  -->
		"9nlqeag3gk.skadnetwork", // <!--  Edge 226  -->
		"cj5566h2ga.skadnetwork", // <!--  EngageBDR  -->
		"wzmmz9fp6w.skadnetwork", // <!--  InMobi Pte. Ltd  -->
		"yclnxrl5pm.skadnetwork", // <!--  Jampp/Pubnative  -->
		"t38b2kh725.skadnetwork", // <!--  Lifestreet -->
		"7ug5zh24hu.skadnetwork", // <!--  Liftoff -->
		"5lm9lj6jb7.skadnetwork", // <!--  LoopMe  -->
		"mtkv5xtk9e.skadnetwork", // <!--  Maxpoint Interactive Inc -->
		"9t245vhmpl.skadnetwork", // <!--  Moloco -->
		"7953jerfzd.skadnetwork", // <!--  Mopub  -->
		"n9x2a789qt.skadnetwork", // <!--  MyTarget  -->
		"44jx6755aq.skadnetwork", // <!--  Personaly  -->
		"tl55sbb4fm.skadnetwork", // <!--  Pubnative  -->
		"4468km3ulz.skadnetwork", // <!--  Realtime Technologies GmbH  -->
		"2u9pt9hc89.skadnetwork", // <!--  Remerge  -->
		"8s468mfl3y.skadnetwork", // <!--  RTB House  -->
		"glqzh8vgby.skadnetwork", // <!--  Sabio  -->
		"ppxm28t8ap.skadnetwork", // <!--  Smadex S.L.  -->
		"424m5254lk.skadnetwork", // <!--  Snap Inc.  -->
		"uw77j35x4d.skadnetwork", // <!--  Trade Desk Inc. -->
		"9yg77x724h.skadnetwork", // <!--  Twitter -->
		"n66cz3y3bx.skadnetwork", // <!--  Twitter -->
		"prcb7njmu6.skadnetwork", // <!--  Twitter -->
		"5tjdwbrq8w.skadnetwork", // <!--  Webeye -->
		"3rd42ekr43.skadnetwork", // <!--  YouAppi  -->
	};

	private static Dictionary<string, bool> XCodeFramework = new Dictionary<string, bool>()
	{
		{ "AdSupport.framework", false },
		{ "CoreTelephony.framework", false },
		{ "StoreKit.framework", false },
		{ "WebKit.framework", false },
		// For iOS 14. Weak link the framework for XCode 11
		{ "AppTrackingTransparency.framework", true },
	};

	private static string[] XCodeFiles = new string[]
	{
		"libsqlite3.dylib",
		"libz.1.1.3.dylib"
	};
#endif

	private static string PostBuildDirectoryKey { get { return "VunglePostBuildPath-" + PlayerSettings.productName; } }
	private static string PostBuildDirectory
	{
		get
		{
			return EditorPrefs.GetString(PostBuildDirectoryKey);
		}
		set
		{
			EditorPrefs.SetString(PostBuildDirectoryKey, value);
		}
	}

	[PostProcessBuild(800)]
	private static void OnPostProcessBuildPlayer(BuildTarget target, string pathToBuiltProject)
	{
		switch (target)
		{
			case BuildTarget.iOS:
#if !UNITY_IOS
				Debug.Log("VunglePostBuilder: The build process was started when the active target is not iOS. You may need to run the post-processor manually.");
				EditorUserBuildSettings.SwitchActiveBuildTarget(BuildTargetGroup.iOS, BuildTarget.iOS);

				// If the build process started off with the active target not as iOS
				// Force the scripts to recompile after switching to enable the UNITY_IOS
				// preprocessor flag
				AssetDatabase.Refresh(ImportAssetOptions.ForceSynchronousImport);
#endif
				PostBuildDirectory = pathToBuiltProject;
				PostProcessIosBuild();
				break;
		}
	}

	[UnityEditor.MenuItem("Tools/Vungle/Run iOS Post Processor")]
	private static void PostProcessIosBuild()
	{
#if UNITY_IOS
		UnityEditor.iOS.Xcode.PBXProject project = new UnityEditor.iOS.Xcode.PBXProject();
		string pbxPath = UnityEditor.iOS.Xcode.PBXProject.GetPBXProjectPath(PostBuildDirectory);
		project.ReadFromFile(pbxPath);

#if UNITY_2019_3_OR_NEWER
		string targetId = project.GetUnityFrameworkTargetGuid();
#else
		string targetId = project.TargetGuidByName(UnityEditor.iOS.Xcode.PBXProject.GetUnityTargetName());
#endif
		foreach(KeyValuePair<string, bool> kvp in XCodeFramework)
		{
			project.AddFrameworkToProject(targetId, kvp.Key, kvp.Value);
		}

		for (int i = 0, n = XCodeFiles.Length; i < n; i++)
		{
			string path = System.IO.Path.Combine(System.IO.Path.Combine("usr", "lib"), XCodeFiles[i]);
			string projectPath = System.IO.Path.Combine("Frameworks", XCodeFiles[i]);
			project.AddFileToBuild(targetId, project.AddFile(path, projectPath, UnityEditor.iOS.Xcode.PBXSourceTree.Sdk));
		}

		project.AddBuildProperty(targetId, "OTHER_LDFLAGS", "-ObjC");

		string plistPath = System.IO.Path.Combine(PostBuildDirectory, "Info.plist");
		UnityEditor.iOS.Xcode.PlistDocument plist = new UnityEditor.iOS.Xcode.PlistDocument();
		plist.ReadFromFile(plistPath);

		UnityEditor.iOS.Xcode.PlistElementDict rootDict = plist.root;
		UnityEditor.iOS.Xcode.PlistElementArray skAdNetworkArray = rootDict.CreateArray("SKAdNetworkItems");
		UnityEditor.iOS.Xcode.PlistElementDict skAdNetworkIdentifierElement;

		string SKAdNetworkIdentifier = "SKAdNetworkIdentifier";
		for (int i = 0, n = SKAdNetworks.Length; i < n; i++)
		{
			skAdNetworkIdentifierElement = skAdNetworkArray.AddDict();
			skAdNetworkIdentifierElement.SetString(SKAdNetworkIdentifier, SKAdNetworks[i]);
		}
		rootDict.SetString("NSUserTrackingUsageDescription", NSUserTrackingUsageDescription);

		plist.WriteToFile(plistPath);

		project.WriteToFile(pbxPath);

		Debug.Log("Vungle iOS post processor completed.");
#else
		Debug.LogWarning("VunglePostBuilder: The active build target is not iOS. The Vungle post-processor has not run.");
#endif
	}

	[UnityEditor.MenuItem("Tools/Vungle/Open Documentation Website...")]
	static void DocumentationSite()
	{
		UnityEditor.Help.BrowseURL("https://support.vungle.com/hc/en-us/articles/360003455452");
	}

	[MenuItem("Tools/Vungle/Switch Platform - Android")]
	public static void PerformSwitchAndroid()
	{
		// Switch to Android build.
		EditorUserBuildSettings.SwitchActiveBuildTarget(BuildTargetGroup.Android, BuildTarget.Android);
	}

	[MenuItem("Tools/Vungle/Switch Platform - iOS")]
	public static void PerformSwitchiOS()
	{
		// Switch to iOS build.
		EditorUserBuildSettings.SwitchActiveBuildTarget(BuildTargetGroup.iOS, BuildTarget.iOS);
	}

	[MenuItem("Tools/Vungle/Switch Platform - Windows")]
	public static void PerformSwitchWindows()
	{
		// Switch to UWP build.
		EditorUserBuildSettings.SwitchActiveBuildTarget(BuildTargetGroup.WSA, BuildTarget.WSAPlayer);
	}
}
