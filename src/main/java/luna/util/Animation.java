package luna.util;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class Animation {
    private int speed;
    private int frames;

    private int index = 0;
    private int count = 0;

    private BufferedImage[] images;
    private BufferedImage currentImg;

    public Animation(int speed, BufferedImage... args){
        this.speed = speed;
        images = new BufferedImage[args.length];
        for(int i = 0; i < args.length; i ++){
            images[i] = args[i];
        }
        frames = args.length;
    }
    // /
    public Animation(int speed, boolean flip, BufferedImage... args) {
        if (flip) {
            this.speed = speed;
            images = new BufferedImage[(args.length * 2) - 2];
            for (int i = 0; i < args.length; i++) {
                images[i] = args[i];
            }
            BufferedImage[] temp = new BufferedImage[args.length - 2];
            for (int i = args.length - 2; i > 0; i--) {
                temp[args.length - i - 2] = images[i];
            }
            for (int i = args.length; i < images.length; i++) {
                images[i] = temp[i - args.length];
            }
            frames = images.length;
        }else{
            this.speed = speed;
            images = new BufferedImage[args.length];
            for (int i = 0; i < args.length; i++) {
                images[i] = args[i];
            }
            frames = args.length;
        }
        runAnimation();
    }

    public void runAnimation(){
        //System.out.println("Updating frame");
        index ++;
        if(index > speed){
            index = 0;
            nextFrame();
        }
    }

    private void nextFrame(){
        for(int i = 0; i < frames; i ++){
            if(count == i)
                currentImg = images[i];
        }

        count ++;

        if(count >= frames)
            count = 0;
    }

    public void drawAnimation(Graphics2D g, int x, int y, int scaleX, int scaleY){
        //System.out.println("Drawing frame");
        g.drawImage(currentImg, x, y, scaleX, scaleY, null);
    }
}
