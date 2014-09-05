package people;

import interactive.Door;
import interactive.Elevator;
import interactive.ElevatorButton;
import interactive.LightSwitch;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import main.Building;
import base.BuildingObject;
import base.Interactive;
import base.Visible;
import boundaries.Floor;
import boundaries.Wall;
import constants.Constants;

public class Person extends BuildingObject implements Interactive, Visible, Runnable
{
	public Person( Building building, double x, double y )
	{
		super( building, x, y, Constants.PERSON_WIDTH, Constants.PERSON_HEIGHT );

		try
		{
			this.head = ImageIO.read( getClass().getResource(
					"/resources/images/person/standard/_head.png" ) );
			this.body = ImageIO.read( getClass().getResource(
					"/resources/images/person/standard/_body.png" ) );
		}
		catch ( IOException e )
		{
			System.out.println( "Could not read character graphics." );
		}
	}

	/**
	 * Objects that the given person has already interacted with
	 * (used to make conversation less repetitive, etc.)
	 */
	ArrayList < BuildingObject > interactedWith = new ArrayList < BuildingObject >();

	public BuildingObject interactiveObjectWithinReach;

	public double velocityX = 0, velocityY = 0;

	public boolean wantsToInteract = false;

	public boolean isFloorBelow()
	{
		Rectangle bounds = new Rectangle( ( int ) this.x, ( int ) this.y + this.height + 1,
				this.width, 1 );
		for ( Floor floor : this.building.floors )
		{
			if ( bounds.intersects( floor.getBounds() ) )
			{
				return true;
			}
		}
		return false;
	}

	/**
	 * Finds the closest object to a person within its BoundingBox.
	 * 
	 * @param sourcePerson is the person trying to interact.
	 * @return the interactive object closest to the person
	 */
	public BuildingObject getClosestInteractiveObject()
	{
		Rectangle origBounds = this.getBounds();
		Rectangle bounds = new Rectangle( origBounds.x - 10, origBounds.y - 10,
				origBounds.width + 20, origBounds.height + 20 );
		for ( Person person : this.building.people )
		{
			if ( person.getBounds().intersects( bounds ) )
			{
				return person;
			}
		}
		for ( LightSwitch lightSwitch : this.building.lightSwitches )
		{
			if ( lightSwitch.getBounds().intersects( bounds ) )
			{
				return lightSwitch;
			}
		}
		for ( Elevator elevator : this.building.elevators )
		{
			if ( bounds.intersects( ( new Rectangle( ( int ) elevator.x, ( int ) elevator
					.getCarHeight(), Constants.ELEVATOR_WIDTH, Constants.ELEVATOR_CAR_HEIGHT ) ) ) )
			{
				return elevator;
			}

		}

		for ( ElevatorButton elevatorButton : this.building.elevatorButtons )
		{
			if ( elevatorButton.getBounds().intersects( bounds ) )
			{
				return elevatorButton;
			}
		}

		for ( Door door : this.building.doors )
		{
			if ( door.getBounds().intersects( bounds ) )
			{
				return door;
			}
		}
		return null;
	}

	/**
	 * Image used for the face of the given person.
	 */
	BufferedImage head;
	/**
	 * Image used for the body of the given person.
	 */
	BufferedImage body;

	public BufferedImage getHead()
	{
		return head;
	}

	String toBeSaid;

	int time = 0;

	protected String name = "Anonymous" + ( 1 + ( int ) Math.random() * 100 );

	public String getName()
	{
		return this.name;
	}

