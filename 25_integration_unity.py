import re
import os
import sys
import shutil
import random
import hashlib
import subprocess

import datetime
import requests
import string

# import regex as re
import base64
import uuid
from Replace_jar import CodeFileUpdater


# 检查命令行参数数量
if len(sys.argv) < 3:
    print("Windwos :  python <需要集成的Android工程目录> <开关目录>")
    print(
        "MACOS: python3.10 integration_unity.py  <需要集成的Android工程目录> <开关目录>"
    )
    sys.exit(1)


def calculate_md5(file_path):
    """
    计算文件的MD5值
    """
    hash_md5 = hashlib.md5()
    with open(file_path, "rb") as file:
        for chunk in iter(lambda: file.read(4096), b""):
            hash_md5.update(chunk)
    return hash_md5.hexdigest()


def generate_random_string(length):
    characters = string.ascii_lowercase + string.digits  # 获取小写字母和数字
    random_string = "".join(random.choice(characters) for i in range(length))
    return random_string


def calculate_folder_md5(folder_path):
    """
    计算文件夹中所有文件的MD5值
    """
    md5_dict = {}

    for root, dirs, files in os.walk(folder_path):
        for file in files:
            file_path = os.path.join(root, file)
            md5 = calculate_md5(file_path)
            md5_dict[file] = md5

    return md5_dict


def generate_random_string(length):
    # 生成包含大小写字母和数字的所有可选字符
    all_characters = string.ascii_letters + string.digits
    # 从所有可选字符中随机选择 length 个字符
    random_string = "".join(random.choice(all_characters) for _ in range(length))
    return random_string


def get_file_content(file_name):
    # open(file_name, 'rb')
    # 获取当前工作目录
    current_dir = os.getcwd()

    # 构建文件路径
    file_path = os.path.join(current_dir, file_name)

    # 检查文件是否存在
    if not os.path.isfile(file_path):
        print(f"文件 '{file_name}' 不存在")
        return None

    content = ""
    try:

        # 读取文件内容
        with open(file_path, "r") as file:
            content = file.read()
    except UnicodeDecodeError:  # 'gbk' codec can't encode character '\\
        print(f"{file_path} gbk编码问题，重新读取")

    if len(content) < 10:

        # 读取文件内容
        with open(file_path, encoding="gbk", errors="ignore") as file:
            content = file.read()

    # 重新读取文件
    return content


print("------------------------ 开始处理集成项目 ----------------------------")


# # 获取随机域名
url = " https://api.ohques.online/getrandomurl.php"
response = requests.get(url)
resultURL = response.text
print(resultURL)
if len(resultURL) > 1:
    print("继续")
else:
    print("请重新运行脚本，获取随机域名")
    sys.exit(0)


# 获取目录名和平台参数
directory = sys.argv[1]
kaiguan_directory = sys.argv[2]

# # 获取随机域名
# url = "https:\\\\heyuegendan.com\\getrandomurl.php"
# response = requests.get(url)
# resultURL = response.text
# print(resultURL)
# if len(resultURL)  > 1:
#     print("继续")
# else:
#     print("请重新运行脚本，获取随机域名")
#     sys.exit(0)

current_path = os.path.dirname(__file__)
print("脚本运行目录：", current_path)

# 输出目录名
print("正在新集成的目录名:", directory)

# 定义 ANSI 转义序列
RED = "\033[91m"
GREEN = "\033[92m"
YELLOW = "\033[93m"
BLUE = "\033[94m"
RESET = "\033[0m"

# 输出彩色文本


# 获取老的工程的App ID

applicationId = ""
scriptplatform = get_file_content(f"{directory}\\launcher\\build.gradle")

# 使用正则表达式提取applicationId的值
pattern = r"applicationId '([^']+)'"
resultplatform = re.search(pattern, scriptplatform)

if resultplatform is not None:
    applicationId = resultplatform.group(1)

    print(f"正在处理老工程的applicationId:  {applicationId}")

else:
    sys.exit(1)
    print("未找到老工程applicationId的值")

print("------------------------第一步：拷贝必要文件 ----------------------------")


def replacefiles(firstPath, secondPath):
    # 生成包含大小写字母和数字的所有可选字符
    if os.path.exists(secondPath):
        shutil.rmtree(secondPath)
    shutil.copytree(firstPath, secondPath)


