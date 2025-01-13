// This is an example game program written in Java as a project
// between me and Elliott. Elliott designed the screen and characters;
// he also told me how he wanted the game to be played. I then used
// my old Trap games to lift the graphics processing and main
// loop as a starting point.

// This is the class for a game board that contains an array for
// the entire game space showing where the player and blocks and aliens are.
// This class includes member functions to access the board. It makes
// use of gprahics2d so that the characters can be designed outside java
// and called in as gif files.

import java.util.Random;
import java.awt.*;
import java.awt.event.*;
import java.awt.Image;
import java.awt.Toolkit;

public class PacMan extends Frame implements KeyListener 
{
	// Set up charaters for game
	private static char SPACE   =  1;	// Blank space
	private static char POINT   =  2;	// points which must be cleared
	private static char POWER   =  3;	// big dot for power-up
	private static char BLOCK   = 10;	// Fixed blocks for screen
	private static char PACMN   = 20;	// Player

	private static int  BLKSIZE = 32;	// size of graphic block squares
	// Part block sizes used for smoother animations
	private static int  BLKSIZ1 =  8;	// quarter of graphic block

	// Constants for game screen size and position
	private static int  OFFSETX = 25;	// pixel offset to start of screen X
	private static int  OFFSETY = 40;	// pixel offset to start of screen Y
	private static int  MAXX    = 20;	// board size X axis
	private static int  MAXY    = 18;	// board size Y axis

	// minimum game dealy setting maximum speed of play
	private static int  MINDEL  = 50;	// Minimum game delay

	// constants used to select movement direction of Aliens
	private static char UP 		= 2;	// directions
	private static char RIGHT	= 1;
	private static char DOWN 	= 3;
	private static char LEFT 	= 4;

	private static int	score;						// score total
	private static int	screen;						// current screen number
	private static int	xold;						// previous position for PacMan animation
	private static int	yold;
	private static int	direction;					// use for animation.
	private static int	animation = 0;				// use for animation sequencing.
	private static int  alienx[] = new int[30];		// positions of aliens
	private static int  alieny[] = new int[30];
	private static char aliend[] = new char[30];	// alien direction.
	private static int  alienxold[] = new int[30];	// used for animation.
	private static int  alienyold[] = new int[30];	// used for animation.
	private static int  Naliens = 3;				// number of aliens in game which can increase
	private static int	playerx;					// pacMan player position
	private static int	playery;

	// Array of characters used to represent game play for game progress and for display processing.
	// Each character represents a block on the screen.
	private static char[][]	store = new char[MAXX+1][MAXY+1];

	// Note that PACMAN PACMN appears on the gameboard. The Aliens are sprites to that they do not
	// permanently overwrite the gameboard contents.

