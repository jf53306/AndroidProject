package com.example.jf.nestscrolling;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.annotation.Px;
import android.support.v4.view.NestedScrollingParent;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewConfigurationCompat;
import android.support.v4.view.animation.FastOutLinearInInterpolator;
import android.support.v4.widget.ScrollerCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Scroller;

/**
 * Created by jf on 17-3-13.
 *
 */

public abstract class NestScrollParentView extends LinearLayout implements NestedScrollingParent,IViewRefreshing{

    public static final int ST_TOP_SHOW_REFRESHING=10;
    public static final int ST_TOP_SHOW_DRAG=11;
    public static final int ST_BOT_SHOW_REFRESHING=12;
    public static final int ST_BOT_SHOW_DRAG=13;

    public static final int ST_NORMAL=0;
//    public static final int ST_BOT_SHOW=1;
//    public static final int ST_TOP_SHOW=2;
    private ScrollerCompat mScroller;

    private int topHeight=200;
    private int st=ST_NORMAL;
    private int fingerAction;

    private FrameLayout topView;
    private FrameLayout botView;
    private RecyclerView listView;

    private OnRefreshListener refreshListener;

    public NestScrollParentView(Context context) {
        super(context);
        init();
    }

    public NestScrollParentView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public NestScrollParentView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }


    private void init(){
        setOrientation(LinearLayout.VERTICAL);
        mScroller=ScrollerCompat.create(getContext(),new Interpolator() {
            @Override
            public float getInterpolation(float t) {
                t -= 1.0f;
                return t * t * t * t * t + 1.0f;
            }
        });

        topView=new FrameLayout(getContext());
        LayoutParams topparams=new LayoutParams(LayoutParams.MATCH_PARENT,topHeight);
        topparams.setMargins(0,-topHeight,0,0);
        addView(topView,topparams);

        listView=new RecyclerView(getContext());
        listView.setLayoutManager(new LinearLayoutManager(getContext()));
        addView(listView,new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT));

        botView=new FrameLayout(getContext());
        LayoutParams botParams=new LayoutParams(LayoutParams.MATCH_PARENT,topHeight);
        addView(botView,botParams);

        final View topChild=createTopView();
        if (topChild!=null){
            topView.addView(topChild,new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,topHeight));
        }

        final View botChild=createBotView();
        if (botChild!=null){
            botView.addView(botChild,new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,topHeight));
        }
    }

    public int getTopHeight(){
        return topHeight;
    }

    public RecyclerView getListView(){
        return listView;
    }

    @Override
    public void computeScroll() {

        if (mScroller.computeScrollOffset()){
            scrollTo(mScroller.getCurrX(),mScroller.getCurrY());
            postInvalidate();
        }

//        super.computeScroll();
    }



    @Override
    public void scrollTo(@Px int x, @Px int y) {
        int limiy=y;
        switch (st){
            case ST_NORMAL:
                break;
            case ST_TOP_SHOW_DRAG:
            case ST_TOP_SHOW_REFRESHING:
                if (y>0){
                    limiy=0;
                    st=ST_NORMAL;
                    topRefreshCancle();
                }
                break;
            case ST_BOT_SHOW_DRAG:
            case ST_BOT_SHOW_REFRESHING:
                if (y<0){
                    limiy=0;
                    st=ST_NORMAL;
                    botRefreshCancle();
                }
                break;
        }
        if (limiy==0){
            st=ST_NORMAL;
            mScroller.abortAnimation();
            reset();
        }
        super.scrollTo(x, limiy);
    }

    public boolean isTopShow(){
        return st==ST_TOP_SHOW_DRAG|st==ST_TOP_SHOW_REFRESHING;
    }
    public boolean isBotShow(){
        return st==ST_BOT_SHOW_DRAG|st==ST_BOT_SHOW_REFRESHING;
    }

    public boolean isRefreshing(){
        return st==ST_TOP_SHOW_REFRESHING|st==ST_BOT_SHOW_REFRESHING;
    }

    private int getRangeValue(int value,int min,int max){
        return Math.max(min,Math.min(max,value));
    }

    private void smoothScroll(int dy,int vy){
        mScroller.startScroll(0,getScrollY(),0,dy,computeScrollDuration(0,dy,0,vy));
        invalidate();
    }

    private float getFriction(){
        final float offset=Math.abs(getScrollY());
        final float maxOffset=getHeight();
        final float friction=1-Math.max(0,Math.min(offset/maxOffset,1));
        final float result=friction/2f;
//        Log.e("friction","fff:"+result);
        return result;
    }

    private void smoothScrollBack(){
        mScroller.startScroll(0,getScrollY(),0,-getScrollY(),600);
        invalidate();
    }
    private void smoothScroll(int dy){
        mScroller.startScroll(0,getScrollY(),0,dy,400);
        invalidate();
    }

    private void fling(int velocityY){
        mScroller.fling(0,getScrollY(),0,  velocityY,0,0,Integer.MIN_VALUE,Integer.MAX_VALUE);
        invalidate();
    }

    public void finishRefresh(){
        if (!mScroller.isFinished()){
            mScroller.abortAnimation();
        }
        if (isTopShow()){
            topFinished();
        }else {
            botFinished();
        }
        smoothScrollBack();
    }

    private float distanceInfluenceForSnapDuration(float f) {
        f -= 0.5f; // center the values about 0.
        f *= 0.3f * Math.PI / 2.0f;
        return (float) Math.sin(f);
    }

    private int computeScrollDuration(int dx, int dy, int vx, int vy) {
        final int absDx = Math.abs(dx);
        final int absDy = Math.abs(dy);
        final boolean horizontal = absDx > absDy;
        final int velocity = (int) Math.sqrt(vx * vx + vy * vy);
        final int delta = (int) Math.sqrt(dx * dx + dy * dy);
        final int containerSize = horizontal ? getWidth() : getHeight();
        final int halfContainerSize = containerSize / 2;
        final float distanceRatio = Math.min(1.f, 1.f * delta / containerSize);
        final float distance = halfContainerSize + halfContainerSize *
                distanceInfluenceForSnapDuration(distanceRatio);

        final int duration;
        if (velocity > 0) {
            duration = 4 * Math.round(1000 * Math.abs(distance / velocity));
        } else {
            float absDelta = (float) (horizontal ? absDx : absDy);
            duration = (int) (((absDelta / containerSize) + 1) * 300);
        }
        return Math.min(duration, 1000);
    }

    //nestedscrolling
    //allow vertical scroll only


    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        fingerAction=ev.getAction();
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onStartNestedScroll(View child, View target, int nestedScrollAxes) {
        return (nestedScrollAxes& ViewCompat.SCROLL_AXIS_VERTICAL)!=0;
    }

    @Override
    public void onNestedScrollAccepted(View child, View target, int nestedScrollAxes) {

    }

    //call when touch down and up
    @Override
    public void onStopNestedScroll(View target) {
//        if (fingerAction==MotionEvent.ACTION_DOWN){
//            reset();
//            return;
//        }
        if (st==ST_NORMAL){
            return;
        }


        final int scrollY=getScrollY();
        if (scrollY==0){
            return;
        }
        //is cancle by fling
        if (mScroller.isFinished()){
            final boolean toRefresh=Math.abs(scrollY)>topHeight;
            if (toRefresh){
                final int dy=isTopShow()?-getScrollY()-topHeight:topHeight-getScrollY();
                smoothScroll(dy);
                if (st==ST_TOP_SHOW_DRAG){
                    topRefresh();
                    if (refreshListener!=null){
                        refreshListener.onTopRefresh(this);
                    }
                }else if (st==ST_BOT_SHOW_DRAG){
                    botRefresh();
                    if (refreshListener!=null){
                        refreshListener.onBotRefresh(this);
                    }
                }

                st=isTopShow()?ST_TOP_SHOW_REFRESHING:ST_BOT_SHOW_REFRESHING;
            }else {
                //action cancle
                smoothScrollBack();
            }

            return;
        }
    }

    @Override
    public void onNestedScroll(View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {


    }

    //ViewCompat.canScrollVertically(target, direction)  direction:-1 scroll down,1 scroll up.
    @Override
    public void onNestedPreScroll(View target, int dx, int dy, int[] consumed) {
        final boolean showTopView=!ViewCompat.canScrollVertically(target, -1)&&(dy<0||getScrollY()<0);
        final boolean showBotView=!ViewCompat.canScrollVertically(target, 1)&&(getScrollY()>0||dy>0);
        int offset=dy;
        if (showTopView){
            if (!isTopShow()){
                st=ST_TOP_SHOW_DRAG;
            }

            //only pull down we shuold set a friction,or,pull up will bug
            if (dy<0){
                offset=(int)(getFriction()*dy);
            }
            scrollBy(0,offset);
            consumed[1]=offset;   //if dy ,the dy-offset will come back and the friction will no useful
            topDrag(offset,getScrollY());
        }else if (showBotView){
            if (!isBotShow()){
                st=ST_BOT_SHOW_DRAG;
            }

            //only pull up we should set a friction,or,pull down will bug
            if (dy>0){
                offset= (int) (getFriction()*dy);
            }
            scrollBy(0, offset);
            consumed[1]=offset;
            botDrag(offset,getScrollY());
        }
    }

    @Override
    public boolean onNestedFling(View target, float velocityX, float velocityY, boolean consumed) {
//        Log.e("consumed",":"+consumed);
        return false;
    }

    //call when the we fling the childview
    @Override
    public boolean onNestedPreFling(View target, float velocityX, float velocityY) {
        //when on refresh,we must eat the fling event,so that the recycler view do not scroll the content;
        if (isTopShow()||isBotShow()){
            final boolean topFling=velocityY>2000&&isTopShow();
            final boolean botFling=velocityY<-2000&&isBotShow();
            if (topFling){
                fling((int) Math.max(velocityY,5000));
//                topRefreshCancle();
            }else if (botFling){
                fling((int) Math.min(velocityY,-5000));
//                botRefreshCancle();
            };
            return true;
        }

        return false;
    }

    @Override
    public int getNestedScrollAxes() {
        return 0;
    }





//    //deal ui
//    @Override
//    public void topDrag(int dy, int offset) {
//
//    }
//
//    @Override
//    public void topRefresh() {
//
//    }
//
//    @Override
//    public void topFinished() {
//
//    }
//
//    @Override
//    public void botDrag(int dy, int offset) {
//
//    }
//
//    @Override
//    public void botRefresh() {
//
//    }
//
//    @Override
//    public void botFinished() {
//
//    }
//
//    @Override
//    public void topRefreshCancle() {
//
//    }
//
//    @Override
//    public void botRefreshCancle() {
//
//    }

    public void setOnRefreshListener(OnRefreshListener refreshListener){
        this.refreshListener=refreshListener;
    }

    public interface OnRefreshListener{
        public void onTopRefresh(NestScrollParentView view);
        public void onBotRefresh(NestScrollParentView view);
    }
}
