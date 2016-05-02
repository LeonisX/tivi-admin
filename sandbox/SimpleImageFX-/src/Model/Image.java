package Model;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;

public class Image {
    private BufferedImage srcImage;
    private BufferedImage image;
    private double brightness;
    private double contrast;
    private int height;
    private int width;
    private int centerX;
    private int centerY;
    private double radius;
    private double multiple;


    public Image(BufferedImage image){
        setSrcImage(image);
        setImage(image);
        setHeight(image.getHeight());
        setWidth(image.getWidth());
        setBrightness(0);
        setContrast(0);
        setCenterX(50);
        setCenterY(50);
        setRadius(50);
        setMultiple(1.5);
    }

    public void adjustColor(int rDegree, int gDegree, int bDegree){
        BufferedImage tImage = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
        //BufferedImage tImage = new BufferedImage(200, 200,  BufferedImage.TYPE_BYTE_INDEXED);

        int k = rDegree;
        Kernel kernel = new Kernel(3, 3, new float[] { -k/8f, -k/8f, -k/8f, -k/8f, k + 1, -k/8f, -k/8f, -k/8f, -k/8f });
        //Kernel kernel = new Kernel(3, 3, new float[] { -0,1, -0,1, -0,1, -0,1, 1,8, -0,1, -0,1, -0,1, -0,1,  });
        BufferedImageOp op = new ConvolveOp(kernel);
        tImage = op.filter(srcImage, null);
/*        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                Color c = new Color(srcImage.getRGB(j, i));
                int red = c.getRed();
                int green = c.getGreen();
                int blue = c.getBlue();
                red += rDegree;
                green += gDegree;
                blue += bDegree;
                red = red > 255 ? 255 : red;
                red = red < 0 ? 0 : red;
                green = green > 255 ? 255 : green;
                green = green < 0 ? 0 : green;
                blue = blue > 255 ? 255 : blue;
                blue = blue < 0 ? 0 : blue;
                tImage.setRGB(j, i, new Color(red, green, blue).getRGB());
            }
        }*/
        setImage(tImage);
    }

