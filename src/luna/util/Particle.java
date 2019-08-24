package luna.util;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Graphics;


public class Particle{
    protected Vector2d loc;
    protected Vector2d vel;
    protected Vector2d acc;
    protected Vector2d size;
    protected Vector2d maxSize;
    protected Vector2d growth;
    protected Vector2d life;
    protected Color color;
    protected boolean ultSize = false;
    protected boolean defaultSize = false;
    protected int scale = 1;

    public Particle(double x, double y, double dx, double dy, double size, double life, Color c, int scale){
        this.loc = new Vector2d(x,y);
        this.vel = new Vector2d(dx,dy);
        this.acc = new Vector2d(0,0);
        this.life = new Vector2d(life,life);
        this.size = new Vector2d(size,size);
        this.growth = new Vector2d(0,0);
        this.maxSize = new Vector2d(0,0);
        this.color = c;
        this.scale = scale;
    }// need to create some more constructors for these guys
    ////////////////////////////////////////
    public boolean update(){
        //System.out.println(toString() + " - " + size);
        /////this will depend on what movement the particle will take
        //for now
        vel.add(acc);
        loc.add(vel);
        size.add(growth);
        life.x--;
        life.y--;

        if(life.x <= 0 && life.y <= 0)
            return true;

        if(defaultSize){
            if(size.x >= maxSize.x){
                if(size.y >= maxSize.y)
                    return true;
                else
                    size.x = maxSize.x;
            }
            if(size.y >= maxSize.y) //Note: we already checked if both x and y are bigger.
                size.y = maxSize.y;
            if(size.x <= 0)
                if(size.y <= 0)
                    return true;
                else
                    size.x = 1;
            if(size.y <= 0)
                size.y = 1;
            return false; // we are done
        }

        if(ultSize){ // We will shrink and grow back and forth
            if(size.x > maxSize.x){
                size.x = maxSize.x;
                growth.x *= -1;
            }
            if(size.y > maxSize.y){
                size.y = maxSize.y;
                growth.y *= -1;
            }
            if(size.x <= 0){
                size.x = 1;
                growth.x *= -1;
            }
            if(size.y <= 0){
                size.y = 1;
                growth.y *= -1;
            }
        }
        else{ //We stop growing or shrinking.
            if(size.x > maxSize.x)
                size.x = maxSize.x;
            if(size.y > maxSize.y)
                size.y = maxSize.y;
            if(size.x <= 0)
                size.x = 1;
            if(size.y <= 0)
                size.y = 1;
        }
        return false;
    }
    ////////////////////////////////////////
    public void render(Graphics2D g){
        g.setColor(color);
        g.fillRect((int)(loc.x-(size.x/2)), (int)(loc.y-(size.y/2)), (int)size.x + 2, (int)size.y + 2);
    }
    //////////////////////////////////////////////////////////
    public void setLoc(double x,  double y){
        loc.x = x;
        loc.y = y;
    }

    public void setVel(double x,  double y){
        vel.x = x;
        vel.y = y;
    }

    public void setAcc(double x,  double y){
        acc.x = x;
        acc.y = y;
    }

    public void setSize(double x,  double y){
        size.x = x;
        size.y = y;
    }

    public void setMaxSize(double x,  double y){
        maxSize.x = x;
        maxSize.y = y;
    }

    public void setGrowth(double x,  double y){
        growth.x = x;
        growth.y = y;
    }

    public void setLife(double num){
        life.x = num;
        life.y = num;
    }

    public void setSizeDeault(boolean c){
        defaultSize = c;
    }

    public void setUltSize(boolean c){
        defaultSize = false;
        ultSize = c;
    }

    public Vector2d getLoc(){
        return loc;
    }

    public Vector2d getVel(){
        return vel;
    }
}
