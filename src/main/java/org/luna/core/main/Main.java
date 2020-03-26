package org.luna.core.main;

// for starters: from the lwjgl guid
import org.luna.logic.GameLogic;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

public class Main extends Canvas implements MouseListener, MouseMotionListener {
    // window handle
    private GameLogic game;

    private static final int WIDTH = 512;// 512
    private static final int HEIGHT = 512;// 256
    private static final String NAME = "Sim-Engine";
    private static final int world_scale = 32; // tile size tile area = (world_scale * world_scale)
    //private static final int sub_world_scale = (world_scale/2); // Note: will be used at a later time

    private BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
    private int[] pixels = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();

    private Color back_color = new Color(167, 231, 250);//new Color(230, 255, 242);
    private boolean gameRunning = true;
    private boolean paused = false; // TODO: add pausing the sim
    private int fps = 60;
    private int frameCount = 0;

    private int mx, my;

    private void run(){
        try {
            System.out.println("Starting up");
            init();
            loop();
            System.out.println("done");
        }catch (Exception ex){
            ex.printStackTrace();
        }
        dispose();

    }//

    private void init(){
        mx = 0;
        my = 0;
        setMinimumSize(new Dimension(WIDTH + world_scale*6, HEIGHT));
        setMaximumSize(new Dimension(WIDTH + world_scale*6, HEIGHT));
        setPreferredSize(new Dimension(WIDTH + world_scale*6, HEIGHT));

        JFrame frame = new JFrame(NAME);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        frame.add(this, BorderLayout.CENTER);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                game.shutdown();
                gameRunning = false;
                // done closing the program
            }
        });
        addMouseListener(this);
        addMouseMotionListener(this);
        // some more mouse stuff
        BufferedImage cursorImg = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);

        // Create a new blank cursor.
        Cursor blankCursor = Toolkit.getDefaultToolkit().createCustomCursor(
                cursorImg, new Point(0, 0), "blank cursor");
        frame.setCursor(blankCursor);
        frame.pack();

        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        // end of frame setup

        // other init calls here
        game = new GameLogic(HEIGHT, WIDTH, world_scale);

    }//

    private void update(int fps){
        //System.out.println("update");
        game.update(fps);
    }

    private void render(float interp){
        //System.out.println("render " + alpha);
        BufferStrategy bs = getBufferStrategy();
        if (bs == null){
            createBufferStrategy(3);
            return;
        }
        Graphics2D g = (Graphics2D) bs.getDrawGraphics();
        // draw here
        g.setColor(back_color);
        g.fillRect(0, 0, getWidth(), getHeight());
        //
        game.render(g, paused);
        //
        g.setColor(Color.black);
        g.fillRect(mx,my,5,5);

        frameCount++;
        g.dispose();
        bs.show();
    }

    private void dispose(){
        // dispose calls here
    }

    private void input(){
        // TODO - add input logic
    }

    // https://gamedev.stackexchange.com/questions/69753/game-loop-best-way-to-limit-the-fps
    private void loop(){
        //This value would probably be stored elsewhere.
        final double GAME_HERTZ = 30.0;
        //Calculate how many ns each frame should take for our target game hertz.
        final double TIME_BETWEEN_UPDATES = 1000000000 / GAME_HERTZ;
        //At the very most we will update the game this many times before a new render.
        //If you're worried about visual hitches more than perfect timing, set this to 1.
        final int MAX_UPDATES_BEFORE_RENDER = 5;
        //We will need the last update time.
        double lastUpdateTime = System.nanoTime();
        //Store the last time we rendered.
        double lastRenderTime = System.nanoTime();
        //If we are able to get as high as this FPS, don't render again.
        final double TARGET_FPS = 60;
        final double TARGET_TIME_BETWEEN_RENDERS = 1000000000 / TARGET_FPS;
        //Simple way of finding FPS.
        int lastSecondTime = (int) (lastUpdateTime / 1000000000);
        System.out.println("Starting up");
        while(gameRunning){
            double now = System.nanoTime();
            int updateCount = 0;
            if(!paused){
                //Do as many game updates as we need to, potentially playing catchup.
                while( now - lastUpdateTime > TIME_BETWEEN_UPDATES && updateCount < MAX_UPDATES_BEFORE_RENDER ) {
                    update(fps);
                    lastUpdateTime += TIME_BETWEEN_UPDATES;
                    updateCount++;
                }
                //If for some reason an update takes forever, we don't want to do an insane number of catchups.
                //If you were doing some sort of game that needed to keep EXACT time, you would get rid of this.
                if ( now - lastUpdateTime > TIME_BETWEEN_UPDATES) {
                    lastUpdateTime = now - TIME_BETWEEN_UPDATES;
                }
                //Render. To do so, we need to calculate interpolation for a smooth render.
                float interpolation = Math.min(1.0f, (float) ((now - lastUpdateTime) / TIME_BETWEEN_UPDATES) );
                render(interpolation);
                lastRenderTime = now;
                //Update the frames we got.
                int thisSecond = (int) (lastUpdateTime / 1000000000);
                if (thisSecond > lastSecondTime){
                    System.out.println("NEW SECOND " + fps + " " + frameCount);
                    fps = frameCount;
                    frameCount = 0;
                    lastSecondTime = thisSecond;
                }
                //Yield until it has been at least the target time between renders. This saves the CPU from hogging.
                while ( now - lastRenderTime < TARGET_TIME_BETWEEN_RENDERS && now - lastUpdateTime < TIME_BETWEEN_UPDATES) {
                    Thread.yield();

                    //This stops the app from consuming all your CPU. It makes this slightly less accurate, but is worth it.
                    //You can remove this line and it will still work (better), your CPU just climbs on certain OSes.
                    //FYI on some OS's this can cause pretty bad stuttering. Scroll down and have a look at different peoples' solutions to this.
                    try {Thread.sleep(1);} catch(Exception e) {e.printStackTrace();}
                    now = System.nanoTime();
                }
            }
        }//
        // done
        System.out.println("Stopping");
    }//

    @Override
    public void mouseClicked(MouseEvent e) {
        mx = e.getX()-5;
        my = e.getY()-5;
    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {
        mx = e.getX()-5;
        my = e.getY()-5;
    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void mouseDragged(MouseEvent e) {
        mx = e.getX()-5;
        my = e.getY()-5;
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        mx = e.getX()-5;
        my = e.getY()-5;
    }

    private Rectangle getCursorBound(){
        return new Rectangle(mx,my,5,5);
    }
///////////////////////////////////////////////////////
    public static void main(String[] args){
        new Main().run();
    }


}
