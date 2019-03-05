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

/**
 * The interface Rvh view holder.
 */
public interface RVHViewHolder {

  /**
   * Called when the ItemTouchHelper first registers an item as being moved or swiped.
   * Implementations should update the item view to indicate it's active state.
   *
   * @param actionstate
   *     the actionstate
   */
  void onItemSelected(int actionstate,int position);

  /**
   * Called when the ItemTouchHelper has completed the move or swipe, and the active item
   * state should be cleared.
   */
  void onItemClear();
}
