import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JLabel;

public class Village{
	private coordinates position;
	private Color color;
	private int type;
    private String id;
    private boolean visible=true;
    private boolean full=false; 
	private int Imgsize;
	private int capacity=10;
    public Village(coordinates position,Color color,int type,String id,int Imgsize){
		this.position=position;
		this.color=color;
		this.type=type;
		this.id=id;
		this.Imgsize=Imgsize;
	}
    
	public void drawVillage(Graphics g) throws IOException
	{   Image Village0= ImageIO.read(this.getClass().getResource("Hidden_Village-icon.png")).getScaledInstance(Imgsize, Imgsize, Image.SCALE_DEFAULT);
	    Image Village1= ImageIO.read(this.getClass().getResource("Mahi_Mahi_Village-icon.png")).getScaledInstance(Imgsize, Imgsize, Image.SCALE_DEFAULT);
	    Image Village2= ImageIO.read(this.getClass().getResource("Hula_Village-icon.png")).getScaledInstance(Imgsize, Imgsize, Image.SCALE_DEFAULT);
	

		g.setFont( new Font("TimesRoman",Font.PLAIN,20));
        g.setColor(Color.WHITE);
		if (this.type==0)
		g.drawImage(Village0, (int)(position.getX()), (int)(position.getY()), null);
		g.drawString(("Village "+id), (int)(position.getX()), (int)(position.getY()));
		if (this.type==1)
		g.drawImage(Village1, (int)(position.getX()), (int)(position.getY()), null);
		g.drawString(("Village "+id), (int)(position.getX()), (int)(position.getY()));
		
		if (this.type==2)
		g.drawImage(Village2, (int)(position.getX()), (int)(position.getY()), null);
		g.drawString(("Village "+id), (int)(position.getX()), (int)(position.getY()));
		
	}
	
	public String getId(){
		return this.id;
	}
	public coordinates getPosition(){
		return this.position;
	}
    public void setPosition(coordinates position){
    	this.position=position;
    }
    public void setType(int type){
    	this.type=type;
    }

	/**
	 * @return the full
	 */
	public boolean isFull() {
		return full;
	}

	/**
	 * @param full the full to set
	 */
	public void setFull(boolean full) {
		this.full = full;
	}

	/**
	 * @return the capacity
	 */
	public int getCapacity() {
		return capacity;
	}

	/**
	 * @param capacity the capacity to set
	 */
	public void setCapacity(int capacity) {
		this.capacity = capacity;
	}
}