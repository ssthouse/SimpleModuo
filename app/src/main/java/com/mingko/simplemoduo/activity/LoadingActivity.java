package com.mingko.simplemoduo.activity;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;
import android.widget.ImageView;

import com.mingko.simplemoduo.R;

/**
 * App初始化进入的loading界面
 * 加载初始化动画, 默认1.5秒后跳转主界面
 * 接受事件{
 *     AppIntroFinishEvent: app介绍页面结束事件
 * }
 * Created by ssthouse on 2015/12/17.
 */
public class LoadingActivity extends AppCompatActivity {

    /**
     * 启动当前activity
     */
    public static void start(Context context) {
        Intent intent = new Intent(context, LoadingActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //全屏---不息屏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_loading);

        //启动动画
        final ImageView ivBg = (ImageView) findViewById(R.id.id_iv_bg);
        ValueAnimator animator = ObjectAnimator.ofFloat(ivBg, "scaleX", 1f, 1.3f);
        animator.setDuration(1500);
        ValueAnimator animator1 = ObjectAnimator.ofFloat(ivBg, "scaleY", 1f, 1.3f);
        final AnimatorSet animatorSet = new AnimatorSet();
        animator1.setDuration(1500);
        animatorSet.play(animator).with(animator1);
        animatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                jump2Main();
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
        animatorSet.start();
    }

    //跳转MainActivity
    private void jump2Main() {
        MainActivity.start(LoadingActivity.this);
        this.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }
}
