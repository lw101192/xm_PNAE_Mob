package xm.mina;

/**
 * Created by liuwei on 2017/2/20.
 */
public interface ClientCallBack {
    void onSuccess(int var1,String var2);

    void onFaliure(int var1);

    void onProgress(int var1,String var2);
}
