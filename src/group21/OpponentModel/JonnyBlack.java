package group21.OpponentModel;

import genius.core.Bid;
import genius.core.Domain;
import genius.core.actions.Offer;
import genius.core.boaframework.SortedOutcomeSpace;
import genius.core.issue.Issue;
import genius.core.issue.IssueDiscrete;
import genius.core.issue.ValueDiscrete;
import genius.core.utility.EvaluatorDiscrete;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class JonnyBlack {

    private HashMap<Integer, HashMap<String, Integer>> frequencyTable = new HashMap();
    private HashMap<String, Integer> options = new HashMap();
    private int numberOfBids = 0;


    public JonnyBlack(Domain domain){

        List<Issue> issues = domain.getIssues();
        for (Issue issue : issues) {
            int issueNumber = issue.getNumber();
            IssueDiscrete issueDiscrete = (IssueDiscrete) issue;
            for (ValueDiscrete valueDiscrete : issueDiscrete.getValues()) {
                options.put(valueDiscrete.getValue(), 0);
            }
            frequencyTable.put(issueNumber, options);
        }
    }

    public void updateTable(Bid b){
        numberOfBids += 1;
        List<Issue> offerIssues = b.getIssues();
            for (Issue iss : offerIssues) {
                int issueNum = iss.getNumber();
                String option = ((ValueDiscrete) b.getValue(issueNum)).getValue();
                int count = frequencyTable.get(issueNum).get(option);
                frequencyTable.get(issueNum).put(option, count + 1);
            }
        }

    private double[] getOptionsRank(Bid bid, List<Issue> issues) {
        double[] ranks = new double[issues.size()];
        int rank = 0;
        for (int i = 0; i < issues.size(); i++) {
            Issue issue = issues.get(i);
            int issueNumber = issue.getNumber();
            HashMap<String, Integer> options = frequencyTable.get(issueNumber);
            double numOptions = options.size();

            String bidOption = ((ValueDiscrete) bid.getValue(issueNumber)).getValue();
            int bidOptionValue = options.get(bidOption);

            for (String option : options.keySet()) {
                if (options.get(option) >= bidOptionValue)
                    rank += 1;
            }

            ranks[i] = (numOptions - rank + 1) / numOptions;

        }
        return ranks;
    }

    private double[] normalizedWeights(List<Issue> issues) {

        double[] weights = new double[issues.size()];

        for (int i = 0; i < issues.size(); i++) {
            List<Integer> optionFrequency = new ArrayList<>(frequencyTable.get(issues.get(i).getNumber()).values());
            double weight = 0;
            for (Integer option : optionFrequency)
                weight += (Math.pow(option, 2) / Math.pow(numberOfBids, 2));

            weights[i] = weight;
        }
        double weightedSum = Arrays.stream(weights).sum();
        double[] normalisedWeights = new double[issues.size()];
        for (int i = 0; i < weights.length; i++)
            normalisedWeights[i] = weights[i] / weightedSum;

        return normalisedWeights;
    }

    public double predictedValuation(Bid offer) {

        List<Issue> issues = offer.getIssues();

        double predictedValue = 0;
        double[] ranks = getOptionsRank(offer, issues);
        double[] weights = normalizedWeights(issues);
        for (int i = 0; i < ranks.length; i++) {
            predictedValue += ranks[i] * weights[i];
        }
        return predictedValue;
    }








}
