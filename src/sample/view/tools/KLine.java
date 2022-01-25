package sample.view.tools;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.DialogPane;
import javafx.scene.paint.Color;
import sample.model.DailyIndex;
import sample.util.StockUtil;

import java.util.Comparator;
import java.util.List;

public class KLine extends DialogPane {

    private List<DailyIndex> dailyIndices;

    public KLine(List<DailyIndex> dailyIndices) {
        this.dailyIndices = dailyIndices;
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
        dailyIndices.sort(Comparator.comparing(DailyIndex::getDate));
        for (DailyIndex d : dailyIndices){
            maxPrice = Math.max(maxPrice, d.getHighestPrice().doubleValue());
            minPrice = Math.min(minPrice, d.getLowestPrice().doubleValue());
        }
        double diffP = maxPrice - minPrice;
        minPrice = minPrice - 1;
        double stepHPerPixel = height/(diffP + 2);
        double stepWPerPixel =  width/(dailyIndices.size() + 2);

        int klineWidht = (int) (stepWPerPixel - 4);


        for (int i=0 ; i<dailyIndices.size() ; i++){
            DailyIndex di = dailyIndices.get(i);
            double x = paddingWidth + (i + 1) * stepWPerPixel;
            double yd = paddingHeight + (di.getLowestPrice().doubleValue() - minPrice)* stepHPerPixel ;
            double yt = paddingHeight + (di.getHighestPrice().doubleValue() - minPrice)*  stepHPerPixel;

            double ykd;
            double ykt;
            // draw shaddow
            if (StockUtil.isGreenKey(di) ){
                gc.setFill(Color.GREEN);
                ykt = paddingHeight + (di.getOpeningPrice().doubleValue() - minPrice) * stepHPerPixel;
                ykd = paddingHeight + (di.getClosingPrice().doubleValue() - minPrice) * stepHPerPixel;
            }else{
                ykd = paddingHeight + (di.getOpeningPrice().doubleValue() - minPrice) * stepHPerPixel;
                ykt = paddingHeight + (di.getClosingPrice().doubleValue() - minPrice) * stepHPerPixel;
                gc.setFill(Color.RED);
            }


            gc.fillRect(x - shaddowLineWidth/2, height - yt, shaddowLineWidth, Math.max(1, yt-yd));
            gc.fillRect(x - klineWidht/2, height - ykt, klineWidht, Math.max(1,ykt - ykd));
        }
    }

    private double formatY(double v, double h, double step){
        return h - v * step;
    }
}
