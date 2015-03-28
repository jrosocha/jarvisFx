package com.jhr.jarvis;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Import;

@Import(JarvisConfig.class)
public class Jarvis extends AbstractJavaFxApplicationSupport {

    private Stage primaryStage;
    
    @Autowired
    private SpringFxmlLoader loader;
        
    private BorderPane rootLayout;
    
    private VBox left;
    private TabPane center;
    
	@Override
	public void start(Stage stage) throws Exception {

        this.primaryStage = stage;
        this.primaryStage.setTitle("Jarvis");

        initRootLayout();
	}

	public static void main(String[] args) {
		launchApp(Jarvis.class, args);
	}

	public void initRootLayout() {

	    RootLayoutController controller = this.getApplicationContext().getBean(RootLayoutController.class);
	    rootLayout = (BorderPane) controller.getView();
	    left = controller.getLeft();
	    center = controller.getCenter();
	    
        Scene scene = new Scene(rootLayout);
        primaryStage.setScene(scene);
        primaryStage.show();
        
        showCurrentSystem();
        showStationOverview();
        showShip();
    }
	
    public void showCurrentSystem() {

        CurrentSystemController controller = this.getApplicationContext().getBean(CurrentSystemController.class);
        left.getChildren().add(controller.getView());
    }
    
    public void showStationOverview() {

        StationOverviewController controller = this.getApplicationContext().getBean(StationOverviewController.class);
        
        Tab stationOverviewTab = new Tab();
        stationOverviewTab.setText("Station Overview");
        stationOverviewTab.setContent(controller.getView());
        center.getTabs().add(stationOverviewTab);
    }
    
    public void showShip() {

        ShipController controller = this.getApplicationContext().getBean(ShipController.class);
        left.getChildren().add(controller.getView());
    }

}
