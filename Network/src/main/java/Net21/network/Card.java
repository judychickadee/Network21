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
    this.path = path;
      
   

    try {
       
        InputStream imgStream = getClass().getResourceAsStream(path);
        
        
        if (imgStream != null) {
            BufferedImage img = ImageIO.read(imgStream);
            if (img == null) {
                throw new IOException("ImageIO.read returned null");
            }
            this.image = scaleImage(new ImageIcon(img), 150, 200);
            imgStream.close();
            System.out.println("Successfully loaded: " + path);
        } else {
            throw new FileNotFoundException("Resource not found");
        }
    } catch (Exception e) {
        System.err.println("Failed loading " + path + ": " + e.getMessage());
        this.image = createBlankCard();
    }
    
}
   private ImageIcon scaleImage(ImageIcon icon, int width, int height) {
    java.awt.Image img = icon.getImage();
    java.awt.Image scaledImg = img.getScaledInstance(width, height, java.awt.Image.SCALE_SMOOTH);
    return new ImageIcon(scaledImg);
}


private ImageIcon createBlankCard() {
    BufferedImage img = new BufferedImage(150, 200, BufferedImage.TYPE_INT_RGB);
    Graphics2D g = img.createGraphics();
    g.setColor(Color.WHITE);
    g.fillRect(0, 0, 150, 200);
    g.setColor(Color.BLACK);
    g.drawString(rank + " of " + suit, 10, 20);
    g.dispose();
    return new ImageIcon(img);
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
}
