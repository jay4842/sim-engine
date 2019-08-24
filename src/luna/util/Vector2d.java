package luna.util;

public class Vector2d {
	public double x,y;
	
	public Vector2d(double n1, double n2){
		this.x = n1;
		this.y = n2;
	}
	///////////////////////////////////////////
	public void add(double d){
		x+=d;
		y+=d;
	}
	public void addX(double d){
		x+=d;
	}
	public void addY(double d){
		y+=y;
	}
	
	public void setX(double d){
		x = d;
	}
	public void setY(double d){
		y = d;
	}
	public void sub(double d){
		x-=d;
		y-=d;
	}
	public void div(double d){
		x/=d;
		y/=d;
	}
	public void multi(double d){
		x*=d;
		y*=d;
	}
	public void add(Vector2d other){
		x+=other.x;
		y+=other.y;
	}
	public void sub(Vector2d other){
		x-=other.x;
		y-=other.y;
	}
	public void div(Vector2d other){
		x/=other.x;
		y/=other.y;
	}
	public void multi(Vector2d other){
		x*=other.x;
		y*=other.y;
	}
	                                     
	
}
