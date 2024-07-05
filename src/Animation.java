import java.awt.image.BufferedImage;
import java.util.List;
import java.util.ArrayList;

public class Animation {
   
	private int frameShift;
	public int currentFrame;
	public int totalFrames;
	private boolean stopped;
	private List<Frame> framesForAnimation = new ArrayList<Frame>(); 
	
	public Animation(BufferedImage[] frames) 
	{
		// on rajoute chaque frame du tableau dans la List de frames pour l'animation
		for (int i = 0 ; i < frames.length ; i++) {
			framesForAnimation.add(new Frame(frames[i]));
		}
		this.frameShift = 0;
		this.currentFrame = 0;
		this.totalFrames = this.framesForAnimation.size();
		this.stopped = false; 
	}

	public BufferedImage getSprite()
	{
		return framesForAnimation.get(currentFrame).getFrame();
	}

	public void stop() { stopped = true; }
    
	public void restart()
	{
		stopped = false;
		currentFrame = totalFrames - 1;
		updateMove();
	}

    	// Pour les animations de déplacement, on a besoin de frameShift pour décaler les images
	public void updateMove()
	{
		if (!stopped) {
			currentFrame = (currentFrame + 1) % totalFrames;
			frameShift = currentFrame*World.SPRITE_LENGTH/(totalFrames - 1);
			if (currentFrame == totalFrames - 1){
				stop();
			}
		}
	}
	
	// Pour les animations de manger/dormir, l'image doit pas se décaler donc pas de frameShift
	public void updateAction()
	{
		if (!stopped) {
			currentFrame = (currentFrame + 1) % totalFrames;
			frameShift = 0;
			if (currentFrame == totalFrames - 1){
				stop();
			}
		}
	}
	
	public int getFrameShift() { return frameShift; }

}
