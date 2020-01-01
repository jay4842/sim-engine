package org.luna.core.util;

// Manage index of frame, the object this belongs to will call the image
//   utility class to get the actual image related to the object
public class Animation {
    private int speed;
    private int frames;

    private int index = 0;
    private int count = 0;

    public Animation(int speed, int numFrames){
        this.speed = speed;
        this.frames = numFrames;
    }

    public void runAnimation(){
        index ++;
        if(index > speed){
            index = 0;
            nextFrame();
        }
    }

    private void nextFrame(){
        count ++;

        if(count >= frames)
            count = 0;
    }

    public int getCount() {
        return count;
    }
}