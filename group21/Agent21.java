package group21;

import genius.core.AgentID;
import genius.core.Bid;
import genius.core.actions.Accept;
import genius.core.actions.Action;
import genius.core.actions.EndNegotiation;
import genius.core.actions.Offer;
import genius.core.boaframework.SortedOutcomeSpace;
import genius.core.issue.Issue;
import genius.core.issue.IssueDiscrete;
import genius.core.issue.ValueDiscrete;
import genius.core.parties.AbstractNegotiationParty;
import genius.core.parties.NegotiationInfo;
import genius.core.utility.AbstractUtilitySpace;
import genius.core.utility.AdditiveUtilitySpace;
import genius.core.utility.EvaluatorDiscrete;
import group21.Acceptance.AcceptanceStrategy;
import group21.Concession.ConcessionStrategy;
import group21.Concession.ConcessionType;
import group21.NashBargaining.NashUtil;
import group21.Offering.OfferingStrategy;
import group21.OpponentModel.JonnyBlack;

import java.util.List;

//todo connect strategy

public class Agent21 extends AbstractNegotiationParty {

    private Bid opponentLastOffer;
    private OfferingStrategy offeringStrategy;
    private AcceptanceStrategy acceptanceStrategy;
    private JonnyBlack jonnyBlack;
    private NashUtil nashUtil;


    /**
     * Initializes a new instance of the agent.
     */
    @Override
    public void init(NegotiationInfo info) {
        super.init(info);
        AbstractUtilitySpace utilitySpace = info.getUtilitySpace();
        AdditiveUtilitySpace additiveUtilitySpace = (AdditiveUtilitySpace) utilitySpace;
        SortedOutcomeSpace outcomeSpace = new SortedOutcomeSpace(utilitySpace);
        List<Bid> allBids = outcomeSpace.getAllBidsWithoutUtilities();
        jonnyBlack = new JonnyBlack(additiveUtilitySpace.getDomain());
        nashUtil = new NashUtil(jonnyBlack,additiveUtilitySpace,allBids);
        offeringStrategy = new OfferingStrategy(utilitySpace, additiveUtilitySpace, nashUtil);
        acceptanceStrategy = new AcceptanceStrategy(offeringStrategy,nashUtil);
        List<Issue> issues = additiveUtilitySpace.getDomain().getIssues();

        for (Issue issue : issues) {
            int issueNumber = issue.getNumber();
            // System.out.println(">> " + issue.getName() + " weight: " + additiveUtilitySpace.getWeight(issueNumber));

            // Assuming that issues are discrete only
            IssueDiscrete issueDiscrete = (IssueDiscrete) issue;
            EvaluatorDiscrete evaluatorDiscrete = (EvaluatorDiscrete) additiveUtilitySpace.getEvaluator(issueNumber);

            for (ValueDiscrete valueDiscrete : issueDiscrete.getValues()) {
                //  System.out.println(valueDiscrete.getValue());
                // System.out.println("Evaluation(getValue): " + evaluatorDiscrete.getValue(valueDiscrete));
                try {
                    //   System.out.println("Evaluation(getEvaluation): " + evaluatorDiscrete.getEvaluation(valueDiscrete));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }


    /**
     * Makes a random offer above the minimum utility target
     * Accepts everything above the reservation value at the end of the negotiation; or breaks off otherwise.
     */
    @Override
    public Action chooseAction(List<Class<? extends Action>> possibleActions) {
        //System.out.println(opponentLastOffer);
        offeringStrategy.setTime(timeline.getTime());
        if (opponentLastOffer == null) {
            return new Offer(getPartyId(), offeringStrategy.makeOffer(opponentLastOffer));
        } else {
            if (acceptanceStrategy.isAcceptable(opponentLastOffer,  timeline.getTime())){
                return new Accept(getPartyId(), opponentLastOffer);
            }
            return new Offer(getPartyId(), offeringStrategy.makeOffer(opponentLastOffer));
        }
    }

    /**
     * Remembers the offers received by the opponent.
     */
    @Override
    public void receiveMessage(AgentID sender, Action action) {
        if (action instanceof Offer) {
            opponentLastOffer = ((Offer) action).getBid();
            jonnyBlack.updateTable(opponentLastOffer);
        }
    }


    @Override
    public String getDescription() {
        return "Group 21 Agent that makes trade offs";
    }

    /**
     * This stub can be expanded to deal with preference uncertainty in a more sophisticated way than the default behavior.
     */
    @Override
    public AbstractUtilitySpace estimateUtilitySpace() {
        return super.estimateUtilitySpace();
    }

}