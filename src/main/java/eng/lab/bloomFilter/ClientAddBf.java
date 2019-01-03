package eng.lab.bloomFilter;

import redis.clients.jedis.Client;
import redis.clients.jedis.Connection;
import redis.clients.jedis.Protocol;
import redis.clients.jedis.exceptions.JedisConnectionException;
import redis.clients.util.RedisInputStream;
import redis.clients.util.RedisOutputStream;
import redis.clients.util.SafeEncoder;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Created by wenxueliang on 2018/12/27.
 */
public class ClientAddBf extends Client {

    public static final byte DOLLAR_BYTE = '$';
    public static final byte ASTERISK_BYTE = '*';

    private String host = Protocol.DEFAULT_HOST;
    private int port = Protocol.DEFAULT_PORT;
    private Socket socket;
    private RedisOutputStream outputStream;
    private RedisInputStream inputStream;
    private int connectionTimeout = Protocol.DEFAULT_TIMEOUT;
    private int soTimeout = Protocol.DEFAULT_TIMEOUT;

    public ClientAddBf(String host, int port) {
        super(host, port);
        this.host = host;
        this.port = port;
    }

    public void bfAdd(String key, String value) {
        bfAdd(SafeEncoder.encode(key), SafeEncoder.encode(value));
    }

    public void bfAdd(final byte[] key, final byte[] value) {
        sendCommand(CommandAddBf.BF_ADD, key, value);
    }

    public String getStatusCodeReply() {
        flush();
        final byte[] resp = (byte[]) readProtocolWithCheckingBroken();
        if (null == resp) {
            return null;
        } else {
            return SafeEncoder.encode(resp);
        }
    }

    protected void flush() {
        try {
            outputStream.flush();
        } catch (IOException ex) {
            throw new JedisConnectionException(ex);
        }
    }

    protected Object readProtocolWithCheckingBroken() {
        try {
            return Protocol.read(inputStream);
        } catch (JedisConnectionException exc) {
            throw exc;
        }
    }

    protected Connection sendCommand(final CommandAddBf cmd, final byte[]... args) {
        try {
            connect();
            sendCommand(outputStream, cmd, args);
            return this;
        } catch (JedisConnectionException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public void connect() {
        if (!isConnected()) {
            try {
                socket = new Socket();
                // ->@wjw_add
                socket.setReuseAddress(true);
                socket.setKeepAlive(true); // Will monitor the TCP connection is
                // valid
                socket.setTcpNoDelay(true); // Socket buffer Whetherclosed, to
                // ensure timely delivery of data
                socket.setSoLinger(true, 0); // Control calls close () method,
                // the underlying socket is closed
                // immediately
                // <-@wjw_add

                socket.connect(new InetSocketAddress(host, port), connectionTimeout);
                socket.setSoTimeout(soTimeout);

                outputStream = new RedisOutputStream(socket.getOutputStream());
                inputStream = new RedisInputStream(socket.getInputStream());
            } catch (IOException ex) {
                throw new JedisConnectionException(ex);
            }
        }
    }

    public static void sendCommand(final RedisOutputStream os, final CommandAddBf command,
                                   final byte[]... args) {
        sendCommand(os, command.raw, args);
    }

    private static void sendCommand(final RedisOutputStream os, final byte[] command,
                                    final byte[]... args) {
        try {
            os.write(ASTERISK_BYTE);
            os.writeIntCrLf(args.length + 1);
            os.write(DOLLAR_BYTE);
            os.writeIntCrLf(command.length);
            os.write(command);
            os.writeCrLf();

            for (final byte[] arg : args) {
                os.write(DOLLAR_BYTE);
                os.writeIntCrLf(arg.length);
                os.write(arg);
                os.writeCrLf();
            }
        } catch (IOException e) {
            throw new JedisConnectionException(e);
        }
    }
}
