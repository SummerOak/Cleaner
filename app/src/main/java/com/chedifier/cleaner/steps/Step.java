package com.chedifier.cleaner.steps;

/**
 * Created by Administrator on 2017/8/18.
 */

public abstract class Step {

    private Step mNext;

    public abstract void doAction();

    protected void doNext(){
        if(mNext != null){
            mNext.doAction();
        }
    }

    public Step setNext(Step next){
        mNext = next;
        return this;
    }

}
