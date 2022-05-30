package group21.TradeOff;

import genius.core.Bid;
import genius.core.issue.Issue;
import genius.core.utility.AdditiveUtilitySpace;

import java.util.HashMap;
import java.util.List;

public class SimpleTradeOffStrategy {

    private static final double UTILITY_DEVIATION = .01D;
    private static final double SIMILARITY_THRESHOLD = .950D;
    private static final double SIMILARITY_DEVIATION = .005;

    public Bid getBestNextBid(Bid opponentBid, Bid lastBid, HashMap<Bid, Double> bidList, AdditiveUtilitySpace utilitySpace, double targetUtil) {
        Bid tradeOffBid = getTradeOffBid(targetUtil, opponentBid, bidList, utilitySpace);
        if (determineBidSimilarity(lastBid, tradeOffBid, utilitySpace) > SIMILARITY_THRESHOLD) {
            tradeOffBid = this.getTradeOffBid(targetUtil - SIMILARITY_DEVIATION , opponentBid, bidList, utilitySpace);
        }
        return tradeOffBid;
    }


    private Bid getTradeOffBid(double x, Bid y, HashMap<Bid, Double> bidSpace, AdditiveUtilitySpace utilitySpace) {
        Bid b = null;
        Bid tempBid = null;
        double lambda = -1.0D;
        for (Bid i : bidSpace.keySet()) {
            tempBid = i;
            double tempUtility = bidSpace.get(i);
            if (Math.abs(tempUtility - x) < UTILITY_DEVIATION) {
                double similarity = determineBidSimilarity(tempBid, y, utilitySpace);
                if (similarity > lambda) {
                    lambda = similarity;
                    b = tempBid;
                }
            }
        }
        if (b == null) {
            return tempBid;
        }

        return b;
    }

    private double determineBidSimilarity(Bid newBid, Bid opponentBid, AdditiveUtilitySpace utilitySpace) {
        double score = 0;
        List<Issue> bidIssues = newBid.getIssues();
        for (int i = 0; i < bidIssues.size(); i++) {
            int issueNumber = bidIssues.get(i).getNumber();
            score += utilitySpace.getWeight(issueNumber) * (1 - (utilitySpace.getEvaluation(bidIssues.get(i).getNumber(), newBid) - utilitySpace.getEvaluation(bidIssues.get(i).getNumber(), opponentBid)));
        }
        return score;
    }

}
