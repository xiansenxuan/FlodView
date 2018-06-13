package com.xuan.foldview;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.ListViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ListView;

/**
 * com.xuan.foldview
 *
 * @author by xuan on 2018/5/27
 * @version [版本号, 2018/5/27]
 * @update by xuan on 2018/5/27
 * @descript
 */
public class VerticalFoldListView extends FrameLayout {

    //拖动辅助类
    private ViewDragHelper dragHelper;

    private View firstChild;
    private View secondChild;

    private int firstChildHeight;

    //first是否完全可见
    private boolean menuIsOpen=false;

    public VerticalFoldListView(@NonNull Context context) {
        this(context,null);
    }

    public VerticalFoldListView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }


    public VerticalFoldListView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        dragHelper=ViewDragHelper.create(this,dragHelperCallBack);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        if(changed)
        firstChildHeight=firstChild.getMeasuredHeight();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //可以回调onTouchEvent实现触摸拖动
        dragHelper.processTouchEvent(event);
        return true;
    }

    private ViewDragHelper.Callback dragHelperCallBack=new ViewDragHelper.Callback() {
        @Override
        public boolean tryCaptureView(@NonNull View child, int pointerId) {
            Log.i("TAG","pointerId " + pointerId);
            //指定子view是否可以拖动
//            if(child==firstChild){
//                return false;
//            }else if(child==secondChild){
//                return true;
//            }else{
//                return false;
//            }
            return child==secondChild;
        }

        @Override//垂直拖动移动的坐标
        public int clampViewPositionVertical(@NonNull View child, int top, int dy) {
            if(top<0){
                return 0;
            }else if(top>firstChildHeight){
                return firstChildHeight;
            }
            return top;
        }

//        @Override//水平拖动移动的坐标
//        public int clampViewPositionHorizontal(@NonNull View child, int left, int dx) {
//            return left;
//        }


        @Override//手指松开 xvel、yvel为离开屏幕时的速率
        public void onViewReleased(@NonNull View releasedChild, float xvel, float yvel) {
            //滚动到first的一半高度以上 就完全打开
            if(releasedChild.getTop()>firstChildHeight/2){
                //打开菜单 设置滚动到距离top first的高度 就是打开
                dragHelper.settleCapturedViewAt(0,firstChildHeight);
                menuIsOpen=true;
            }else{
                //关闭菜单 设置滚动到 距离top 0的位置 就是关闭
                dragHelper.settleCapturedViewAt(0,0);
                menuIsOpen=false;
            }

            //用，smoothSlideViewTo、settleCapturedViewAt、flingCapturedView，
            //动画移动会回调continueSettling(boolean)方法，
            //在内部是用的ScrollerCompat来实现滑动的。
            //在computeScroll方法中判断continueSettling(boolean)的返回值，来动态刷新界面
            invalidate();
        }

    };

    // ListView可以滑动 菜单的ViewDragHelper无效

    /*@Override
    public void requestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        if (disallowIntercept == ((mGroupFlags & FLAG_DISALLOW_INTERCEPT) != 0)) {
            // We're already in this state, assume our ancestors are too
            return;
        }

        // 改变了mGroupFlags的值相当于改变了
            public boolean dispatchTouchEvent(MotionEvent ev) {
                final boolean disallowIntercept = (mGroupFlags & FLAG_DISALLOW_INTERCEPT) != 0;
                if (!disallowIntercept) {
                    intercepted = onInterceptTouchEvent(ev);这里就不会调用
                    ev.setAction(action); // restore action in case it was changed
                } else {
                    intercepted = false;
                }
            }
            ListView -
                if (parent != null) {
                    parent.requestDisallowInterceptTouchEvent(true);
                }

        if (disallowIntercept) {
            mGroupFlags |= FLAG_DISALLOW_INTERCEPT;
        } else {
            mGroupFlags &= ~FLAG_DISALLOW_INTERCEPT;
        }

        // Pass it up to our parent
        if (mParent != null) {
            mParent.requestDisallowInterceptTouchEvent(disallowIntercept);
        }

        //向下滑动 VerticalFoldListView拦截ListView的滑动事件
    }*/

    float moveY=0;
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if(menuIsOpen){
            return true;
        }

        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                moveY=ev.getY();

                //让dragHelper拿一次down事件
                dragHelper.processTouchEvent(ev);
                break;
            case MotionEvent.ACTION_MOVE:
                float diffY=ev.getY()-moveY;
                Log.i("TAG"," diffY "+diffY);
                if(diffY>0 && !canChildScrollUp()){
                    // VerticalFoldListView向下滑动 并且ListView滚动到了顶部
                    // 拦截list不处理

                    /*   SwipeRefreshLayout -
                    public boolean canChildScrollUp() {
                        if (mChildScrollUpCallback != null) {
                            return mChildScrollUpCallback.canChildScrollUp(this, mTarget);
                        }
                        if (mTarget instanceof ListView) {
                            return ListViewCompat.canScrollList((ListView) mTarget, -1);
                        }
                        return mTarget.canScrollVertically(-1);
                    }*/

                    return true;
                }

                break;

        }
        return super.onInterceptTouchEvent(ev);
        /*
        Ignoring pointerId=0 because ACTION_DOWN was not received for this pointer
        before ACTION_MOVE. It likely happened
        because  ViewDragHelper did not receive all the events in the event stream.

        VerticalFoldListView.onInterceptTouchEvent() ACTION_DOWN ->
        ListView.onTouch() ->
        VerticalFoldListView.onInterceptTouchEvent() ACTION_MOVE ->
        VerticalFoldListView.onTouchEvent().ACTION_MOVE
        导致 dragHelper.processTouchEvent(event); 只执行了move没有执行down 报错


        */
    }

    //判断view是否滚动到了顶部 true还能向上滚 false已经滚动到顶部
    public boolean canChildScrollUp() {
        if (secondChild instanceof ListView) {
            return ListViewCompat.canScrollList((ListView) secondChild, -1);
        }
        return secondChild.canScrollVertically(-1);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        //加载完成布局 去拿子View
        if(getChildCount()>2){
            throw new RuntimeException("最多只能放置2个子View");
        }

        firstChild = getChildAt(0);
        secondChild = getChildAt(1);

    }

//    @Override
//    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//        firstChildHeight=firstChild.getMeasuredHeight();
//    }


//    @Override
//    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
//        super.onSizeChanged(w, h, oldw, oldh);
//
//        firstChildHeight=firstChild.getMeasuredHeight();
//    }


    @Override
    public void computeScroll() {
        if(dragHelper.continueSettling(true)){
            invalidate();
        }
    }
}