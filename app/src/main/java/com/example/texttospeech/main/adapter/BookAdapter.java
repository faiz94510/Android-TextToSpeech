package com.example.texttospeech.main.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;


import com.example.texttospeech.R;
import com.example.texttospeech.library.entity.FontEntity;
import com.example.texttospeech.library.util.EpubUtil;
import com.example.texttospeech.library.view.EpubView;
import com.example.texttospeech.library.view.OnHrefClickListener;

import java.util.List;


public class BookAdapter extends RecyclerView.Adapter<BookAdapter.MyViewHolder> {

    private List<String> data;
    private OnHrefClickListener onHrefClickListener;
    private String baseUrl;
    private FontEntity fontEntity;
    private int fontSize = -1;

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public EpubView epubView;

        public MyViewHolder(View v) {
            super(v);
            epubView = v.findViewById(R.id.epub_view);
        }

        public void bind(String content) {
            if (fontSize != -1)
                epubView.setFontSize(fontSize);
            if (fontEntity != null)
                epubView.setFont(fontEntity);
            epubView.setBaseUrl(baseUrl);
            if (onHrefClickListener != null)
                epubView.setOnHrefClickListener(onHrefClickListener);
            epubView.setUp(content);
        }
    }

    public BookAdapter(List<String> data, String baseUrl) {
        this.data = data;
        this.baseUrl = baseUrl;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_book, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        String content = "Error";
        try {
            content = EpubUtil.getHtmlContent(data.get(position));

        } catch (Exception e) {
            e.printStackTrace();
        }
        holder.bind(content);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }


    public OnHrefClickListener getOnHrefClickListener() {
        return onHrefClickListener;
    }

    public void setOnHrefClickListener(OnHrefClickListener onHrefClickListener) {
        this.onHrefClickListener = onHrefClickListener;
    }

    public FontEntity getFontEntity() {
        return fontEntity;
    }

    public void setFontEntity(FontEntity fontEntity) {
        this.fontEntity = fontEntity;
    }

    public int getFontSize() {
        return fontSize;
    }

    public void setFontSize(int fontSize) {
        this.fontSize = fontSize;
    }
}
