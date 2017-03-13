package xm.mina;

import com.xm.Bean.MessageBean;

/**
 * Created by liuwei on 2017/2/21.
 */

public interface RequestCallBack<T> {
    void Response(T messageBean);
}
