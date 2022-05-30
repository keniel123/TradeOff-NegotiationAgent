package group21.Concession;

public class  ConcessionStrategy {


   private double minimumUtility;
   private double maximumUtility;
   private double concessionRate;
   private double allowedInitialConcession;

    public double getMinimumUtility() {
        return minimumUtility;
    }

    public void setMinimumUtility(double minimumUtility) {
        this.minimumUtility = minimumUtility;
    }

    public double getMaximumUtility() {
        return maximumUtility;
    }

    public void setMaximumUtility(double maximumUtility) {
        this.maximumUtility = maximumUtility;
    }

    public double getConcessionRate() {
        return concessionRate;
    }

    public void setConcessionRate(double concessionRate) {
        this.concessionRate = concessionRate;
    }

    public double getAllowedInitialConcession() {
        return allowedInitialConcession;
    }

    public void setAllowedInitialConcession(double allowedInitialConcession) {
        this.allowedInitialConcession = allowedInitialConcession;
    }

    public ConcessionStrategy(double maxUtility, double minUtility , double initialConcession, double concessionRate){
       this.minimumUtility = minUtility;
       this.maximumUtility = maxUtility;
       this.allowedInitialConcession = initialConcession;
       this.concessionRate = concessionRate;
   }

   public double generateTargetUtility(double time){
       double alpha = allowedInitialConcession + ((1 - allowedInitialConcession) * Math.pow(time, 1 / concessionRate));
       return minimumUtility + (1 - alpha) * (maximumUtility - minimumUtility);
   }

   public void changeConcessionStrategy(double rate){
       this.concessionRate = rate;
   }
}