package base;

/**
 * Superclass on which all objects in the building are based on. Include
 * universal attributes applicable to any object.
 * 
 * @author Logan Moore
 */
public abstract class BuildingObject
{
	protected BuildingObject( int x, int y, int width, int height )
	{
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}

	/**
	 * Speed and direction of the current object in pixels per tick.
	 * Directions/signs follow the pixel coordinate conventions.
	 */
	protected transient int velocityX = 0, velocityY = 0;

	public int getVelocityX()
	{
		return velocityX;
	}

	public int getVelocityY()
	{
		return velocityY;
	}

	/**
	 * Visibility of the given object (whether or not it is to be drawn).
	 */
	protected final boolean visible = false;

	public boolean isVisible()
	{
		return visible;
	}

	/**
	 * Whether the object can be walked through
	 */
	boolean passable = true;

	public boolean isPassable()
	{
		return passable;
	}

	/**
	 * Pixel coordinates of the object.
	 */
	public transient int x, y;

	/**
	 * Pixel dimensions of the object
	 */
	protected int width, height;

	/**
	 * Returns the width of the object in pixels
	 * 
	 * @return width of the object
	 */
	public int getWidth()
	{
		return this.width;
	}

	/**
	 * Returns the height of the object in pixels
	 * 
	 * @return height of the object
	 */
	public int getHeight()
	{
		return this.height;
	}

	/**
	 * Whether or not the given object is interactive. Overridden by the
	 * interactive class.
	 */
	protected boolean interactive = false;

	/**
	 * Returns whether or not the given object is interactive.
	 * 
	 * @return interactivity of the object
	 */
	public boolean isInteractive()
	{
		return interactive;
	}

	/**
	 * Whether or not the given object is being used by something else.
	 */
	protected boolean inUse = false;

	public boolean isInUse()
	{
		return inUse;
	}
}