	// This is a constant array representing the starting point of the game.
	final static char scr[][]	=
		{{ BLOCK, BLOCK, BLOCK, BLOCK, BLOCK, BLOCK, BLOCK, BLOCK, BLOCK, BLOCK, BLOCK, BLOCK, BLOCK, BLOCK, BLOCK, BLOCK, BLOCK, BLOCK, BLOCK, BLOCK, BLOCK },
		 { BLOCK, POINT, PACMN, POINT, BLOCK, BLOCK, BLOCK, BLOCK, POINT, POINT, POINT, POINT, POINT, BLOCK, BLOCK, BLOCK, BLOCK, POINT, POWER, POINT, BLOCK },
		 { BLOCK, POINT, POINT, POINT, POINT, POINT, POINT, POINT, POINT, POINT, BLOCK, POINT, POINT, POINT, POINT, POINT, POINT, POINT, POINT, POINT, BLOCK },
		 { BLOCK, POINT, BLOCK, BLOCK, BLOCK, POINT, POINT, BLOCK, BLOCK, BLOCK, BLOCK, BLOCK, BLOCK, BLOCK, POINT, POINT, BLOCK, BLOCK, BLOCK, POINT, BLOCK },
		 { BLOCK, POINT, POINT, POINT, POINT, POINT, POINT, POINT, POINT, POINT, POINT, POINT, POINT, POINT, POINT, POINT, POINT, POINT, POINT, POINT, BLOCK },
		 { BLOCK, BLOCK, BLOCK, POINT, BLOCK, POINT, POINT, POINT, POINT, POINT, BLOCK, POINT, POINT, POINT, POINT, POINT, BLOCK, POINT, BLOCK, BLOCK, BLOCK },
		 { SPACE, SPACE, BLOCK, POINT, BLOCK, BLOCK, POINT, BLOCK, BLOCK, BLOCK, BLOCK, BLOCK, BLOCK, BLOCK, POINT, BLOCK, BLOCK, POINT, BLOCK, SPACE, SPACE },
		 { SPACE, SPACE, BLOCK, POINT, POINT, POINT, POINT, POINT, POINT, POINT, POINT, POINT, POINT, POINT, POINT, POINT, POINT, POINT, BLOCK, SPACE, SPACE },
		 { SPACE, SPACE, BLOCK, BLOCK, POINT, POINT, POINT, POINT, BLOCK, BLOCK, SPACE, BLOCK, BLOCK, POINT, POINT, POWER, POINT, BLOCK, BLOCK, SPACE, SPACE },
		 { SPACE, SPACE, SPACE, BLOCK, POINT, BLOCK, BLOCK, BLOCK, BLOCK, SPACE, SPACE, SPACE, BLOCK, BLOCK, BLOCK, BLOCK, POINT, BLOCK, SPACE, SPACE, SPACE },
		 { SPACE, SPACE, BLOCK, BLOCK, POINT, POINT, POINT, POINT, BLOCK, BLOCK, SPACE, BLOCK, BLOCK, POINT, POINT, POINT, POINT, BLOCK, BLOCK, SPACE, SPACE },
		 { SPACE, SPACE, BLOCK, POINT, POINT, POINT, POWER, POINT, POINT, POINT, POINT, POINT, POINT, POINT, POINT, POINT, POINT, POINT, BLOCK, SPACE, SPACE },
		 { SPACE, SPACE, BLOCK, POINT, BLOCK, BLOCK, POINT, BLOCK, BLOCK, BLOCK, BLOCK, BLOCK, BLOCK, BLOCK, POINT, BLOCK, BLOCK, POINT, BLOCK, SPACE, SPACE },
		 { BLOCK, BLOCK, BLOCK, POINT, BLOCK, POINT, POINT, POINT, POINT, POINT, BLOCK, POINT, POINT, POINT, POINT, POINT, BLOCK, POINT, BLOCK, BLOCK, BLOCK },
		 { BLOCK, POINT, POINT, POINT, POINT, POINT, POINT, POINT, POINT, POINT, POINT, POINT, POINT, POWER, POINT, POINT, POINT, POINT, POINT, POINT, BLOCK },
		 { BLOCK, POINT, BLOCK, BLOCK, BLOCK, POINT, POINT, BLOCK, BLOCK, BLOCK, BLOCK, BLOCK, BLOCK, BLOCK, POINT, POINT, BLOCK, BLOCK, BLOCK, POINT, BLOCK },
		 { BLOCK, POINT, POWER, POINT, POINT, POINT, POINT, POINT, POINT, POINT, BLOCK, POINT, POINT, POINT, POINT, POINT, POINT, POINT, POINT, POINT, BLOCK },
		 { BLOCK, POINT, POINT, POINT, BLOCK, BLOCK, BLOCK, BLOCK, POINT, POINT, POINT, POINT, POINT, BLOCK, BLOCK, BLOCK, BLOCK, POINT, POINT, POINT, BLOCK },
		 { BLOCK, BLOCK, BLOCK, BLOCK, BLOCK, BLOCK, BLOCK, BLOCK, BLOCK, BLOCK, BLOCK, BLOCK, BLOCK, BLOCK, BLOCK, BLOCK, BLOCK, BLOCK, BLOCK, BLOCK, BLOCK }};

