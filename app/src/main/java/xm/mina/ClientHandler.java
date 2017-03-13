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
public class ClientHandler extends IoHandlerAdapter {
    @Override
    public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
        cause.printStackTrace();
    }

    @Override
    public void messageReceived(IoSession session, Object message) throws Exception {
            super.messageReceived(session,message);
        System.out.println("获得服务器传过来的数据");
        if(message instanceof MessageBean){
            MessageBean messageBean = (MessageBean)message;
            System.out.println("action>>"+messageBean.getAction());
//            File filePath = Environment.getExternalStorageDirectory();
//            String savePath = filePath + "/" + "32.doc";
//            System.out.print("savePath>>" + savePath);
//            FileOutputStream out = new FileOutputStream(savePath);
//            FileChannel fc = out.getChannel();
//            fc.write(ByteBuffer.wrap(messageBean.getContent().getBytecontent()));
//            System.out.println("文件接收完成");
            switch (messageBean.getAction()){
                case "login":
                    System.out.println("messageBean.getAckcode()>>"+messageBean.getAckcode());
                    if(messageBean.getAckcode()==10){
                            Client.getInstance().onSuccess(messageBean.getAckcode(),messageBean.getAction());
                        }else{
                            Client.getInstance().onFaliure(messageBean.getAckcode());
                        }

                    break;
                case "kickoff":
                    MainActivity.handler.sendEmptyMessage(StaticVar.LADNDING_IN_DIFFERENT_PLACES);
                    Client.getInstance().closeNow(true);
                    break;
                case "SendCmd":
                    Object[] objmsg = new Object[3];
                    objmsg[0] = messageBean.getFrom().getId();//FromID
                    objmsg[1] = messageBean.getTo().getId();//ToID
                    objmsg[2] = messageBean.getContent().getStringcontent();//Content
                    System.out.println("obj>>"+objmsg[2].toString());
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
        System.out.println("messageSent:"+((MessageBean)message).getAction());
    }

    @Override
    public void sessionClosed(IoSession session) throws Exception {
        super.sessionClosed(session);

        Client.getInstance().closeNow(true);
        System.out.println("sessionClosed");
    }

    @Override
    public void sessionCreated(IoSession session) throws Exception {
        super.sessionCreated(session);
        System.out.println("sessionCreated");
    }

    @Override
    public void sessionOpened(IoSession session) throws Exception {
        super.sessionOpened(session);
        System.out.println("sessionOpened");
    }

    @Override
    public void sessionIdle(IoSession session, IdleStatus status) throws Exception {
        super.sessionIdle(session, status);
        System.out.println("sessionIdle");
        session.write(0x00);
    }
}
