package com.ning.http.client.providers.apache;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by IntelliJ IDEA.
 * User: jason
 * Date: 8/7/13
 * Time: 11:54 PM
 */
public class ChecksumClassFileTest {

    private ChecksumClassFile checksum = new ChecksumClassFile();

    @Test
    public void testSha() throws Exception{

        String sum = checksum.sha(String.class);
        assertEquals("kl51hmCMcuBn9sYweInlqpMijHc=", sum);
    }

    @Test
    public void testMd5() throws Exception{

        String sum = checksum.md5(Integer.class);
        assertEquals("cy1FLhG2dmFaRv+JaTccQQ==", sum);

    }

}
