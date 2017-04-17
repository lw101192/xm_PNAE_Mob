package xm.mina;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Looper;
import android.support.annotation.WorkerThread;
import android.text.TextUtils;
import android.widget.Toast;

import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.service.IoConnector;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.keepalive.KeepAliveFilter;
import org.apache.mina.filter.keepalive.KeepAliveMessageFactory;
import org.apache.mina.filter.keepalive.KeepAliveRequestTimeoutHandler;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.example.xm.activities.MainActivity;
import com.example.xm.bean.StaticVar;
import com.example.xm.fragment.UserFragment;
import com.example.xm.thread.ReLoginThread;
import com.example.xm.util.GetIpPort;
import com.xm.Bean.ContentBean;
import com.xm.Bean.MessageBean;
import com.xm.Bean.UserBean;

/**
 * Created by liuwei on 2017/2/20.
 */

public class Client {
    public static final String TAG = "Client";
    private static Client instance = null;
    public Socket Client_Socket = null;
    public ObjectInputStream Client_in = null;
    public ObjectOutputStream Client_out = null;
    private ExecutorService executor = Executors.newCachedThreadPool();
    private ExecutorService mainQuene = Executors.newSingleThreadExecutor();
    private Context context;
    private boolean isServerIsConnected = false;
    private boolean isLogin = false;
    private IoConnector conn = null;
    private IoSession session = null;
    private ClientCallBack callBack = null;
    private String userID;
    private String nickName;
    private RequestCallBack requestCallBack;
    private boolean shortConnnection = false;
    private ReLoginThread reloginThread = null;
    public MessageBean response = null;
    private boolean isNetworkAvailable = false;
    public long fileLength;

    private static final int HEARTBEATRATE = 10;
    /**
     * 心跳包内容
     */
    private static final String HEARTBEATREQUEST = "0x11";
    private static final String HEARTBEATRESPONSE = "0x12";


    public static Client getInstance() {
        if (instance == null) {
            instance = new Client();
        }
        return instance;
    }

    public IoSession getSession() {
        return session;
    }

    public void init(Context context) {
        this.context = context.getApplicationContext();

        registerBraodCast();


    }

    public void closeNow(boolean now) {
        if (conn != null)
            conn.dispose();
        conn = null;
        if (session != null) {
            if (now)
                session.closeNow();
            else
                session.closeOnFlush();
        }

        session = null;

        isServerIsConnected = false;

        if (userID != null)
            userID = null;
    }


    private void connectServer() {
        closeNow(true);
        if (!isServerIsConnected()) {
            this.session = createSession();
        }
    }

    private IoSession createSession() {
        try {
            GetIpPort getIpPort = new GetIpPort();
            conn = new NioSocketConnector();
            conn.setConnectTimeoutMillis(5000L);
            conn.getFilterChain().addLast("code", new ProtocolCodecFilter(new MyObjectSerializationCodecFactory()));

            KeepAliveMessageFactory keepAliveMessageFactory = new KeepAliveMessageFactoryImpl();
//            KeepAliveRequestTimeoutHandlerImpl keepAliveRequestTimeoutHandlerImpl = new KeepAliveRequestTimeoutHandlerImpl();
            KeepAliveFilter keepAliveFilter = new KeepAliveFilter(keepAliveMessageFactory, IdleStatus.BOTH_IDLE, KeepAliveRequestTimeoutHandler.CLOSE);


            keepAliveFilter.setForwardEvent(true);
            keepAliveFilter.setRequestInterval(HEARTBEATRATE);
            keepAliveFilter.setRequestTimeout(20);
            conn.getFilterChain().addLast("heartbeat", keepAliveFilter);

            conn.setHandler(new ClientHandler());
//            conn.getSessionConfig().setIdleTime(IdleStatus.BOTH_IDLE,10);
            ConnectFuture future = conn.connect(new InetSocketAddress(getIpPort.getIpString(), Integer.parseInt(getIpPort.getPortString())));
            future.awaitUninterruptibly();
            session = future.getSession();
            isServerIsConnected = true;
        } catch (Exception e) {
            isServerIsConnected = false;
            callBack.onFaliure(0);
            if(UserFragment.handler!=null)
                UserFragment.handler.sendEmptyMessage(StaticVar.OFFLINE);
        }


        return session;
    }
//    class KeepAliveRequestTimeoutHandlerImpl implements KeepAliveRequestTimeoutHandler {
//
//        @Override
//        public void keepAliveRequestTimedOut(KeepAliveFilter arg0,
//                                             IoSession arg1) throws Exception {
//            // TODO Auto-generated method stub
//        }
//
//    }

    /**
     * @author cruise
     * @ClassName KeepAliveMessageFactoryImpl
     * @Description 内部类，实现KeepAliveMessageFactory（心跳工厂）
     */
    private static class KeepAliveMessageFactoryImpl implements
            KeepAliveMessageFactory {

        @Override
        public boolean isRequest(IoSession session, Object message) {
//            if (message.equals(HEARTBEATREQUEST))
//                return true;
            return false;
        }

        @Override
        public boolean isResponse(IoSession session, Object message) {
            return message.equals(HEARTBEATRESPONSE);
        }

        @Override
        public Object getRequest(IoSession session) {
            /** 返回预设语句 */
            return HEARTBEATREQUEST;
        }

        @Override
        public Object getResponse(IoSession session, Object request) {
            /** 返回预设语句 */
//            return HEARTBEATRESPONSE;
            return null;
        }

    }