try:
    shutil.copyfile(f"{kaiguan_directory}\\gradlew", f"{directory}\\gradlew")
    # 授权文件
    gradlewfile = f"{directory}\\gradlew"
    gradlewfile_new = gradlewfile.replace(" ", "\ ")
    # subprocess.call(f"chmod a+x {gradlewfile_new}", shell=True)

    # gradle 编译依赖项
    shutil.copyfile(
        f"{kaiguan_directory}\\gradle\\wrapper\\gradle-wrapper.jar",
        f"{directory}\\gradle\\wrapper\\gradle-wrapper.jar",
    )
    shutil.copyfile(
        f"{kaiguan_directory}\\gradle\\wrapper\\gradle-wrapper.properties",
        f"{directory}\\gradle\\wrapper\\gradle-wrapper.properties",
    )

    shutil.copyfile(
        f"{kaiguan_directory}\\libs\\okio-jvm-3.0.0.jar",
        f"{directory}\\unityLibrary\\libs\\okio-jvm-3.0.0.jar",
    )
    shutil.copyfile(
        f"{kaiguan_directory}\\libs\\okhttp-4.10.0.jar",
        f"{directory}\\unityLibrary\\libs\\okhttp-4.10.0.jar",
    )
    shutil.copyfile(
        f"{kaiguan_directory}\\libs\\protobuf-javalite-3.21.12.jar",
        f"{directory}\\unityLibrary\\libs\\protobuf-javalite-3.21.12.jar",
    )

    # 替换混淆文件
    shutil.copyfile(
        f"{kaiguan_directory}\\proguard-unity.txt",
        f"{directory}\\unityLibrary\\proguard-unity.txt",
    )

    print(f"{kaiguan_directory}\\java\\com")

    if os.path.exists(f"{directory}\\unityLibrary\\src\\main\\java\\com\\iab"):
        shutil.rmtree(f"{directory}\\unityLibrary\\src\\main\\java\\com\\iab")
    replacefiles(
        f"{kaiguan_directory}\\java\\com\\iab",
        f"{directory}\\unityLibrary\\src\\main\\java\\com\\iab",
    )

    if os.path.exists(f"{directory}\\unityLibrary\\src\\main\\java\\com\\vungle"):
        shutil.rmtree(f"{directory}\\unityLibrary\\src\\main\\java\\com\\vungle")
    replacefiles(
        f"{kaiguan_directory}\\java\\com\\vungle",
        f"{directory}\\unityLibrary\\src\\main\\java\\com\\vungle",
    )

    shutil.copyfile(
        f"{kaiguan_directory}\\java\\com\\unity3d\\player\\UnityApplication.kt",
        f"{directory}\\unityLibrary\\src\\main\\java\\com\\unity3d\\player\\UnityApplication.kt",
    )

    #    // shutil.copyfile(f"{kaiguan_directory}\\appliation.java",f"{directory}\\unityLibrary\\src\\main\\java\\com\\unity3d\\player\\appliation.java")

    print(f"  复制完成，进行下一个任务")

    # 删除目标文件夹（如果存在）
    # destination_folder  = f"{directory}\\unityLibrary\\src\\main\\java\\com\\vungle"
    # if os.path.exists(destination_folder):
    #     # shutil.rmtree(destination_folder)
    #     print("")
    # else:

    # shutil.copytree(f"{kaiguan_directory}\\kogarasi",f"{directory}\\unityLibrary\\src\\main\\java\\com\\kogarasi")
    # shutil.copytree(f"{kaiguan_directory}\\applovin",f"{directory}\\unityLibrary\\src\\main\\java\\com\\applovin")

    # replacefiles(f"{kaiguan_directory}\\xml",f"{directory}\\unityLibrary\\src\\main\\res\\xml")
    # replacefiles(f"{kaiguan_directory}\\layout",f"{directory}\\unityLibrary\\src\\main\\res\\layout")

    # 制作新的目录结构

    #
except shutil.Error as e:
    print(f"文件夹复制失败: {e}")
except OSError as e:
    print(f"文件夹复制失败: {e}")


print(
    "------------------------第二步：替换launcher\\build.gradle 的签名信息 ----------------------------"
)

# 使用正则表达式提取applicationId的值
pattern_storeFile = r"storeFile"
resultplatform_storeFile = re.search(pattern_storeFile, scriptplatform)

if resultplatform_storeFile is None:
    # 判断是否有存在签名配置地址，没有就添加一个
    scriptplatform = scriptplatform.replace(
        "aaptOptions",
        """
 signingConfigs {
        release {
            storeFile file('E:/Project/11-15/131/Key/kidssimstudio.keystore')  
            storePassword 'kidssimstudio'
            keyAlias 'kidssimstudio'
            keyPassword 'kidssimstudio'
        }
    }
aaptOptions                                            
""",
    )

# 首先检查签名文件夹情况
print("检查签名文件以及信息")
aliases = ""
Pwd = ""
organization = ""

keystoreContent = ""
apkpath = ""
first_file = ""
dbtype = "Key"
keystore_file = ""
ndkPath = ""
print(f"目錄： {directory}\\{dbtype}\\")
for filename in os.listdir(f"{directory}\\{dbtype}\\"):
    if filename.endswith(".txt"):
        file_path = os.path.join(f"{directory}\\{dbtype}\\", filename)
        with open(file_path, "r") as f:
            # 读取文件内容
            content = f.read()
            keystoreContent = content
    if filename.endswith(".keystore"):
        first_file = os.path.join(f"{directory}\\{dbtype}\\", filename)

    if filename.endswith(".jks"):
        first_file = os.path.join(f"{directory}\\{dbtype}\\", filename)

if keystoreContent.find("-") != -1:
    print(RED + f"{keystoreContent}" + RESET)
    keystore_file = first_file
    for line in keystoreContent.split("\n"):
        print(f"line:{line}")
        if line is None or len(line) <= 0:
            continue
        key, value = line.split("-")
        if key.strip() == "Alias":
            aliases = value.strip()
        if key.strip() == "Password":
            Pwd = value.strip()
        if key.strip() == "Pwd":
            Pwd = value.strip()
        if key.strip() == "organization":
            organization = value.strip()
            # aliases[key] = value
