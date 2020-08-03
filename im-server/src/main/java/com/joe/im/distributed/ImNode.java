package com.joe.im.distributed;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicInteger;

@NoArgsConstructor
@Getter@Setter@ToString
public class ImNode implements Comparable<ImNode>, Serializable {
    private static final long serialVersionUID = 8504646315432965523L;

    //分布式id,由zookeeper生成
    private long id;

    private AtomicInteger balanace;

    private String host;

    private String port;

    public static ImNode getInstance(){
        return Singleton.INSTANCE.getInstance();
    }

    private enum Singleton{
        INSTANCE;

        private ImNode instance;

        Singleton(){
            instance = new ImNode();
        }

        public ImNode getInstance() {
            return instance;
        }
    }

    public ImNode(String host, String port) {
        this.host = host;
        this.port = port;
    }

    @Override
    public int compareTo(ImNode o) {
        return this.balanace.get() - o.balanace.get();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ImNode imNode = (ImNode) o;

        if (id != imNode.id) return false;
        if (!host.equals(imNode.host)) return false;
        return port.equals(imNode.port);
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + host.hashCode();
        result = 31 * result + port.hashCode();
        return result;
    }
}
