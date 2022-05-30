package group21.Offering;

import genius.core.Bid;
import genius.core.bidding.BidDetails;
import genius.core.boaframework.SortedOutcomeSpace;
import genius.core.misc.Range;
import genius.core.utility.AbstractUtilitySpace;
import genius.core.utility.AdditiveUtilitySpace;
import genius.core.utility.UtilitySpace;
import group21.Concession.ConcessionStrategy;
import group21.Concession.ConcessionType;
import group21.NashBargaining.NashUtil;
import group21.TradeOff.SimpleTradeOffStrategy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class OfferingStrategy {
    SimpleTradeOffStrategy simpleTradeOffStrategy;
    AdditiveUtilitySpace additiveUtilitySpace;
    ConcessionStrategy concessionStrategy;
    NashUtil nashUtil;
    private final UtilitySpace utilitySpace;
    private final HashMap<Bid, Double> bidSpace = new HashMap<>();
    private final AbstractUtilitySpace abstractUtilitySpace;
    private final SortedOutcomeSpace outcomeSpace;
    private Bid myLastBid;
    private int numBids = 0;
    private double time;
    private double MAXIMUM_UTILITY = 1.0;
    private double MINIMUM_UTILITY = 0.5;
    private double ALLOWED_CONCEDING_VALUE = 0.05;
    private List<Bid> opponentBidsList = new ArrayList<>();

    public Bid getMyLastBid() {
        return myLastBid;
    }

    public double getMAXIMUM_UTILITY() {
        return MAXIMUM_UTILITY;
    }

    public void setMAXIMUM_UTILITY(double MAXIMUM_UTILITY) {
        this.MAXIMUM_UTILITY = MAXIMUM_UTILITY;
    }

    public double getMINIMUM_UTILITY() {
        return MINIMUM_UTILITY;
    }

    public void setMINIMUM_UTILITY(double MINIMUM_UTILITY) {
        this.MINIMUM_UTILITY = MINIMUM_UTILITY;
    }

    public void setMyLastBid(Bid myLastBid) {
        this.myLastBid = myLastBid;
    }

    public double getTime() {
        return time;
    }

    public void setTime(double time) {
        this.time = time;
    }

    public double getUtilForBid(Bid b){
        return utilitySpace.getUtility(b);
    }

    public ConcessionStrategy getConcessionStrategy() {
        return concessionStrategy;
    }


    public OfferingStrategy(AbstractUtilitySpace abstractUtilSpace, UtilitySpace utilSpace, NashUtil nashUtility) {
        simpleTradeOffStrategy = new SimpleTradeOffStrategy();
        utilitySpace = utilSpace;
        additiveUtilitySpace = (AdditiveUtilitySpace) utilitySpace;
        abstractUtilitySpace = abstractUtilSpace;
        outcomeSpace = new SortedOutcomeSpace(abstractUtilSpace);
        concessionStrategy = new ConcessionStrategy(MAXIMUM_UTILITY,MINIMUM_UTILITY,ALLOWED_CONCEDING_VALUE, ConcessionType.NORMAL);
        nashUtil = nashUtility;
        List<Bid> allBids = outcomeSpace.getAllBidsWithoutUtilities();
        for (int i = 0; i < allBids.size(); i++) {
            bidSpace.put(allBids.get(i), utilitySpace.getUtility(allBids.get(i)));
        }
    }


    private Bid getRandomBidBetweenRange(double x, double y) {
        List<BidDetails> bidsInRange = outcomeSpace.getBidsinRange(new Range(x, y));
        if (bidsInRange.size() < 1) {
            return null;
        } else if (bidsInRange.size() < 2) {
            return bidsInRange.get(0).getBid();
        } else {
            int c = (new Random()).nextInt(bidsInRange.size() - 1);
            return bidsInRange.get(c).getBid();
        }
    }

    public Bid makeOffer(Bid opponentBid) {
        Bid nashBid = null;
        if (numBids == 0){
            this.myLastBid = generateInitialBid();
        }else{
            if (opponentBidsList.size() > 3){
                if (utilitySpace.getUtility(opponentBid) <= utilitySpace.getUtility(opponentBidsList.get(opponentBidsList.size()-2))){
                    concessionStrategy.changeConcessionStrategy(concessionStrategy.getConcessionRate() + .4);
                }
            }
            double targetUtil = concessionStrategy.generateTargetUtility(getTime());
            concessionStrategy.changeConcessionStrategy(.4);
            nashBid = generateNashPoint();
            System.out.println(" nash bid   "+ nashBid);
            //System.out.println(" nash distance   "+ nashUtil.distanceToNash(opponentBid));

            this.myLastBid = simpleTradeOffStrategy.getBestNextBid(opponentBid, myLastBid, bidSpace, additiveUtilitySpace, targetUtil);
        }
        numBids++;
        opponentBidsList.add(opponentBid);
        return myLastBid;
    }

    private Bid generateNashPoint(){
        nashUtil.updateOpponentUtilitySpace();
        return nashUtil.getNashBid();
    }

    public Bid generateInitialBid() {
        Bid initBid = null;
        Bid maxBid = null;
        try {
            maxBid = abstractUtilitySpace.getMaxUtilityBid();
            double maxUtility = utilitySpace.getUtility(maxBid);
            initBid = getRandomBidBetweenRange(maxUtility * 0.95D, maxUtility); // change constant value
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (initBid != null) {
            return initBid;
        } else {
            return maxBid;
        }
    }


}