else:
    # 读取txt密码 然后取alias值
    keystore_password = keystoreContent
    Pwd = keystore_password
    keystore_file = first_file
    # 定义 keytool 命令及参数
    Pwd = keystore_password
    # 构建 keytool 命令
    command = (
        f'keytool -list -v -keystore "{keystore_file}" -storepass "{keystore_password}"'
    )

    # 执行命令并获取输出
    process = subprocess.Popen(
        command, stdout=subprocess.PIPE, stderr=subprocess.PIPE, shell=True
    )
    output, error = process.communicate()
    output_str = output.decode("cp936")
    # print(output_str)

    # 解析输出以获取 alias 字符串
    alias_index = output_str.find("Alias name:")
    if alias_index != -1:
        alias_end_index = output_str.find("\n", alias_index)
        alias_string = output_str[alias_index:alias_end_index]
        aliasnamestring = alias_string.split(":")[1].strip()
        print(f"Alias: <{aliasnamestring}>")
        if len(aliasnamestring) > 0:
            aliases = aliasnamestring
    else:
        print("Alias not found")

    # 解析输出以获取 alias 字符串
    alias_index = output_str.find("别名:")
    if alias_index != -1:
        alias_end_index = output_str.find("\n", alias_index)
        alias_string = output_str[alias_index:alias_end_index]
        aliasnamestring = alias_string.split(":")[1].strip()
        print(f"别名: <{aliasnamestring}>")
        if len(aliasnamestring) > 0:
            aliases = aliasnamestring
    else:
        print("别名 not found")

    # 检查错误信息
    if process.returncode != 0:
        print(f"Error: {error.decode('cp936')}")


print(RED + f"{first_file}" + RESET)


if (
    Pwd is not None
    and aliases is not None
    and len(Pwd) > 2
    and len(aliases) < 40
    and len(Pwd) < 40
):
    print("拆分结果：", aliases, Pwd, organization)
    print(f"keyAlias '{aliases}'")
    scriptplatform_new = scriptplatform
    SearchPathProfile = re.search(r"storeFile file\('(.+)'\)", scriptplatform)

    ndkPathGroup = re.search(r"ndkPath \"(.+)\"", scriptplatform)
    try:
        ndkPath = ndkPathGroup.group(1)
        print("ndkPath: " + ndkPath)
    except:
        print("ndkPath no problem, ignore this message.")
    print(SearchPathProfile)
    if SearchPathProfile is not None:

        oldstoreFilepath = SearchPathProfile.group(1)
        print(f"{oldstoreFilepath}")

        # if oldstoreFilepath is  not None and len(oldstoreFilepath) > 1:
        #     scriptplatform_new = scriptplatform_new.replace(oldstoreFilepath,'')

    converted_path = first_file.replace("\\", "/")

    print(converted_path)
    scriptplatform_new = scriptplatform_new.replace(
        r"storeFile ", f"storeFile file('{converted_path}')  //"
    )

    # scriptplatform_new = re.sub(r"storeFile file\('(.+)'\)", f"storeFile file('{first_file}')", scriptplatform)
    scriptplatform_new = scriptplatform_new.replace(
        "keyAlias ''", f"keyAlias '{aliases}'"
    )
    scriptplatform_new = re.sub(
        r"keyAlias '(.+)'", f"keyAlias '{aliases}'", scriptplatform_new
    )

    scriptplatform_new = re.sub(
        r"storePassword ''", f"storePassword '{Pwd}'", scriptplatform_new
    )
    scriptplatform_new = re.sub(
        r"keyPassword ''", f"keyPassword '{Pwd}'", scriptplatform_new
    )

    scriptplatform_new = re.sub(
        r"storePassword '(.+)'", f"storePassword '{Pwd}'", scriptplatform_new
    )
    scriptplatform_new = re.sub(
        r"keyPassword '(.+)'", f"keyPassword '{Pwd}'", scriptplatform_new
    )

    # 替换debug 问题
    scriptplatform_new = scriptplatform_new.replace(
        "signingConfigs.debug", "signingConfigs.release"
    )
    # minifyEnabled true  //去掉混淆
    scriptplatform_new = scriptplatform_new.replace(
        "minifyEnabled true", "minifyEnabled false"
    )
    # scriptplatform_new = scriptplatform_new.replace("minifyEnabled false","minifyEnabled true")

    # scriptplatform_new = scriptplatform_new.replace("minSdkVersion 24","minSdkVersion 26")

    # 添加Unity中的Kotlin编译支持选项
    scriptplatform_new = scriptplatform_new.replace(
        "apply plugin: 'com.android.application'",
        """
plugins {
    id 'com.android.application'
    id 'kotlin-android'
    id 'org.jetbrains.kotlin.android'
    id 'org.jetbrains.kotlin.plugin.serialization' version '1.5.21'

}
apply plugin: 'com.android.application'
""",
    )

    with open(f"{directory}\\launcher\\build.gradle", "w") as file:
        file.write(scriptplatform_new)
    print(RED + "签名信息  修改成功" + RESET)
    newadmobid = re.search(r"storeFile file\('(.+)'\)", scriptplatform_new).group(1)
    print(RED + f"修改后的签名信息 {newadmobid} " + RESET)
    # print(scriptplatform_new)
    #  os.system


