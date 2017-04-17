package xm.mina;


import android.os.Message;

import com.example.xm.activities.MainActivity;
import com.example.xm.bean.StaticVar;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;

import com.xm.Bean.MessageBean;

/**
 * Created by liuwei on 2017/1/20.
 */
class ClientHandler extends IoHandlerAdapter {

    private boolean isKicked = false;
    @Override
    public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
        cause.printStackTrace();
    }

    @Override
    public void messageReceived(IoSession session, Object message) throws Exception {
            super.messageReceived(session,message);
        if(message instanceof MessageBean){
            MessageBean messageBean = (MessageBean)message;
//            File filePath = Environment.getExternalStorageDirectory();
//            String savePath = filePath + "/" + "32.doc";
//            FileOutputStream out = new FileOutputStream(savePath);
//            FileChannel fc = out.getChannel();
//            fc.write(ByteBuffer.wrap(messageBean.getContent().getBytecontent()));
            switch (messageBean.getAction()){
                case "login":
                    if(messageBean.getAckcode()==10){
                            Client.getInstance().onSuccess(messageBean.getAckcode(),messageBean.getAction());
                        }else{
                            Client.getInstance().onFaliure(messageBean.getAckcode());
                        }

                    break;
                case "kickoff":
                    isKicked = true;
//                    if(session==Client.getInstance().getSession()){
                        MainActivity.handler.sendEmptyMessage(StaticVar.LADNDING_IN_DIFFERENT_PLACES);
                        Client.getInstance().closeNow(true);
//                    }
                    break;
                case "SendCmd":
                    Object[] objmsg = new Object[3];
                    objmsg[0] = messageBean.getFrom().getId();//FromID
                    objmsg[1] = messageBean.getTo().getId();//ToID
                    objmsg[2] = messageBean.getContent().getStringcontent();//Content
                    Message.obtain(MainActivity.handler, StaticVar.SEND_MESSAGE, objmsg).sendToTarget();
                    break;
                case "resonse":
                default:
                    Client.getInstance().onResopnse(messageBean);
                    break;
            }
        }


    }

    @Override
    public void messageSent(IoSession session, Object message) throws Exception {
        super.messageSent(session, message);
    }

    @Override
    public void sessionClosed(IoSession session) throws Exception {
        super.sessionClosed(session);
        Client.getInstance().closeNow(true);
        if(Client.getInstance().isLogin()&&!isKicked)
            MainActivity.handler.sendEmptyMessage(StaticVar.RELOGIN);

    }

    @Override
    public void sessionCreated(IoSession session) throws Exception {
        super.sessionCreated(session);
    }

    @Override
    public void sessionOpened(IoSession session) throws Exception {
        super.sessionOpened(session);
    }

    @Override
    public void sessionIdle(IoSession session, IdleStatus status) throws Exception {
        super.sessionIdle(session, status);
    }
}
