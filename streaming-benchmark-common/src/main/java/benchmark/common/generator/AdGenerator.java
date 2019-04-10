package benchmark.common.generator;

import java.io.Serializable;
import java.util.*;

public class AdGenerator implements Serializable {

    private int adsIdx = 0;
    private int eventsIdx = 0;
    private StringBuilder sb = new StringBuilder();
    private String pageID = UUID.randomUUID().toString();
    private String userID = UUID.randomUUID().toString();
    private final String[] eventTypes = new String[]{"view", "click", "purchase"};

    private List<String> ads;
    private final Map<String, List<String>> campaigns;

    public AdGenerator() {
        this.campaigns = generateCampaigns();
        this.ads = flattenCampaigns();
    }

    public String generateElement() {
        if (adsIdx == ads.size()) {
            adsIdx = 0;
        }
        if (eventsIdx == eventTypes.length) {
            eventsIdx = 0;
        }
        sb.setLength(0);
        sb.append("{\"user_id\":\""); //12
        sb.append(pageID); //16
        sb.append("\",\"page_id\":\""); //13
        sb.append(userID); //16
        sb.append("\",\"ad_id\":\""); //11
        sb.append(ads.get(adsIdx++)); //16
        sb.append("\",\"ad_type\":\""); //13
        sb.append("banner78"); //8 // value is immediately discarded. The original generator would put a string with 38/5 = 7.6 chars. We put 8.
        sb.append("\",\"event_type\":\""); //16
        sb.append(eventTypes[eventsIdx++]); //5
        sb.append("\",\"event_time\":\""); //16
        sb.append(System.currentTimeMillis()); //13
        sb.append("\",\"ip_address\":\"1.2.3.4\"}"); //25

        // tot = 180
        return sb.toString();
    }

    /**
     * Generate a random list of ads and campaigns
     */
    private Map<String, List<String>> generateCampaigns() {
        int numCampaigns = 100;
        int numAdsPerCampaign = 10;
        Map<String, List<String>> adsByCampaign = new LinkedHashMap<>();
        for (int i = 0; i < numCampaigns; i++) {
            String campaign = UUID.randomUUID().toString();
            ArrayList<String> ads = new ArrayList<>();
            adsByCampaign.put(campaign, ads);
            for (int j = 0; j < numAdsPerCampaign; j++) {
                ads.add(UUID.randomUUID().toString());
            }
        }
        return adsByCampaign;
    }

    /**
     * Flatten into just ads
     */
    private List<String> flattenCampaigns() {
        // Flatten campaigns into simple list of ads
        List<String> ads = new ArrayList<>();
        for (Map.Entry<String, List<String>> entry : campaigns.entrySet()) {
            for (String ad : entry.getValue()) {
                ads.add(ad);
            }
        }
        return ads;
    }

    public Map<String, List<String>> getCampaigns() {
        return campaigns;
    }
}
