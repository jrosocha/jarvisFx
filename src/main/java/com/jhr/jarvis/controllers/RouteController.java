package com.jhr.jarvis.controllers;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.web.WebView;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;

import com.jhr.jarvis.JarvisConfig;
import com.jhr.jarvis.event.DrawMapEvent;
import com.jhr.jarvis.event.DrawRouteMapEvent;
import com.jhr.jarvis.event.UpdateMapEvent;
import com.jhr.jarvis.model.MapData;
import com.jhr.jarvis.model.Settings;
import com.jhr.jarvis.service.ShipService;
import com.jhr.jarvis.service.StarSystemService;

/**
 * Not annotated as @Component because we want to use the springFxmlLoader so the @FXML annotations get populated.
 * Wired in @ JarvisConfig.
 * @author jrosocha
 *
 */
public class RouteController implements ApplicationListener<ApplicationEvent> {
    
    @FXML
    private Node view;
    
    @FXML
    private WebView map;
    
    @FXML
    private Label mapInformation;
    
    @FXML
    private ProgressIndicator mapLoading;
    
    @Autowired
    private Settings settings;

    @Autowired
    private StarSystemService starSystemService;
    
    @Autowired
    private ShipService shipService;
    
    String mapHtml = null;
    
    @PostConstruct
    public void initMap() {
        Platform.runLater(()->{mapLoading.setVisible(false);});
        try {
            mapHtml = new String(Files.readAllBytes(Paths.get(this.getClass().getResource("/mapTemplate.html").toURI())));
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
        
    }
    
    @Override
    public void onApplicationEvent(ApplicationEvent event) {

        if (event instanceof DrawMapEvent) {

            Runnable task=() -> {
            
                MapData mapData = null;
                
                if (event instanceof DrawRouteMapEvent) {
                    Platform.runLater(()->{
                        mapLoading.setVisible(true);
                    });  
                    DrawRouteMapEvent drawRouteMapEvent = (DrawRouteMapEvent) event;
                    Platform.runLater(()->{
                        mapInformation.setText(String.format("Exchange Route from system %s with a %f ly jump range", drawRouteMapEvent.getSystemsInRoute().get(0), shipService.getActiveShip().getJumpDistance()));
                    });            
                    mapData = starSystemService.calculateShortestPathBetweenSystems(shipService.getActiveShip(), drawRouteMapEvent.getSystemsInRoute());
                } else if (event instanceof UpdateMapEvent){
                    UpdateMapEvent updateMapEvent = (UpdateMapEvent) event;
                    mapData = updateMapEvent.getMessage();
                } else {
                    return;
                }
                
                try {
                    String mapDataAsString = JarvisConfig.MAPPER.writeValueAsString(mapData);
                    final String newMapHtml = mapHtml.replace("__DATA__", mapDataAsString);
                    
                    Platform.runLater(()->{            
                        try {
                            map.getEngine().loadContent(newMapHtml);
                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            mapLoading.setVisible(false);  
                        }
                    });
                    
                } catch (IOException e) {
                    e.printStackTrace();
                }
            
          };
          Thread thread = new Thread(task);
          thread.setDaemon(true);
          thread.start();
        }

    }
    
    public Node getView() {
        return view;
    }

}