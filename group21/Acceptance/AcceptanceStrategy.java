package group21.Acceptance;

import genius.core.Bid;
import group21.Concession.ConcessionStrategy;
import group21.NashBargaining.NashUtil;
import group21.Offering.OfferingStrategy;

public class AcceptanceStrategy {


    private double NASH_DISTANCE_THRESHOLD = .14;

    private OfferingStrategy offeringStrategy;
    private NashUtil nashUtil;
    private ConcessionStrategy concessionStrategy;

    public AcceptanceStrategy(OfferingStrategy offerStrategy, NashUtil nashUtility){
        this.offeringStrategy = offerStrategy;
        this.nashUtil = nashUtility;
    }

    public static boolean isBetween(double value, double min, double max)
    {
        return((value > min) && (value < max));
    }

    public boolean isAcceptable(Bid offer, double time){
        nashUtil.updateOpponentUtilitySpace();
        double nashDistance = nashUtil.distanceToNash(offer);
        System.out.println(nashDistance);
        System.out.println(isBetween(nashDistance,1.2,NASH_DISTANCE_THRESHOLD));
        boolean nashBoolean  = isBetween(nashDistance,1.2,NASH_DISTANCE_THRESHOLD);
        if (time >= 0.9) {
            if (offeringStrategy.getUtilForBid(offer) >= offeringStrategy.getMINIMUM_UTILITY()) {
                return true;
            }
            else if (time >= 0.95){
                return true;
            }
        }else{
            if (offeringStrategy.getUtilForBid(offer) >= offeringStrategy.getConcessionStrategy().generateTargetUtility(time) || nashBoolean) {
                return true;
            }
        }
        return false;
    }





}

