package com.martinrgb.waterfalllayout.recyclerview;

import android.content.Context;
import android.os.Handler;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.martinrgb.waterfalllayout.R;
import com.zach.salman.springylib.springyRecyclerView.SpringyAdapterAnimationType;
import com.zach.salman.springylib.springyRecyclerView.SpringyAdapterAnimator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import github.nisrulz.recyclerviewhelper.RVHAdapter;
import github.nisrulz.recyclerviewhelper.RVHItemClickListener;
import github.nisrulz.recyclerviewhelper.RVHViewHolder;

import com.facebook.rebound.SimpleSpringListener;
import com.facebook.rebound.Spring;
import com.facebook.rebound.SpringConfig;
import com.facebook.rebound.SpringSystem;
import com.facebook.rebound.SpringUtil;

/**
 * Created by Zach on 6/30/2017.
 */


//public class Adapter extends BaseRecyclerAdapter<Card>
public class Adapter extends RecyclerView.Adapter<Adapter.ItemViewHolder> implements RVHAdapter {



    private SpringyAdapterAnimator mAnimator;
    public final List<Card> CardList = new ArrayList<>();

    public ImageView icon,cardImg;
    public CardView card;
    public TextView title;
    public boolean disableItemEventListener = false;
    private Context mContext;
    private boolean hasVibrate = false;
    private static final int VIEW_TYPE_ONE = 1;
    private static final int VIEW_TYPE_TWO = 2;
    private RecyclerView rv;

    private View prevDragView;
    private int prevLongPressPosition;
    public boolean canSwap = false;
    private long downTime, upTime;


    public Adapter(List<Card> cardList , RecyclerView recyclerView,onItemEventListener listener) {
        this.mListener = listener;
        //this.CardList = cardList;
        //notifyData(cardList);
        this.rv = recyclerView;
        mAnimator = new SpringyAdapterAnimator(recyclerView);
        mAnimator.setSpringAnimationType(SpringyAdapterAnimationType.SLIDE_FROM_BOTTOM);
        mAnimator.addConfig(100,18);
    }

//    public View getViewByPosition(int pos, RecyclerView recyclerView) {
//        return recyclerView.getChildAt(pos);
//    }

    public void notifyData(List<Card> poiItemList) {
        if (poiItemList != null) {
            int previousSize = CardList.size();
            CardList.clear();
            notifyItemRangeRemoved(0, previousSize);
            CardList.addAll(poiItemList);
            notifyItemRangeInserted(0, poiItemList.size());
            Log.e("ValueNotify",String.valueOf(CardList.size()));

        }
    }




    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        mContext = parent.getContext();


        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item, parent, false);

        itemView.setClipToOutline(false);
        return new ItemViewHolder(itemView);


    }



    int[] startPosition =  new int[2];
//    private boolean isScaled = true;

    private int lastAnimatedPosition = -1;
    @Override
    public void onViewDetachedFromWindow(final ItemViewHolder holder) {
        holder.clearAnimation();
    }

    @Override
    public void onBindViewHolder(final ItemViewHolder holder, final int position) {


        if(position == 0){
            card.getLayoutParams().width = 468;
            card.getLayoutParams().height = 720; //720
            card.requestLayout();
        }





        cardImg.setImageResource(CardList.get(position).imgSrc);

        if(CardList.get(position).name !=null){
            title.setText(CardList.get(position).name);
            icon.setImageResource(CardList.get(position).iconSrc);

        }

        holder.itemTagIndex = CardList.get(position).tagNum;

//        if (position > lastAnimatedPosition) {
//            lastAnimatedPosition = position;
//            Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.slide_up);
//            animation.setInterpolator(new AccelerateDecelerateInterpolator());
//            ((ItemViewHolder) holder).itemView.setAnimation(animation);
//            animation.start();
//        }
//
//        if (position == 0) {
//            lastAnimatedPosition = position;
//            Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.slide_up);
//            animation.setInterpolator(new AccelerateDecelerateInterpolator());
//            ((ItemViewHolder) holder).itemView.setAnimation(animation);
//            animation.start();
//        }



        //mAnimator.onSpringItemBind(holder.itemView, position);


        // 瞎改

        mAnimator.setSpringAnimationType(SpringyAdapterAnimationType.NULL);
        mAnimator.onSpringItemCreate(holder.itemView,position);
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//
//                mAnimator.setSpringAnimationType(SpringyAdapterAnimationType.NULL);
//                mAnimator.onSpringItemCreate(holder.itemView,position);
//
//            }
//        },100 );


//        final View measureView = holder.itemView;
//        measureView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//
//            @Override
//            public void onGlobalLayout() {
//                // Do what you need to do here.
//                // Then remove the listener:
//
//
//                measureView.getLocationInWindow(startPosition);
//                //Log.e("PosX", String.valueOf(startPosition[0]));
//                //Log.e("PosY", String.valueOf(startPosition[1]));
//                measureView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
//            }
//
//        });

        //Orig
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Updating old as well as new positions

                if(!disableItemEventListener) {
                    //int finalOrigPos =  CardList.get(holder.getAdapterPosition()).tagNum;
                    mListener.onItemClick(v, CardList.get(holder.getAdapterPosition()), holder.getAdapterPosition(),holder.itemTagIndex);
                    hasSwaped = false;

                }

            }
        });

