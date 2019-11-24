import controller.BalanceViewController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * основной класс приложения
 */
public class MainApplication extends Application {

    @Override
    // инициализация основного окна приложения
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/balanceView.fxml"));
        loader.setController(new BalanceViewController(primaryStage));
        primaryStage.setScene(new Scene(loader.load()));
        primaryStage.setTitle("Учет семейного бюджета");
        primaryStage.show();
    }

    /**
     * точка входа в приложение
     */
    public static void main(String[] args) {
        launch(args);
    }

}
