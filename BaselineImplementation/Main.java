import java.util.ArrayList;
import java.util.List;

class Database 
{
    public String saveSubmission(String data) 
    {
        System.out.println("Database: saveSubmission(data)");
        return "confirmation";
    }

    public List<String> fetchReviewers() 
    {
        System.out.println("Database: fetchReviewers()");
        return List.of("Reviewer_A", "Reviewer_B");
    }

    public void saveScore(int score) 
    {
        System.out.println("Database: saveScore(" + score + ")");
    }
}

class Validator 
{
    public String validateFormat(String data) 
    {
        System.out.println("Validator: validateFormat(data)");
        // Return "valid" or "invalid" to test different diagram paths
        return "valid"; 
    }
}

class Reviewer 
{
    private String name;
    // The Reviewer needs a reference to the manager to "call back"
    private EvaluationManager manager;

    public Reviewer(String name) 
    { 
        this.name = name; 
    }

    public Reviewer(String name, EvaluationManager manager) { 
        this.name = name; 
        this.manager = manager;
    }

    public void assignReview() {
        System.out.println("Reviewer (" + name + "): assignReview()");
    }

    public void setEvalManager(EvaluationManager em) { this.manager = em; }

    public int performReview() 
    {
        //Reviewer -> EvaluationManager.submitScore()
        int score = 80; 
        System.out.println("Reviewer (" + name + "): Calling evalManager.submitScore(" + score + ")");
        manager.submitScore(score);
        return score; 
    }
}

class ReviewerManager 
{
    private Database db = new Database();

    public List<Reviewer> getAvailableReviewers() 
    {
        System.out.println("ReviewerManager: getAvailableReviewers()");
        List<String> reviewerList = db.fetchReviewers();
        filterConflicts(reviewerList);
        checkWorkload(reviewerList);
        
        List<Reviewer> filteredReviewers = new ArrayList<>();
        for (String r : reviewerList) filteredReviewers.add(new Reviewer(r));
        return filteredReviewers;
    }

    private void filterConflicts(List<String> list) 
    {
        System.out.println("ReviewerManager: filterConflicts(list)");
    }

    private void checkWorkload(List<String> list) 
    {
        System.out.println("ReviewerManager: checkWorkload(list)");
    }
}

class NotificationService 
{

    public void notifyAcceptance() {
        System.out.println("NotificationService: notifyAcceptance()");
        Main.sendNotification("ACCEPTED");
    }

    public void notifyRejection() {
        System.out.println("NotificationService: notifyRejection()");
        Main.sendNotification("REJECTED");
    }

    public void notifyRevision() {
        System.out.println("NotificationService: notifyRevision()");
        Main.sendNotification("REVISION");
    }

}

class EvaluationManager 
{
    private Database db = new Database();

    public void startEvaluation(List<Reviewer> reviewers) 
    {
        System.out.println("EvaluationManager: startEvaluation()");
        
        
        for(Reviewer r : reviewers) 
        {
            r.setEvalManager(this); // Connecting them
            int score = r.performReview(); 
             
        }

        calculateAverage();
        checkConsensus();
        applyRules();

        // Alt block: logic based on outcome
        String outcome = "accepted"; // Mocked decision
        NotificationService ns = new NotificationService();
        
        if (outcome.equals("accepted")) ns.notifyAcceptance();
        else if (outcome.equals("rejected")) ns.notifyRejection();
        else ns.notifyRevision();

        
    }

    public void submitScore(int score) 
    {
        System.out.println("EvaluationManager: submitScore(" + score + ") received from Reviewer");
        db.saveScore(score); 
    }

    private void calculateAverage() { System.out.println("EvaluationManager: calculateAverage()"); }
    private void checkConsensus() { System.out.println("EvaluationManager: checkConsensus()"); }
    private void applyRules() { System.out.println("EvaluationManager: applyRules()"); }
}

class SubmissionController 
{
    
    public void submit(String data, UI ui) 
    {
        System.out.println("SubmissionController: submit(data)");
        Validator v = new Validator();
        String status = v.validateFormat(data);

        if(status.equals("invalid")) 
        {
            ui.returnError(); 
        } 
        else 
        {
            Database db = new Database();
            db.saveSubmission(data);

            ReviewerManager rm = new ReviewerManager();
            List<Reviewer> reviewers = rm.getAvailableReviewers();

            for(Reviewer r : reviewers) 
            {
                r.assignReview();
            }

            EvaluationManager em = new EvaluationManager();
            em.startEvaluation(reviewers);

            
        }
    }
}

class UI 
{
    public void submitResearchOutput(String data) 
    {
        System.out.println("UI: submitResearchOutput(data)");
        new SubmissionController().submit(data,this);
    }

    public void returnError() { System.out.println("UI: returnError() -> ERROR: Format validation failed."); }
    
}

public class Main 
{
    public static void main(String[] args) 
    {
        UI userInterface = new UI();
        long startTime = System.nanoTime();
        for(int i=0; i<1000; i++) 
        {
            userInterface.submitResearchOutput("Pretoria Research Sample 2026");
        }
        long endTime = System.nanoTime();
        System.out.println("Average execution time: " + (endTime - startTime) / 1000 + " ns");
        
    }

    public static void sendNotification(String message) 
    {
        System.out.println("--------------------------------------------------");
        System.out.println(">>> TO ACTOR (Researcher): " + message);
        System.out.println("--------------------------------------------------");
        
    }
}