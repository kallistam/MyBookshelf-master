package com.monke.monkeybook.widget.modialog;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;

import com.monke.basemvplib.BaseActivity;
import com.monke.monkeybook.R;
import com.monke.monkeybook.bean.BookInfoBean;
import com.monke.monkeybook.bean.BookmarkBean;
import com.monke.monkeybook.bean.ReplaceRuleBean;

/**
 * 对话框
 */
public class MoDialogHUD {
    private Boolean isFinishing = false;

    private Context context;
    private ViewGroup decorView;//activity的根View
    private ViewGroup rootView;// mSharedView 的 根View
    private MoDialogView mSharedView;

    private Animation inAnim;
    private Animation outAnim;

    private MoDialogView.OnDismissListener onDismissListener;

    private Boolean canBack = false;
    private Animation.AnimationListener outAnimListener = new Animation.AnimationListener() {
        @Override
        public void onAnimationStart(Animation animation) {
            isFinishing = true;
        }

        @Override
        public void onAnimationEnd(Animation animation) {
            dismissImmediately();
        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    };

    public MoDialogHUD(Context context) {
        this.context = context;
        initViews();
        initCenter();
        initAnimation();
    }

    private void initAnimation() {
        inAnim = getInAnimation();
        outAnim = getOutAnimation();
    }

    private void initFromTopRight() {
        inAnim = AnimationUtils.loadAnimation(context, R.anim.moprogress_in_top_right);
        outAnim = AnimationUtils.loadAnimation(context, R.anim.moprogress_out_top_right);
    }

    private void initFromBottomRight() {
        inAnim = AnimationUtils.loadAnimation(context, R.anim.moprogress_in_bottom_right);
        outAnim = AnimationUtils.loadAnimation(context, R.anim.moprogress_out_bottom_right);
    }

    private void initFromBottomAnimation() {
        inAnim = AnimationUtils.loadAnimation(context, R.anim.moprogress_bottom_in);
        outAnim = AnimationUtils.loadAnimation(context, R.anim.moprogress_bottom_out);
    }

    private void initCenter() {
        if (mSharedView != null) {
            mSharedView.setGravity(Gravity.CENTER);
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) mSharedView.getLayoutParams();
            if (layoutParams != null) {
                layoutParams.setMargins(0, 0, 0, 0);
                mSharedView.setLayoutParams(layoutParams);
            }
            mSharedView.setPadding(0, 0, 0, 0);
        }
    }

