android下拉刷新，上拉加载控件，内置原生RecyclerView。
----
#依赖google对于NestedScrollingChild的默认实现，实现了NestedScrollingParent接口，可高度自定义刷新动画<br>

##xml布局<br>
    
    <LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
        android:orientation="vertical" android:layout_width="match_parent"
        android:layout_height="match_parent">

        <com.example.jf.nestscrolling.RefreshingView
            android:id="@+id/view_list_view"
            android:layout_width="match_parent"
            android:orientation="vertical"
            android:layout_height="match_parent">
        </com.example.jf.nestscrolling.RefreshingView>
    </LinearLayout>
    
    
##java<br>
###获取内置recyclerview，并设置adapter<br>

    viewList= (RefreshingView) this.findViewById(R.id.view_list_view);
    viewList.getListView().setAdapter(new AdapterList(dd));
    
###设置手势监听<br>

    viewList.setOnRefreshListener(new NestScrollParentView.OnRefreshListener() {
            @Override
            public void onTopRefresh(final NestScrollParentView view) {
                //下拉刷新操作
            }

            @Override
            public void onBotRefresh(final NestScrollParentView view) {
                //上拉加载操作
            }
        });