    public void login(MessageBean messageBean, ClientCallBack callBack) {

        if (callBack == null) {
            throw new IllegalArgumentException("callback is null!");
        } else if (!TextUtils.isEmpty(messageBean.getFrom().getId()) && !TextUtils.isEmpty(messageBean.getFrom().getOriginpw())) {
            _login(messageBean, callBack, false);
        } else {
            throw new IllegalArgumentException("username or password is illeagal!");
        }

    }

    private void _login(final MessageBean messageBean, final ClientCallBack callBack, final boolean autoLogin) {

        this.callBack = callBack;
        this.excute(new Runnable() {
            @Override
            public void run() {
                if (!Client.this.isServerIsConnected()) {
                    connectServer();
                }

                if (session != null)
                    session.write(messageBean);


            }
        });
    }

    public void logout(MessageBean messageBean) {
        isLogin = false;//先置位isLogin，后closeNow(true)
        messageBean.getFrom().setType("mob/snaeii32");
        if (session != null) {
            session.write(messageBean);
        }
        closeNow(true);


    }

    void onSuccess(int var1, String var2) {
        callBack.onSuccess(var1, var2);
    }

    void onFaliure(int var1) {
        closeNow(true);
        callBack.onFaliure(var1);
    }

    public void onProgress(int var1, String var2) {
        callBack.onProgress(var1, var2);
    }

    public boolean isServerIsConnected() {
        return isServerIsConnected;
    }

    public boolean isLogin() {
        return isLogin;
    }

    void excute(Runnable var) {
        this.executor.execute(var);
    }


    public void NewUser(String uname) {
        userID = uname;
        isLogin = true;
    }

    public String getUserID() {
        return userID;
    }


    /**
     * 发送获得响应的请求
     *
     * @param messageBean
     * @param shortConnnection true为短链接
     * @param requestCallBack
     */
    public void sendRquestForResponse(MessageBean messageBean, boolean shortConnnection, RequestCallBack requestCallBack) {
        messageBean.getFrom().setType("mob/snaeii32");
        if (isNetworkAvailable(context)) {
            this.shortConnnection = shortConnnection;
            if (shortConnnection) {
                session = createSession();
            }
            if (session != null) {

                session.write(messageBean);
            } else {
                MainActivity.handler.sendEmptyMessage(StaticVar.RELOGIN);
            }

            this.requestCallBack = requestCallBack;
        } else {
            Looper.prepare();
            Toast.makeText(this.context, "网络异常", Toast.LENGTH_SHORT).show();
            Looper.loop();
        }


    }

    public void sendRquest(boolean shortConnnection, MessageBean messageBean) {
        messageBean.getFrom().setType("mob/snaeii32");
        if (isNetworkAvailable(context)) {
            if (shortConnnection) {
                this.shortConnnection = shortConnnection;
                session = createSession();
                if (session != null) {

                    session.write(messageBean);
                }
                session.closeNow();
//            session.getCloseFuture().awaitUninterruptibly();// 等待连接断开
//            session.closeOnFlush();

            } else {
                if (session != null) {

                    this.session.write(messageBean);
                } else {
                    MainActivity.handler.sendEmptyMessage(StaticVar.RELOGIN);
                }
            }
        } else {
            Looper.prepare();
            Toast.makeText(this.context, "网络异常", Toast.LENGTH_SHORT).show();
            Looper.loop();
        }

    }

    public void onResopnse(MessageBean messageBean) {
        this.requestCallBack.Response(messageBean);
//
        if (shortConnnection) {
            closeNow(true);
            shortConnnection = false;
        }
    }


    public void registerBraodCast() {
        this.context.registerReceiver(connectivityBroadcastReceiver, new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));
    }

    private BroadcastReceiver connectivityBroadcastReceiver = new BroadcastReceiver() {


        public void onReceive(Context var1, Intent var2) {
//            String var3 = var2.getAction();
//            if(!var3.equals("android.net.conn.CONNECTIVITY_CHANGE")) {
////                EMLog.d("EMClient", "skip no connectivity action");
//            } else {
////                EMLog.d("EMClient", "connectivity receiver onReceiver");
////                EMClient.this.excute(new Runnable() {
////                    public void run() {
////                        EMClient.this.onNetworkChanged();
////                    }
////                });
//            }


            if (isNetworkAvailable(var1)) {
                if (Client.getInstance().isLogin() && session == null)
                    MainActivity.handler.sendEmptyMessage(StaticVar.RELOGIN);
            } else {
                if (UserFragment.handler != null)
                    UserFragment.handler.sendEmptyMessage(StaticVar.OFFLINE);

                isServerIsConnected = false;
                session = null;
            }
        }
    };


    /**
     * 检测当的网络（WLAN、3G/2G）状态
     *
     * @param context Context
     * @return true 表示网络可用
     */
    public boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo info = connectivity.getActiveNetworkInfo();
            if (info != null && info.isConnected()) {
                // 当前网络是连接的
                if (info.getState() == NetworkInfo.State.CONNECTED) {
                    // 当前所连接的网络可用
                    isNetworkAvailable = true;
                    return true;
                }
            }
        }
        isNetworkAvailable = false;
        return false;
    }


    public MessageBean getDownloadFileRequestBean(String type, String filename) {
        MessageBean requestBean = new MessageBean();
        requestBean.setAction("downloadfile");
        UserBean from = new UserBean();
        from.setType(type);
        requestBean.setFrom(from);
        ContentBean contentBean = new ContentBean();
        contentBean.setContenttype(filename);
        requestBean.setContent(contentBean);
        return requestBean;
    }

}