	// Import the PacMan character images from gif files to use in the game.
	// Import the Aliens, blocks and other components as well.
	// The system resource bit is used so that the images still work in a jar file.
	Image PacManA = Toolkit.getDefaultToolkit().getImage(ClassLoader.getSystemResource("PacMana.gif"));
	Image PacManB = Toolkit.getDefaultToolkit().getImage(ClassLoader.getSystemResource("PacManb.gif"));
	Image PacManC = Toolkit.getDefaultToolkit().getImage(ClassLoader.getSystemResource("PacManc.gif"));
	Image BlkA    = Toolkit.getDefaultToolkit().getImage(ClassLoader.getSystemResource("blka.gif"));
	Image PowerU  = Toolkit.getDefaultToolkit().getImage(ClassLoader.getSystemResource("BigDot.gif"));
	Image Point   = Toolkit.getDefaultToolkit().getImage(ClassLoader.getSystemResource("Dot.gif"));
	Image AlienA  = Toolkit.getDefaultToolkit().getImage(ClassLoader.getSystemResource("AlienA.gif"));
	Image AlienB  = Toolkit.getDefaultToolkit().getImage(ClassLoader.getSystemResource("AlienB.gif"));
	Image AlienC  = Toolkit.getDefaultToolkit().getImage(ClassLoader.getSystemResource("AlienC.gif"));

	Dimension offDimension;		// set up off screen graphics to build screen in background
	Image offImage;
	Graphics2D offGraphics;		// cnvert to 2D graphics so that Images from gif files can be used.

	static int key = 0;		// key pressed used to control PacMan movement

	// This uses the Thread class to provide a delay in ms.
	// sleep relinquishes control back to windows to allow other tasks to be performed.
	static void Delay ( int del )
	{
		try
		{
			Thread.sleep(del);
		}
		catch ( InterruptedException e)
		{
		}
	}

	// Variable delay for movements to increase speed during game
	private void moveDelay()
	{
		int del;

		del = ( 9000 - score ) / 100;
		if ( del <= MINDEL ) del = MINDEL;
		Delay ( del );
	}

	// get key
	public void keyPressed(KeyEvent input) {
		key = input.getKeyCode();
	}

	// Capture key inputs, released and typed are not necessary for this game but
	// must
	// be declared for implementing keyListener
	public void keyReleased(KeyEvent input) {
	}

	public void keyTyped(KeyEvent input) {
	}

