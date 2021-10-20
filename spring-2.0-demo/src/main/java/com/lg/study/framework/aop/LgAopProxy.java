package com.lg.study.framework.aop;

import com.sun.istack.internal.Nullable;

public interface LgAopProxy {

    Object getProxy();

    Object getProxy(@Nullable ClassLoader classLoader);
}