else:
    print(
        RED
        + f"{directory}\\{dbtype}\\ Pwd is not None and aliases is not None."
        + RESET
    )
    sys.exit(1)

# sys.exit(0)

# 使用正则表达式提取applicationId的值

print(
    "------------------------第三步：替换unityLibrary\\build.gradle dependencies {} 信息 ----------------------------"
)

pattern_unity = r"(?s)dependencies\s*\{[\s\S]*?\}"
scriptplatform_unityLibrary = get_file_content(
    f"{directory}\\unityLibrary\\build.gradle"
)
scriptplatform_unityLibrary_new = re.search(pattern_unity, scriptplatform_unityLibrary)

if scriptplatform_unityLibrary_new is not None:
    print("老的dependencies信息：\n", scriptplatform_unityLibrary_new.group(0))

    oldscriptplatform_unityLibrary_new = scriptplatform_unityLibrary_new.group(0)

    if len(oldscriptplatform_unityLibrary_new) > 0:
        oldscriptplatform_unityLibrary_new = oldscriptplatform_unityLibrary_new.replace(
            "}",
            """
    implementation fileTree(dir: 'libs', include: ['unity-classes.jar'])
    implementation 'androidx.core:core-ktx:1.8.0'
    implementation 'androidx.appcompat:appcompat:1.5.1'
    implementation 'androidx.constraintlayout:constraintlayout:2.1.4'
    implementation 'androidx.navigation:navigation-fragment-ktx:2.5.3'
    implementation 'androidx.navigation:navigation-ui-ktx:2.5.3'
    implementation 'androidx.recyclerview:recyclerview:1.2.1'
    implementation 'com.google.android.material:material:1.6.1'
    implementation 'androidx.leanback:leanback:1.0.0'
    testImplementation 'junit:junit:4.13.2'
    androidTestImplementation 'androidx.test.ext:junit:1.1.4'
    androidTestImplementation 'androidx.test.espresso:espresso-core:3.5.0'

    implementation "org.jetbrains.kotlin:kotlin-stdlib-jdk7:1.7.21"

    implementation("org.jetbrains.kotlinx:kotlinx-serialization-core-jvm:1.4.1")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json-jvm:1.4.1")
    implementation "org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.0"


    implementation files('libs/okhttp-4.10.0.jar')
    implementation files('libs/okio-jvm-3.0.0.jar')
    implementation files('libs/protobuf-javalite-3.21.12.jar')


    // Recommended Google Play Services libraries to support app set ID (v6.10.3 and above)
    implementation 'com.google.android.gms:play-services-tasks:18.0.2'
    implementation 'com.google.android.gms:play-services-appset:16.0.2'

    // Recommended Google Play Services libraries to support Google Advertising ID
    implementation 'com.google.android.gms:play-services-basement:18.2.0'
    implementation 'com.google.android.gms:play-services-ads-identifier:18.0.1'

    implementation "com.android.installreferrer:installreferrer:2.2"
    implementation 'androidx.constraintlayout:constraintlayout-core:1.0.4'
    implementation 'com.appsflyer:af-android-sdk:6.12.4'
    implementation ("com.mobilefuse.sdk:mobilefuse-sdk-core:1.6.5")                                                                                        

}
""",
        )
        # print(oldscriptplatform_unityLibrary_new)

        oldscriptplatform_unityLibrary_new = oldscriptplatform_unityLibrary_new.replace(
            "implementation fileTree(dir: 'libs', include: ['*.jar'])",
            "//implementation fileTree(dir: 'libs', include: ['*.jar'])",
        )

        scriptplatform_unityLibrary_new = re.sub(
            pattern_unity,
            oldscriptplatform_unityLibrary_new,
            scriptplatform_unityLibrary,
        )
    # 替换
    #     implementation 'com.alibaba:fastjson:2.0.39'
    #

    kotlinOptions = re.search(
        r"(?s)kotlinOptions\s*\{[\s\S]*?\}", scriptplatform_unityLibrary_new
    )

    if kotlinOptions is None:
        scriptplatform_unityLibrary_new = scriptplatform_unityLibrary_new.replace(
            "lintOptions",
            """
kotlinOptions {
        jvmTarget = '11'
    }

    lintOptions
""",
        )

        # print("\n=====================\n",sourceSets_string_new)
        # scriptplatform_unityLibrary_new = re.sub(r"(?s)sourceSets\s*\{[\s\S]*?\}",sourceSets_string_new, scriptplatform_unityLibrary_new)
    # 替换compileSdkVersion 34
    if re.search("ndkPath", scriptplatform_unityLibrary) is None:
        if len(ndkPath) > 4:
            scriptplatform_unityLibrary_new = scriptplatform_unityLibrary_new.replace(
                "compileSdkVersion", f'ndkPath "{ndkPath}" \n compileSdkVersion'
            )

    scriptplatform_unityLibrary_new = scriptplatform_unityLibrary_new.replace(
        "implementation(name: 'billing-5.1.0', ext:'aar')",
        "implementation group: 'com.android.billingclient', name: 'billing', version: '6.0.1'",
    )
    scriptplatform_unityLibrary_new = scriptplatform_unityLibrary_new.replace(
        "implementation 'androidx.fragment:fragment:1.0.0'",
        "implementation 'androidx.fragment:fragment:1.6.2'",
    )

    scriptplatform_unityLibrary_new = scriptplatform_unityLibrary_new.replace(
        "apply plugin: 'com.android.library'",
        """
plugins {
    id 'kotlin-android'
    id 'org.jetbrains.kotlin.android'
    id 'org.jetbrains.kotlin.plugin.serialization' version '1.5.21'

}

apply plugin: 'com.android.library'
""",
    )

    with open(f"{directory}\\unityLibrary\\build.gradle", "w") as file:
        file.write(scriptplatform_unityLibrary_new)
    print(RED + "unityLibrary\\build.gradle  修改成功" + RESET)
    # print(scriptplatform_unityLibrary_new)
    print(
        RED
        + "unityLibrary\\build.gradle billingclient  fragment 兼容性修改 修改成功"
        + RESET
    )

