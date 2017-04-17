package com.example.jf.nestscrolling;

import android.view.View;

/**
 * Created by jf on 17-3-14.
 */

public interface IViewRefreshing {
    public void topDrag(int offset,int scroll);
    public void topRefresh();
    public void topFinished();


    public void botDrag(int ofset,int scroll);
    public void botRefresh();
    public void botFinished();

    public void topRefreshCancle();
    public void botRefreshCancle();

    public View createTopView();
    public View createBotView();

    public void reset();
}
