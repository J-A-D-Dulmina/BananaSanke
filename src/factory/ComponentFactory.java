package factory;

import api.APIClient;
import controller.AccountController;
import controller.GameOverController;
import controller.LeaderboardController;
import controller.ResetPasswordController;
import interfaces.IAPIClient;
import interfaces.IAccountController;
import interfaces.IAccountModel;
import interfaces.IAuthenticationService;
import interfaces.IGameOverController;
import interfaces.IGameOverModel;
import interfaces.ILeaderboardController;
import interfaces.ILeaderboardModel;
import interfaces.IResetPasswordController;
import interfaces.IResetPasswordModel;
import interfaces.ISessionManager;
import interfaces.ISoundManager;
import interfaces.IUserService;
import model.AccountModel;
import model.GameOverModel;
import model.LeaderboardModel;
import model.ResetPasswordModel;
import model.SessionManagerImpl;
import model.SoundManager;
import service.AuthenticationService;
import service.UserService;
import view.AccountPanel;
import view.GameMainInterface;
import view.GameOverPanel;
import view.LeaderboardPanel;
import view.ResetPasswordUI;

/**
 * Factory class for creating components with proper dependency injection.
 */
public class ComponentFactory {
    // Singleton services
    private static final IAPIClient apiClient = APIClient.getInstance();
    private static final ISessionManager sessionManager = SessionManagerImpl.getInstance();
    private static final ISoundManager soundManager = SoundManager.getInstance();
    private static final IUserService userService = new UserService(apiClient, sessionManager);
    private static final IAuthenticationService authService = new AuthenticationService();
    
    /**
     * Creates an AccountController with all its dependencies.
     * @param view The AccountPanel view
     * @param mainFrame The main game interface
     * @return A new AccountController instance
     */
    public static IAccountController createAccountController(AccountPanel view, GameMainInterface mainFrame) {
        IAccountModel model = new AccountModel(userService);
        return new AccountController(model, view, mainFrame, soundManager, sessionManager);
    }
    
    /**
     * Creates an AccountModel with all its dependencies.
     * @return A new AccountModel instance
     */
    public static IAccountModel createAccountModel() {
        return new AccountModel(userService);
    }
    
    /**
     * Creates a GameOverController with all its dependencies.
     * @param view The GameOverPanel view
     * @return A new GameOverController instance
     */
    public static IGameOverController createGameOverController(GameOverPanel view) {
        return new GameOverController(view);
    }
    
    /**
     * Creates a GameOverModel with all its dependencies.
     * @return A new GameOverModel instance
     */
    public static IGameOverModel createGameOverModel() {
        return new GameOverModel();
    }
    
    /**
     * Creates a LeaderboardController with all its dependencies.
     * @param view The LeaderboardPanel view
     * @return A new LeaderboardController instance
     */
    public static ILeaderboardController createLeaderboardController(LeaderboardPanel view) {
        ILeaderboardModel model = new LeaderboardModel();
        return new LeaderboardController(model, view);
    }
    
    /**
     * Creates a LeaderboardModel with all its dependencies.
     * @return A new LeaderboardModel instance
     */
    public static ILeaderboardModel createLeaderboardModel() {
        return new LeaderboardModel();
    }
    
    /**
     * Creates a ResetPasswordController with all its dependencies.
     * @param view The ResetPasswordUI view
     * @param username The username for password reset
     * @param email The email for password reset
     * @return A new ResetPasswordController instance
     */
    public static IResetPasswordController createResetPasswordController(ResetPasswordUI view, String username, String email) {
        return new ResetPasswordController(view, username, email);
    }
    
    /**
     * Creates a ResetPasswordModel with all its dependencies.
     * @param username The username for password reset
     * @param email The email for password reset
     * @return A new ResetPasswordModel instance
     */
    public static IResetPasswordModel createResetPasswordModel(String username, String email) {
        return new ResetPasswordModel(username, email);
    }
    
    /**
     * Gets the singleton instance of the UserService.
     * @return The UserService instance
     */
    public static IUserService getUserService() {
        return userService;
    }
    
    /**
     * Gets the singleton instance of the APIClient.
     * @return The APIClient instance
     */
    public static IAPIClient getAPIClient() {
        return apiClient;
    }
    
    /**
     * Gets the singleton instance of the SessionManager.
     * @return The SessionManager instance
     */
    public static ISessionManager getSessionManager() {
        return sessionManager;
    }
    
    /**
     * Gets the singleton instance of the SoundManager.
     * @return The SoundManager instance
     */
    public static ISoundManager getSoundManager() {
        return soundManager;
    }
    
    /**
     * Gets the singleton instance of the AuthenticationService.
     * @return The AuthenticationService instance
     */
    public static IAuthenticationService getAuthenticationService() {
        return authService;
    }
} 