	/**
	 * Method for person-to-person interaction
	 * 
	 * @param otherPerson in the person interacting with the current person.
	 * @throws InterruptedException
	 */
	public void interact( Person otherPerson )
	{
		this.inUse = true;
		otherPerson.velocityX = 0;
		otherPerson.inUse = true;
		this.animationStep[ 2 ] = 255;
		otherPerson.animationStep[ 2 ] = 255;

		if ( this.interactedWith.contains( otherPerson ) )
		{ // Already interacted with
			otherPerson.toBeSaid = "Hi.";
			this.toBeSaid = "Hi.";
		}
		else
		{ // Seen for the first time
			this.interactedWith.add( otherPerson );
			otherPerson.interactedWith.add( this );

			this.toBeSaid = "Hi, my name is " + this.name;
			otherPerson.toBeSaid = "Nice to meet you " + this.name + ", my name is "
					+ otherPerson.name;
			otherPerson.animationStep[ 2 ] = 255;
		}

		this.inUse = false;
		otherPerson.inUse = false;
	}

	@Override
	public void run()
	{
		boolean LEFT = false;
		boolean RIGHT = true;
		boolean direction = true;

		while ( true )
		{
			// Movement
			if ( this.velocityX == 0 )
			{
				if ( direction == RIGHT )
				{
					Rectangle bounds = new Rectangle( ( int ) this.x, ( int ) this.y,
							this.width + 1, this.height );
					boolean wasADoor = false;
					for ( Door door : this.building.doors )
					{
						if ( door.getBounds().intersects( bounds ) )
						{
							wasADoor = true;
							door.interact( this );
						}
					}
					if ( wasADoor )
					{
						this.velocityX = 1;
					}
					else
					{
						direction = LEFT;
						this.velocityX = -1;
					}
				}
				else
				{
					Rectangle bounds = new Rectangle( ( int ) this.x - 1, ( int ) this.y,
							this.width, this.height );
					boolean wasADoor = false;
					for ( Door door : this.building.doors )
					{
						if ( door.getBounds().intersects( bounds ) )
						{
							wasADoor = true;
							door.interact( this );
						}
					}
					if ( wasADoor )
					{
						this.velocityX = -1;
					}
					else
					{
						direction = RIGHT;
						this.velocityX = 1;
					}
				}
			}

			// Interaction
			/*
			 * for ( Person person : this.building.people )
			 * {
			 * if ( person.getBounds().intersects( this.getBounds() ) && !person.equals( this ) )
			 * {
			 * person.interact( this );
			 * }
			 * }
			 * if ( this.building.me.getBounds().intersects( this.getBounds() ) )
			 * {
			 * this.building.me.interact( this );
			 * }
			 */

			// Delay
			try
			{
				Thread.sleep( Constants.AI_CYCLE );
			}
			catch ( InterruptedException e )
			{
				e.printStackTrace();
			}
		}
	}

	@Override
	public void interact( BuildingObject object )
	{
		this.inUse = true;
		this.toBeSaid = "This shouldn't even happen, because that would imply some object is interacting with a person. Only people should interact with other people.";
		this.inUse = false;
	}

	public void say( Graphics2D g2d )
	{
		if ( this.toBeSaid != null && this.toBeSaid != "" && this.animationStep[ 2 ] != 0 )
		{
			int textWidth = this.toBeSaid.length() * 3;
			g2d.setColor( new Color( 0, 0, 0, this.animationStep[ 2 ] ) );
			g2d.drawString( this.toBeSaid, ( int ) this.x - ( ( textWidth - this.width ) / 2 ),
					( int ) this.y - Constants.TEXT_BOX_DISTANCE );
			this.animationStep[ 2 ]--;
		}
	}

	public boolean canMoveX( Building b )
	{
		boolean canMoveX = true;
		Rectangle nextLocation = new Rectangle( ( int ) ( this.x + this.velocityX ),
				( int ) ( this.y + this.velocityY ), this.width, this.height );

		for ( Wall wall : b.walls )
		{
			if ( nextLocation.intersects( wall.getBounds() ) )
			{
				canMoveX = false;
			}
		}
		for ( Door door : b.doors )
		{
			if ( door.open == 0 && nextLocation.intersects( door.getBounds() ) )
			{
				canMoveX = false;
			}
		}
		return canMoveX;
	}