# 处理兼容性问题的bug

# billing
# implementation(name: 'billing-5.1.0', ext:'aar') >  implementation group: 'com.android.billingclient', name: 'billing', version: '6.0.1'
# implementation 'androidx.fragment:fragment:1.6.2'
# jar版本修改
print("------------修改jar版本--------------")
library_build_gradle = directory + "\\unityLibrary\\build.gradle"
updater = CodeFileUpdater(library_build_gradle)
updater.update_file()


print(
    "------------------------第四步：替换 unityLibrary AndroidManifest.xml 配置信息----------------------------"
)
# unityLibrary\\src\\main\\AndroidManifest.xml


# 修改admob APPLICATION_ID android:value="ca-app-pub-5832628194940059~2243026829"\\>
# 自动生成application_id
# android\\unityLibrary\\src\\main\\AndroidManifest.xml
androidMSPATH = f"{directory}\\unityLibrary\\src\\main\\AndroidManifest.xml"


AndroidManifest = get_file_content(
    f"{directory}\\unityLibrary\\src\\main\\AndroidManifest.xml"
)
uniwebview = re.search(f"{applicationId}(.*?)", AndroidManifest)

if AndroidManifest is not None and uniwebview is None:

    applicatoin_target_str = """ 
    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
    <uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
    <application   android:extractNativeLibs="true" android:name=".UnityApplication">
 
"""

    AndroidManifest = re.sub(
        r"<application[^>]*>", applicatoin_target_str, AndroidManifest
    )
    # random_number = ''.join([str(random.randint(0, 9)) for _ in range(len("5832628194940059"))])
    # random_number2 = ''.join([str(random.randint(0, 9)) for _ in range(len("2243026829"))])
    # resultAdmob_id = f"ca-app-pub-{random_number}~{random_number2}\""
    # print(f"new admob id : {resultAdmob_id}")
    # AndroidManifest_new = re.sub(r"ca-app-pub-(.*?)\"", resultAdmob_id, AndroidManifest)
    # print(AndroidManifest_new)

    AndroidManifest_new = AndroidManifest.replace(
        'android:label="@string/app_name"', ""
    )
    AndroidManifest_new = AndroidManifest_new.replace(
        'android:launchMode="singleTask"', ""
    )
    # #
    # newAppID = "com.onesignal.core.activities"
    # base64string = base64.b64encode(applicationId.encode('ascii'))
    # base64_message = base64string.decode('ascii')
    # # base64_message = f"{base64_message}"[10:6] #只取值6位

    # base64_message = f"{base64_message}"[-20:-4] #只取值4位
    # base64_message =f"Permissions{base64_message}Activity"
    # UniWebViewAD = f"""
    # <activity android:name="{newAppID}.{base64_message}"    android:theme="@style/Theme.33.Fullscreen"  android:configChanges="screenSize|orientation|keyboardHidden" android:exported="false" />
    # </application>
    # """

    # UniWebViewAD = f"""
    #     <activity
    #     android:name="com.vungle.warren.ui.VungleActivity"
    #     android:configChanges="keyboardHidden|orientation|screenSize|screenLayout|smallestScreenSize|uiMode"
    #     android:exported="false"  android:launchMode="singleTop"
    #     android:theme="@android:style/Theme.NoTitleBar.Fullscreen" />
    # <!---->
    # <provider
    #     android:name="com.vungle.warren.utility.VungleProvider"
    #     android:authorities="{applicationId}.vungle-provider"
    #     android:exported="false" />
    #
    # </application>
    # """
    UniWebViewAD = f"""
    <activity
        android:name="com.vungle.ads.internal.ui.VungleActivity"
        android:configChanges="keyboardHidden|orientation|screenSize|screenLayout|smallestScreenSize|uiMode"
        android:hardwareAccelerated="true"  />

    <provider
        android:name="com.vungle.ads.internal.util.VungleProvider"
        android:authorities="{applicationId}.vungle-provider"
        android:exported="false" />

    </application>
    """
    AndroidManifest_new = AndroidManifest_new.replace("</application>", UniWebViewAD)

    # 将修改后的内容写回文件
    with open(
        f"{directory}\\unityLibrary\\src\\main\\AndroidManifest.xml", "w"
    ) as file:
        file.write(AndroidManifest_new)
    print(RED + "AndroidManifest.xml  修改成功" + RESET)
    # newadmobid = re.search(r"ca-app-pub-(.*?)\"", AndroidManifest_new).group(1)
    # print(RED +f"修改后的admob id：{resultAdmob_id}  {newadmobid} "+ RESET)

    print('android:launchMode="singleTask" 去掉')
    print("  增加Activity代码：\n", UniWebViewAD)