    private void initBottom() {
        if (mSharedView != null) {
            mSharedView.setGravity(Gravity.BOTTOM);
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) mSharedView.getLayoutParams();
            if (layoutParams != null) {
                layoutParams.gravity = Gravity.BOTTOM;
                layoutParams.setMargins(0, 0, 0, 0);
                mSharedView.setLayoutParams(layoutParams);
            }
            mSharedView.setPadding(0, 0, 0, 0);
        }
    }

    private void initMarRightTop() {
        if (mSharedView != null) {
            mSharedView.setGravity(Gravity.TOP);
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) mSharedView.getLayoutParams();
            if (layoutParams != null) {
                layoutParams.setMargins(0, 0, 0, 0);
                mSharedView.setLayoutParams(layoutParams);
            }
            mSharedView.setPadding(0, 0, 0, 0);
        }
    }

    private void initViews() {
        decorView = ((Activity) context).getWindow().getDecorView().findViewById(android.R.id.content);
        rootView = new FrameLayout(context);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
        );
        rootView.setLayoutParams(layoutParams);
        rootView.setClickable(true);
        rootView.setBackgroundColor(context.getResources().getColor(R.color.btn_bg_press_tp));

        mSharedView = new MoDialogView(context);
        mSharedView.onAttach(this);
    }

    private Animation getInAnimation() {
        return AnimationUtils.loadAnimation(context, R.anim.moprogress_in);
    }

    private Animation getOutAnimation() {
        return AnimationUtils.loadAnimation(context, R.anim.moprogress_out);
    }

    private boolean isShowing() {
        return rootView.getParent() != null;
    }

    private void onAttached() {
        decorView.addView(rootView);
        if (mSharedView.getParent() != null)
            ((ViewGroup) mSharedView.getParent()).removeView(mSharedView);

        rootView.addView(mSharedView);

        isFinishing = false;
    }

    public void dismiss() {
        //消失动画
        if (mSharedView != null && rootView != null && mSharedView.getParent() != null) {
            hideIMM(rootView);
            if (!isFinishing) {
                new Handler().post(() -> {
                    outAnim.setAnimationListener(outAnimListener);
                    mSharedView.getChildAt(0).startAnimation(outAnim);
                });
            }
        }
    }

    public boolean isShown() {
        return (mSharedView != null && mSharedView.getParent() != null);
    }

    private void dismissImmediately() {
        if (mSharedView != null && rootView != null && mSharedView.getParent() != null) {
            new Handler().post(() -> {
                rootView.removeView(mSharedView);
                decorView.removeView(rootView);
                if (onDismissListener != null) {
                    onDismissListener.onDismiss();
                }
            });
        }
        isFinishing = false;
    }

    /**
     * 返回键事件
     */
    public Boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (isShowing()) {
                if (canBack) {
                    dismiss();
                }
                return true;
            }
        }
        return false;
    }

    //隐藏输入法
    private void hideIMM(View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    /**
     * 加载动画
     */
    public void showLoading(String msg) {
        initCenter();
        initAnimation();
        canBack = false;
        rootView.setOnClickListener(null);
        if (!isShowing()) {
            onAttached();
        }
        mSharedView.showLoading(msg);
        mSharedView.getChildAt(0).startAnimation(inAnim);
    }

    /**
     * 单个按钮的提示信息
     */
    public void showInfo(String msg) {
        initCenter();
        initAnimation();
        canBack = true;
        rootView.setOnClickListener(v -> dismiss());
        mSharedView.showInfo(msg, v -> dismiss());
        if (!isShowing()) {
            onAttached();
        }
        mSharedView.getChildAt(0).startAnimation(inAnim);
    }

    /**
     * 单个按钮的提示信息
     */
    public void showInfo(String msg, String btnText, View.OnClickListener listener) {
        initCenter();
        initAnimation();
        canBack = true;
        rootView.setOnClickListener(v -> dismiss());
        mSharedView.showInfo(msg, btnText, listener);
        if (!isShowing()) {
            onAttached();
        }
        mSharedView.getChildAt(0).startAnimation(inAnim);
    }

    /**
     * 两个不同等级的按钮
     */
    public void showTwoButton(String msg, String b_s, View.OnClickListener c_s, String b_f, View.OnClickListener c_f) {
        initCenter();
        initAnimation();
        canBack = true;
        rootView.setOnClickListener(v -> dismiss());
        mSharedView.showTwoButton(msg, b_f, c_f, b_s, c_s);
        if (!isShowing()) {
            onAttached();
        }
        mSharedView.getChildAt(0).startAnimation(inAnim);
    }

    /**
     * 显示一段文本
     */
    public void showText(String text) {
        initCenter();
        initAnimation();
        canBack = true;
        rootView.setOnClickListener(v -> dismiss());
        mSharedView.showText(text);
        if (!isShowing()) {
            onAttached();
        }
        mSharedView.getChildAt(0).startAnimation(inAnim);
    }

    /**
     * 显示asset Markdown
     */
    public void showAssetMarkdown(String assetFileName) {
        initCenter();
        initAnimation();
        canBack = true;
        rootView.setOnClickListener(v -> dismiss());
        mSharedView.showAssetMarkdown(assetFileName);
        if (!isShowing()) {
            onAttached();
        }
        mSharedView.getChildAt(0).startAnimation(inAnim);
    }

    /**
     * 离线下载
     */
    public void showDownloadList(int startIndex, int endIndex, int all, DownLoadView.OnClickDownload clickDownload) {
        initCenter();
        initAnimation();
        canBack = true;
        rootView.setOnClickListener(v -> dismiss());
        DownLoadView.newInstance(mSharedView)
                .showDownloadList(startIndex, endIndex, all, clickDownload, v -> dismiss());
        if (!isShowing()) {
            onAttached();
        }
        mSharedView.getChildAt(0).startAnimation(inAnim);
    }

    /**
     * 换源
     */
    public void showChangeSource(BaseActivity activity, BookInfoBean bookInfo, ChangeSourceView.OnClickSource clickSource) {
        initCenter();
        initAnimation();
        canBack = true;
        rootView.setOnClickListener(v -> dismiss());
        ChangeSourceView.newInstance(activity, mSharedView)
                .showChangeSource(bookInfo, clickSource);
        if (!isShowing()) {
            onAttached();
        }
        mSharedView.getChildAt(0).startAnimation(inAnim);
    }

    /**
     * 弹出输入框
     */
    public void showInputBox(String title, String defaultValue, String[] adapterValues, InputView.OnInputOk onInputOk) {
        initCenter();
        initAnimation();
        canBack = true;
        rootView.setOnClickListener(v -> dismiss());
        InputView.newInstance(mSharedView)
                .showInputView(onInputOk, title, defaultValue, adapterValues);
        if (!isShowing()) {
            onAttached();
        }
        mSharedView.getChildAt(0).startAnimation(inAnim);
    }

    /**
     * 编辑替换规则
     */
    public void showPutReplaceRule(ReplaceRuleBean replaceRuleBean, EditReplaceRuleView.OnSaveReplaceRule onSaveReplaceRule) {
        initCenter();
        initAnimation();
        canBack = true;
        rootView.setOnClickListener(v -> dismiss());
        EditReplaceRuleView.newInstance(mSharedView)
                .showEditReplaceRule(replaceRuleBean, onSaveReplaceRule);
        if (!isShowing()) {
            onAttached();
        }
        mSharedView.getChildAt(0).startAnimation(inAnim);
    }

    /**
     * 书签
     */
    public void showBookmark(BookmarkBean bookmarkBean, boolean isAdd, EditBookmarkView.OnBookmarkClick bookmarkClick) {
        initCenter();
        initAnimation();
        canBack = true;
        rootView.setOnClickListener(v -> dismiss());
        EditBookmarkView.newInstance(mSharedView)
                .showBookmark(bookmarkBean, isAdd, bookmarkClick);
        if (!isShowing()) {
            onAttached();
        }
        mSharedView.getChildAt(0).startAnimation(inAnim);
    }

    public void setOnDimissListener(MoDialogView.OnDismissListener dimissListener) {
        this.onDismissListener = dimissListener;
    }
}