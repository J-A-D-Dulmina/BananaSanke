package model;

import java.util.ArrayList;
import java.util.List;

import interfaces.IHighScoreListener;
import interfaces.IHighScoreNotifier;
import interfaces.IAPIClient;
import org.json.JSONObject;
import factory.ComponentFactory;

/**
 * Implements high score notification functionality
 */
public class HighScoreNotifier implements IHighScoreNotifier {
    private static HighScoreNotifier instance;
    private final List<IHighScoreListener> listeners = new ArrayList<>();
    private final IAPIClient apiClient;
    private int currentHighScore = 0;
    
    private HighScoreNotifier() {
        this.apiClient = ComponentFactory.getAPIClient();
        fetchCurrentHighScore();
    }
    
    /**
     * Singleton instance getter
     */
    public static synchronized HighScoreNotifier getInstance() {
        if (instance == null) {
            instance = new HighScoreNotifier();
        }
        return instance;
    }
    
    /**
     * Fetches the current high score from the API
     */
    private void fetchCurrentHighScore() {
        try {
            String response = apiClient.getBestScore();
            JSONObject jsonResponse = new JSONObject(response);
            
            if (jsonResponse.getString("status").equals("success")) {
                currentHighScore = jsonResponse.getInt("best_score");
                System.out.println("Current high score: " + currentHighScore);
            }
        } catch (Exception e) {
            System.err.println("Error fetching high score: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    @Override
    public void notifyNewHighScore(int score) {
        System.out.println("Notifying new high score: " + score);
        for (IHighScoreListener listener : listeners) {
            listener.onNewHighScore(score);
        }
    }
    
    @Override
    public boolean checkAndNotifyHighScore(int score) {
        if (score <= 0) {
            return false;
        }
        
        // Refresh current high score
        fetchCurrentHighScore();
        
        boolean isHighScore = (currentHighScore == 0 || score > currentHighScore);
        
        if (isHighScore) {
            System.out.println("New high score detected: " + score + " > " + currentHighScore);
            
            // Save to API
            try {
                String response = apiClient.updateHighScore(score);
                JSONObject jsonResponse = new JSONObject(response);
                
                if (jsonResponse.getString("status").equals("success")) {
                    currentHighScore = score;
                    
                    // Notify listeners
                    notifyNewHighScore(score);
                    return true;
                }
            } catch (Exception e) {
                System.err.println("Error saving high score: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            System.out.println("Not a high score: " + score + " <= " + currentHighScore);
        }
        
        return false;
    }
    
    @Override
    public void addHighScoreListener(IHighScoreListener listener) {
        if (listener != null && !listeners.contains(listener)) {
            listeners.add(listener);
        }
    }
    
    @Override
    public void removeHighScoreListener(IHighScoreListener listener) {
        listeners.remove(listener);
    }
    
    /**
     * Gets the current high score
     */
    public int getCurrentHighScore() {
        return currentHighScore;
    }
} 