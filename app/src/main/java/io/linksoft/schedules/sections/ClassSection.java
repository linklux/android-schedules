package io.linksoft.schedules.sections;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.List;

import io.github.luizgrp.sectionedrecyclerviewadapter.StatelessSection;
import io.linksoft.schedules.R;
import io.linksoft.schedules.data.Class;
import io.linksoft.schedules.util.DateUtil;
import io.linksoft.schedules.viewholders.ClassHeaderViewHolder;
import io.linksoft.schedules.viewholders.ClassItemViewHolder;

public class ClassSection extends StatelessSection {

    private String title;
    private List<Class> classes;

    public ClassSection(String title, List<Class> list) {
        super(R.layout.section_class_header, R.layout.section_class_item);

        this.title = title;
        this.classes = list;
    }

    @Override
    public int getContentItemsTotal() {
        return classes.size();
    }

    @Override
    public RecyclerView.ViewHolder getItemViewHolder(View view) {
        return new ClassItemViewHolder(view);
    }

    @Override
    public void onBindItemViewHolder(RecyclerView.ViewHolder holder, int position) {
        final ClassItemViewHolder itemHolder = (ClassItemViewHolder) holder;

        Class cls = classes.get(position);

        itemHolder.timeFrame.setText(DateUtil.getClassTimeframe(cls.getTimeStart(), cls.getTimeEnd()));
        itemHolder.location.setText(cls.getLocation());
        itemHolder.name.setText(String.format("%s - %s", cls.getClassName(), cls.getComments()));
    }

    @Override
    public RecyclerView.ViewHolder getHeaderViewHolder(View view) {
        return new ClassHeaderViewHolder(view);
    }

    @Override
    public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder) {
        final ClassHeaderViewHolder headerHolder = (ClassHeaderViewHolder) holder;

        headerHolder.title.setText(title);
    }

}