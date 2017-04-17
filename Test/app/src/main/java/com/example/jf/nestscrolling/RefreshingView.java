package com.example.jf.nestscrolling;


import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.jf.test.R;
import com.nineoldandroids.animation.ValueAnimator;
import com.nineoldandroids.view.ViewHelper;

/**
 * Created by jf on 17-3-14.
 */

public class RefreshingView extends NestScrollParentView{

    ValueAnimator valueAnimator;
    ImageView arrowH;
    ImageView completeH;
    TextView noticeH;
    ProgressBar progressBarH;

    ImageView arrowF;
    TextView noticeF;
    ImageView completeF;
    ProgressBar progressBarF;

    boolean animaFlag;

    ValueAnimator.AnimatorUpdateListener rotateH=new ValueAnimator.AnimatorUpdateListener() {
        @Override
        public void onAnimationUpdate(ValueAnimator valueAnimator) {
            float value= (float) valueAnimator.getAnimatedValue();
            ViewHelper.setRotation(arrowH,value*180);
        }
    };

    ValueAnimator.AnimatorUpdateListener alphaH=new ValueAnimator.AnimatorUpdateListener() {
        @Override
        public void onAnimationUpdate(ValueAnimator valueAnimator) {
            float value= (float) valueAnimator.getAnimatedValue();
            ViewHelper.setAlpha(arrowH,1-value);
            ViewHelper.setAlpha(progressBarH,value);
        }
    };

    ValueAnimator.AnimatorUpdateListener rotateF=new ValueAnimator.AnimatorUpdateListener() {
        @Override
        public void onAnimationUpdate(ValueAnimator valueAnimator) {
            float value= (float) valueAnimator.getAnimatedValue();
            ViewHelper.setRotation(arrowF,(1-value)*180);
        }
    };

    ValueAnimator.AnimatorUpdateListener alphaF=new ValueAnimator.AnimatorUpdateListener() {
        @Override
        public void onAnimationUpdate(ValueAnimator valueAnimator) {
            float value= (float) valueAnimator.getAnimatedValue();
            ViewHelper.setAlpha(arrowF,1-value);
            ViewHelper.setAlpha(progressBarF,value);
        }
    };

    public RefreshingView(Context context) {
        super(context);
        valueAnimator=ValueAnimator.ofFloat(0,1);
        valueAnimator.setDuration(200);
    }

    public RefreshingView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        valueAnimator=ValueAnimator.ofFloat(0,1);
        valueAnimator.setDuration(200);
    }

    public RefreshingView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        valueAnimator=ValueAnimator.ofFloat(0,1);
        valueAnimator.setDuration(200);
    }

    @Override
    public void topDrag(int offset, int scroll) {
        Log.e("eee",scroll+","+animaFlag);
        if (Math.abs(scroll)>getTopHeight()){
            if (!animaFlag){
                animaFlag=true;
                //go anima
                valueAnimator.removeAllUpdateListeners();
                valueAnimator.addUpdateListener(rotateH);
                noticeH.setText("松开刷新");
                valueAnimator.start();
            }
        }
    }

    @Override
    public void topRefresh() {
        valueAnimator.removeAllUpdateListeners();
        valueAnimator.addUpdateListener(alphaH);
        noticeH.setText("正在刷新");
        valueAnimator.start();
    }

    @Override
    public void topFinished() {
        animaFlag=false;
        ViewHelper.setAlpha(arrowH,0);
        ViewHelper.setAlpha(progressBarH,0);
        ViewHelper.setRotation(arrowH,0);
        ViewHelper.setAlpha(completeH,1);
        noticeH.setText("刷新完成");
    }

    @Override
    public void botDrag(int offset, int scroll) {
        if (Math.abs(scroll)>getTopHeight()){
            if (!animaFlag){
                animaFlag=true;
                //go anima
                valueAnimator.removeAllUpdateListeners();
                valueAnimator.addUpdateListener(rotateF);
                noticeF.setText("松开加载");
                valueAnimator.start();
            }
        }
    }

    @Override
    public void botRefresh() {
        valueAnimator.removeAllUpdateListeners();
        valueAnimator.addUpdateListener(alphaF);
        noticeF.setText("正在加载");
        valueAnimator.start();
    }

    @Override
    public void botFinished() {
        animaFlag=false;
        ViewHelper.setAlpha(arrowF,0);
        ViewHelper.setAlpha(progressBarF,0);
        ViewHelper.setRotation(arrowF,0);
        ViewHelper.setAlpha(completeF,1);
        noticeF.setText("加载完成");
    }

    @Override
    public void topRefreshCancle() {
        animaFlag=false;
    }

    @Override
    public void botRefreshCancle() {
        animaFlag=false;
    }

    @Override
    public View createTopView() {
        View hd= LayoutInflater.from(getContext()).inflate(R.layout.item_refresh_header,null);
        arrowH= (ImageView) hd.findViewById(R.id.ic_pd);
        noticeH= (TextView) hd.findViewById(R.id.tv_pd);
        progressBarH= (ProgressBar) hd.findViewById(R.id.progress_pd);
        completeH= (ImageView) hd.findViewById(R.id.iv_complete);

        ViewHelper.setAlpha(progressBarH,0);
        ViewHelper.setAlpha(completeH,0);
        ViewHelper.setAlpha(arrowH,1);
        ViewHelper.setRotation(arrowH,0);
        noticeH.setText("下拉刷新");
        return hd;
    }

    @Override
    public View createBotView() {
        View ft= LayoutInflater.from(getContext()).inflate(R.layout.item_refresh_header,null);
        arrowF= (ImageView) ft.findViewById(R.id.ic_pd);
        noticeF= (TextView) ft.findViewById(R.id.tv_pd);
        progressBarF= (ProgressBar) ft.findViewById(R.id.progress_pd);
        completeF= (ImageView) ft.findViewById(R.id.iv_complete);

        ViewHelper.setAlpha(progressBarF,0);
        ViewHelper.setAlpha(completeF,0);
        ViewHelper.setAlpha(arrowF,1);
        ViewHelper.setRotation(arrowF,180);
        noticeF.setText("上拉加载");
        return ft;
    }

    @Override
    public void reset() {

            ViewHelper.setAlpha(progressBarH,0);
            ViewHelper.setAlpha(arrowH,1);
            ViewHelper.setAlpha(completeH,0);
            ViewHelper.setRotation(arrowH,0);
            noticeH.setText("下拉刷新");

            ViewHelper.setAlpha(progressBarF,0);
            ViewHelper.setAlpha(arrowF,1);
            ViewHelper.setAlpha(completeF,0);
            ViewHelper.setRotation(arrowF,180);
            noticeF.setText("上拉加载");
    }

}
