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

import android.graphics.Canvas;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import static android.support.v7.widget.helper.ItemTouchHelper.ACTION_STATE_IDLE;
import static android.support.v7.widget.helper.ItemTouchHelper.ACTION_STATE_SWIPE;
import static android.support.v7.widget.helper.ItemTouchHelper.Callback;
import static android.support.v7.widget.helper.ItemTouchHelper.DOWN;
import static android.support.v7.widget.helper.ItemTouchHelper.END;
import static android.support.v7.widget.helper.ItemTouchHelper.START;
import static android.support.v7.widget.helper.ItemTouchHelper.UP;

/**
 * The type Rvh item touch helper callback.
 */
public class RVHItemTouchHelperCallback extends Callback {

  @Deprecated
  private final RVHAdapter mAdapter;

  private final boolean isLongPressDragEnabled;
  private final boolean isItemViewSwipeEnabledLeft;
  private final boolean isItemViewSwipeEnabledRight;
  public boolean disableSwipe;
  public int orginIndex;

  public RecyclerView referenceRV;

  /**
   * Instantiates a new Rvh item touch helper callback.
   *
   * @param adapter
   *     the adapter
   * @param isLongPressDragEnabled
   *     the is long press drag enabled
   * @param isItemViewSwipeEnabledLeft
   *     the is item view swipe enabled left
   * @param isItemViewSwipeEnabledRight
   *     the is item view swipe enabled right
   */
  public RVHItemTouchHelperCallback(RVHAdapter adapter, boolean isLongPressDragEnabled,
                                    boolean isItemViewSwipeEnabledLeft, boolean isItemViewSwipeEnabledRight) {
    mAdapter = adapter;
    this.isItemViewSwipeEnabledLeft = isItemViewSwipeEnabledLeft;
    this.isItemViewSwipeEnabledRight = isItemViewSwipeEnabledRight;
    this.isLongPressDragEnabled = isLongPressDragEnabled;
  }

  @Override
  public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
    final int dragFlags = UP | DOWN | START | END; // UP | DOWN
    final int swipeFlags;
    if (isItemViewSwipeEnabledLeft && isItemViewSwipeEnabledRight) {
      swipeFlags = START | END  ; // START | END
    }
    else if (isItemViewSwipeEnabledRight) {
      swipeFlags = START;
    }
    else {
      swipeFlags = END;
    }

    return Callback.makeMovementFlags(dragFlags, swipeFlags);
  }

  @Override
  public boolean canDropOver(RecyclerView recyclerView, RecyclerView.ViewHolder current,
                             RecyclerView.ViewHolder target) {
    return current.getItemViewType() == target.getItemViewType();
  }

  @Override
  public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder source,
                        RecyclerView.ViewHolder target) {
    // Notify the adapter of the move
    ((RVHAdapter) referenceRV.getAdapter()).onItemMove(source.getAdapterPosition(), target.getAdapterPosition());
    return true;
  }

  @Override
  public boolean isLongPressDragEnabled() {
    return isLongPressDragEnabled;
  }

  @Override
  public boolean isItemViewSwipeEnabled() {

    if(disableSwipe){
      return false;
    }
    else{

      return isItemViewSwipeEnabledLeft || isItemViewSwipeEnabledRight;
    }
  }

  @Override
  public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
    Log.e("Swipe","On Swipe!");
    ((RVHAdapter) referenceRV.getAdapter()).onItemDismiss(viewHolder.getAdapterPosition(), direction);
  }

  @Override
  public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
    // We only want the active item to change
    if (actionState != ACTION_STATE_IDLE && viewHolder instanceof RVHViewHolder) {
      // Let the view holder know that this item is being moved or dragged
      RVHViewHolder itemViewHolder = (RVHViewHolder) viewHolder;
      itemViewHolder.onItemSelected(actionState,viewHolder.getAdapterPosition());
    }

    super.onSelectedChanged(viewHolder, actionState);
  }

  @Override
  public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
    super.clearView(recyclerView, viewHolder);

    if (viewHolder instanceof RVHViewHolder) {
      // Tell the view holder it's time to restore the idle state
      RVHViewHolder itemViewHolder = (RVHViewHolder) viewHolder;
      itemViewHolder.onItemClear();
    }
  }

  @Override
  public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                          float dX, float dY, int actionState, boolean isCurrentlyActive) {
    if (actionState == ACTION_STATE_SWIPE ) {
      // Fade out the view as it is swiped out of the parent's bounds

      if(orginIndex !=0){

        viewHolder.itemView.setTranslationX(dX);
        viewHolder.itemView.setTranslationY(dY); //
        ((RVHAdapter) referenceRV.getAdapter()).onItemSwipeDrag(dX,dY,viewHolder.getAdapterPosition()); //
      }


//      viewHolder.itemView.setTranslationX(dX);
//      viewHolder.itemView.setTranslationY(dY); //
//      ((RVHAdapter) referenceRV.getAdapter()).onItemSwipeDrag(dX,dY,viewHolder.getAdapterPosition()); //
    }
    else {
      //super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);

      if(orginIndex !=0){
        ((RVHAdapter) referenceRV.getAdapter()).onItemLongPress(dX,dY,viewHolder.getAdapterPosition());
      }
      //Log.e("Here","here");
    }
  }

}
