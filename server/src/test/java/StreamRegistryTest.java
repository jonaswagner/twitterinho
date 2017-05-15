import ch.uzh.ase.Blackboard.Blackboard;
import ch.uzh.ase.TweetRetrieval.StreamRegistry;
import ch.uzh.ase.TweetRetrieval.TweetStream;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by jonas on 11.05.2017.
 *
 * This Test requires a running internet connection!
 */
public class StreamRegistryTest {

    private static final String NON_EXISTENT_STREAM_ID = "anything else";
    private static final String STREAM_ID_1 = "Test1";
    private static final String STREAM_ID_2 = "Test2";

    private StreamRegistry registry = null;
    private Blackboard board = null;
    private InputStream input = null;
    private Properties prop = new Properties();

    @Before
    public void before() throws IOException {
        registry = StreamRegistry.getInstance();
        input = new FileInputStream("server/config.properties");
        prop.load(input);
        //FIXME jwa init Properties for Twitter API
    }

    @After
    public void after() {
        registry = null;
        Blackboard.shutdown = true;
        input = null;
        prop = null;
    }

    @Test
    public void streamRegistryTest() {

        registry.register(STREAM_ID_1);
        TweetStream stream = registry.locateStream(STREAM_ID_1);
        Blackboard blackboard = registry.locateBlackboard(STREAM_ID_1);
        Assert.assertNotNull(stream);
        Assert.assertNotNull(blackboard);

        registry.register(STREAM_ID_1);
        Assert.assertEquals(stream, registry.locateStream(STREAM_ID_1));
        Assert.assertEquals(blackboard, registry.locateBlackboard(STREAM_ID_1));

        registry.register(STREAM_ID_2);
        Assert.assertEquals(stream, registry.locateStream(STREAM_ID_2));
        Assert.assertEquals(blackboard, registry.locateBlackboard(STREAM_ID_2));

        Assert.assertNull(registry.locateBlackboard(NON_EXISTENT_STREAM_ID));
        Assert.assertNull(registry.locateStream(NON_EXISTENT_STREAM_ID));

        registry.unRegister(STREAM_ID_1);
        registry.unRegister(STREAM_ID_2);
        Assert.assertNull(registry.locateStream(STREAM_ID_1));
        Assert.assertNull(registry.locateBlackboard(STREAM_ID_1));
        Assert.assertNull(registry.locateStream(STREAM_ID_2));
        Assert.assertNull(registry.locateBlackboard(STREAM_ID_2));


    }
}