//        holder.itemView.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View view, MotionEvent e) {
//                switch (e.getAction()) {
//                    case MotionEvent.ACTION_DOWN:
//                        Log.e("Down","DOWN");
//                        return false;
//                    case MotionEvent.ACTION_UP:
//                        Log.e("Down","DOWN-UP");
//                        return false;
//                    case MotionEvent.ACTION_CANCEL:
//                        break;
//                }
//                return true;
//            }
//        });


    }

    @Override
    public int getItemCount() {
        return CardList.size();
    }

    public void onItemAnimContinue(){

        final int size = mAnimator.mSpringListArray.size();

        mAnimator.setSpringAnimationType(SpringyAdapterAnimationType.SLIDE_FROM_BOTTOM);

        for(int i =0;i<size;i++){

            if(rv.getLayoutManager().findViewByPosition(i) !=null){

                mAnimator.mSpringListArray.get(i).setEndValue(1);
            }
        }

    }


    public class ItemViewHolder extends RecyclerView.ViewHolder implements RVHViewHolder {
        protected View container;
        public int getItemTagIndex() {
            return itemTagIndex;
        }

        public void setItemTagIndex(int itemTagIndex) {
            this.itemTagIndex = itemTagIndex;
        }

        public int itemTagIndex = -1;

        public ItemViewHolder(View view) {
            super(view);
            container = view;
            title =  container.findViewById(R.id.tv_title);
            icon = container.findViewById(R.id.iv_icon);
            card = container.findViewById(R.id.iv_card);
            cardImg = container.findViewById(R.id.iv_cardimg);


            card.setElevation(18);
        }


        @Override
        public void onItemSelected(int actionstate,int position) {
            System.out.println("Item is selected,"+"Number is " + Integer.valueOf(position));



            hasSelect = true;





            if(rv.getLayoutManager().findViewByPosition(4) == null && CardList.size() > 4 ){

                rv.getLayoutParams().height = 6000;
                rv.requestLayout();
                //Log.e("Here","213213231");
            }
            else if(rv.getLayoutManager().findViewByPosition(0) == null){
                //Log.e("Here","21321325342523452345235231");
            }



            prevDragView = rv.findViewHolderForAdapterPosition(position).itemView;
            prevDragView.setTranslationZ(10000);

            if(!disableItemEventListener) {
                mListener.onItemSelected(position);
            }

        }

        public void clearAnimation() {
            container.clearAnimation();
        }


        @Override
        public void onItemClear() {
            System.out.println("Item is unselected");
            mListener.onItemOnDragging(false);


            if(hasSelect && rv.getLayoutParams().height != 2340){
                rv.getLayoutParams().height = 2340;
                rv.requestLayout();
                hasSelect = false;
            }


            if(prevDragView != null){
                prevDragView.setTranslationZ(0);
            }

            if(!disableItemEventListener) {
                mListener.onItemUnselected(prevLongPressPosition);
                mListener.onItemLongPressed(false, prevLongPressPosition);

            }

            //mListener.onItemUnselected(prevLongPressPosition);
            //mListener.onItemLongPressed(false, prevLongPressPosition);


        }
    }

    private boolean hasSwaped = false;
    private int removedPos;
    private boolean hasRemoved = false;
    private boolean hasSelect = false;
    private int firstSwapPos,secondSwapPos;

    // ###### Delete Item ######
    private void remove(int position) {


            Log.e("Value",String.valueOf(CardList.size()));

            CardList.remove(position);

            mListener.onItemRemove(position,CardList.size());
            notifyItemRemoved(position);




            if(hasSelect && rv.getLayoutParams().height != 2340){

                if(CardList.size() == 2){
                    rv.getLayoutParams().height = 10000;
                    rv.requestLayout();
                }
                else{

                    rv.getLayoutParams().height = 2340;
                    rv.requestLayout();
                }

            }

            if(hasSwaped){

                if(position == firstSwapPos){
                    System.out.println("Item is removed at " + secondSwapPos);
                }
                else if(position == secondSwapPos){
                    System.out.println("Item is removed at " + firstSwapPos);
                }
            }
            else{
                System.out.println("Item is removed at hasentSwp " + position);
            }
    }


    @Override
    public void onItemDismiss(int position, int direction) {
        if(position != 0){
            remove(position);
            hasRemoved = true;
            removedPos = position;
            Log.e("removePos",String.valueOf(removedPos));

//            if(rv.getLayoutManager().findViewByPosition(removedPos) !=null){
//
//                rv.getLayoutManager().findViewByPosition(removedPos).findViewById(R.id.cardcontainer).setScaleX(1);
//                rv.getLayoutManager().findViewByPosition(removedPos).findViewById(R.id.cardcontainer).setScaleY(1);
//            }
//            new Handler().postDelayed(new Runnable() {
//                @Override
//                public void run() {
//
//
//                }
//            },100 );
//            mSpring.setCurrentValue(0);
        }
    }

    // ###### Swap Item (deprecated) ######
    private void swap(int firstPosition, int secondPosition) {
        System.out.println("Item " + Integer.valueOf(firstPosition) + " and " + Integer.valueOf(secondPosition) + " is swapped");


        Collections.swap(CardList, firstPosition, secondPosition);
        mListener.onItemSwap(firstPosition, secondPosition);
        notifyItemMoved(firstPosition, secondPosition);

        Log.e("first",String.valueOf(firstPosition));
        Log.e("second",String.valueOf(secondPosition));




        hasSwaped = true;
        firstSwapPos = firstPosition;
        secondSwapPos = secondPosition;
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        if(CardList.size() != 0 && canSwap) {
            swap(fromPosition, toPosition);
        }


        return false;
    }


    public void outTransition(){
        final int size = this.CardList.size();


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                if (size > 0) {
                    for (int i = 0; i < size; i++) {
                        if(CardList.size()>0){

                            CardList.remove(0);
                        }
                    }

                    notifyItemRangeRemoved(0, size);
                }
            }
        },300 );
    }

    public void outTransition2(){
        final int size = this.CardList.size();


        mAnimator.setSpringAnimationType(SpringyAdapterAnimationType.SLIDE_FROM_TOP);
        mAnimator.DELETE_PER_DELAY = 0;

        for(int i =0;i<size;i++){

            if(rv.getLayoutManager().findViewByPosition(i) !=null){

                mAnimator.onSpringItemDelete(rv.getLayoutManager().findViewByPosition(i),i);
            }
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                if (size > 0) {
                    for (int i = 0; i < size; i++) {
                        if(CardList.size()>0){

                            CardList.remove(0);
                        }
                    }

                    notifyItemRangeRemoved(0, size);
                }
            }
        },300 );
    }

    public void outTransition3(){
        final int size = this.CardList.size();


        mAnimator.setSpringAnimationType(SpringyAdapterAnimationType.SLIDE_FROM_BOTTOM);
        mAnimator.DELETE_PER_DELAY = 0;

        for(int i =0;i<size;i++){

            if(rv.getLayoutManager().findViewByPosition(i) !=null){

                mAnimator.onSpringItemDelete(rv.getLayoutManager().findViewByPosition(i),i);
            }
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                if (size > 0) {
                    for (int i = 0; i < size; i++) {
                        if(CardList.size()>0){

                            CardList.remove(0);
                        }
                    }

                    notifyItemRangeRemoved(0, size);
                }
            }
        },300 );
    }

    public void removeAll(){
        final int size = this.CardList.size();
        final int size2 =  mAnimator.mSpringListArray.size();

        mAnimator.setSpringAnimationType(SpringyAdapterAnimationType.SPREAD);
        mAnimator.DELETE_PER_DELAY = 50;

        for(int i =0;i<size;i++){

            if(rv.getLayoutManager().findViewByPosition(i) !=null){

                mAnimator.onSpringItemDelete(rv.getLayoutManager().findViewByPosition(i),i);
            }
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                if (size > 0) {
                    for (int i = 0; i < size; i++) {
                        CardList.remove(0);
                    }

                    notifyItemRangeRemoved(0, size);
                }
            }
        },300 );


    }


    // ###### Drag Item ######
    private int lastDragPos;
    @Override
    public void onItemSwipeDrag(float dX,float dY,int position) {
//        System.out.println("Item On Drag" + "at " + Integer.valueOf(position));
//        if(!hasVibrate){
//
//            VibratorUtil.Vibrate(mContext,10);
//            hasVibrate =true;
//
//        }

        if(rv.getLayoutManager().findViewByPosition(position) !=null){
            mListener.onItemOnDragging(true);

//            if(rv.getLayoutManager().findViewByPosition(position).findViewById(R.id.cardcontainer).getScaleX() == 1){
//                rv.getLayoutManager().findViewByPosition(position).findViewById(R.id.cardcontainer).animate().scaleX(1.05f).setDuration(50).setInterpolator(new DecelerateInterpolator()).start();
//                rv.getLayoutManager().findViewByPosition(position).findViewById(R.id.cardcontainer).animate().scaleY(1.05f).setDuration(50).setInterpolator(new DecelerateInterpolator()).start();
//            }

//            rv.getLayoutManager().findViewByPosition(position).findViewById(R.id.cardcontainer).setScaleX(Math.min(1.05f,1 + Math.abs(dX/4000)));
//            rv.getLayoutManager().findViewByPosition(position).findViewById(R.id.cardcontainer).setScaleY(Math.min(1.05f,1 + Math.abs(dX/4000)));
//            rv.getLayoutManager().findViewByPosition(position).findViewById(R.id.cardcontainer).setTranslationX(dX);
            rv.getLayoutManager().findViewByPosition(position).findViewById(R.id.iv_card).setElevation(18 + Math.abs(dX)/200);
            rv.getLayoutManager().findViewByPosition(position).findViewById(R.id.iv_container).setAlpha(1 - Math.abs(dX/500));
            rv.getLayoutManager().findViewByPosition(position).findViewById(R.id.iv_container).setTranslationX(-dX);
        }
    }

    // ###### Long Press Item (deprecated) ######
    @Override
    public void onItemLongPress(float dX,float dY,int position) {
        //System.out.println("Item On Long Press" + "at " + Integer.valueOf(position) + "X is " + Float.valueOf(dX) + "Y is " + Float.valueOf(dY));

//        if(position !=0){
//            System.out.println("Long Press2");
//
//            if(!disableItemEventListener){
//                mListener.onItemLongPressed(true,position);
//            }
//
//            prevLongPressPosition = position;
//        }

        //System.out.println("Long Press2");

        if(!disableItemEventListener){
            mListener.onItemOnDragging(false);
            mListener.onItemLongPressed(true,position);
        }

        prevLongPressPosition = position;

    }


    private final onItemEventListener mListener;
    public interface onItemEventListener {
        /**
         * On item click.
         *
         * @param view
         *     the view
         * @param position
         *     the position
         */
        void onItemSelected(int position);
        void onItemUnselected(int position);
        void onItemLongPressed(boolean boo,int position);
        void onItemClick(View view, Card item,int position,int orignPos);
        void onItemRemove(int pos,int itemSize);
        void onItemSwap(int firstPosition, int secondPosition);
        void onItemOnDragging(boolean boo);
    }


}