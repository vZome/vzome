package com.vzome.core.math.symmetry;

/**
 * @deprecated
 * @author Scott Vorthmann
 *
 */
public interface Constants{

	int RED = 0, YELLOW = 1, BLUE = 2, GREEN = 3, ORANGE = 4, PURPLE = 5, BLACK = 6, NO_AXIS = 7;

	/**
	 * Blue axes of basis.
	 */
	int X = 2, Y = 5, Z = 13;
	
	int SHORT = 3, MEDIUM = 4, LONG = 5;

	int JUST_RED = 1 << RED;

	int JUST_YELLOW = 1 << YELLOW;

	int JUST_BLUE = 1 << BLUE;

    int JUST_GREEN = 1 << GREEN;

    int JUST_ORANGE = 1 << ORANGE;

	int JUST_PURPLE = 1 << PURPLE;
	
	int JUST_BLACK = 1 << BLACK;

    int ORIGINAL_STRUTS = JUST_RED | JUST_YELLOW | JUST_BLUE;

    int ALL_STRUTS = ORIGINAL_STRUTS | JUST_GREEN | JUST_ORANGE | JUST_PURPLE | JUST_BLACK;


    int[][] RED_AXIS_YELLOW_NEIGHBORS = new int [] []{ { 0, 1, 2, 3, 4 },
                                                      { 1, 0, 5, -8, 6 },
                                                      { 2, 1, 6, -9, 7 },
                                                      { 3, 2, 7, -5, 8 },
                                                      { 4, 3, 8, -6, 9 },
                                                      { 0, 4, 9, -7, 5 } };

    int[] AXIS_SYMMETRY = new int[] { 5, 3, 2, 1, 1, 1, 1 };
}