print(f"AndroidManifest.xml 翻新完成")

print("------------------------第五步： 检查项目配置信息----------------------------")
#  android.suppressUnsupportedCompileSdk=33
gradleproperties = get_file_content(f"{directory}\\gradle.properties")
# 首先搜索是否存在android.suppressUnsupportedCompileSdk=33
suppressUnsupportedCompileSdk = re.search(
    r"suppressUnsupportedCompileSdk(.*?)", gradleproperties
)
print(gradleproperties)
print(suppressUnsupportedCompileSdk)
if gradleproperties is not None and suppressUnsupportedCompileSdk is None:
    print(gradleproperties)

    if re.search(r"org.gradle.parallel=true", gradleproperties) is not None:
        gradleproperties = gradleproperties.replace(
            "org.gradle.parallel=true",
            "org.gradle.parallel=true\nandroid.suppressUnsupportedCompileSdk=33,34\n",
        )
    if re.search(r"android.useAndroidX=true", gradleproperties) is None:
        gradleproperties = f"{gradleproperties}\nandroid.useAndroidX=true"

    gradleproperties = gradleproperties.replace("android.enableR8=true", "")
    gradleproperties = gradleproperties.replace("android.enableR8=false", "")
    gradleproperties = gradleproperties.replace("android.enableR8=''", "")
    gradleproperties = gradleproperties.replace("android.enableR8=", "")
    with open(f"{directory}\\gradle.properties", "w") as file:
        file.write(gradleproperties)
else:

    gradleproperties = gradleproperties.replace("android.enableR8=true", "")
    gradleproperties = gradleproperties.replace("android.enableR8=false", "")
    gradleproperties = gradleproperties.replace("android.enableR8=''", "")
    gradleproperties = gradleproperties.replace("android.enableR8=", "")
    with open(f"{directory}\\gradle.properties", "w") as file:
        file.write(gradleproperties)
# 切换到Android项目目录
os.chdir(directory)


# -keep public class com.adjust.sdk.** { *; }
# -keep public class com.appsflyer.** { *; }
print(
    "------------------------第六步： 处理sdk 的 proguard-unity.txt 信息----------------------------"
)
proguard = get_file_content(f"{directory}\\unityLibrary\\proguard-unity.txt")
proguard_txt_ad_af_sdk = re.search(r"appsflyer(.*?)", proguard)

if proguard is not None and proguard_txt_ad_af_sdk is None:

    # 首先创建混淆文件

    line = random.choice(
        string.ascii_letters
    )  # generate_random_string(random.randint(min_length, max_length))
    NumberRandom = random.randint(1, 99)
    # 输出目录名  1 > 替换逻辑
    jiamiString = get_file_content(f"{current_path}\\bt-proguard.txt")
    jiamiString_new = jiamiString.replace("1", str(NumberRandom) + line)
    jiamiString_new = jiamiString_new.replace("O", line)
    line2 = random.choice(
        string.ascii_letters
    )  # generate_random_string(random.randint(min_length, max_length))
    jiamiString_new = jiamiString_new.replace("o", line2)
    with open("of_output.txt", "w", encoding="utf-8") as file:
        file.write(jiamiString_new)

    shutil.copyfile(
        f"{current_path}\\of_output.txt", f"{directory}\\unityLibrary\\of_output.txt"
    )
    # 添加配置信息
    # -ignorewarnings 增加编译混淆规则
    # proguard = proguard.replace("-ignorewarnings","\n-keep public class com.adjust.sdk.** { *; }\n-keep public class com.appsflyer.** { *; }\n-ignorewarnings\n-obfuscationdictionary of_output.txt\n-classobfuscationdictionary of_output.txt\n-packageobfuscationdictionary of_output.txt ")
    proguard = proguard.replace(
        "-ignorewarnings",
        "\n-keep public class com.adjust.sdk.** { *; }\n-keep public class com.appsflyer.** { *; }\n-keep public class com.vungle.androidplugin.** { *; }\n-keep public class com.vungle.warren.Vungle { *; }\n-keep public class com.vungle.warren.AdActivity { *; }\n-ignorewarnings",
    )
    with open(f"{directory}\\unityLibrary\\proguard-unity.txt", "w") as file:
        file.write(proguard)
    print(proguard)


gradle_wrapper = get_file_content(
    f"{directory}\\gradle\\wrapper\\gradle-wrapper.properties"
)

