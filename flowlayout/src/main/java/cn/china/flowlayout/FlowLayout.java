package cn.china.flowlayout;


import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * 流式布局，把长宽不一的子项流式排列
 * Created by lenovo on 2016/10/16.
 */
public class FlowLayout extends ViewGroup  {
    private TagAdapter mTagAdapter;
    private Context context;
    private float space_horizontal,space_vertical;
    //除去两旁空白后的行宽度
    private int maxWidth;
    //储存每一行的列表
    private List<Line> mLines = new ArrayList();
    //当前行
    private Line mCurrentLine = null;

    public FlowLayout(Context context) {
        super(context,null);
        this.context = context;
    }
    public void setSpace(View view) {


    }
    public FlowLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        //获取用户赋予的属性，包括水平和竖直标签的间距
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.FlowLayout);
        space_horizontal = array.getDimension(R.styleable.FlowLayout_space_horizontal,1);
        space_vertical = array.getDimension(R.styleable.FlowLayout_space_vertical,1);
        array.recycle();
        this.context = context;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // 每次测量之前都先清空集合，不让会覆盖掉以前
        mLines.clear();
        mCurrentLine = null;

        // 获取总宽度
        int width = MeasureSpec.getSize(widthMeasureSpec);
        // 计算最大的宽度
        maxWidth = width - getPaddingLeft() - getPaddingRight();

        // ******************** 测量孩子 ********************
        // 遍历获取孩子
        int childCount = this.getChildCount();
        //Toast.makeText(context,childCount,Toast.LENGTH_SHORT).show();
        for (int i = 0; i < childCount; i++) {
            View childView = getChildAt(i);
            // 测量孩子
            measureChild(childView, widthMeasureSpec, heightMeasureSpec);

            // 测量完需要将孩子添加到管理行的孩子的集合中，将行添加到管理行的集合中

            if (mCurrentLine == null) {

                Log.e("space_horizontal","space_horizontal"+space_horizontal);
                // 初次添加第一个孩子的时候
                mCurrentLine = new Line(maxWidth, space_horizontal);

                // 添加孩子
                mCurrentLine.addView(childView);
                // 添加行
                mLines.add(mCurrentLine);

            } else {
                // 行中有孩子的时候，判断时候能添加
                if (mCurrentLine.canAddView(childView)) {
                    // 继续往该行里添加
                    mCurrentLine.addView(childView);
                } else {
                    //  添加到下一行
                    mCurrentLine = new Line(maxWidth, space_horizontal);
                    mCurrentLine.addView(childView);
                    mLines.add(mCurrentLine);
                }
            }
        }

        // ******************** 测量自己 *********************
        // 测量自己只需要计算高度，宽度肯定会被填充满的
        int height = getPaddingTop() + getPaddingBottom();
        for (int i = 0; i < mLines.size(); i++) {
            // 所有行的高度
            height += mLines.get(i).height;
        }
        // 所有竖直的间距
        height += (mLines.size() - 1) * space_vertical;

        // 测量
        setMeasuredDimension(width, height);

    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int i2, int i3) {
        // 这里只负责高度的位置，具体的宽度和子孩子的位置让具体的行去管理
        l = getPaddingLeft();
        t = getPaddingTop();
        for (int i = 0; i < mLines.size(); i++) {
            // 获取行
            Line line = mLines.get(i);
            // 管理
            line.layout(t, l);

            // 更新高度
            t += line.height;
            if (i != mLines.size() - 1) {
                // 不是最后一条就添加间距
                t += space_vertical;
            }
        }

    }

    /**
     * 内部类，行管理器，管理每一行的孩子
     */
    public class Line {
        // 定义一个行的集合来存放子View
        private List<View> views = new ArrayList<>();
        // 行的最大宽度
        private int maxWidth;
        // 行中已经使用的宽度
        private int usedWidth;
        // 行的高度
        private int height;
        // 孩子之间的距离
        private float space;

        // 通过构造初始化最大宽度和边距
        public Line(int maxWidth, float horizontalSpace) {
            this.maxWidth = maxWidth;
            this.space = horizontalSpace;
        }

        /**
         * 往集合里添加孩子
         */
        public void addView(View view) {
            int childWidth = view.getMeasuredWidth();
            int childHeight = view.getMeasuredHeight();

            // 更新行的使用宽度和高度
            if (views.size() == 0) {
                // 集合里没有孩子的时候
                if (childWidth > maxWidth) {
                    usedWidth = maxWidth;
                    height = childHeight;
                } else {
                    usedWidth = childWidth;
                    height = childHeight;
                }
            } else {
                usedWidth += childWidth + space;
                height = childHeight > height ? childHeight : height;
            }

            // 添加孩子到集合
            views.add(view);
        }


        /**
         * 判断当前的行是否能添加孩子
         *
         * @return
         */
        public boolean canAddView(View view) {
            // 集合里没有数据可以添加
            if (views.size() == 0) {
                return true;
            }

            // 最后一个孩子的宽度大于剩余宽度就不添加
            if (view.getMeasuredWidth() > (maxWidth - usedWidth - space)) {
                return false;
            }

            // 默认可以添加
            return true;
        }

        /**
         * 指定孩子显示的位置
         *
         * @param t
         * @param l
         */
        public void layout(int t, int l) {
            // 平分剩下的空间
            int avg = (maxWidth - usedWidth) / views.size();

            // 循环指定孩子位置
            for (View view : views) {
                // 获取宽高
                int measuredWidth = view.getMeasuredWidth();
                int measuredHeight = view.getMeasuredHeight();
                // 重新测量
                view.measure(MeasureSpec.makeMeasureSpec(measuredWidth + avg, MeasureSpec.EXACTLY),
                        MeasureSpec.makeMeasureSpec(measuredHeight, MeasureSpec.EXACTLY));
                // 重新获取宽度值
                measuredWidth = view.getMeasuredWidth();


                int top = t;
                int left = l;
                int right = measuredWidth + left;
                int bottom = measuredHeight + top;
                // 指定位置
                view.layout(left, top, right, bottom);

                // 更新数据
                l += measuredWidth + space;
            }
        }
    }

    

}