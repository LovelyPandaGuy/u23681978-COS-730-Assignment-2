import java.util.ArrayList;
import java.util.List;

enum OutcomeType { ACCEPT, REVISE, REJECT }

class DecisionService 
{
    public OutcomeType getOutcome(double avgScore, boolean consensus, boolean violations) {
        if (violations) return OutcomeType.REJECT; // Rule 5
        if (avgScore >= 75 && consensus) return OutcomeType.ACCEPT; // Rule 2
        // Rules 3 & 4: (Score >= 75 but no consensus) OR (Score < 75 but consensus)
        return OutcomeType.REVISE; //
    }
}


class Database 
{
    public void saveSubmission(String data) 
    {
        System.out.println("Database: saveSubmission(data)");
    }

    public List<String> fetchReviewers() 
    {
        System.out.println("Database: fetchReviewers()");
        return List.of("Dr. Smith", "Prof. Jones", "Dr. Brown");
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
        return data.contains("2026") ? "valid" : "invalid";
    }
}

class Reviewer 
{
    private String name;
    private EvaluationManager evalManager;
    public Reviewer(String name) { this.name = name; }
    public void assignReview() 
    {
        System.out.println("Reviewer (" + name + "): assignReview()");
    }
    public void performReview(EvaluationManager em) 
    {
        int score = 85; 
        em.submitScore(score); 
    }
}

class ReviewerManager {
    private Database db = new Database();

    public List<Reviewer> getAvailableReviewers() 
    {
        System.out.println("ReviewerManager: getAvailableReviewers()");
        List<String> reviewerList = db.fetchReviewers();
        List<String> filteredList = filterReviewerList(reviewerList);
        List<Reviewer> reviewers = new ArrayList<>();
        for(String name : filteredList) 
        {
            reviewers.add(new Reviewer(name));
        }

        //Delegated responsibility: Manager handles assignments
        assignReviews(reviewers);
        return reviewers;
    }

    private List<String> filterReviewerList(List<String> list) 
    {
        System.out.println("ReviewerManager: Internal filtering of conflicts and workload...");
        System.out.println("ReviewerManager: filterConflicts(list)");
        System.out.println("ReviewerManager: checkWorkload(list)");
        return list; 
    }

    private void assignReviews(List<Reviewer> reviewers) 
    {
        for (Reviewer r : reviewers) 
        {
            r.assignReview();
        }
    }
}

class NotificationService 
{
    public String prepareMessage(OutcomeType outcome) {
        System.out.println("NotificationService: prepareMessage(" + outcome + ")");
        switch (outcome) 
        {
            case ACCEPT: return "Congratulations! Your submission was accepted.";
            case REVISE: return "Revision Required: Please update your submission.";
            case REJECT: return "Rejected: Your submission did not meet the criteria.";
            default: return "Status Pending.";
        }
    }
}

class EvaluationManager 
{
    private Database db = new Database();
    private DecisionService decisionService = new DecisionService();
    private NotificationService notificationService = new NotificationService();

    public String startEvaluation(List<Reviewer> reviewers) 
    {
        System.out.println("EvaluationManager: startEvaluation()");
        
        
        for(Reviewer r : reviewers) 
        {
            r.performReview(this);
        }

        
        calculateAverage();
        checkConsensus();
        applyRules();

        OutcomeType outcome = decisionService.getOutcome(80.0, true, false);
        return notificationService.prepareMessage(outcome);
    }

    public void submitScore(int score) 
    {
        System.out.println("EvaluationManager: submitScore(" + score + ")");
        db.saveScore(score);
    }

    private void calculateAverage() { System.out.println("EvaluationManager: calculateAverage()"); }
    private void checkConsensus() { System.out.println("EvaluationManager: checkConsensus()"); }
    private void applyRules() { System.out.println("EvaluationManager: applyRules()"); }
}

class SubmissionController 
{
    public void submit(String data, UI callerUI) 
    {
        System.out.println("SubmissionController: submit(data)");
        
        Validator validator = new Validator();

        if(validator.validateFormat(data).equals("invalid")) 
        {
            callerUI.displayNotification("Error: Invalid Submission Format.");
            return;
        }

        Database db = new Database();
        db.saveSubmission(data);
        ReviewerManager rm = new ReviewerManager();
        List<Reviewer> reviewers = rm.getAvailableReviewers();
        EvaluationManager em = new EvaluationManager();
        String finalStatus = em.startEvaluation(reviewers);
        callerUI.displayNotification(finalStatus);
    }
}

class UI 
{
    private final SubmissionController controller = new SubmissionController();

    public void submitResearchOutput(String data) 
    {
        System.out.println("UI: submitResearchOutput(data)");
        controller.submit(data,this);
    }

    public void displayNotification(String message) 
    {
        System.out.println("UI: displayNotification(" + message + ")");
        
        System.out.println("RESEARCHER NOTIFICATION: " + message);
        
    }
}

public class Main 
{
    public static void main(String[] args) 
    {
        UI app = new UI();
        long startTime = System.nanoTime();
        for(int i=0; i<1000; i++) 
        {
            app.submitResearchOutput("Pretoria Research Output 2026");
        }
        long endTime = System.nanoTime();
        System.out.println("Average execution time: " + (endTime - startTime) / 1000 + " ns");
    }
}