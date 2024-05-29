/*
Taken from https://forum.unity.com/threads/uwp-get-swapchainpanel-grid-in-il2cpp-xaml-build.502176/
Used for Unity 2018 and 2019 IL2CPP builds
*/

#include <windows.ui.xaml.controls.h>
#include <wrl.h>
 
using namespace ABI::Windows::UI::Xaml;
using namespace ABI::Windows::UI::Xaml::Controls;
using namespace Microsoft::WRL;
 
extern "C" HRESULT __stdcall GetPageContent(IInspectable* frame, IUIElement** pageContent)
{
   *pageContent = nullptr;
 
   ComPtr<IContentControl> frameContentControl;
   auto hr = frame->QueryInterface(__uuidof(frameContentControl), &frameContentControl);
   if (FAILED(hr))
       return hr;
 
   ComPtr<IInspectable> frameContentInspectable;
   hr = frameContentControl->get_Content(&frameContentInspectable);
   if (FAILED(hr))
       return hr;
 
   if (frameContentInspectable == nullptr)
       return S_OK;
 
   ComPtr<IUserControl> frameContent;
   hr = frameContentInspectable.As(&frameContent);
   if (FAILED(hr))
       return hr;
 
   return frameContent->get_Content(pageContent);
}
