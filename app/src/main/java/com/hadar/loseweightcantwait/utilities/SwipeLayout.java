package com.hadar.loseweightcantwait.utilities;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.HapticFeedbackConstants;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.FrameLayout;

import com.hadar.loseweightcantwait.R;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.customview.widget.ViewDragHelper;

public class SwipeLayout extends FrameLayout {
    @Deprecated
    public static final int EMPTY_LAYOUT = -1;
    private static final int DRAG_LEFT = 1;
    private static final int DRAG_RIGHT = 2;
    private static final int DRAG_TOP = 4;
    private static final int DRAG_BOTTOM = 8;
    private static final DragEdge DefaultDragEdge = DragEdge.Right;

    private int mTouchSlop;

    private DragEdge mCurrentDragEdge = DefaultDragEdge;
    private ViewDragHelper mDragHelper;

    private int mDragDistance = 0;
    private LinkedHashMap<DragEdge, View> mDragEdges = new LinkedHashMap<>();
    private ShowMode mShowMode;

    private float[] mEdgeSwipesOffset = new float[4];

    private List<SwipeListener> mSwipeListeners = new ArrayList<>();
    private List<SwipeDenier> mSwipeDeniers = new ArrayList<>();
    private Map<View, ArrayList<OnRevealListener>> mRevealListeners = new HashMap<>();
    private Map<View, Boolean> mShowEntirely = new HashMap<>();
    private Map<View, Rect> mViewBoundCache = new HashMap<>();

    private boolean[] mSwipesEnabled = new boolean[]{true, true, true, true};
    private boolean mClickToClose = false;

    public enum DragEdge {
        Left,
        Top,
        Right,
        Bottom
    }

    public enum ShowMode {
        LayDown,
        PullOut
    }

    public SwipeLayout(Context context) {
        this(context, null);
    }

