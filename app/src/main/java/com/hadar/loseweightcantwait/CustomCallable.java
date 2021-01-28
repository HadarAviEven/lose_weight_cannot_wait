package com.hadar.loseweightcantwait;

import java.util.concurrent.Callable;

public interface CustomCallable<R> extends Callable<R> {

    void setUiForLoading();

    void setDataAfterLoading(R result);
}
