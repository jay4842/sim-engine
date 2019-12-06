package luna.main;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.*;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;

import javax.swing.JFrame;
import luna.util.Particle;
import luna.util.Util;
import luna.util.db.dbo.Data;
import luna.world.World;

//TODO: Fix open defects
// - when an entity begins close to an encounter then tries to go to the submap it never transitions correctly
//   - Add a timeout function, if we have too many tries for a position change abort current command
//TODO: Database integration
// - planning
// - database selection
// - implementation
public class Game extends Canvas implements MouseListener, MouseMotionListener {
    boolean gameRunning = true;
    private static final long serialVersionUID = 1L;

    public static final int WIDTH = 512;// 512
    public static final int HEIGHT = 512;// 256
    public static final int SCALE = 3;
    public static final String NAME = "Game";

    public Color back_color = new Color(167, 231, 250);//new Color(230, 255, 242);
    private JFrame frame;

    public boolean running = false;
    public int tickCount = 0;

    public static final int world_scale = 8;
    public static List<Particle> particles = Collections.synchronizedList(new ArrayList<Particle>());
    
    private BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
    private int[] pixels = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();

    World world;

    int mx = 0, my = 0;
    public static int seconds = 0;

    public static final int ACTUAL_WIDTH = WIDTH + world_scale*6;

    public static Data data;

    public Game(){
        setMinimumSize(new Dimension(WIDTH + world_scale*6, HEIGHT));
        setMaximumSize(new Dimension(WIDTH + world_scale*6, HEIGHT));
        setPreferredSize(new Dimension(WIDTH + world_scale*6, HEIGHT));

        frame = new JFrame(NAME);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        frame.add(this, BorderLayout.CENTER);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                world.shutdown();
                System.exit(0);
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


        this.world = new World(WIDTH, HEIGHT, this.world_scale);
        this.data = new Data();
    }

    public void gameLoop() {
        long lastLoopTime = System.nanoTime();
        final int TARGET_FPS = 60;
        final long OPTIMAL_TIME = 1000000000 / TARGET_FPS;
        int lastFpsTime = 0;
        int fps = 0;

        // keep looping round til the game ends
        while (gameRunning) {
            // work out how long its been since the last update, this
            // will be used to calculate how far the entities should
            // move this loop
            long now = System.nanoTime();
            long updateLength = now - lastLoopTime;
            lastLoopTime = now;
            double delta = updateLength / ((double)OPTIMAL_TIME);

            // update the frame counter
            lastFpsTime += updateLength;
            fps++;

            // update our FPS counter if a second has passed since
            // we last recorded
            if (lastFpsTime >= 1000000000)
            {
                System.out.println("(FPS: "+fps+")");
                lastFpsTime = 0;
                fps = 0;
                seconds ++;
            }

            // update the game logic
            tick(delta);

            // draw everyting
            render();

            // we want each frame to take 10 milliseconds, to do this
            // we've recorded when we started the frame. We add 10 milliseconds
            // to this and then factor in the current time to give
            // us our final value to wait for
            // remember this is in ms, whereas our lastLoopTime etc. vars are in ns.
            try{
                Thread.sleep( (lastLoopTime-System.nanoTime() + OPTIMAL_TIME)/1000000 );
            }catch (Exception ex){

            }
        }
    }// end of loop

    public void tick(double delta){
        // update stuff here
        synchronized (particles) {
            for (int i = 0; i <= particles.size() - 1; i++) {
                if (particles.get(i).update())
                    particles.remove(i);
            }
        }
        world.update(seconds);
        // This is a visualizer for the update stuff, it looks cool
        //for(int i = 0; i < this.pixels.length; i++){
        //   this.pixels[i] = i + (int)delta;

        //}
    }//

    // render using graphics g
    public void render(){
        BufferStrategy bs = getBufferStrategy();
        if (bs == null){
            createBufferStrategy(3);
            return;
        }

        Graphics2D g = (Graphics2D) bs.getDrawGraphics();
        // draw here
        g.setColor(back_color);
        g.fillRect(0, 0, getWidth(), getHeight());

        world.render(g);

        g.setColor(Color.black);
        g.fillRect(mx,my,5,5);
        for(int i = 0; i <= particles.size() - 1;i++){
            particles.get(i).render(g);
        }
        //
        //g.drawImage(image, 0, 0, getWidth(), getHeight(), null);
        g.dispose();
        bs.show();
    }
    //
    public void runGameLoop(){
        Thread loop = new Thread()
        {
            public void run()
            {
                gameLoop();
            }
        };
        loop.start();
    }


    @Override
    public void mouseClicked(MouseEvent m) {
        mx = m.getX()-5;
        my = m.getY()-5;
        System.out.println("Click");
        for(int i = 0; i < 10; i++)
            particles.add(Util.makeParticle(mx,my,Color.black, this.world_scale));
    }

    @Override
    public void mousePressed(MouseEvent mouseEvent) {

    }

    @Override
    public void mouseReleased(MouseEvent mouseEvent) {

    }

    @Override
    public void mouseEntered(MouseEvent mouseEvent) {

    }

    @Override
    public void mouseExited(MouseEvent mouseEvent) {

    }

    @Override
    public void mouseDragged(MouseEvent mouseEvent) {

    }

    @Override
    public void mouseMoved(MouseEvent m) {
        mx = m.getX()-5;
        my = m.getY()-5;
    }

    public Rectangle getCursorBound(){
        return new Rectangle(mx,my,5,5);
    }
}
