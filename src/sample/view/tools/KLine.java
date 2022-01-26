package sample.view.tools;

import com.google.common.collect.Lists;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogEvent;
import javafx.scene.control.DialogPane;
import javafx.scene.paint.Color;
import sample.model.DailyIndex;
import sample.util.StockUtil;

import java.text.MessageFormat;
import java.util.Comparator;
import java.util.List;

public class KLine extends Canvas {

    private List<DailyIndex> dailyIndices;

    private int paddingHeight = 1;
    private int paddingWidth = 1;

    private static double bestWidth = 400;
    private static double bestHeight = 300;

    public KLine(List<DailyIndex> dailyIndices, double width, double height) {
        super(width, height);
        this.dailyIndices = dailyIndices;
        drawKLine(width, height);
    }


    public static void showKLine(String title, List<DailyIndex> dailyIndices){
        Dialog dialog = new Dialog();
        KLine kLine = new KLine(dailyIndices, bestWidth, bestHeight);
        DialogPane dialogPane = new DialogPane();
        dialogPane.setContent(kLine);
        dialogPane.setMaxSize(bestWidth, bestHeight);
        dialog.setDialogPane(dialogPane);
        dialog.setTitle(title);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        Node closeButton = dialog.getDialogPane().lookupButton(ButtonType.CLOSE);
        closeButton.setVisible(false);
        dialogPane.getScene().getWindow().sizeToScene();
        dialog.show();
    }

    public void drawKLine(double width , double height){
        GraphicsContext gc = getGraphicsContext2D();
        height = height - paddingHeight * 2;
        width = width - paddingWidth * 2;
        gc.setStroke(Color.BLUE);
        gc.strokeRect(paddingWidth, paddingHeight, width, height);
        double baseHeight = height/3;
        double kheight = baseHeight * 2;
        double vheight = baseHeight;

        double maxPrice = 0;
        double minPrice = 99999999;
        dailyIndices.sort(Comparator.comparing(DailyIndex::getDate));
        long maxVolume = 0;
        long minVolume = Long.MAX_VALUE;
        List<Kindle> kindleList = Lists.newArrayList();
        int idx = 0;
        for (DailyIndex d : dailyIndices){
            maxPrice = Math.max(maxPrice, d.getHighestPrice().doubleValue());
            minPrice = Math.min(minPrice, d.getLowestPrice().doubleValue());
            maxVolume = Math.max(d.getTradingVolume(), maxVolume);
            minVolume = Math.min(d.getTradingVolume(), minVolume);
            kindleList.add(new Kindle(d, idx ++));
        }
        double diffP = maxPrice - minPrice;
        if (diffP == 0) {
            diffP = 1;
        }

        long diffVolume = maxVolume - minVolume;
        if (diffVolume == 0){
            diffVolume = 1;
        }

        double stepHPerPixel = kheight/diffP;
        double stepWPerPixel =  width/(dailyIndices.size());
        double stepVolume = vheight / diffVolume;


        for (Kindle k : kindleList){
            k.normalize(kheight, vheight, minPrice, stepWPerPixel, stepHPerPixel, stepVolume);
            k.draw(gc);
        }
    }


    class Kindle{

        private double x;
        private double open;
        private double close;
        private double max;
        private double min;
        private long volume;
        private double baseVolume;

        private Color color;
        private Color vColor;

        public Kindle(DailyIndex dailyIndex, int idx) {
            if (StockUtil.isGreenKey(dailyIndex) ){
                color = Color.GREEN;
            }else{
                color = Color.RED;
            }

            x = idx;
            open = dailyIndex.getOpeningPrice().doubleValue();
            close = dailyIndex.getClosingPrice().doubleValue();
            max = dailyIndex.getHighestPrice().doubleValue();
            min = dailyIndex.getLowestPrice().doubleValue();
            volume = dailyIndex.getTradingVolume();
            if (dailyIndex.getClosingPrice().compareTo(dailyIndex.getPreClosingPrice()) > 0){
                color = Color.RED;
            }else {
                color = Color.GREEN;
            }
        }

        void draw(GraphicsContext gc) {
            gc.setStroke(color);
            gc.setLineWidth(4);
            gc.strokeLine(x, open, x, close);
            gc.setLineWidth(1);
            gc.strokeLine(x, min, x, max);

            gc.setStroke(vColor);
            gc.setLineWidth(4);
            gc.strokeLine(x, baseVolume, x, volume);

        }

        void normalize(double kheight, double vheight, double basePrice, double wPerPixel, double hPerPixel,  double stepVolume){
            x = x * wPerPixel + paddingWidth;
            kheight += paddingHeight;
            open = kheight - (open - basePrice) * hPerPixel;
            close = kheight - (close - basePrice) * hPerPixel;
            min = kheight - (min - basePrice) * hPerPixel;
            max = kheight - (max - basePrice) * hPerPixel;
            System.out.println(MessageFormat.format("open:{0}, close:{1} , min:{2}, max:{3}", open, close, min, max));
            vheight += kheight;
            volume = (long) (vheight - (volume * stepVolume + paddingHeight));
            baseVolume = vheight - paddingHeight;
        }
    }



    private double formatY(double v, double h, double step){
        return h - v * step;
    }
}