    public void adjustImage(){
        BufferedImage tImage = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                Color c = new Color(srcImage.getRGB(j, i));
                int red = c.getRed();
                int green = c.getGreen();
                int blue = c.getBlue();
                red = (int) ((red - 127.5 * (1 - brightness)) * contrast + 127.5 * (1 + brightness));
                green = (int) ((green - 127.5 * (1 - brightness)) * contrast + 127.5 * (1 + brightness));
                blue = (int) ((blue - 127.5 * (1 - brightness)) * contrast + 127.5 * (1 + brightness));
                red = red > 255 ? 255 : red;
                red = red < 0 ? 0 : red;
                green = green > 255 ? 255 : green;
                green = green < 0 ? 0 : green;
                blue = blue > 255 ? 255 : blue;
                blue = blue < 0 ? 0 : blue;
                tImage.setRGB(j, i, new Color(red, green, blue).getRGB());
            }
        }
        setImage(tImage);
    }

    public void sharpening(){
        int[][] mask= {
                {-1, -1, -1},
                {-1, 30, -1},
                {-1, -1, -1}};
        BufferedImage tImage = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                double[][] f = getAroundPixels(srcImage, j, i);
                Color color = new Color(srcImage.getRGB(j, i));
                double v = 0;
                double h = getHue(color.getRed(), color.getGreen(), color.getBlue());
                double s = getSaturation(color.getRed(), color.getGreen(), color.getBlue());
                for (int k = 0; k < 3; k++) {
                    for (int l = 0; l < 3; l++) {
                        v += mask[k][l] * f[k][l];
                    }
                }
                int[] rgb = hsvToRGB(h, s, v);
                rgb[0] = rgb[0] > 255 ? 255 : rgb[0];
                rgb[0] = rgb[0] < 0 ? 0 : rgb[0];
                rgb[1] = rgb[1] > 255 ? 255 : rgb[1];
                rgb[1] = rgb[1] < 0 ? 0 : rgb[1];
                rgb[2] = rgb[2] > 255 ? 255 : rgb[2];
                rgb[2] = rgb[2] < 0 ? 0 : rgb[2];
                tImage.setRGB(j, i, new Color(rgb[0], rgb[1], rgb[2]).getRGB());
            }
        }
        setImage(tImage);
    }

    private double[][] getAroundPixels(BufferedImage image, int x, int y){
        int height = image.getHeight(), width = image.getWidth();
        double[][] retPixels = new double[3][3];
        for (int i = -1; i < 2; i++) {
            for (int j = -1; j < 2; j++) {
                if((x + j < 0 && y + i < 0) || (x + j >= width && y + i >= height) || (x + j < 0 && y + i >= height) || (x + j >= width && y + i < 0)){
                    Color color = new Color(image.getRGB(x, y));
                    retPixels[i + 1][j + 1] = getValue(color.getRed(), color.getGreen(), color.getBlue());
                } else if(x + j < 0 || x + j >= width){
                    Color color = new Color(image.getRGB(x, y + i));
                    retPixels[i + 1][j + 1] = getValue(color.getRed(), color.getGreen(), color.getBlue());
                } else if(y + i < 0 || y + i >= height){
                    Color color = new Color(image.getRGB(x + j, y));
                    retPixels[i + 1][j + 1] = getValue(color.getRed(), color.getGreen(), color.getBlue());
                } else{
                    Color color = new Color(image.getRGB(x + j, y + i));
                    retPixels[i + 1][j + 1] = getValue(color.getRed(), color.getGreen(), color.getBlue());
                }
            }
        }
        return retPixels;
    }

    public void negative(){
        BufferedImage tImage = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_BYTE_GRAY);
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                tImage.setRGB(j, i, 255 - srcImage.getRGB(j, i));
            }
        }
        setImage(tImage);
    }

    public void applyHE(){
        BufferedImage tImage = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
        double[] n = new double[256];
        double[] p = new double[256];
        double[] c = new double[256];
        int max = 0, min = 0;
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                Color color = new Color(srcImage.getRGB(j, i));
                int v = (int)(getValue(color.getRed(), color.getGreen(), color.getBlue()) * 255);
                n[v]++;
                if(max < v){
                    max = v;
                } else if(min > v){
                    min = v;
                }
            }
        }
        for (int i = 0; i < 256; i++) {
            p[i] = n[i]/(width*height);
        }
        for(int i = 0; i < 256; i++){
            for(int j = 0; j <= i; j++){
                c[i] += p[j];
            }
        }
        for(int i = 0; i < height; i++){
            for(int j = 0; j < width; j++){
                Color color = new Color(srcImage.getRGB(j, i));
                double v = (c[(int)(getValue(color.getRed(), color.getGreen(), color.getBlue()) * 255)] * (max - min) + min) / 255;
                double h = getHue(color.getRed(), color.getGreen(), color.getBlue());
                double s = getSaturation(color.getRed(), color.getGreen(), color.getBlue());
                int[] rgb = hsvToRGB(h, s, v);
                rgb[0] = rgb[0] > 255 ? 255 : rgb[0];
                rgb[0] = rgb[0] < 0 ? 0 : rgb[0];
                rgb[1] = rgb[1] > 255 ? 255 : rgb[1];
                rgb[1] = rgb[1] < 0 ? 0 : rgb[1];
                rgb[2] = rgb[2] > 255 ? 255 : rgb[2];
                rgb[2] = rgb[2] < 0 ? 0 : rgb[2];
                tImage.setRGB(j, i, new Color(rgb[0], rgb[1], rgb[2]).getRGB());
            }
        }
        setImage(tImage);
    }

    public void distorting(){
        BufferedImage tImage = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                int rgb = srcImage.getRGB(j, i);
                //int radius = 300;
                //int centerX = width / 2, centerY = height / 2;
                //double multiple = 1.5;
                double distance = ((centerY - i) * (centerY - i) + (centerX - j) * (centerX - j));
                if (distance < radius * radius)
                {
                    // Í¼Ïñ·Å´óÐ§¹û
                    int src_x = (int) ((float) (j - centerX) / multiple);
                    int src_y = (int) ((float) (i - centerY) / multiple);
                    src_x = (int)(src_x * (Math.sqrt(distance) / radius));
                    src_y = (int)(src_y * (Math.sqrt(distance) / radius));
                    src_x = src_x + centerX;
                    src_y = src_y + centerY;
                    rgb = srcImage.getRGB(src_x, src_y);
                }
                tImage.setRGB(j, i, rgb);
            }
        }
        setImage(tImage);
    }

    public double getRadius(){
        return radius / 100;
    }

    public void setRadius(double radius){
        if(width < height){
            this.radius = radius * (width / 2.0 / 100);
        } else{
            this.radius = radius * (height / 2.0 / 100);
        }
    }

    public int getCenterX(){
        return centerX / 100;
    }

    public void setCenterX(int centerX){
        this.centerX = (int) (centerX * (width - 2 * radius) / 100 + radius);
    }

    public int getCenterY() {
        return centerY / 100;
    }

    public void setCenterY(int centerY) {
        this.centerY = (int) (centerY * (height - 2 * radius) / 100 + radius);
    }

    public double getMultiple() {
        return multiple;
    }

    public void setMultiple(double multiple) {
        this.multiple = multiple;
    }

    public BufferedImage getImage() {
        return image;
    }

    public void setImage(BufferedImage image) {
        this.image = image;
    }

    public BufferedImage getSrcImage() {
        return srcImage;
    }

    public void setSrcImage(BufferedImage srcImage) {
        this.srcImage = srcImage;
    }

    public double getBrightness() {
        return brightness;
    }

    public double getContrast() {
        return contrast;
    }

    public void setBrightness(double brightness) {
        this.brightness = brightness / 100;
    }

    public void setContrast(double contrast) {

        this.contrast = Math.tan((contrast / 100 * 44 + 45) / 180 * Math.PI);
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    private int max(int r, int g, int b) {
        int t = r > g ? r : g;
        return t > b ? t : b;
    }

    private int min(int r, int g, int b) {
        int t = r < g ? r : g;
        return t < b ? t : b;
    }

    private double getHue(int r, int g, int b) {
        int max = max(r, g, b);
        int min = min(r, g, b);
        int c = max - min;
        double hue = 0;
        if (0 == c) {
            hue = 3.5;
        } else if (max == r) {
            hue = (double) (g - b) / c;
            if (hue < 0) {
                hue += 6.0;
            }
        } else if (max == g) {
            hue = 2.0 + (double) (b - r) / c;
        } else {
            hue = 4.0 + (double) (r - g) / c;
        }
        hue *= 60;
        return hue;
    }

    private double getValue(int r, int g, int b) {
        return max(r, g, b) / 255.0;
    }

    private double getSaturation(int r, int g, int b) {
        int v = max(r, g, b);
        int c = max(r, g, b) - min(r, g, b);
        double s;
        if (0 == c) {
            s = 0.0;
        } else {
            s = (double) c / v;
        }
        return s;
    }

    private int[] hsvToRGB(double h, double s, double v) {

        int r, g, b;
        int[] ret = new int[3];
        int max, min;
        double h1 = h;

        max = double2int (v * 255);
        min = double2int (v * (1.0 - s) * 255);

        if (h <= 60.0) {
            h1 /= 60;
            b = min;
            r = max;
            g = double2int (h1 * (r - b) + b);
        } else if (h > 60.0 && h <= 120.0) {
            h1 /= 60;
            h1 -= 2.0;
            b = min;
            g = max;
            r = double2int (-h1 * (g - b) + b);
        } else if (h > 120.0 && h <= 180.0) {
            h1 /= 60;
            h1 -= 2.0;
            r = min;
            g = max;
            b = double2int (h1 * (g - r) + r);
        } else if (h > 180.0 && h <= 240.0) {
            h1 /= 60;
            h1 -= 4.0;
            r = min;
            b = max;
            g = double2int (-h1 * (b - r) + r);
        } else if (h > 240.0 && h <= 300.0) {
            h1 /= 60;
            h1 -= 4.0;
            g = min;
            b = max;
            r = double2int (h1 * (b - g) + g);
        } else {
            h1 /= 60;
            h1 -= 6.0;
            g = min;
            r = max;
            b = double2int (-h1 * (r - g) + g);
        }

        ret[0] = r;
        ret[1] = g;
        ret[2] = b;

        return ret;
    }

    private int double2int(double d) {
        double mod = d;
        int ret;
        if( d < 0.0 ) {
            mod *= -1;
        }
        double error = mod - (int)mod;
        if( 0.5 > error ) {
            ret = (int)mod;
        }
        else {
            ret = (int)mod + 1;
        }
        if( d < 0.0 ) {
            ret *= -1;
        }
        return ret;
    }
}
