package xm.mina;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolEncoder;

public class MyObjectSerializationCodecFactory implements ProtocolCodecFactory {
    private final MyObjectSerializationEncoder encoder;
    private final MyObjectSerializationDecoder decoder;

    public MyObjectSerializationCodecFactory() {
        this(Thread.currentThread().getContextClassLoader());
    }

    public MyObjectSerializationCodecFactory(ClassLoader classLoader) {
        this.encoder = new MyObjectSerializationEncoder();
        this.decoder = new MyObjectSerializationDecoder(classLoader);
    }

    public ProtocolEncoder getEncoder(IoSession session) {
        return this.encoder;
    }

    public ProtocolDecoder getDecoder(IoSession session) {
        return this.decoder;
    }

    public int getEncoderMaxObjectSize() {
        return this.encoder.getMaxObjectSize();
    }

    public void setEncoderMaxObjectSize(int maxObjectSize) {
        this.encoder.setMaxObjectSize(maxObjectSize);
    }

    public int getDecoderMaxObjectSize() {
        return this.decoder.getMaxObjectSize();
    }

    public void setDecoderMaxObjectSize(int maxObjectSize) {
        this.decoder.setMaxObjectSize(maxObjectSize);
    }
}
