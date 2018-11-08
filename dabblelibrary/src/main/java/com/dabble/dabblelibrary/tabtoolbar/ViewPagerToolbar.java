package com.dabble.dabblelibrary.tabtoolbar;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.view.View;

import com.dabble.dabblelibrary.R;

public class ViewPagerToolbar extends ConstraintLayout implements View.OnClickListener {

    private TypedArray a;
    private ViewPager viewPager;
    private ToolbarAdapter adapter;
    private int selectedColor, unselectedColor;
    private AppCompatTextView titleText;
    private AppCompatImageView leftImage;
    private AppCompatImageView rightImage;
    private int leftPosition = -1, centerPosition = -1, rightPosition = -1;

    // CONSTRUCTORS
    public ViewPagerToolbar(Context context) {
        super(context);

        a = context.getTheme().obtainStyledAttributes(R.styleable.ViewPagerToolbar);
        init();
    }

    public ViewPagerToolbar(Context context, AttributeSet attrs) {
        super(context, attrs);

        a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.ViewPagerToolbar, 0, 0);
        init();
    }

    public ViewPagerToolbar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.ViewPagerToolbar, 0, 0);
        init();
    }

    private void init() {

        inflate(getContext(), R.layout.tab_toolbar, this);

        this.titleText = findViewById(R.id.toolbar_title);
        this.leftImage = findViewById(R.id.toolbar_left);
        this.rightImage = findViewById(R.id.toolbar_right);

        selectedColor = a.getColor(R.styleable.ViewPagerToolbar_selected_tab_color, Color.BLACK);
        unselectedColor = a.getColor(R.styleable.ViewPagerToolbar_unselected_tab_color, Color.LTGRAY);

        titleText.setTextColor(selectedColor);
        leftImage.setColorFilter(unselectedColor);
        rightImage.setColorFilter(unselectedColor);

        a.recycle();
    }

    // METHODS
    public ViewPagerToolbar setUpWithViewPager(ViewPager viewPager, final FragmentManager fragmentManager) {

        this.viewPager = viewPager;

        this.adapter = new ToolbarAdapter(fragmentManager);

        this.viewPager.setAdapter(adapter);

        this.viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                if (position == centerPosition) {

                    titleText.setTextColor(selectedColor);
                    leftImage.setColorFilter(unselectedColor);
                    rightImage.setColorFilter(unselectedColor);

                } else if (position == leftPosition) {
                    titleText.setTextColor(unselectedColor);
                    leftImage.setColorFilter(selectedColor);
                    rightImage.setColorFilter(unselectedColor);

                } else if (position == rightPosition) {
                    titleText.setTextColor(unselectedColor);
                    leftImage.setColorFilter(unselectedColor);
                    rightImage.setColorFilter(selectedColor);
                    }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        return this;
    }

    public ViewPagerToolbar setTitle(String title) {

        titleText.setOnClickListener(this);

        titleText.setText(title);

        return this;
    }

    public ViewPagerToolbar setLeftDrawable(Drawable drawable) {

        leftImage.setImageDrawable(drawable);
        leftImage.setOnClickListener(this);

        return this;
    }

    public AppCompatImageView getLeftImage() {
        return leftImage;
    }

    public ViewPagerToolbar setRightDrawable(Drawable drawable) {

        rightImage.setImageDrawable(drawable);
        rightImage.setOnClickListener(this);

        return this;
    }

    public ViewPagerToolbar setCenterFragment(@Nullable Fragment fragment) {

        if (fragment == null && centerPosition != -1) {
            adapter.removeFragment(centerPosition);
            centerPosition = -1;
        } else {
            centerPosition = adapter.getCount();
            adapter.addFragment(fragment);
        }

        viewPager.setAdapter(adapter);

        return this;
    }

    public ViewPagerToolbar setLeftFragment(@Nullable Fragment fragment) {

        if (fragment == null && leftPosition != -1) {
            adapter.removeFragment(leftPosition);
            leftPosition = -1;
        } else {
            leftPosition = adapter.getCount();
            adapter.addFragment(fragment);
        }

        viewPager.setAdapter(adapter);

        return this;
    }

    public ViewPagerToolbar setRightFragment(@Nullable Fragment fragment) {

        if (fragment == null && rightPosition != -1) {
            adapter.removeFragment(rightPosition);
            rightPosition = -1;
        } else {
            rightPosition = adapter.getCount();
            adapter.addFragment(fragment);
        }

        viewPager.setAdapter(adapter);

        return this;
    }

    public ViewPagerToolbar setCenterListener(@Nullable View.OnClickListener listener) {

        if (listener == null) {
            titleText.setOnClickListener(this);
        } else {
            setCenterFragment(null);
            titleText.setOnClickListener(listener);
        }
        return this;
    }

    public ViewPagerToolbar setLeftListener(@Nullable View.OnClickListener listener) {

        if (listener == null) {
            leftImage.setOnClickListener(this);
        } else {
            setLeftFragment(null);
            leftImage.setOnClickListener(listener);
        }

        return this;
    }

    public ViewPagerToolbar setRightListener(@Nullable View.OnClickListener listener) {

        if (listener == null) {
            rightImage.setOnClickListener(this);
        } else {
            setRightFragment(null);
            rightImage.setOnClickListener(listener);
        }

        return this;
    }

    public void build() {

        if (viewPager.getAdapter().getCount() > 0)
            viewPager.setCurrentItem(0);

        if (centerPosition != -1) {
            viewPager.setCurrentItem(centerPosition);
        }
    }

    //ON CLICK LISTENERS
    @Override
    public void onClick(View v) {

        if (v.getId() == titleText.getId() && centerPosition != -1) {

            viewPager.setCurrentItem(centerPosition);

        } else if (v.getId() == leftImage.getId() && leftPosition != -1) {

            viewPager.setCurrentItem(leftPosition);

        } else if (v.getId() == rightImage.getId() && rightPosition != -1) {

            viewPager.setCurrentItem(rightPosition);
        }
    }
}