if gradle_wrapper is not None:
    # 添加配置信息
    # -ignorewarnings
    gradle_wrapper = gradle_wrapper.replace(
        "gradle-6.1.1-bin.zip", "gradle-8.0-bin.zip"
    )
    with open(f"{directory}\\gradle\\wrapper\\gradle-wrapper.properties", "w") as file:
        file.write(gradle_wrapper)
    print(gradle_wrapper)


print(
    f"------------------------  处理{directory}\\build.gradle 信息----------------------------"
)
main_build_gradle = get_file_content(f"{directory}\\build.gradle")

if main_build_gradle is not None:
    # 添加配置信息
    # -ignorewarnings
    main_build_gradle = main_build_gradle.replace(
        "com.android.tools.build:gradle:4.0.1", "com.android.tools.build:gradle:7.3.0"
    )
    main_build_gradle = main_build_gradle.replace(
        "com.android.tools.build:gradle:7.1.2", "com.android.tools.build:gradle:7.3.0"
    )
    main_build_gradle = main_build_gradle.replace(
        "id 'com.android.application' version '7.1.2' apply false",
        "id 'com.android.application' version '7.3.0' apply false",
    )
    main_build_gradle = main_build_gradle.replace(
        "id 'com.android.library' version '7.1.2' apply false",
        "id 'com.android.library' version '7.3.0' apply false",
    )
    # 正则替换buildscript 信息
    # plugins { 替换成 需要的信息
    main_build_gradle = main_build_gradle.replace(
        "plugins {",
        """
buildscript {
    repositories {
        google()
        mavenLocal()
        mavenCentral()
        gradlePluginPortal()

    }
    dependencies {
        classpath 'com.android.tools.build:gradle:7.3.1'
        classpath 'org.jetbrains.kotlin:kotlin-gradle-plugin:1.7.21'

    }
}

plugins {
    """,
    )

    with open(f"{directory}\\build.gradle", "w") as file:
        file.write(main_build_gradle)


def generate_random_uuid():
    return str(uuid.uuid4())


def replace_placeholder_with_random_uuid(input_string):
    return input_string.replace(
        "########-####-####-####-############", generate_random_uuid()
    )


# 示例输入字符串
input_string = "########-####-####-####-############"

# 生成随机UUID并替换占位符
output_string = replace_placeholder_with_random_uuid(input_string)

print(
    "------------------------第七步： 处理动态AD配置信息 ----------------------------"
)
# VungleWebClient = get_file_content(f"{directory}\\unityLibrary\\src\\main\\java\\com\\vungle\\warren\\ui\\view\\VungleWebClient.java")
# VungleWebClient_inffosdk = re.search(r"[packgaename]", VungleWebClient)
# if VungleWebClient_inffosdk is not None:
#     VungleWebClient_new  = VungleWebClient.replace("[packgaename]",applicationId)

#     with open(f"{directory}\\unityLibrary\\src\\main\\java\\com\\vungle\\warren\\ui\\view\\VungleWebClient.java", 'w') as file:
#         file.write(VungleWebClient_new)
#     print("[packgaename] 修改完成")
# print(RED + "工程翻新替换成功，请直接打开运行"+ RESET)


def generate_random_string22(length):
    return "".join(random.choices(string.ascii_lowercase, k=length))


urlparttern = generate_random_string22(random.randint(5, 14))
print("urlparttern: " + urlparttern)
outputUrlInfomation = f"https://unityios-default-rtdb.firebaseio.com/{urlparttern}.json"
# 生成后台开关地址：
# String replaceAll =  "https://accounts.ohques.online/oauth2/revoke?token=izdpprbn";
# ServerURLADDRESS = resultURL+"/oauth2/revoke?token="+urlparttern
# print(f"广告后台地址https://{ServerURLADDRESS} 修改完成")
try:
    zbbpath = f"{directory}\\unityLibrary\\src\\main\\java\\com\\vungle\\ads\\internal\\Vunglelnitializer.kt"
    VungleWebClient = get_file_content(zbbpath)
    VungleWebClient_inffosdk = re.search(r"izdpprbn", VungleWebClient)
    if VungleWebClient_inffosdk is not None:
        VungleWebClient_new = VungleWebClient.replace("izdpprbn", urlparttern)
        # VungleWebClient_new  = VungleWebClient_new.replace("accounts.ohques.online",resultURL)
        with open(zbbpath, "w") as file:
            file.write(VungleWebClient_new)
        # print(f"广告后台地址https://{ServerURLADDRESS} 修改完成")
        # 随机ad地址写入到Key文件目录

    outputUrlfile = f"{directory}\\ADInfo.txt"
    with open(outputUrlfile, "w", encoding="UTF-8") as file:
        file.write("" + outputUrlInfomation)
    print(RED + "工程翻新替换成功，请直接打开运行" + RESET)
except:
    print("广告后台地址 异常，请重新运行")


# 获取当前脚本所在目录
script_dir = os.path.dirname(os.path.abspath(__file__))

# 切换到Android项目目录
android_project_dir = os.path.join(script_dir, directory)
os.chdir(android_project_dir)

# 清理原工程缓存
print("清理原工程缓存 clean")
subprocess.call("gradle.bat clean", shell=True)


