import org.apache.hadoop.hbase.util.Bytes;
import org.hbase.async.Config;
import org.hbase.async.GetRequest;
import org.hbase.async.HBaseClient;
import org.hbase.async.KeyValue;

import java.util.List;

/**
 * Tested with:
 *  - Cloudera Version: CDH 5.11.1
 *  - Hadoop 2.6.0-cdh5.11.1
 *  - HBase 1.2.0-cdh5.11.1
 */
public class HBaseAsyncApplication {

    public static void main(String[] args) throws Exception {
//        localHBase();
//        remoteNoAuthHBase();
        remoteHBase();
    }

    /**
     * Tests local HBase Standalone server running locally with embedded Zookeeper.
     *
     * Use IP(127.0.0.1) in the zookeeper quorum string instead of the local DNS name ("localhost")
     * @throws Exception
     */
    public static void localHBase() throws Exception {
        HBaseClient client = new HBaseClient("127.0.0.1:2181");
        testHBaseConnection(client, nvTestGetRequest());
    }

    /**
     * Tests remote HBase connection (i.e. Cloudera Express/Enterprise Cluster) with security disabled.
     *
     * @throws Exception
     */
    public static void remoteNoAuthHBase() throws Exception {
        Config config = new Config();
        config.overrideConfig("hbase.zookeeper.znode.parent", "/export/local/cloudera/HBase/hbase");
        config.overrideConfig("hbase.zookeeper.quorum", "<ZOOKEEPER QUORUM STRING>");
        HBaseClient client = new HBaseClient(config);
        testHBaseConnection(client, nvTestGetRequest());
    }

    /**
     * Test against remote HBase connection within a Hadoop Cluster with Kerberos authentication enabled.
     *
     * @throws Exception
     */
    public static void remoteHBase() throws Exception {
        Config config = new Config();
        config.overrideConfig("hbase.zookeeper.znode.parent", "/hbase");
        config.overrideConfig("hbase.zookeeper.quorum", "<ZOOKEEPER QUORUM STRING>");

        config.overrideConfig("hbase.security.auth.enable", "true");
        config.overrideConfig("hbase.security.authentication", "kerberos");
        config.overrideConfig("hbase.sasl.clientconfig", "Client");
        config.overrideConfig("hbase.kerberos.regionserver.principal", "hbase/_HOST@EXAMPLE.COM");

        System.setProperty("java.security.auth.login.config", "src/main/resources/jaas.conf");
        System.setProperty("java.security.krb5.conf", "src/main/resources/krb5.conf");
        System.setProperty("sun.security.krb5.debug", "true");
        System.setProperty("javax.net.debug", "ssl");

        HBaseClient client = new HBaseClient(config);
        testHBaseConnection(client, nvTestGetRequest());
    }

    public static String testHBaseConnection(HBaseClient client, GetRequest get) throws Exception {
        List<KeyValue> row = client.get(get).join(30000L);
        if (row == null || row.isEmpty()) {
            throw new RuntimeException("No records found");
        } else {
            final byte[] valueBytes = row.get(0).value();
            System.out.println("------------------");
            System.out.println("------------------");
            String _result = Bytes.toString(valueBytes);
            System.out.println(_result);
            System.out.println("------------------");
            System.out.println("------------------");
            return _result;
        }
    }

    public static GetRequest nvTestGetRequest() {
        return new GetRequest("nvtest", "row1", "f1", "type");
    }
}
