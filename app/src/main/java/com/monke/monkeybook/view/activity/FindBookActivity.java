//Copyright (c) 2017. 章钦豪. All rights reserved.
package com.monke.monkeybook.view.activity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.monke.monkeybook.R;
import com.monke.monkeybook.base.MBaseActivity;
import com.monke.monkeybook.bean.FindKindBean;
import com.monke.monkeybook.bean.FindKindGroupBean;
import com.monke.monkeybook.presenter.FindBookPresenterImpl;
import com.monke.monkeybook.presenter.contract.FindBookContract;
import com.monke.monkeybook.view.adapter.FindKindAdapter;
import com.monke.monkeybook.widget.AppCompat;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FindBookActivity extends MBaseActivity<FindBookContract.Presenter> implements FindBookContract.View {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.expandable_list)
    ExpandableListView expandableList;
    @BindView(R.id.tv_empty)
    TextView tvEmpty;

    private FindKindAdapter adapter;

    @Override
    protected FindBookContract.Presenter initInjector() {
        return new FindBookPresenterImpl();
    }

    @Override
    protected void onCreateActivity() {
        setContentView(R.layout.activity_expandable_list_vew);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        setupActionBar();

        initExpandableList();
    }

    @Override
    protected void initData() {
        mPresenter.initData();
    }

    private void initExpandableList() {
        adapter = new FindKindAdapter();
        expandableList.setAdapter(adapter);
        tvEmpty.setText(R.string.find_empty);
        expandableList.setEmptyView(tvEmpty);

        Drawable indicator = getResources().getDrawable(R.drawable.ic_group_expander);
        AppCompat.setTint(indicator, getResources().getColor(R.color.tv_text_default));
        expandableList.setGroupIndicator(indicator);
        //  设置分组项的点击监听事件
        expandableList.setOnGroupClickListener((parent, v, groupPosition, id) -> {
            // 请务必返回 false，否则分组不会展开
            if (!expandableList.isGroupExpanded(groupPosition)) {
                expandOnlyOne(groupPosition);
                expandableList.expandGroup(groupPosition);
                expandableList.post(() -> expandableList.setSelectedGroup(groupPosition));
            } else {
                expandableList.collapseGroup(groupPosition);
            }
            return true;
        });

        //  设置子选项点击监听事件
        expandableList.setOnChildClickListener((parent, v, groupPosition, childPosition, id) -> {
            FindKindBean kindBean = adapter.getDataList().get(groupPosition).getChildren().get(childPosition);
            Intent intent = new Intent(this, ChoiceBookActivity.class);
            intent.putExtra("url", kindBean.getKindUrl());
            intent.putExtra("title", kindBean.getKindName());
            intent.putExtra("tag", kindBean.getTag());
            startActivity(intent);
            return true;
        });

    }

    // 每次展开一个分组后，关闭其他的分组
    private boolean expandOnlyOne(int expandedPosition) {
        boolean result = true;
        int groupLength = expandableList.getExpandableListAdapter().getGroupCount();
        for (int i = 0; i < groupLength; i++) {
            if (i != expandedPosition && expandableList.isGroupExpanded(i)) {
                result &= expandableList.collapseGroup(i);
            }
        }
        expandableList.setSelectedGroup(expandedPosition);
        return result;
    }

    private boolean autoExpandGroup() {
        return getPreferences().getBoolean(getString(R.string.pk_find_expand_group), false);
    }

    //设置ToolBar
    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(R.string.find_on_www);
        }
    }

    // 添加菜单
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_library_activity, menu);
        return super.onCreateOptionsMenu(menu);
    }

    //菜单
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_search:
                //点击搜索
                startActivityByAnim(new Intent(this, SearchBookActivity.class), toolbar, "to_search");
                return true;
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void updateUI(List<FindKindGroupBean> group) {
        if (group.size() > 0) {
            adapter.resetDataS(group);
            if (autoExpandGroup() || group.size() == 1) {
                expandableList.expandGroup(0);
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (!autoExpandGroup() && adapter.getGroupCount() > 1) {
                for (int i = 0; i < adapter.getGroupCount(); i++) {
                    if (expandableList.isGroupExpanded(i)) {
                        expandableList.collapseGroup(i);
                        return true;
                    }
                }
            }
        }

        return super.onKeyDown(keyCode, event);
    }

}