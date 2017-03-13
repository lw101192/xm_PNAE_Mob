package xm.mina;

import android.os.Message;

import com.example.xm.bean.StaticVar;
import com.example.xm.fragment.UserFragment;

import org.apache.mina.core.buffer.BufferDataException;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;

import java.io.Serializable;

/**
 * A {@link ProtocolDecoder} which deserializes {@link Serializable} Java
 * objects using {@link IoBuffer#getObject(ClassLoader)}.
 *
 * @author <a href="http://mina.apache.org">Apache MINA Project</a>
 */
public class MyObjectSerializationDecoder extends CumulativeProtocolDecoder {
    private final ClassLoader classLoader;

    private int maxObjectSize = 10485760; // 10MB

    /**
     * Creates a new instance with the {@link ClassLoader} of
     * the current thread.
     */
    public MyObjectSerializationDecoder() {
        this(Thread.currentThread().getContextClassLoader());
    }

    /**
     * Creates a new instance with the specified {@link ClassLoader}.
     * 
     * @param classLoader The class loader to use
     */
    public MyObjectSerializationDecoder(ClassLoader classLoader) {
        if (classLoader == null) {
            throw new IllegalArgumentException("classLoader");
        }
        this.classLoader = classLoader;
    }

    /**
     * @return the allowed maximum size of the object to be decoded.
     * If the size of the object to be decoded exceeds this value, this
     * decoder will throw a {@link BufferDataException}.  The default
     * value is <tt>1048576</tt> (1MB).
     */
    public int getMaxObjectSize() {
        return maxObjectSize;
    }

    /**
     * Sets the allowed maximum size of the object to be decoded.
     * If the size of the object to be decoded exceeds this value, this
     * decoder will throw a {@link BufferDataException}.  The default
     * value is <tt>1048576</tt> (1MB).
     * 
     * @param maxObjectSize The maximum size for an object to be decoded
     */
    public void setMaxObjectSize(int maxObjectSize) {
        if (maxObjectSize <= 0) {
            throw new IllegalArgumentException("maxObjectSize: " + maxObjectSize);
        }

        this.maxObjectSize = maxObjectSize;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean doDecode(IoSession session, IoBuffer in, ProtocolDecoderOutput out) throws Exception {
        if(Client.getInstance().fileLength!=0)
//        System.out.println("decode 进度  "+(int)((float)in.limit()/Client.getInstance().fileLength*100)+"%");
        Message.obtain(UserFragment.handler, StaticVar.UPDATE_DOWNLOAD_PERCENT,in.limit(),(int)Client.getInstance().fileLength).sendToTarget();
//        System.out.println("decode 进度  "+in.limit()+"  totallength:"+Client.getInstance().fileLength);
        if (!in.prefixedDataAvailable(4, maxObjectSize)) {
            return false;
        }

        out.write(in.getObject(classLoader));
        return true;
    }
}
