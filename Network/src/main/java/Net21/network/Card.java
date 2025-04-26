package Net21.network;
import java.awt.*;
import java.awt.Color;
import java.awt.Graphics2D;
import javax.swing.ImageIcon;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

class Card {
    String suit;
    String rank;
    int value;
    String path;
    private ImageIcon image;
    
 public Card(String suit, String rank, int value, String path) {
    this.suit = suit;
    this.rank = rank;
    this.value = value;
    this.path = path; //to get pictures
      
    try {
        // First try to load from resources folder
        InputStream imgStream = getClass().getResourceAsStream(path);
        
        if (imgStream != null) {
            BufferedImage img = ImageIO.read(imgStream);
            if (img != null) {
                this.image = scaleImage(new ImageIcon(img), 150, 200);
                imgStream.close();
                System.out.println("Successfully loaded card: " + path);
            } else {
                throw new IOException("ImageIO.read returned null for " + path);
            }
        } else {
            // If resource not found, try with File
            File imgFile = new File(System.getProperty("user.dir") + path);
            if (imgFile.exists()) {
                BufferedImage img = ImageIO.read(imgFile);
                this.image = scaleImage(new ImageIcon(img), 150, 200);
                System.out.println("Successfully loaded card from file: " + imgFile.getAbsolutePath());
            } else {
                throw new FileNotFoundException("Resource not found: " + path);
            }
        }
    } catch (Exception e) {
        System.err.println("Failed loading " + path + ": " + e.getMessage());
        
    }
}
  private ImageIcon scaleImage(ImageIcon icon, int width, int height) { //help make sure the images are scaled
    java.awt.Image img = icon.getImage();
    java.awt.Image scaledImg = img.getScaledInstance(width, height, java.awt.Image.SCALE_SMOOTH);
    return new ImageIcon(scaledImg);
}


    @Override
    public String toString() {
        return rank + " of " + suit;
    }
      public ImageIcon getImage() { //test
        return image;
    }


    public int getValue(){
        return value;
    }
    
    public String getImagePath(){
        return path;
    }
}
