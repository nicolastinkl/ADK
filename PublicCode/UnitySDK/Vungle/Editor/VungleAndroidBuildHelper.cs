using System.IO;
using System.Text;
using System.Xml;

// IPostGenerateGradleAndroidProject is supported only from 2018.2 and higher
#if UNITY_2018_2_OR_NEWER && UNITY_ANDROID
// Post process script to enable hardware acceleration
// Revised from https://raw.githubusercontent.com/gree/unity-webview/master/plugins/Editor/UnityWebViewPostprocessBuild.cs
public class VungleGradlePostProcessor : UnityEditor.Android.IPostGenerateGradleAndroidProject
{
	public void OnPostGenerateGradleAndroidProject(string basePath)
	{
		var androidManifest = new VungleAndroidManifest(GetManifestPath(basePath));
		var changed = androidManifest.SetHardwareAccelerated(true);
		if (changed)
		{
			androidManifest.Save();
			UnityEngine.Debug.Log("Successfully updated the hardwareAccelerated to true.");
		}
		else
		{
			UnityEngine.Debug.LogWarning("Failed to update the hardwareAccelerated to true.");
		}
	}

	public int callbackOrder
	{
		get
		{
			return 1;
		}
	}

	private string GetManifestPath(string basePath)
	{
		var pathBuilder = new StringBuilder(basePath);
		pathBuilder.Append(Path.DirectorySeparatorChar).Append("src");
		pathBuilder.Append(Path.DirectorySeparatorChar).Append("main");
		pathBuilder.Append(Path.DirectorySeparatorChar).Append("AndroidManifest.xml");
		return pathBuilder.ToString();
	}
}

internal class VungleAndroidXmlDocument : XmlDocument
{
	private string m_Path;
	protected XmlNamespaceManager nsMgr;
	public readonly string AndroidXmlNamespace = "http://schemas.android.com/apk/res/android";

	public VungleAndroidXmlDocument(string path)
	{
		m_Path = path;
		using (var reader = new XmlTextReader(m_Path))
		{
			reader.Read();
			Load(reader);
		}
		nsMgr = new XmlNamespaceManager(NameTable);
		nsMgr.AddNamespace("android", AndroidXmlNamespace);
	}

	public string Save()
	{
		return SaveAs(m_Path);
	}

	public string SaveAs(string path)
	{
		using (var writer = new XmlTextWriter(path, new UTF8Encoding(false)))
		{
			writer.Formatting = Formatting.Indented;
			Save(writer);
		}
		return path;
	}
}

internal class VungleAndroidManifest : VungleAndroidXmlDocument
{
	public VungleAndroidManifest(string path) : base(path)
	{
	}

	internal XmlNode GetActivityWithLaunchIntent()
	{
		return
			SelectSingleNode(
				"/manifest/application/activity[intent-filter/action/@android:name='android.intent.action.MAIN' and "
				+ "intent-filter/category/@android:name='android.intent.category.LAUNCHER']",
				nsMgr);
	}

	internal bool SetHardwareAccelerated(bool enabled)
	{
		bool changed = false;
		var activity = GetActivityWithLaunchIntent() as XmlElement;
		if (activity.GetAttribute("hardwareAccelerated", AndroidXmlNamespace) != ((enabled) ? "true" : "false"))
		{
			activity.SetAttribute("hardwareAccelerated", AndroidXmlNamespace, (enabled) ? "true" : "false");
			changed = true;
		}
		return changed;
	}
}
#endif
