package com.example.texttospeech.library.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.example.texttospeech.library.entity.FontEntity;
import com.example.texttospeech.library.entity.HtmlBuilderEntity;
import com.example.texttospeech.library.module.HtmlBuilderModule;


public class EpubView extends WebView {

    public static final String TAG = "EPUB_VIEW";

    private OnHrefClickListener onHrefClickListener;
    private String baseUrl;
    private int fontSize;
    private FontEntity fontEntity;

    public EpubView(Context context) {
        super(context);
    }

    public EpubView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public EpubView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    public OnHrefClickListener getOnHrefClickListener() {
        return onHrefClickListener;
    }

    public void setOnHrefClickListener(OnHrefClickListener onHrefClickListener) {
        this.onHrefClickListener = onHrefClickListener;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public void setFontDefaultSize(int size) {
        this.fontSize = size;
    }

    public void setFontSize(int size) {
        this.fontSize = size;
        getSettings().setDefaultFontSize(size);
//            loadUrl("javascript:setFontSize('" + size + "px')");
    }

    public int getFontSize() {
        return fontSize;
    }

    public void setFont(FontEntity fontEntity) {
        this.fontEntity = fontEntity;
    }

    public FontEntity getFont() {
        return fontEntity;
    }

    public void setUp(String content) {
        setSetting();
        showLogs();
        setHyperLinkClickListener();

        String html;
        if (getFont() != null)
            html = generateContent(content, getFont().getUrl());
        else
            html = generateContent(content);

        loadDataWithBaseURL(getBaseUrl(), html, "text/html", "UTF-8", null);
    }

    private void setSetting() {
        getSettings().setJavaScriptEnabled(true);
        getSettings().setDefaultTextEncodingName("utf-8");
        getSettings().setDomStorageEnabled(true);
        getSettings().setAllowFileAccess(true);
    }

    private void showLogs() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setWebContentsDebuggingEnabled(true);
        }

        setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
                Log.i(TAG, "onConsoleMessage: " + consoleMessage.message());
                return super.onConsoleMessage(consoleMessage);
            }
        });
    }

    private void setHyperLinkClickListener() {
        setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if ((url.endsWith(".xhtml") || url.endsWith(".html")) && getOnHrefClickListener() != null) {
                    getOnHrefClickListener().onClick(url);
                    return true;
                } else
                    return super.shouldOverrideUrlLoading(view, url);
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
            }

        });
    }

    private String generateContent(String content, String fontFamily) {
        try {
            content = getContentWithoutBugs(content);
        } catch (Exception e) {
            e.printStackTrace();
            content = "404";
        }
        HtmlBuilderModule htmlBuilderModule = new HtmlBuilderModule();
        HtmlBuilderEntity entity = new HtmlBuilderEntity(
                "img{display: inline; height: auto; max-width: 100%;}",
                fontFamily,
                content
        );
        return htmlBuilderModule.getBaseContent(entity);
    }

    private String getContentWithoutBugs(String content) throws Exception {
        content = content.replaceAll("src=\"../", "src=\"" + getBaseUrl() + "");
        content = content.replaceAll("href=\"../", "href=\"" + getBaseUrl() + "");
        return content;
    }

    private String generateContent(String htmlPage) {
        return generateContent(htmlPage, "");
    }
}
