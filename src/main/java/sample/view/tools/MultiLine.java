package sample.view.tools;

import javafx.application.Platform;
import javafx.geometry.Point2D;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.stage.Modality;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class MultiLine{

    private static Dot cursor;

    public static void drawKLine(Map<String, List<Dot>> multiMap){
        Dialog dialog = new Dialog();
        dialog.initModality(Modality.NONE);
        DialogPane dialogPane = new DialogPane();
        BorderPane kLine = new BorderPane();
        dialogPane.setContent(kLine);
        dialogPane.setMaxSize(800, 600);
        dialog.setDialogPane(dialogPane);
        dialog.setTitle("test");
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        Node closeButton = dialog.getDialogPane().lookupButton(ButtonType.CLOSE);
        closeButton.setVisible(false);
        dialogPane.getScene().getWindow().sizeToScene();
        drawKLine(kLine, multiMap);
        dialog.show();
    }


    public static void drawKLine(BorderPane pane, Map<String, List<Dot>> multiMap){
        pane.setOnZoomFinished(ze->{
        });
        int width = 600;
        int height = 400;
        Canvas canvas = new Canvas(width, height);
        canvas.setOnMouseMoved(e->{
            cursor = new Dot(e.getX(), e.getY());
        });
        pane.setPrefWidth(width);
        pane.setPrefHeight(height);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        pane.setCenter(canvas);
        Timer timer = new Timer(true);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        paint(gc, width, height, multiMap);
                    }
                });
            }
        }, 80, 80);
    }

    private static void paint(GraphicsContext gc, double width, double height, Map<String, List<Dot>> multiMap){
        int paddingWidth = 20;
        int paddingHeight = 20;

        double orgWidth = width;
        double orgHeight = height;

        gc.clearRect(0, 0, width, height);

        if (cursor != null){
            gc.setStroke(Color.LIGHTPINK);
            gc.strokeLine(paddingWidth, cursor.y, width, cursor.y);
            gc.strokeLine(cursor.x, height, cursor.x, paddingHeight);
        }

        height = height - paddingHeight * 2;
        width = width - paddingWidth * 2;
        Color array[] = new Color[]{Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW, Color.BROWN};

        double xSpace = 20;
        double ySpace = 20;

        gc.setStroke(Color.BLACK);
        gc.strokeLine(paddingWidth, height, width, height);
        gc.strokeLine(paddingWidth, height, paddingWidth, paddingHeight);
        gc.setTextAlign(TextAlignment.CENTER);
        gc.setTextBaseline(VPos.CENTER);
        gc.setFont(Font.font("宋体", 10));

        double maxY = 0;
        double maxX = 0;
        for (List<Dot> dots : multiMap.values()) {
            for (Dot dot : dots) {
                maxX = Math.max(maxX, dot.x);
                maxY = Math.max(maxY, dot.y);
            }
        }

        int xSize = (int) (width / xSpace);
        int ySize = (int) (height / ySpace);

        int skipX = (int) Math.max(Math.ceil(maxX/xSize), 1);
        int skipY = (int) Math.max(Math.ceil(maxY/ySize) , 1);

        for (int i=0 ; i<xSize ; i++) {
            gc.strokeText((i * skipX) +"", i * xSpace + paddingWidth , height + 10);
        }

        for (int i=1 ; i<ySize ; i++) {
            gc.strokeText((i * skipY) +"", paddingWidth - 10 , height - i*ySpace);
        }

        xSpace = xSpace / skipX;
        ySpace = ySpace / skipY;


        int i=0 ;

        Map<String, Color> upCornerMap = new HashMap<>();

        for (Map.Entry<String, List<Dot>> entry : multiMap.entrySet()){
            Color curColor = array[i++];
            upCornerMap.put(entry.getKey(), curColor);
            PointLine line = new PointLine(entry.getValue() , curColor , width, height, xSpace, ySpace);
            line.draw(gc, paddingWidth, paddingHeight);
        }

        double lineHeight = 10;
        double lineWidth = 50;

        double x = width - lineWidth;
        double y = lineHeight;
        double lineLength = 10;
        double textLength = lineWidth - lineLength;
        int num = 1 ;
        gc.setFont(Font.font("宋体", 10));
        for (Map.Entry<String, Color> entry : upCornerMap.entrySet()){
            gc.setLineWidth(2);
            gc.setStroke(entry.getValue());
            gc.strokeLine(x, y, x + lineLength, y);
            gc.setStroke(Color.BLACK);
            gc.setTextAlign(TextAlignment.LEFT);
            gc.setLineWidth(1);
            gc.strokeText("-"+entry.getKey(), x + lineLength, y);
            y+= lineHeight * num ++;
        }
    }


    public static class PointLine {
        private List<Dot> point2DList;
        private Color color;
        private double lineWidth;
        private double width;
        private double height;
        private double XSpane;
        private double ySpace;

        public PointLine(List<Dot> point2DList, Color color, double width, double height, double xSpace, double ySpace) {
            this.point2DList = point2DList;
            this.color = color;
            this.lineWidth = 2;
            this.width = width;
            this.height = height;
            this.ySpace = ySpace;
            this.XSpane = xSpace;
        }

        public void draw(GraphicsContext gc, double paddingWidth, double paddingHeight){
            gc.setLineWidth(lineWidth);
            Font font = Font.font("宋体", 15);
            gc.setFont(font);
            for (int i=0 ; i<point2DList.size()  - 1; i++){
                Dot begin = point2DList.get(i);
                Dot end = point2DList.get(i+1);
                gc.setStroke(color);
                gc.strokeLine(formatX(begin.getX()) + paddingWidth, formatY(begin.getY()), formatX(end.getX()) + paddingWidth, formatY(end.getY()));

                gc.setStroke(Color.BLACK);
                gc.strokeOval(formatX(begin.getX()) + paddingWidth - 1, formatY(begin.getY()), 2, 2);
                gc.strokeOval(formatX(end.getX()) + paddingWidth - 1, formatY(end.getY()), 2, 2);

                if (cursor != null && Math.abs(formatX(begin.getX()) + paddingWidth - cursor.getX()) < 2){
                    gc.strokeText((int)begin.y + "", formatX(begin.getX()) + paddingWidth, formatY(begin.getY()) - 10);
                }

                if (cursor != null && Math.abs(formatX(end.getX()) + paddingWidth - cursor.getX()) < 2){
                    gc.strokeText((int)end.y + "", formatX(end.getX()) + paddingWidth, formatY(end.getY()) - 10);
                }

            }
        }

        private double formatY(double d){
            return height - (d * this.ySpace );
        }

        private double formatX(double d){
            return d * this.XSpane;
        }
    }

}

