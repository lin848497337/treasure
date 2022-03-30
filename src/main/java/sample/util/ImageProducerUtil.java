package sample.util;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;

public class ImageProducerUtil {
  

      
    private static int[] getWidthAndHeight(java.util.List<String> text, Font font) {
        String maxText = "";
        int len = 0;
        for (String t : text){
            if (t.length() > len){
                maxText = t;
                len = t.length();
            }
        }
        Rectangle2D r = font.getStringBounds(maxText, new FontRenderContext(
                AffineTransform.getScaleInstance(1, 1), false, false));
        int unitHeight = (int) Math.floor(r.getHeight());//   
        // 获取整个str用了font样式的宽度这里用四舍五入后+1保证宽度绝对能容纳这个字符串作为图片的宽度  
        int width = (int) Math.round(r.getWidth()) + 1;  
        // 把单个字符的高度+3保证高度绝对能容纳字符串作为图片的高度  
        int height = unitHeight + 4;
        System.out.println("width:" + width + ", height:" + height);  
        return new int[]{width, height * text.size()};
    }  
  
    // 根据str,font的样式以及输出文件目录  
    public static void createImage(java.util.List<String> text, Font font, File outFile)
            throws Exception {  
        // 获取font的样式应用在str上的整个矩形  
        int[] arr = getWidthAndHeight(text, font);  
        int width = arr[0];  
        int height = arr[1];  
        // 创建图片  
        BufferedImage image = new BufferedImage(width, height,
                BufferedImage.TRANSLUCENT);//创建图片画布
        Graphics g = image.getGraphics();  
//        g.setColor(Color.WHITE); // 先用白色填充整张图片,也就是背景
//        g.fillRect(0, 0, width, height);//画出矩形区域，以便于在矩形区域内写入文字
        g.setColor(Color.black);// 再换成黑色，以便于写入文字  
        g.setFont(font);// 设置画笔字体
        int i = 2;
        for (String t : text){
            g.drawString(t, 0, font.getSize() * i++);// 画出一行字符串
        }
        g.dispose();  
        ImageIO.write(image, "png", outFile);// 输出png图片
    }  
}
