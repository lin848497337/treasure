package sample.view.tools;

import com.sun.javafx.geom.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.DialogPane;

import java.util.List;
import java.util.Map;

public class MultiLine extends DialogPane {
    private Map<String, List<Point2D>> linesMap;

    public MultiLine(Map<String, List<Point2D>> linesMap) {
        this.linesMap = linesMap;
    }


    public void drawKLine(double height, double width){
        Canvas canvas = new Canvas(width, height);
        setPrefWidth(width);
        setPrefHeight(height);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        this.getChildren().add(canvas);
        int paddingWidth = 10;
        int paddingHeight = 10;
        int shaddowLineWidth = 2;

        height = height - paddingHeight * 2;
        width = width - paddingWidth * 2;

        double maxPrice = 0;
        double minPrice = 99999999;

    }
}