	public boolean canMoveY( Building b )
	{
		boolean canMoveY = true;
		Rectangle nextLocation = new Rectangle( ( int ) ( this.x + this.velocityX ),
				( int ) ( this.y + this.velocityY ), this.width, this.height );
		for ( Floor floor : b.floors )
		{
			if ( nextLocation.intersects( floor.getBounds() ) )
			{
				canMoveY = false;
			}
		}
		return canMoveY;
	}

	public void move( Building b )
	{
		if ( this.canMoveX( b ) )
		{
			this.x += this.velocityX;
		}
		else
		{
			this.velocityX = 0;
		}

		if ( this.canMoveY( b ) )
		{
			if ( this.velocityY < Constants.TERMINAL_VELOCITY )
			{
				this.velocityY += 0.1;
			}
			this.y += this.velocityY;
		}
		else
		{
			this.velocityY = 0;
		}
	}

	@Override
	public void paint( Graphics2D g2d )
	{
		g2d.setColor( Color.BLACK );
		if ( this.velocityX == 0 )
		{ // Standing still
			// Head
			this.animationStep[ 0 ] = 0;
			g2d.drawImage(
					this.head.getSubimage( 0, 20, 10, 10 ).getScaledInstance(
							Constants.PERSON_WIDTH, Constants.PERSON_HEAD_HEIGHT, 0 ),
					( int ) this.x, ( int ) this.y, null );

			// Body
			this.animationStep[ 1 ] = 0;
			g2d.drawImage(
					this.body.getSubimage( 0, 30, 10, 15 ).getScaledInstance(
							Constants.PERSON_WIDTH, Constants.PERSON_BODY_HEIGHT, 0 ),
					( int ) this.x, ( int ) this.y + Constants.PERSON_HEAD_HEIGHT, null );
		}
		else if ( this.velocityX > 0 )
		{ // Moving to the right
			// Head
			g2d.drawImage(
					this.head.getSubimage(
							10 * ( this.animationStep[ 0 ] - ( this.animationStep[ 0 ] % 9 ) ) / 9,
							10, 10, 10 ).getScaledInstance( Constants.PERSON_WIDTH,
							Constants.PERSON_HEAD_HEIGHT, 0 ), ( int ) this.x, ( int ) this.y, null );
			this.animationStep[ 0 ] = ( this.animationStep[ 0 ] + 1 ) % 81;

			// Body
			g2d.drawImage(
					this.body
							.getSubimage(
									10 * ( this.animationStep[ 1 ] - ( this.animationStep[ 1 ] % 10 ) ) / 10,
									15, 10, 15 ).getScaledInstance( Constants.PERSON_WIDTH,
									Constants.PERSON_BODY_HEIGHT, 0 ), ( int ) this.x,
					( int ) this.y + Constants.PERSON_HEAD_HEIGHT, null );
			this.animationStep[ 1 ] = ( this.animationStep[ 1 ] + 1 ) % 100;
		}
		else
		{ // Moving to the left
			// Head
			g2d.drawImage(
					this.head.getSubimage(
							10 * ( this.animationStep[ 0 ] - ( this.animationStep[ 0 ] % 9 ) ) / 9,
							0, 10, 10 ).getScaledInstance( Constants.PERSON_WIDTH,
							Constants.PERSON_HEAD_HEIGHT, 0 ), ( int ) this.x, ( int ) this.y, null );
			this.animationStep[ 0 ] = ( this.animationStep[ 0 ] + 1 ) % 81;

			// Body
			g2d.drawImage(
					this.body
							.getSubimage(
									10 * ( this.animationStep[ 1 ] - ( this.animationStep[ 1 ] % 10 ) ) / 10,
									0, 10, 15 ).getScaledInstance( Constants.PERSON_WIDTH,
									Constants.PERSON_BODY_HEIGHT, 0 ), ( int ) this.x,
					( int ) this.y + Constants.PERSON_HEAD_HEIGHT, null );
			this.animationStep[ 1 ] = ( this.animationStep[ 1 ] + 1 ) % 100;
		}

		this.say( g2d );

		if ( this.drawBounds )
		{
			this.drawBounds( g2d );
		}
	}
}
