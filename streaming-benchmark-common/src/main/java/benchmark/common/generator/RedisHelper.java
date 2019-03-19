package benchmark.common.generator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;

import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

/**
 * Some basic Redis support for preparing to run tests compatible with Yahoo's
 * original benchmarking tools.
 */
public class RedisHelper {
  private static final Logger LOG = LoggerFactory.getLogger(RedisHelper.class);

  private String redisHost;
  private int redisDb;
  private boolean redisFlush;

  public RedisHelper(String redisHost, int redisDb, boolean redisFlush){
    this.redisHost = redisHost;
    this.redisDb = redisDb;
    this. redisFlush = redisFlush;

  }

  public void prepareRedis(Map<String, List<String>> campaigns) {
    Jedis redis = new Jedis(redisHost);
    redis.select(redisDb);
    if (redisFlush) {
      LOG.info("Flushing Redis DB.");
      redis.flushDB();
    }

    LOG.info("Preparing Redis with campaign data.");
    for (Map.Entry<String, List<String>> entry : campaigns.entrySet()) {
      String campaign = entry.getKey();
      redis.sadd("campaigns", campaign);
      for (String ad : entry.getValue()) {
        redis.set(ad, campaign);
      }
    }
    redis.close();
  }

  public void writeCampaignFile(Map<String, List<String>> campaigns) {
    try {
      PrintWriter adToCampaignFile = new PrintWriter("ad-to-campaign-ids.txt");
      for (Map.Entry<String, List<String>> entry : campaigns.entrySet()) {
        String campaign = entry.getKey();
        for (String ad : entry.getValue()) {
          adToCampaignFile.println("{\"" + ad + "\":\"" + campaign + "\"}");
        }
      }
      adToCampaignFile.close();
    } catch (Throwable t) {
      throw new RuntimeException("Error opening ads file", t);
    }
  }
}
