package group21.NashBargaining;

import genius.core.Bid;
import genius.core.Domain;
import genius.core.utility.UtilitySpace;
import group21.OpponentModel.JonnyBlack;

import java.util.ArrayList;
import java.util.List;

public class NashUtil {

    private JonnyBlack opponentModel;
    private UtilitySpace agentUtilitySpace;
    private List<EvaluatedBid> paretoLine;
    private List<EvaluatedBid> bidSpace;
    private List<Bid> allBids;
    private boolean spaceUpdated;
    private EvaluatedBid nashBid;

    public NashUtil(JonnyBlack jb, UtilitySpace utilspace, List<Bid> allBidsInSpace) {
        this.opponentModel = jb;
        this.agentUtilitySpace = utilspace;
        this.allBids = allBidsInSpace;
        paretoLine = new ArrayList<EvaluatedBid>();

    }

    private void initBidSpace() {
        bidSpace = new ArrayList<EvaluatedBid>();
        for (int i = 0; i < allBids.size(); i++) {
            bidSpace.add(new EvaluatedBid(allBids.get(i), agentUtilitySpace.getUtility(allBids.get(i)), opponentModel.predictedValuation(allBids.get(i))));
        }
        spaceUpdated= true;

    }

    public void updateOpponentUtilitySpace() {
        if (bidSpace == null || bidSpace.size() == 0) {
            initBidSpace();
        } else {
            for (int i = 0; i < bidSpace.size(); i++) {
                bidSpace.get(i).setOpponentUtility(opponentModel.predictedValuation(bidSpace.get(i).getBid()));
            }
        }
        spaceUpdated= true;
    }

    public Bid getNashBid(){
        if (spaceUpdated){
            for (int i = 0; i < bidSpace.size(); i++) {
                addBidToPareto(bidSpace.get(i));
            }
            double max = -1.0;
            double product = 0;
            for (int i = 0; i < paretoLine.size(); i++) {
                product = paretoLine.get(i).getAgentUtility() * paretoLine.get(i).getOpponentUtility();
                if (product > max) {
                    nashBid = paretoLine.get(i);
                    max = product;
                }
            }
        }
        return nashBid == null ? null : nashBid.getBid();
    }

    public double distanceToNash(Bid b){
        double nashDistance = -1.0;
        if(nashBid != null){
            double agentUtil = nashBid.getAgentUtility() - agentUtilitySpace.getUtility(b);
            double opponentUtil = nashBid.getOpponentUtility() - opponentModel.predictedValuation(b);

            //Euclidean Distance - Two Dimensions
            nashDistance = Math.sqrt(((Math.pow(agentUtil, 2)) + (Math.pow(opponentUtil, 2))));
        }
        return nashDistance;

    }

    private void addBidToPareto(EvaluatedBid evaluatedBid){
        for (int i = 0; i < paretoLine.size(); i++) {
            // if there exists a bid that makes a agent better off
            if(betterBid(evaluatedBid, paretoLine.get(i))){
                return;
            }
            // replace bid that might no longer be on the line
            if(betterBid(paretoLine.get(i),evaluatedBid)){
                removeWorseBid(evaluatedBid,paretoLine.get(i));
            }
        }
        paretoLine.add(evaluatedBid);
    }

    private void removeWorseBid(EvaluatedBid bidToAdd, EvaluatedBid bidToRemove){
        List<EvaluatedBid> bidsToRemove = new ArrayList<EvaluatedBid>();
        paretoLine.remove(bidToRemove);

        for (int i = 0; i < paretoLine.size(); i++) {
            if(betterBid(paretoLine.get(i), bidToAdd)){
                bidsToRemove.add(paretoLine.get(i));
            }
        }
        paretoLine.removeAll(bidsToRemove);
        paretoLine.add(bidToAdd);
    }

    private boolean betterBid(EvaluatedBid b1, EvaluatedBid b2){
        boolean betterBid = false;
        if (b2 != b1) {
            if ((b2.getAgentUtility() < b1.getAgentUtility()) || (b2.getOpponentUtility() < b1.getOpponentUtility())) {
                // One of the utilities is smaller
                betterBid = false;
            } else if ((b2.getAgentUtility() > b1.getAgentUtility()) || (b2.getOpponentUtility() > b1.getOpponentUtility())) {
                // At least one utility is strictly greater
                betterBid = true;
            }
        }
        return betterBid;
    }

    private class EvaluatedBid {

        private Bid bid;
        private double agentUtility;
        private double opponentUtility;

        public EvaluatedBid(Bid b, double agentUtil, double opponentUtil) {
            this.bid = b;
            this.agentUtility = agentUtil;
            this.opponentUtility = opponentUtil;
        }

        public Bid getBid() {
            return bid;
        }

        public void setBid(Bid bid) {
            this.bid = bid;
        }

        public double getAgentUtility() {
            return agentUtility;
        }

        public void setAgentUtility(double agentUtility) {
            this.agentUtility = agentUtility;
        }

        public double getOpponentUtility() {
            return opponentUtility;
        }

        public void setOpponentUtility(double opponentUtility) {
            this.opponentUtility = opponentUtility;
        }


    }


}
