/*
 * Copyright (C) 2016 Nishant Srivastava
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package github.nisrulz.recyclerviewhelper;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * The type Rvh item divider decoration.
 */
public class RVHItemDividerDecoration extends RecyclerView.ItemDecoration {

  /**
   * The constant HORIZONTAL_LIST.
   */
  public static final int HORIZONTAL_LIST = LinearLayoutManager.HORIZONTAL;
  /**
   * The constant VERTICAL_LIST.
   */
  public static final int VERTICAL_LIST = LinearLayoutManager.VERTICAL;
  private static final int[] ATTRS = new int[] {
      android.R.attr.listDivider
  };
  private final Drawable mDivider;

  private int mOrientation;

  /**
   * Instantiates a new Rvh item divider decoration.
   *
   * @param context
   *     the context
   * @param orientation
   *     the orientation
   */
  public RVHItemDividerDecoration(Context context, int orientation) {
    final TypedArray a = context.obtainStyledAttributes(ATTRS);
    mDivider = a.getDrawable(0);
    a.recycle();
    setOrientation(orientation);
  }

  /**
   * Sets orientation.
   *
   * @param orientation
   *     the orientation
   */
  public void setOrientation(int orientation) {
    if (orientation != HORIZONTAL_LIST && orientation != VERTICAL_LIST) {
      throw new IllegalArgumentException("invalid orientation");
    }
    mOrientation = orientation;
  }

  @Override
  public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
    super.onDraw(c, parent, state);

    if (mOrientation == VERTICAL_LIST) {
      drawVertical(c, parent);
    } else {
      drawHorizontal(c, parent);
    }
  }

  /**
   * Draw vertical.
   *
   * @param c
   *     the c
   * @param parent
   *     the parent
   */
  private void drawVertical(Canvas c, RecyclerView parent) {
    final int left = parent.getPaddingLeft();
    final int right = parent.getWidth() - parent.getPaddingRight();

    final int childCount = parent.getChildCount();
    for (int i = 0; i < childCount; i++) {
      final View child = parent.getChildAt(i);
      final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
      final int top = child.getBottom() + params.bottomMargin + Math.round(child.getTranslationY());
      final int bottom = top + mDivider.getIntrinsicHeight();
      mDivider.setBounds(left, top, right, bottom);
      mDivider.draw(c);
    }
  }

  /**
   * Draw horizontal.
   *
   * @param c
   *     the c
   * @param parent
   *     the parent
   */
  private void drawHorizontal(Canvas c, RecyclerView parent) {
    final int top = parent.getPaddingTop();
    final int bottom = parent.getHeight() - parent.getPaddingBottom();

    final int childCount = parent.getChildCount();
    for (int i = 0; i < childCount; i++) {
      final View child = parent.getChildAt(i);
      final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
      final int left = child.getRight() + params.rightMargin + Math.round(child.getTranslationX());
      final int right = left + mDivider.getIntrinsicHeight();
      mDivider.setBounds(left, top, right, bottom);
      mDivider.draw(c);
    }
  }

  @Override
  public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
      RecyclerView.State state) {
    super.getItemOffsets(outRect, view, parent, state);

    if (mOrientation == VERTICAL_LIST) {
      outRect.set(0, 0, 0, mDivider.getIntrinsicHeight());
    } else {
      outRect.set(0, 0, mDivider.getIntrinsicWidth(), 0);
    }
  }
}