    public SwipeLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SwipeLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        ViewDragHelper.Callback mDragHelperCallback = new ViewDragHelper.Callback() {

            @Override
            public int clampViewPositionHorizontal(@NonNull View child, int left, int dx) {
                if (child == getSurfaceView()) {
                    switch (mCurrentDragEdge) {
                        case Top:
                        case Bottom:
                            return getPaddingLeft();
                        case Left:
                            if (left < getPaddingLeft()) return getPaddingLeft();
                            if (left > getPaddingLeft() + mDragDistance)
                                return getPaddingLeft() + mDragDistance;
                            break;
                        case Right:
                            if (left > getPaddingLeft()) return getPaddingLeft();
                            if (left < getPaddingLeft() - mDragDistance)
                                return getPaddingLeft() - mDragDistance;
                            break;
                    }
                } else if (getCurrentBottomView() == child) {

                    switch (mCurrentDragEdge) {
                        case Top:
                        case Bottom:
                            return getPaddingLeft();
                        case Left:
                            if (mShowMode == ShowMode.PullOut) {
                                if (left > getPaddingLeft()) return getPaddingLeft();
                            }
                            break;
                        case Right:
                            if (mShowMode == ShowMode.PullOut) {
                                if (left < getMeasuredWidth() - mDragDistance) {
                                    return getMeasuredWidth() - mDragDistance;
                                }
                            }
                            break;
                    }
                }
                return left;
            }

            @Override
            public int clampViewPositionVertical(@NonNull View child, int top, int dy) {
                if (child == getSurfaceView()) {
                    switch (mCurrentDragEdge) {
                        case Left:
                        case Right:
                            return getPaddingTop();
                        case Top:
                            if (top < getPaddingTop()) return getPaddingTop();
                            if (top > getPaddingTop() + mDragDistance)
                                return getPaddingTop() + mDragDistance;
                            break;
                        case Bottom:
                            if (top < getPaddingTop() - mDragDistance) {
                                return getPaddingTop() - mDragDistance;
                            }
                            if (top > getPaddingTop()) {
                                return getPaddingTop();
                            }
                    }
                } else {
                    View surfaceView = getSurfaceView();
                    int surfaceViewTop = surfaceView == null ? 0 : surfaceView.getTop();
                    switch (mCurrentDragEdge) {
                        case Left:
                        case Right:
                            return getPaddingTop();
                        case Top:
                            if (mShowMode == ShowMode.PullOut) {
                                if (top > getPaddingTop()) return getPaddingTop();
                            } else {
                                if (surfaceViewTop + dy < getPaddingTop())
                                    return getPaddingTop();
                                if (surfaceViewTop + dy > getPaddingTop() + mDragDistance)
                                    return getPaddingTop() + mDragDistance;
                            }
                            break;
                        case Bottom:
                            if (mShowMode == ShowMode.PullOut) {
                                if (top < getMeasuredHeight() - mDragDistance)
                                    return getMeasuredHeight() - mDragDistance;
                            } else {
                                if (surfaceViewTop + dy >= getPaddingTop())
                                    return getPaddingTop();
                                if (surfaceViewTop + dy <= getPaddingTop() - mDragDistance)
                                    return getPaddingTop() - mDragDistance;
                            }
                    }
                }
                return top;
            }

            @Override
            public boolean tryCaptureView(@NonNull View child, int pointerId) {
                boolean result = child == getSurfaceView() || getBottomViews().contains(child);
                if (result) {
                    isCloseBeforeDrag = getOpenStatus() == Status.Close;
                }
                return result;
            }

            @Override
            public int getViewHorizontalDragRange(@NonNull View child) {
                return mDragDistance;
            }

            @Override
            public int getViewVerticalDragRange(@NonNull View child) {
                return mDragDistance;
            }

            boolean isCloseBeforeDrag = true;

            @Override
            public void onViewReleased(@NonNull View releasedChild, float xvel, float yvel) {
                super.onViewReleased(releasedChild, xvel, yvel);
                processHandRelease(xvel, yvel, isCloseBeforeDrag);
                for (SwipeListener l : mSwipeListeners) {
                    l.onHandRelease(SwipeLayout.this, xvel, yvel);
                }

                invalidate();
            }

            @Override
            public void onViewPositionChanged(@NonNull View changedView, int left, int top, int dx,
                                              int dy) {
                View surfaceView = getSurfaceView();
                if (surfaceView == null) return;
                View currentBottomView = getCurrentBottomView();
                int evLeft = surfaceView.getLeft(),
                        evRight = surfaceView.getRight(),
                        evTop = surfaceView.getTop(),
                        evBottom = surfaceView.getBottom();
                if (changedView == surfaceView) {

                    if (mShowMode == ShowMode.PullOut && currentBottomView != null) {
                        if (mCurrentDragEdge == DragEdge.Left ||
                                mCurrentDragEdge == DragEdge.Right) {
                            currentBottomView.offsetLeftAndRight(dx);
                        } else {
                            currentBottomView.offsetTopAndBottom(dy);
                        }
                    }

                } else if (getBottomViews().contains(changedView)) {

                    if (mShowMode == ShowMode.PullOut) {
                        surfaceView.offsetLeftAndRight(dx);
                        surfaceView.offsetTopAndBottom(dy);
                    } else {
                        Rect rect = computeBottomLayDown(mCurrentDragEdge);
                        if (currentBottomView != null) {
                            currentBottomView.layout(rect.left, rect.top, rect.right, rect.bottom);
                        }

                        int newLeft = surfaceView.getLeft() + dx, newTop =
                                surfaceView.getTop() + dy;

                        if (mCurrentDragEdge == DragEdge.Left && newLeft < getPaddingLeft())
                            newLeft = getPaddingLeft();
                        else if (mCurrentDragEdge == DragEdge.Right && newLeft > getPaddingLeft())
                            newLeft = getPaddingLeft();
                        else if (mCurrentDragEdge == DragEdge.Top && newTop < getPaddingTop())
                            newTop = getPaddingTop();
                        else if (mCurrentDragEdge == DragEdge.Bottom && newTop > getPaddingTop())
                            newTop = getPaddingTop();

                        surfaceView.layout(newLeft, newTop, newLeft + getMeasuredWidth(),
                                newTop + getMeasuredHeight());
                    }
                }

                dispatchRevealEvent(evLeft, evTop, evRight, evBottom);

                dispatchSwipeEvent(evLeft, evTop, dx, dy);

                invalidate();

                captureChildrenBound();
            }
        };
        mDragHelper = ViewDragHelper.create(this, mDragHelperCallback);
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SwipeLayout);
        int dragEdgeChoices = a.getInt(R.styleable.SwipeLayout_drag_edge, DRAG_RIGHT);
        mEdgeSwipesOffset[DragEdge.Left.ordinal()] =
                a.getDimension(R.styleable.SwipeLayout_leftEdgeSwipeOffset, 0);
        mEdgeSwipesOffset[DragEdge.Right.ordinal()] =
                a.getDimension(R.styleable.SwipeLayout_rightEdgeSwipeOffset, 0);
        mEdgeSwipesOffset[DragEdge.Top.ordinal()] =
                a.getDimension(R.styleable.SwipeLayout_topEdgeSwipeOffset, 0);
        mEdgeSwipesOffset[DragEdge.Bottom.ordinal()] =
                a.getDimension(R.styleable.SwipeLayout_bottomEdgeSwipeOffset, 0);
        setClickToClose(a.getBoolean(R.styleable.SwipeLayout_clickToClose, mClickToClose));

        if ((dragEdgeChoices & DRAG_LEFT) == DRAG_LEFT) {
            mDragEdges.put(DragEdge.Left, null);
        }
        if ((dragEdgeChoices & DRAG_TOP) == DRAG_TOP) {
            mDragEdges.put(DragEdge.Top, null);
        }
        if ((dragEdgeChoices & DRAG_RIGHT) == DRAG_RIGHT) {
            mDragEdges.put(DragEdge.Right, null);
        }
        if ((dragEdgeChoices & DRAG_BOTTOM) == DRAG_BOTTOM) {
            mDragEdges.put(DragEdge.Bottom, null);
        }
        int ordinal = a.getInt(R.styleable.SwipeLayout_show_mode, ShowMode.PullOut.ordinal());
        mShowMode = ShowMode.values()[ordinal];
        a.recycle();

    }

    public interface SwipeListener {
        void onStartOpen(SwipeLayout layout);

        void onOpen(SwipeLayout layout);

        void onStartClose(SwipeLayout layout);

        void onClose(SwipeLayout layout);

        void onUpdate(SwipeLayout layout, int leftOffset, int topOffset);

        void onHandRelease(SwipeLayout layout, float xvel, float yvel);
    }

    public interface SwipeDenier {
        /*
         * Called in onInterceptTouchEvent Determines if this swipe event should
         * be denied Implement this interface if you are using views with swipe
         * gestures As a child of SwipeLayout
         *
         * @return true deny false allow
         */
        boolean shouldDenySwipe(MotionEvent ev);
    }

    public interface OnRevealListener {
        void onReveal(View child, DragEdge edge, float fraction, int distance);
    }

    /**
     * save children's bounds, so they can restore the bound in {@link #onLayout(boolean, int, int, int, int)}
     */
    private void captureChildrenBound() {
        View currentBottomView = getCurrentBottomView();
        if (getOpenStatus() == Status.Close) {
            mViewBoundCache.remove(currentBottomView);
            return;
        }

        View[] views = new View[]{getSurfaceView(), currentBottomView};
        for (View child : views) {
            Rect rect = mViewBoundCache.get(child);
            if (rect == null) {
                rect = new Rect();
                mViewBoundCache.put(child, rect);
            }
            rect.left = child.getLeft();
            rect.top = child.getTop();
            rect.right = child.getRight();
            rect.bottom = child.getBottom();
        }
    }

    /**
     * the dispatchRevealEvent method may not always get accurate position, it
     * makes the view may not always get the event when the view is totally
     * show( fraction = 1), so , we need to calculate every time.
     */
    protected boolean isViewTotallyFirstShowed(View child, Rect relativePosition, DragEdge edge,
                                               int surfaceLeft,
                                               int surfaceTop, int surfaceRight,
                                               int surfaceBottom) {
        if (mShowEntirely.get(child)) return false;
        int childLeft = relativePosition.left;
        int childRight = relativePosition.right;
        int childTop = relativePosition.top;
        int childBottom = relativePosition.bottom;
        boolean r = false;
        if (getShowMode() == ShowMode.LayDown) {
            if ((edge == DragEdge.Right && surfaceRight <= childLeft)
                    || (edge == DragEdge.Left && surfaceLeft >= childRight)
                    || (edge == DragEdge.Top && surfaceTop >= childBottom)
                    || (edge == DragEdge.Bottom && surfaceBottom <= childTop)) r = true;
        } else if (getShowMode() == ShowMode.PullOut) {
            if ((edge == DragEdge.Right && childRight <= getWidth())
                    || (edge == DragEdge.Left && childLeft >= getPaddingLeft())
                    || (edge == DragEdge.Top && childTop >= getPaddingTop())
                    || (edge == DragEdge.Bottom && childBottom <= getHeight())) r = true;
        }
        return r;
    }

    protected boolean isViewShowing(Rect relativePosition, DragEdge availableEdge,
                                    int surfaceLeft,
                                    int surfaceTop, int surfaceRight, int surfaceBottom) {
        int childLeft = relativePosition.left;
        int childRight = relativePosition.right;
        int childTop = relativePosition.top;
        int childBottom = relativePosition.bottom;
        if (getShowMode() == ShowMode.LayDown) {
            switch (availableEdge) {
                case Right:
                    if (surfaceRight > childLeft && surfaceRight <= childRight) {
                        return true;
                    }
                    break;
                case Left:
                    if (surfaceLeft < childRight && surfaceLeft >= childLeft) {
                        return true;
                    }
                    break;
                case Top:
                    if (surfaceTop >= childTop && surfaceTop < childBottom) {
                        return true;
                    }
                    break;
                case Bottom:
                    if (surfaceBottom > childTop && surfaceBottom <= childBottom) {
                        return true;
                    }
                    break;
            }
        } else if (getShowMode() == ShowMode.PullOut) {
            switch (availableEdge) {
                case Right:
                    if (childLeft <= getWidth() && childRight > getWidth()) return true;
                    break;
                case Left:
                    if (childRight >= getPaddingLeft() && childLeft < getPaddingLeft()) return true;
                    break;
                case Top:
                    if (childTop < getPaddingTop() && childBottom >= getPaddingTop()) return true;
                    break;
                case Bottom:
                    if (childTop < getHeight() && childTop >= getPaddingTop()) return true;
                    break;
            }
        }
        return false;
    }

    protected Rect getRelativePosition(View child) {
        View t = child;
        Rect r = new Rect(t.getLeft(), t.getTop(), 0, 0);
        while (t.getParent() != null && t != getRootView()) {
            t = (View) t.getParent();
            if (t == this) break;
            r.left += t.getLeft();
            r.top += t.getTop();
        }
        r.right = r.left + child.getMeasuredWidth();
        r.bottom = r.top + child.getMeasuredHeight();
        return r;
    }

    private int mEventCounter = 0;

    protected void dispatchSwipeEvent(int surfaceLeft, int surfaceTop, int dx, int dy) {
        DragEdge edge = getDragEdge();
        boolean open = true;
        if (edge == DragEdge.Left) {
            if (dx < 0) open = false;
        } else if (edge == DragEdge.Right) {
            if (dx > 0) open = false;
        } else if (edge == DragEdge.Top) {
            if (dy < 0) open = false;
        } else if (edge == DragEdge.Bottom) {
            if (dy > 0) open = false;
        }

        dispatchSwipeEvent(surfaceLeft, surfaceTop, open);
    }

    protected void dispatchSwipeEvent(int surfaceLeft, int surfaceTop, boolean open) {
        safeBottomView();
        Status status = getOpenStatus();

        if (!mSwipeListeners.isEmpty()) {
            mEventCounter++;
            for (SwipeListener l : mSwipeListeners) {
                if (mEventCounter == 1) {
                    if (open) {
                        l.onStartOpen(this);
                    } else {
                        l.onStartClose(this);
                    }
                }
                l.onUpdate(SwipeLayout.this, surfaceLeft - getPaddingLeft(),
                        surfaceTop - getPaddingTop());
            }

            if (status == Status.Close) {
                for (SwipeListener l : mSwipeListeners) {
                    l.onClose(SwipeLayout.this);
                }
                mEventCounter = 0;
            }

            if (status == Status.Open) {
                View currentBottomView = getCurrentBottomView();
                if (currentBottomView != null) {
                    currentBottomView.setEnabled(true);
                }
                for (SwipeListener l : mSwipeListeners) {
                    l.onOpen(SwipeLayout.this);
                }
                mEventCounter = 0;
            }
        }
    }

    /**
     * prevent bottom view get any touch event. Especially in LayDown mode.
     */
    private void safeBottomView() {
        Status status = getOpenStatus();
        List<View> bottoms = getBottomViews();

        if (status == Status.Close) {
            for (View bottom : bottoms) {
                if (bottom != null && bottom.getVisibility() != INVISIBLE) {
                    bottom.setVisibility(INVISIBLE);
                }
            }
        } else {
            View currentBottomView = getCurrentBottomView();
            if (currentBottomView != null && currentBottomView.getVisibility() != VISIBLE) {
                currentBottomView.setVisibility(VISIBLE);
            }
        }
    }

    protected void dispatchRevealEvent(final int surfaceLeft, final int surfaceTop,
                                       final int surfaceRight,
                                       final int surfaceBottom) {
        if (mRevealListeners.isEmpty()) return;
        for (Map.Entry<View, ArrayList<OnRevealListener>> entry : mRevealListeners.entrySet()) {
            View child = entry.getKey();
            Rect rect = getRelativePosition(child);
            if (isViewShowing(rect, mCurrentDragEdge, surfaceLeft, surfaceTop,
                    surfaceRight, surfaceBottom)) {
                mShowEntirely.put(child, false);
                int distance = 0;
                float fraction = 0f;
                if (getShowMode() == ShowMode.LayDown) {
                    switch (mCurrentDragEdge) {
                        case Left:
                            distance = rect.left - surfaceLeft;
                            fraction = distance / (float) child.getWidth();
                            break;
                        case Right:
                            distance = rect.right - surfaceRight;
                            fraction = distance / (float) child.getWidth();
                            break;
                        case Top:
                            distance = rect.top - surfaceTop;
                            fraction = distance / (float) child.getHeight();
                            break;
                        case Bottom:
                            distance = rect.bottom - surfaceBottom;
                            fraction = distance / (float) child.getHeight();
                            break;
                    }
                } else if (getShowMode() == ShowMode.PullOut) {
                    switch (mCurrentDragEdge) {
                        case Left:
                            distance = rect.right - getPaddingLeft();
                            fraction = distance / (float) child.getWidth();
                            break;
                        case Right:
                            distance = rect.left - getWidth();
                            fraction = distance / (float) child.getWidth();
                            break;
                        case Top:
                            distance = rect.bottom - getPaddingTop();
                            fraction = distance / (float) child.getHeight();
                            break;
                        case Bottom:
                            distance = rect.top - getHeight();
                            fraction = distance / (float) child.getHeight();
                            break;
                    }
                }

                for (OnRevealListener l : entry.getValue()) {
                    l.onReveal(child, mCurrentDragEdge, Math.abs(fraction), distance);
                    if (Math.abs(fraction) == 1) {
                        mShowEntirely.put(child, true);
                    }
                }
            }

            if (isViewTotallyFirstShowed(child, rect, mCurrentDragEdge, surfaceLeft, surfaceTop,
                    surfaceRight, surfaceBottom)) {
                mShowEntirely.put(child, true);
                for (OnRevealListener l : entry.getValue()) {
                    if (mCurrentDragEdge == DragEdge.Left
                            || mCurrentDragEdge == DragEdge.Right)
                        l.onReveal(child, mCurrentDragEdge, 1, child.getWidth());
                    else
                        l.onReveal(child, mCurrentDragEdge, 1, child.getHeight());
                }
            }

        }
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if (mDragHelper.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    /**
     * {@link android.view.View.OnLayoutChangeListener} added in API 11. I need
     * to support it from API 8.
     */
    public interface OnLayout {
        void onLayout(SwipeLayout v);
    }

    private List<OnLayout> mOnLayoutListeners;

    public void clearDragEdge() {
        mDragEdges.clear();
    }

    public void addDrag(DragEdge dragEdge, View child) {
        addDrag(dragEdge, child, null);
    }

    public void addDrag(DragEdge dragEdge, View child, ViewGroup.LayoutParams params) {
        if (child == null) return;

        if (params == null) {
            params = generateDefaultLayoutParams();
        }
        if (!checkLayoutParams(params)) {
            params = generateLayoutParams(params);
        }
        int gravity = -1;
        switch (dragEdge) {
            case Left:
                gravity = Gravity.LEFT;
                break;
            case Right:
                gravity = Gravity.RIGHT;
                break;
            case Top:
                gravity = Gravity.TOP;
                break;
            case Bottom:
                gravity = Gravity.BOTTOM;
                break;
        }
        if (params instanceof FrameLayout.LayoutParams) {
            ((LayoutParams) params).gravity = gravity;
        }
        addView(child, 0, params);
    }

    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        if (child == null) return;
        int gravity = Gravity.NO_GRAVITY;
        try {
            gravity = (Integer) params.getClass().getField("gravity").get(params);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (gravity > 0) {
            gravity =
                    GravityCompat.getAbsoluteGravity(gravity, ViewCompat.getLayoutDirection(this));

            if ((gravity & Gravity.LEFT) == Gravity.LEFT) {
                mDragEdges.put(DragEdge.Left, child);
            }
            if ((gravity & Gravity.RIGHT) == Gravity.RIGHT) {
                mDragEdges.put(DragEdge.Right, child);
            }
            if ((gravity & Gravity.TOP) == Gravity.TOP) {
                mDragEdges.put(DragEdge.Top, child);
            }
            if ((gravity & Gravity.BOTTOM) == Gravity.BOTTOM) {
                mDragEdges.put(DragEdge.Bottom, child);
            }
        } else {
            for (Map.Entry<DragEdge, View> entry : mDragEdges.entrySet()) {
                if (entry.getValue() == null) {
                    //means used the drag_edge attr, the no gravity child should be use set
                    mDragEdges.put(entry.getKey(), child);
                    break;
                }
            }
        }
        if (child.getParent() == this) {
            return;
        }
        super.addView(child, index, params);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        updateBottomViews();

        if (mOnLayoutListeners != null) for (int i = 0; i < mOnLayoutListeners.size(); i++) {
            mOnLayoutListeners.get(i).onLayout(this);
        }
    }

    void layoutPullOut() {
        View surfaceView = getSurfaceView();
        Rect surfaceRect = mViewBoundCache.get(surfaceView);
        if (surfaceRect == null) surfaceRect = computeSurfaceLayoutArea(false);
        if (surfaceView != null) {
            surfaceView.layout(surfaceRect.left, surfaceRect.top, surfaceRect.right,
                    surfaceRect.bottom);
            bringChildToFront(surfaceView);
        }
        View currentBottomView = getCurrentBottomView();
        Rect bottomViewRect = mViewBoundCache.get(currentBottomView);
        if (bottomViewRect == null)
            bottomViewRect = computeBottomLayoutAreaViaSurface(ShowMode.PullOut, surfaceRect);
        if (currentBottomView != null) {
            currentBottomView.layout(bottomViewRect.left, bottomViewRect.top, bottomViewRect.right,
                    bottomViewRect.bottom);
        }
    }

    void layoutLayDown() {
        View surfaceView = getSurfaceView();
        Rect surfaceRect = mViewBoundCache.get(surfaceView);
        if (surfaceRect == null) surfaceRect = computeSurfaceLayoutArea(false);
        if (surfaceView != null) {
            surfaceView.layout(surfaceRect.left, surfaceRect.top, surfaceRect.right,
                    surfaceRect.bottom);
            bringChildToFront(surfaceView);
        }
        View currentBottomView = getCurrentBottomView();
        Rect bottomViewRect = mViewBoundCache.get(currentBottomView);
        if (bottomViewRect == null)
            bottomViewRect = computeBottomLayoutAreaViaSurface(ShowMode.LayDown, surfaceRect);
        if (currentBottomView != null) {
            currentBottomView.layout(bottomViewRect.left, bottomViewRect.top, bottomViewRect.right,
                    bottomViewRect.bottom);
        }
    }

    private boolean mIsBeingDragged;

    private void checkCanDrag(MotionEvent ev) {
        if (mIsBeingDragged) return;
        if (getOpenStatus() == Status.Middle) {
            mIsBeingDragged = true;
            return;
        }
        Status status = getOpenStatus();
        float distanceX = ev.getRawX() - sX;
        float distanceY = ev.getRawY() - sY;
        float angle = Math.abs(distanceY / distanceX);
        angle = (float) Math.toDegrees(Math.atan(angle));
        if (getOpenStatus() == Status.Close) {
            DragEdge dragEdge;
            if (angle < 45) {
                if (distanceX > 0 && isLeftSwipeEnabled()) {
                    dragEdge = DragEdge.Left;
                } else if (distanceX < 0 && isRightSwipeEnabled()) {
                    dragEdge = DragEdge.Right;
                } else return;

            } else {
                if (distanceY > 0 && isTopSwipeEnabled()) {
                    dragEdge = DragEdge.Top;
                } else if (distanceY < 0 && isBottomSwipeEnabled()) {
                    dragEdge = DragEdge.Bottom;
                } else return;
            }
            setCurrentDragEdge(dragEdge);
        }

        boolean doNothing = false;
        if (mCurrentDragEdge == DragEdge.Right) {
            boolean suitable = (status == Status.Open && distanceX > mTouchSlop)
                    || (status == Status.Close && distanceX < -mTouchSlop);
            suitable = suitable || (status == Status.Middle);

            if (angle > 30 || !suitable) {
                doNothing = true;
            }
        }

        if (mCurrentDragEdge == DragEdge.Left) {
            boolean suitable = (status == Status.Open && distanceX < -mTouchSlop)
                    || (status == Status.Close && distanceX > mTouchSlop);
            suitable = suitable || status == Status.Middle;

            if (angle > 30 || !suitable) {
                doNothing = true;
            }
        }

        if (mCurrentDragEdge == DragEdge.Top) {
            boolean suitable = (status == Status.Open && distanceY < -mTouchSlop)
                    || (status == Status.Close && distanceY > mTouchSlop);
            suitable = suitable || status == Status.Middle;

            if (angle < 60 || !suitable) {
                doNothing = true;
            }
        }

        if (mCurrentDragEdge == DragEdge.Bottom) {
            boolean suitable = (status == Status.Open && distanceY > mTouchSlop)
                    || (status == Status.Close && distanceY < -mTouchSlop);
            suitable = suitable || status == Status.Middle;

            if (angle < 60 || !suitable) {
                doNothing = true;
            }
        }
        mIsBeingDragged = !doNothing;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (!isSwipeEnabled()) {
            return false;
        }
        if (mClickToClose && getOpenStatus() == Status.Open && isTouchOnSurface(ev)) {
            return true;
        }
        for (SwipeDenier denier : mSwipeDeniers) {
            if (denier != null && denier.shouldDenySwipe(ev)) {
                return false;
            }
        }

        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mDragHelper.processTouchEvent(ev);
                mIsBeingDragged = false;
                sX = ev.getRawX();
                sY = ev.getRawY();
                //if the swipe is in middle state(scrolling), should intercept the touch
                if (getOpenStatus() == Status.Middle) {
                    mIsBeingDragged = true;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                boolean beforeCheck = mIsBeingDragged;
                checkCanDrag(ev);
                if (mIsBeingDragged) {
                    ViewParent parent = getParent();
                    if (parent != null) {
                        parent.requestDisallowInterceptTouchEvent(true);
                    }
                }
                if (!beforeCheck && mIsBeingDragged) {
                    //let children has one chance to catch the touch, and request the swipe not intercept
                    //useful when swipeLayout wrap a swipeLayout or other gestural layout
                    return false;
                }
                break;

            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                mIsBeingDragged = false;
                mDragHelper.processTouchEvent(ev);
                break;
            default://handle other action, such as ACTION_POINTER_DOWN/UP
                mDragHelper.processTouchEvent(ev);
        }
        return mIsBeingDragged;
    }

    private float sX = -1, sY = -1;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isSwipeEnabled()) return super.onTouchEvent(event);

        int action = event.getActionMasked();
        gestureDetector.onTouchEvent(event);

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mDragHelper.processTouchEvent(event);
                sX = event.getRawX();
                sY = event.getRawY();


            case MotionEvent.ACTION_MOVE: {
                //the drag state and the direction are already judged at onInterceptTouchEvent
                checkCanDrag(event);
                if (mIsBeingDragged) {
                    getParent().requestDisallowInterceptTouchEvent(true);
                    mDragHelper.processTouchEvent(event);
                }
                break;
            }
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mIsBeingDragged = false;
                mDragHelper.processTouchEvent(event);
                break;

            default://handle other action, such as ACTION_POINTER_DOWN/UP
                mDragHelper.processTouchEvent(event);
        }

        return super.onTouchEvent(event) || mIsBeingDragged || action == MotionEvent.ACTION_DOWN;
    }

    public void setClickToClose(boolean mClickToClose) {
        this.mClickToClose = mClickToClose;
    }

    public boolean isSwipeEnabled() {
        boolean mSwipeEnabled = true;
        return mSwipeEnabled;
    }

    public boolean isLeftSwipeEnabled() {
        View bottomView = mDragEdges.get(DragEdge.Left);
        return bottomView != null && bottomView.getParent() == this
                && bottomView != getSurfaceView() && mSwipesEnabled[DragEdge.Left.ordinal()];
    }

    public boolean isRightSwipeEnabled() {
        View bottomView = mDragEdges.get(DragEdge.Right);
        return bottomView != null && bottomView.getParent() == this
                && bottomView != getSurfaceView() && mSwipesEnabled[DragEdge.Right.ordinal()];
    }

    public boolean isTopSwipeEnabled() {
        View bottomView = mDragEdges.get(DragEdge.Top);
        return bottomView != null && bottomView.getParent() == this
                && bottomView != getSurfaceView() && mSwipesEnabled[DragEdge.Top.ordinal()];
    }

    public boolean isBottomSwipeEnabled() {
        View bottomView = mDragEdges.get(DragEdge.Bottom);
        return bottomView != null && bottomView.getParent() == this
                && bottomView != getSurfaceView() && mSwipesEnabled[DragEdge.Bottom.ordinal()];
    }

    private boolean insideAdapterView() {
        return getAdapterView() != null;
    }

    private AdapterView getAdapterView() {
        ViewParent t = getParent();
        if (t instanceof AdapterView) {
            return (AdapterView) t;
        }
        return null;
    }

    private void performAdapterViewItemClick() {
        if (getOpenStatus() != Status.Close) return;
        ViewParent t = getParent();
        if (t instanceof AdapterView) {
            AdapterView view = (AdapterView) t;
            int p = view.getPositionForView(SwipeLayout.this);
            if (p != AdapterView.INVALID_POSITION) {
                view.performItemClick(view.getChildAt(p - view.getFirstVisiblePosition()), p, view
                        .getAdapter().getItemId(p));
            }
        }
    }

    private void performAdapterViewItemLongClick() {
        if (getOpenStatus() != Status.Close) return;
        ViewParent t = getParent();
        if (t instanceof AdapterView) {
            AdapterView view = (AdapterView) t;
            int p = view.getPositionForView(SwipeLayout.this);
            if (p == AdapterView.INVALID_POSITION) return;
            long vId = view.getItemIdAtPosition(p);
            boolean handled = false;
            try {
                Method m = AbsListView.class
                        .getDeclaredMethod("performLongPress", View.class, int.class, long.class);
                m.setAccessible(true);
                handled = (boolean) m.invoke(view, SwipeLayout.this, p, vId);

            } catch (Exception e) {
                e.printStackTrace();

                if (view.getOnItemLongClickListener() != null) {
                    handled = view.getOnItemLongClickListener()
                            .onItemLongClick(view, SwipeLayout.this, p, vId);
                }
                if (handled) {
                    view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
                }
            }
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (insideAdapterView()) {
            if (clickListener == null) {
                setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        performAdapterViewItemClick();
                    }
                });
            }
            if (longClickListener == null) {
                setOnLongClickListener(new OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        Log.e("SwipeLayout", "onLongClick");
                        performAdapterViewItemLongClick();
                        return true;
                    }
                });
            }
        }
    }

    OnClickListener clickListener;

    @Override
    public void setOnClickListener(OnClickListener l) {
        super.setOnClickListener(l);
        clickListener = l;
    }

    OnLongClickListener longClickListener;

    @Override
    public void setOnLongClickListener(OnLongClickListener l) {
        super.setOnLongClickListener(l);
        longClickListener = l;
    }

    private Rect hitSurfaceRect;

    private boolean isTouchOnSurface(MotionEvent ev) {
        View surfaceView = getSurfaceView();
        if (surfaceView == null) {
            return false;
        }
        if (hitSurfaceRect == null) {
            hitSurfaceRect = new Rect();
        }
        surfaceView.getHitRect(hitSurfaceRect);
        return hitSurfaceRect.contains((int) ev.getX(), (int) ev.getY());
    }

    private GestureDetector gestureDetector =
            new GestureDetector(getContext(), new SwipeDetector());

    class SwipeDetector extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            if (mClickToClose && isTouchOnSurface(e)) {
                close();
            }
            return super.onSingleTapUp(e);
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            return true;
        }
    }

    public DragEdge getDragEdge() {
        return mCurrentDragEdge;
    }

    public ShowMode getShowMode() {
        return mShowMode;
    }

    /**
     * return null if there is no surface view(no children)
     */
    public View getSurfaceView() {
        if (getChildCount() == 0) return null;
        return getChildAt(getChildCount() - 1);
    }

    /**
     * return null if there is no bottom view
     */
    public View getCurrentBottomView() {
        List<View> bottoms = getBottomViews();
        if (mCurrentDragEdge.ordinal() < bottoms.size()) {
            return bottoms.get(mCurrentDragEdge.ordinal());
        }
        return null;
    }

    /**
     * @return all bottomViews: left, top, right, bottom (may null if the edge is not set)
     */
    public List<View> getBottomViews() {
        ArrayList<View> bottoms = new ArrayList<>();
        for (DragEdge dragEdge : DragEdge.values()) {
            bottoms.add(mDragEdges.get(dragEdge));
        }
        return bottoms;
    }

    public enum Status {
        Middle,
        Open,
        Close
    }

    /**
     * get the open status.
     * <p>
     * Middle.
     */
    public Status getOpenStatus() {
        View surfaceView = getSurfaceView();
        if (surfaceView == null) {
            return Status.Close;
        }
        int surfaceLeft = surfaceView.getLeft();
        int surfaceTop = surfaceView.getTop();
        if (surfaceLeft == getPaddingLeft() && surfaceTop == getPaddingTop()) return Status.Close;

        if (surfaceLeft == (getPaddingLeft() - mDragDistance) ||
                surfaceLeft == (getPaddingLeft() + mDragDistance)
                || surfaceTop == (getPaddingTop() - mDragDistance) ||
                surfaceTop == (getPaddingTop() + mDragDistance))
            return Status.Open;

        return Status.Middle;
    }


    /**
     * Process the surface release event.
     *
     * @param xvel                 xVelocity
     * @param yvel                 yVelocity
     * @param isCloseBeforeDragged the open state before drag
     */
    protected void processHandRelease(float xvel, float yvel, boolean isCloseBeforeDragged) {
        float minVelocity = mDragHelper.getMinVelocity();
        View surfaceView = getSurfaceView();
        DragEdge currentDragEdge = mCurrentDragEdge;
        if (currentDragEdge == null || surfaceView == null) {
            return;
        }
        float mWillOpenPercentAfterOpen = 0.75f;
        float mWillOpenPercentAfterClose = 0.25f;
        float willOpenPercent =
                (isCloseBeforeDragged ? mWillOpenPercentAfterClose : mWillOpenPercentAfterOpen);
        if (currentDragEdge == DragEdge.Left) {
            if (xvel > minVelocity) open();
            else if (xvel < -minVelocity) close();
            else {
                float openPercent = 1f * getSurfaceView().getLeft() / mDragDistance;
                if (openPercent > willOpenPercent) open();
                else close();
            }
        } else if (currentDragEdge == DragEdge.Right) {
            if (xvel > minVelocity) close();
            else if (xvel < -minVelocity) open();
            else {
                float openPercent = 1f * (-getSurfaceView().getLeft()) / mDragDistance;
                if (openPercent > willOpenPercent) open();
                else close();
            }
        } else if (currentDragEdge == DragEdge.Top) {
            if (yvel > minVelocity) open();
            else if (yvel < -minVelocity) close();
            else {
                float openPercent = 1f * getSurfaceView().getTop() / mDragDistance;
                if (openPercent > willOpenPercent) open();
                else close();
            }
        } else if (currentDragEdge == DragEdge.Bottom) {
            if (yvel > minVelocity) close();
            else if (yvel < -minVelocity) open();
            else {
                float openPercent = 1f * (-getSurfaceView().getTop()) / mDragDistance;
                if (openPercent > willOpenPercent) open();
                else close();
            }
        }
    }

    /**
     * smoothly open surface.
     */
    public void open() {
        open(true, true);
    }

    public void open(boolean smooth) {
        open(smooth, true);
    }

    public void open(boolean smooth, boolean notify) {
        View surface = getSurfaceView(), bottom = getCurrentBottomView();
        if (surface == null) {
            return;
        }
        int dx, dy;
        Rect rect = computeSurfaceLayoutArea(true);
        if (smooth) {
            mDragHelper.smoothSlideViewTo(surface, rect.left, rect.top);
        } else {
            dx = rect.left - surface.getLeft();
            dy = rect.top - surface.getTop();
            surface.layout(rect.left, rect.top, rect.right, rect.bottom);
            if (getShowMode() == ShowMode.PullOut) {
                Rect bRect = computeBottomLayoutAreaViaSurface(ShowMode.PullOut, rect);
                if (bottom != null) {
                    bottom.layout(bRect.left, bRect.top, bRect.right, bRect.bottom);
                }
            }
            if (notify) {
                dispatchRevealEvent(rect.left, rect.top, rect.right, rect.bottom);
                dispatchSwipeEvent(rect.left, rect.top, dx, dy);
            } else {
                safeBottomView();
            }
        }
        invalidate();
    }

    /**
     * smoothly close surface.
     */
    public void close() {
        close(true, true);
    }

    public void close(boolean smooth) {
        close(smooth, true);
    }

    /**
     * close surface
     *
     * @param smooth smoothly or not.
     * @param notify if notify all the listeners.
     */
    public void close(boolean smooth, boolean notify) {
        View surface = getSurfaceView();
        if (surface == null) {
            return;
        }
        int dx, dy;
        if (smooth)
            mDragHelper.smoothSlideViewTo(getSurfaceView(), getPaddingLeft(), getPaddingTop());
        else {
            Rect rect = computeSurfaceLayoutArea(false);
            dx = rect.left - surface.getLeft();
            dy = rect.top - surface.getTop();
            surface.layout(rect.left, rect.top, rect.right, rect.bottom);
            if (notify) {
                dispatchRevealEvent(rect.left, rect.top, rect.right, rect.bottom);
                dispatchSwipeEvent(rect.left, rect.top, dx, dy);
            } else {
                safeBottomView();
            }
        }
        invalidate();
    }

    /**
     * a helper function to compute the Rect area that surface will hold in.
     *
     * @param open open status or close status.
     */
    private Rect computeSurfaceLayoutArea(boolean open) {
        int l = getPaddingLeft(), t = getPaddingTop();
        if (open) {
            if (mCurrentDragEdge == DragEdge.Left)
                l = getPaddingLeft() + mDragDistance;
            else if (mCurrentDragEdge == DragEdge.Right)
                l = getPaddingLeft() - mDragDistance;
            else if (mCurrentDragEdge == DragEdge.Top)
                t = getPaddingTop() + mDragDistance;
            else t = getPaddingTop() - mDragDistance;
        }
        return new Rect(l, t, l + getMeasuredWidth(), t + getMeasuredHeight());
    }

    private Rect computeBottomLayoutAreaViaSurface(ShowMode mode, Rect surfaceArea) {
        View bottomView = getCurrentBottomView();

        int bl = surfaceArea.left, bt = surfaceArea.top, br = surfaceArea.right, bb =
                surfaceArea.bottom;
        if (mode == ShowMode.PullOut) {
            if (mCurrentDragEdge == DragEdge.Left)
                bl = surfaceArea.left - mDragDistance;
            else if (mCurrentDragEdge == DragEdge.Right)
                bl = surfaceArea.right;
            else if (mCurrentDragEdge == DragEdge.Top)
                bt = surfaceArea.top - mDragDistance;
            else bt = surfaceArea.bottom;

            if (mCurrentDragEdge == DragEdge.Left || mCurrentDragEdge == DragEdge.Right) {
                bb = surfaceArea.bottom;
                br = bl + (bottomView == null ? 0 : bottomView.getMeasuredWidth());
            } else {
                bb = bt + (bottomView == null ? 0 : bottomView.getMeasuredHeight());
                br = surfaceArea.right;
            }
        } else if (mode == ShowMode.LayDown) {
            if (mCurrentDragEdge == DragEdge.Left)
                br = bl + mDragDistance;
            else if (mCurrentDragEdge == DragEdge.Right)
                bl = br - mDragDistance;
            else if (mCurrentDragEdge == DragEdge.Top)
                bb = bt + mDragDistance;
            else bt = bb - mDragDistance;

        }
        return new Rect(bl, bt, br, bb);

    }

    private Rect computeBottomLayDown(DragEdge dragEdge) {
        int bl = getPaddingLeft(), bt = getPaddingTop();
        int br, bb;
        if (dragEdge == DragEdge.Right) {
            bl = getMeasuredWidth() - mDragDistance;
        } else if (dragEdge == DragEdge.Bottom) {
            bt = getMeasuredHeight() - mDragDistance;
        }
        if (dragEdge == DragEdge.Left || dragEdge == DragEdge.Right) {
            br = bl + mDragDistance;
            bb = bt + getMeasuredHeight();
        } else {
            br = bl + getMeasuredWidth();
            bb = bt + mDragDistance;
        }
        return new Rect(bl, bt, br, bb);
    }

    private int dp2px(float dp) {
        return (int) (dp * getContext().getResources().getDisplayMetrics().density + 0.5f);
    }


    /**
     * Deprecated, use {@link #(DragEdge, View)}
     */
    @Deprecated
    public void setDragEdge(DragEdge dragEdge) {
        clearDragEdge();
        if (getChildCount() >= 2) {
            mDragEdges.put(dragEdge, getChildAt(getChildCount() - 2));
        }
        setCurrentDragEdge(dragEdge);
    }

    public void onViewRemoved(View child) {
        for (Map.Entry<DragEdge, View> entry : new HashMap<>(mDragEdges).entrySet()) {
            if (entry.getValue() == child) {
                mDragEdges.remove(entry.getKey());
            }
        }
    }

    /**
     * Deprecated, use {@link #()}
     */
    @Deprecated
    public List<DragEdge> getDragEdges() {
        return new ArrayList<>(mDragEdges.keySet());
    }

    /**
     * Deprecated, use {@link #(DragEdge, View)}
     */
    @Deprecated
    public void setDragEdges(List<DragEdge> dragEdges) {
        clearDragEdge();
        for (int i = 0, size = Math.min(dragEdges.size(), getChildCount() - 1); i < size; i++) {
            DragEdge dragEdge = dragEdges.get(i);
            mDragEdges.put(dragEdge, getChildAt(i));
        }
        if (dragEdges.size() == 0 || dragEdges.contains(DefaultDragEdge)) {
            setCurrentDragEdge(DefaultDragEdge);
        } else {
            setCurrentDragEdge(dragEdges.get(0));
        }
    }

    /**
     * Deprecated, use {@link #addDrag(DragEdge, View)}
     */
    @Deprecated
    public void setDragEdges(DragEdge... mDragEdges) {
        clearDragEdge();
        setDragEdges(Arrays.asList(mDragEdges));
    }

    /**
     * Deprecated, use {@link #addDrag(DragEdge, View)}
     * When using multiple drag edges it's a good idea to pass the ids of the views that
     * you're using for the left, right, top bottom views (-1 if you're not using a particular view)
     */
    @Deprecated
    public void setBottomViewIds(int leftId, int rightId, int topId, int bottomId) {
        addDrag(DragEdge.Left, findViewById(leftId));
        addDrag(DragEdge.Right, findViewById(rightId));
        addDrag(DragEdge.Top, findViewById(topId));
        addDrag(DragEdge.Bottom, findViewById(bottomId));
    }

    private float getCurrentOffset() {
        if (mCurrentDragEdge == null) return 0;
        return mEdgeSwipesOffset[mCurrentDragEdge.ordinal()];
    }

    private void setCurrentDragEdge(DragEdge dragEdge) {
        mCurrentDragEdge = dragEdge;
        updateBottomViews();
    }

    private void updateBottomViews() {
        View currentBottomView = getCurrentBottomView();
        if (currentBottomView != null) {
            if (mCurrentDragEdge == DragEdge.Left || mCurrentDragEdge == DragEdge.Right) {
                mDragDistance = currentBottomView.getMeasuredWidth() - dp2px(getCurrentOffset());
            } else {
                mDragDistance = currentBottomView.getMeasuredHeight() - dp2px(getCurrentOffset());
            }
        }

        if (mShowMode == ShowMode.PullOut) {
            layoutPullOut();
        } else if (mShowMode == ShowMode.LayDown) {
            layoutLayDown();
        }

        safeBottomView();
    }
}


