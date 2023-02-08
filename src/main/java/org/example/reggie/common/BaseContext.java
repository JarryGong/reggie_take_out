package org.example.reggie.common;

/**
 * 基于ThreadLocal封装工具类用户保存和获取当前登录用户id
 */
public class BaseContext {
    private static final ThreadLocal<Long> threadLocal = new ThreadLocal<>();

    /**
     * 设置id
     * @param id
     */
    public static void setCurrentId(Long id){
        threadLocal.set(id);
    }

    /**
     * 获取id
     * @return
     */
    public static Long getCurrentId(){
        return threadLocal.get();
    }

    /**
     * 使用之后移除值，防止内存泄露
     */
    public static void removeCurrentId(){
        if (Thread.currentThread().isAlive()){
            threadLocal.remove();
        }
        //移除存放在当前线程里面的id值，防止内存泄露
        //BaseContext.removeCurrentId();
    }
}
