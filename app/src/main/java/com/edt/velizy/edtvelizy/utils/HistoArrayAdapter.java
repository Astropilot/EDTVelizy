package com.edt.velizy.edtvelizy.utils;

import android.support.annotation.NonNull;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.edt.velizy.edtvelizy.R;
import com.edt.velizy.edtvelizy.fragments.HistoriqueFragment;

import java.util.List;


public class HistoArrayAdapter extends ArrayAdapter<SpannableString> {

    private final List<SpannableString> list;
    private final HistoriqueFragment context;

    private static class ViewHolder {
        protected TextView description;
    }

    public HistoArrayAdapter(HistoriqueFragment context, List<SpannableString> list) {
        super(context.getActivity(), R.layout.histolist_item, list);
        this.context = context;
        this.list = list;
    }

    @Override
    @NonNull
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;

        if (convertView == null) {
            LayoutInflater inflator = context.getActivity().getLayoutInflater();
            view = inflator.inflate(R.layout.histolist_item, null);
            final ViewHolder viewHolder = new ViewHolder();
            viewHolder.description = (TextView) view.findViewById(R.id.description);
            view.setTag(viewHolder);
        } else {
            view = convertView;
        }

        ViewHolder holder = (ViewHolder) view.getTag();
        holder.description.setText(list.get(position));
        return view;
    }
}