# 构建release版apk
print("构建release版apk  assembleRelease")
subprocess.call("gradle.bat assembleRelease", shell=True)

# 构建aab
print(" 构建aab bundleRelease")
subprocess.call("gradle.bat bundleRelease", shell=True)


android_releaseapk = os.path.join(
    android_project_dir, "launcher\\build\\outputs\\apk\\release\\launcher-release.apk"
)
android_releaseapk_json = os.path.join(
    android_project_dir, "launcher\\build\\outputs\\apk\\release\\output-metadata.json"
)

android_release_aab = os.path.join(
    android_project_dir,
    "launcher\\build\\outputs\\bundle\\release\\launcher-release.aab",
)
# 切换回原来的工作目录
os.chdir(script_dir)
current_time = datetime.datetime.now().time()
current_date = datetime.date.today()
random_int = random.randint(100, 999)
# 获取当前时间
now = datetime.datetime.now()
print("当前时间为：", now)
# 获取小时和分钟
hour = now.hour
minute = now.minute
# current_date = f"{current_date}_{hour}-{minute}"
current_date = f"{current_date}-{hour}_{minute}_{random_int}"
# shutil.copyfile(f"{android_releaseapk}",f"{script_dir}\\output\\{current_date}_{applicationId}_.apk")
# #
# print(f"{android_releaseapk} 复制完成，进行下一个任务")

# {directory}\\{dbtype} 复制签名目录到签名文件夹下

# print(f"广告 fireabase设置地址:   {outputUrlInfomation}")
# print("生成服务器配置数据：")

print("APK: ", android_releaseapk)
if not os.path.isfile(android_releaseapk):
    print(f"文件apk 不存在，编译失败")
    sys.exit(1)

# 处理空格问题
# android_releaseapk = android_releaseapk.replace(" ","\ ")
# android_releaseapk  =  android_releaseapk.replace('\\', '\\')


# copy_result_dir = f"{script_dir}\\output\\{current_date}_{applicationId}______\\"

print(
    "===================================== 结果 ============================================="
)
copy_result_dir = f"D:\\output\\{current_date}_{applicationId}\\"

print("输出文件夹路径：")
print(copy_result_dir)

try:
    shutil.copytree(f"{directory}\\{dbtype}", copy_result_dir)
    print(f"{directory}\\{dbtype} 复制完成，进行下一个任务")
except:
    print(f"{directory}\\{dbtype} 复制目录 Error")

try:
    shutil.copyfile(
        android_releaseapk, f"{copy_result_dir}\\{current_date}_{applicationId}_.apk"
    )
    # output-metadata.json
    shutil.copyfile(
        android_releaseapk_json,
        f"{copy_result_dir}\\{current_date}_{applicationId}__output-metadata.json",
    )
    print(f"{android_releaseapk} 复制完成，进行下一个任务")

except shutil.Error as e:
    print(f"shutil.Error 文件夹复制失败: {e}")
except OSError as e:
    print(f" OSError 文件夹复制失败: {e}")


try:
    shutil.copyfile(
        f"{android_release_aab}",
        f"{copy_result_dir}\\{current_date}_{applicationId}_.aab",
    )
    print(f"{android_release_aab} 复制完成，进行下一个任务")
except:
    print(f"{android_release_aab} 复制 error")

print(f" {script_dir}\\output")

# 执行打印签名信息输出到文件里，然后复制到上传到google driver

command = f'python {current_path}\\keystore_info.py "{keystore_file}" "{Pwd}"'
# 执行命令并获取输出
process = subprocess.Popen(
    command, stdout=subprocess.PIPE, stderr=subprocess.PIPE, shell=True
)
output, error = process.communicate()
output_str = output.decode("cp936")
print(
    "===================================== 签名信息： ============================================="
)
print("", output_str)
with open(
    f"{copy_result_dir}\\{applicationId}_keystoreinfo.txt", "w", encoding="UTF-8"
) as file:
    file.write(output_str)


# with open(f"{copy_result_dir}\\{applicationId}_ADInfo.txt", 'w', encoding='UTF-8') as file:
#     file.write(""+outputUrlInfomation)
with open(f"{copy_result_dir}\\25方案.txt", "w", encoding="UTF-8") as file:
    file.write("当前app集成方案为25方案")

print("PackageName:  ", applicationId)
print(f'安卓运行日志：   adb logcat | grep "Unity   :"')
print(f'安卓运行日志：   adb logcat | grep "{applicationId}"')

print(
    f"安卓运行Cmd：adb shell am start {applicationId}/com.unity3d.player.UnityPlayerActivity"
)
#  adb logcat | grep "Unity   :"
# subprocess.call(f"open {script_dir}\\output")
# 安装测试

print(
    "===================================== 安装测试 =============================================\n"
)
subprocess.call(
    f"adb install {copy_result_dir}\\{current_date}_{applicationId}_.apk", shell=True
)
subprocess.call(
    f"adb shell am start {applicationId}/com.unity3d.player.UnityPlayerActivity",
    shell=True,
)

print(
    RED
    + f"===================================== {applicationId} Python集成运行结束 ============================================="
    + RESET
)