	// This is the constructor which sets the board for a game
	// clearing out the old score and screen number.
	// The main game loop is run in this constructor.
	PacMan()
	{
		// This sets up a window for a display, the window adaptor is set up for
		// closing the game
		setTitle("PacMan-ish    Use Arrow Keys         Press S to Start");
		// Allow extra space on window for border etc..
		setBounds(5, 20, (MAXX+1)*BLKSIZE+50, (MAXY+1)*BLKSIZE+70);

		addWindowListener(new WindowAdapter ()
		{
			public void windowClosing(WindowEvent e)
			{
				System.exit(0);
			}
		});
		
		addKeyListener(this);

		setVisible(true);		// to avoid odd screen behaviour only show the screen once it is complete.
		setResizable(false);	// disable re-sizing of screen.

		GameEnd();				// Set blank at the start.

		// Infinte loop for game
		for (;;)
		{
			// Wait to start game.
			while ((key != 's') && (key != 'S')) {
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
				}
				// The try and catch are only here in case the program wakes up early.
			}
			newScreen();	// Set up the screen ready to go.

			// Initialise game ready to go.
			score = 0;
			screen = 1;
			Naliens = 3;

			// infinte loop while playing
			for(;;)
			{
				// Move PacMan depending on player input.
				movePlayer();
				// Move the Aliens depending on game logic
				// exit the main loop if an Alien has caught PacMan
				if (moveAliens() == true) { break; }
				// Check if all dots are clear then screen is finished
				if (screenEnd() == true)
				{
					score = score + 1000;
					newScreen();
				}
				// repaint is called four times to allow for animation
				repaint();
				moveDelay();
				repaint();
				moveDelay();
				repaint();
				moveDelay();
				repaint();
				moveDelay();
			}
			// Do the end screen
			GameEnd();
		}
	}

	// This function is to move the Aliens
	public boolean moveAliens()
	{
		int z; // index
		Random rnd = new Random();
		boolean gend = false;
		for (z = 0; z < Naliens; z++)
		{
			// Capture the current Alien position on the screen to support animation between blocks
			alienxold[z] = alienx[z]*BLKSIZE+OFFSETX;
			alienyold[z] = alieny[z]*BLKSIZE+OFFSETY;
			// If the alien is stopped (i.e. hit a wall pick a new random direction.
			if (aliend[z] == 0)
			{
				aliend[z] = (char)(rnd.nextInt(4) + 1);
			}
			// The following logic keeps moving in the same direction until the alien hits a block.
			// If the Alien can no longer move due to a block it's direction is cancelled.
			if ( aliend[z] == UP )
			{
				if ( store [alienx[z]][alieny[z]-1] != BLOCK )
				{
					alieny[z]--;
				}
				else
				{
					aliend[z] = 0;
				}
			}

			if ( aliend[z] == DOWN )
			{
				if ( store [alienx[z]][alieny[z]+1] != BLOCK )
				{
					alieny[z]++;
				}
				else
				{
					aliend[z] = 0;
				}
			}
			if ( aliend[z] == LEFT )
			{
				if (store [alienx[z]-1][alieny[z]] != BLOCK )
				{
					// This is additional logic to use available up directions.
					// This is necessary so that Aliens can go into some side paths.
					if ((store [alienx[z]][alieny[z]-1] != BLOCK)&&(rnd.nextInt(6) > 4))
					{
						aliend[z] = UP;
					}
					else
					{
						alienx[z]--;
					}
				}
				else
				{
					aliend[z] = 0;
				}
			}
			if ( aliend[z] == RIGHT )
			{
				if (store [alienx[z]+1][alieny[z]] != BLOCK )
				{
					// This is additional logic to use available down directions.
					if ((store [alienx[z]][alieny[z]+1] != BLOCK)&&(rnd.nextInt(6) > 4))
					{
						aliend[z] = DOWN;
					}
					else
					{
						alienx[z]++;
					}
				}
				else
				{
					aliend[z] = 0;
				}
			}
			if (store [alienx[z]][alieny[z]] == PACMN)
			{
				gend = true;
			}
		}
		return gend;
	}

	// This function is to count the dots to see if the screen is over.
	// i.e. if there are no dots of any kind left on the screen it is finished.
	public boolean screenEnd()
	{
		int x,y, z; // workin indexes
		z = 0;
		for (x=0; x<=MAXX; x++)
		{
			for (y=0;y<=MAXY;y++)
			{
				if ((store[x][y] == POWER)||(store[x][y] == POINT))
				{
					z++;
				}
			}
		}
		if (z == 0)
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	// This function moves the player, if the player gets a dot or power up add points
	// It also captures the info for animating the player.
	public void movePlayer()
	{
		// capture the current PacMan position to support animation to next position.
		xold = playerx*BLKSIZE+OFFSETX;
		yold = playery*BLKSIZE+OFFSETY;

		//Depending on the key input value for direction, move the player and capture
		// direction information for animation movement.
		// Also update the scores if dots or power-ups detected.
		// Call the Alien restart function for power-ups.
		direction = ' ';
		if (( key == KeyEvent.VK_UP ) && ( store [playerx][playery-1] <= POWER ))
		{
			store[playerx][playery] = SPACE;
			if ( store [playerx][playery-1] == POWER ) { score= score + 100; aliensBack(); }
			if ( store [playerx][playery-1] == POINT ) { score++; }
			direction = UP;
			playery--;
			store[playerx][playery] = PACMN;
		}
		if (( key == KeyEvent.VK_DOWN ) && ( store [playerx][playery+1] <= POWER ))
		{
			store[playerx][playery] = SPACE;
			if ( store [playerx][playery+1] == POWER ) { score= score + 100; aliensBack(); }
			if ( store [playerx][playery+1] == POINT ) { score++; }
			direction = DOWN;
			playery++;
			store[playerx][playery] = PACMN;
		}
		if (( key == KeyEvent.VK_LEFT ) && ( store [playerx-1][playery] <= POWER ))
		{
			store[playerx][playery] = SPACE;
			if ( store [playerx-1][playery] == POWER ) { score= score + 100; aliensBack(); }
			if ( store [playerx-1][playery] == POINT ) { score++; }
			direction = LEFT;
			playerx--;
			store[playerx][playery] = PACMN;
		}
		if (( key == KeyEvent.VK_RIGHT ) && ( store [playerx+1][playery] <= POWER ))
		{
			store[playerx][playery] = SPACE;
			if ( store [playerx+1][playery] == POWER ) { score= score + 100; aliensBack(); }
			if ( store [playerx+1][playery] == POINT ) { score++; }
			direction = RIGHT;
			playerx++;
			store[playerx][playery] = PACMN;
		}
	}

	// The display array is first cleared of blocks.
	// A border is then put round the outside.
	// then depending on how far through the game a differenr screen shape is created.
	public void newScreen()
	{
		int x,y;	// working indexes

		Delay (500);

		screen++;		// show another screen complete
		Naliens++;
		playerx = 2;	// player to start in top left corner
		playery = 1;

		// Aliens back into their starting pen.
		aliensBack();

		for ( x=0;x<=MAXX;x++ )
		{
			for (y=0;y<=MAXY;y++)
			{
				store[x][y] = scr[y][x];
			}
		}

		store[playerx][playery] = PACMN;
	}

	//Aliens back to the starting pen function.
	public void aliensBack()
	{
		for (int z = 0; z < Naliens; z++)
		{
			alienx[z] = 10;
			alieny[z] = 9;
			aliend[z] = 1;
		}
	}

	// Function to do end screen to show that game is over.
	public void GameEnd()
	{
		int x,y;	// working indexes

		//Finish the movement animation
		repaint();
		moveDelay();
		repaint();
		moveDelay();

		// Don't move any further.
		direction = ' ';
		for(int z = 0; z < Naliens; z++)
		{
			aliend[z] = 0;
		}

		// do end screen
		Delay (300);

		for ( x=0;x<=MAXX;x++ )
		{
			for (y=0;y<=MAXY;y++)
			{
				store[x][y] = BLOCK;
			}
			repaint();
			Delay (100);
		}
		Delay (200);
	}

	// This function creates a PacMan game, which then is entirely run in the constructor.
	public static void main ( String[] args )
	{
		new PacMan();
	}

	// This is the function that updates the entire display.  The detail has to be in
	// the update function so that the actual screen output isn't updated until the
	// whole new screen has been drawn. In this way the game appears to move cleanly.
	public void paint(Graphics g)
	{
	    update(g);
	}

	// A none displayed graphics object is created and filled with the new screen.
	// Once complete a command is used to swap it to the actual display. This
	// avoids making any construction blips visible to the player.
	public void	update(Graphics g)
	{
		int x,y,z; // workin indexes
		int xp,yp;
		Dimension d = getSize();

	    // Create the offscreen graphics context, if no good one exists.
	    if (offGraphics == null || d.width != offDimension.width || d.height != offDimension.height) {
	      offDimension = d;
	      offImage = createImage(d.width, d.height);
	      offGraphics = (Graphics2D)offImage.getGraphics();
	    }

		// This fills the entire window with black.
		offGraphics.setColor(Color.black);
	    offGraphics.fillRect(0, 0, d.width, d.height);

		// Increment the count used to sequence animation which is always built of 4 steps.
	    animation++;

		// The entire gameboard is then drawn onto the window
		for ( y=MAXY;y>=0;y-- )
		{
			for ( x=MAXX;x>=0;x-- )
			{
				xp = x*BLKSIZE+OFFSETX;
				yp = y*BLKSIZE+OFFSETY;

				if ( store[x][y] == BLOCK )
				{
					offGraphics.drawImage(BlkA,xp,yp,this);
				}

				if ( store[x][y] == SPACE )
				{
					// nothing to display at the moment
				}

				if ( store[x][y] == POINT )
				{
					offGraphics.drawImage(Point,xp,yp,this);
				}

				if ( store[x][y] == POWER )
				{
					offGraphics.drawImage(PowerU,xp,yp,this);
				}

				// PacMan is continually animated by swapping between 3 different images in 4 steps.
				// This gives smoother movement and the different animation impages bring PacMan to life.
				if ( store[x][y] == PACMN )
				{
					// Move the animation position based on the direction of movement.
					if (direction == UP) //up
					{
						yold = yold-BLKSIZ1;
					}
					if (direction == DOWN) //down
					{
						yold = yold+BLKSIZ1;
					}
					if (direction == LEFT) //left
					{
						xold = xold-BLKSIZ1;
					}
					if (direction == RIGHT) //right
					{
						xold = xold+BLKSIZ1;
					}
					// Animation is done in 4 steps, use the correct image for each step.
					if (animation%4 == 0)
					{
						offGraphics.drawImage(PacManA,xold,yold,this);
					}
					else if (animation%4 == 1)
					{
						offGraphics.drawImage(PacManB,xold,yold,this);
					}
					else if (animation%4 == 2)
					{
						offGraphics.drawImage(PacManC,xold,yold,this);
					}
					else if (animation%4 == 3)
					{
						offGraphics.drawImage(PacManB,xold,yold,this);
					}
				}
			}
		}

		// Once the screen is complete the Aliens are added.
		// The same approach to animation is used moving in 4 steps for smooth movement
		// Animation based on three different images to bring the characters to life.
		// Loop round to display the Aliens
		for (z = 0; z < Naliens; z++)
		{
			// Move the animation position based on the direction of movement.
			if (aliend[z] == UP) //up
			{
				alienyold[z] = alienyold[z]-BLKSIZ1;
			}
			if (aliend[z] == DOWN) //down
			{
				alienyold[z] = alienyold[z]+BLKSIZ1;
			}
			if (aliend[z] == LEFT) //left
			{
				alienxold[z] = alienxold[z]-BLKSIZ1;
			}
			if (aliend[z] == RIGHT) //right
			{
				alienxold[z] = alienxold[z]+BLKSIZ1;
			}
			if (animation%4 == 0)
			{
				offGraphics.drawImage(AlienB,alienxold[z],alienyold[z],this);
			}
			if (animation%4 == 1)
			{
				offGraphics.drawImage(AlienA,alienxold[z],alienyold[z],this);
			}
			if (animation%4 == 2)
			{
				offGraphics.drawImage(AlienC,alienxold[z],alienyold[z],this);
			}
			if (animation%4 == 3)
			{
				offGraphics.drawImage(AlienA,alienxold[z],alienyold[z],this);
			}
		}

		offGraphics.setColor(Color.white);
		offGraphics.drawString ( "Score " + score + "      ", 50,(MAXY+1)*BLKSIZE+50 );
		offGraphics.drawString ( "Screen " + screen, 150,(MAXY+1)*BLKSIZE+50 );

		// swap screens now that screen is complete
		g.drawImage(offImage, 0, 0, this);
	}
}

