package com.jiulongteng.pipeline;

import java.util.concurrent.Executor;

/**
 * @des:
 * @author: Administrator
 * @createDate: 2022/4/3 0003 15:07
 * @version: 3.3.2
 * @updateDate:
 * @updateUser:
 * @updateRemark:
 * @see {@link }
 */
public interface MainThreadExecutor extends Executor {


    boolean isMainThread();
}
