package com.gencent.concurrent;

public interface CallbackTask<R>
{

    R execute() throws Exception;

    void onBack(R r);

    void onException(Throwable t);
}
