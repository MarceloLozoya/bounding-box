import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.*;

class BoundingBox {

	private static Boolean[][] grid;


    public static void main(String[] args) throws Exception {
		int exitCode = 1;

		List<String> inputLines = getFileLines();

		if (inputLines.size() > 0) {
			build2dGrid(inputLines);
			exitCode = processGrid();
		}

		System.exit(exitCode);
    }





	/**
	 * Method to process the grid that was built in the build2dArray() method
	 * @return int - The intended exit code for the program
	 */
	private static int processGrid() {
		int maxY = grid.length;
		int maxX = grid[0].length;

		Set<Box> boundingBoxes = new HashSet<>();

		for (int i = 0; i < maxY; i++) {
			for (int j = 0; j < maxX; j++) {
				if (grid[i][j]) {
					List<Point> linePoints = findContinuousLineForPoint(new Point(j, i));
					buildBoundingBox(linePoints, boundingBoxes);
				}
			}
		}

		List<Box> standaloneBoxes = getNonOverlappingBoxes(boundingBoxes);

		return printBoxesWithMaxArea(standaloneBoxes);
	}


	/**
	 * Method to print the boxes with the max area to stdout, and sets the intended exit code for the program
	 * @param standaloneBoxes - List of non-overlapping boxes
	 * @return int - The intended exit code for the program
	 */
	private static int printBoxesWithMaxArea(List<Box> standaloneBoxes) {
		int exitCode = 1;

		int maxStandaloneBoxArea = standaloneBoxes.stream().max(Comparator.comparing(Box::getArea)).map(Box::getArea).orElse(-1);

		for (Box box : standaloneBoxes) {
			if (box.getArea() == maxStandaloneBoxArea) {
				System.out.println(box);
				exitCode = 0;
			}
		}

		return exitCode;
	}


	/**
	 * Method that finds boxes in a box set that do not overlap
	 * @param boundingBoxesSet - unique set of bounding boxes
	 * @return - list of non-overlapping boxes
	 */
	private static List<Box> getNonOverlappingBoxes(Set<Box> boundingBoxesSet) {
		List<Box> standaloneBoxes = new ArrayList<>();
		List<Box> boundingBoxes = new ArrayList<>(boundingBoxesSet);

		for (Box b1 : boundingBoxes) {
			boolean overlappingBoxFound = false;

			for (Box b2 : boundingBoxes) {

				if (!b1.equals(b2) && doBoxesOverlap(b1, b2)) {
					overlappingBoxFound = true;
					break;
				}

			}

			if (!overlappingBoxFound) standaloneBoxes.add(b1);
		}

		return standaloneBoxes;
	}


	/**
	 * Method to check if two boxes overlap
	 * @param b1 - box to check
	 * @param b2 - box to check against b1
	 * @return boolean - whether or not the boxes overlap
	 */
	private static boolean doBoxesOverlap(Box b1, Box b2) {
    	boolean boxesOutsideHorizontalRange = b1.getMinX() > b2.getMaxX() || b2.getMinX() > b1.getMaxX();
    	boolean boxesOutsideVerticalRange = b1.getMinY() > b2.getMaxY() || b2.getMinY() > b1.getMaxY();

		return !(boxesOutsideVerticalRange || boxesOutsideHorizontalRange);
	}


	/**
	 * Method that finds a continuous line of points for a given starting point
	 * @param point - starting point of the line to find
	 * @return List - all points in the line
	 */
	private static List<Point> findContinuousLineForPoint(Point point) {
		List<Point> linePoints = new ArrayList<>();
		traverseFromPoint(point, linePoints);
		return linePoints;
	}


	/**
	 * Method that recursively finds all the points in continuous line
	 * @param point - an 'asterisk' point that will be used for the traversal
	 * @param linePoints - list of 'asterisk' points that were found for the current line
	 */
	private static void traverseFromPoint(Point point, List<Point> linePoints) {
		linePoints.add(point);

		Point up = new Point(point.getX(), point.getY() - 1);
		if ((up.getY() >= 0) && grid[up.getY()][up.getX()] && !linePoints.contains(up)) {
			traverseFromPoint(up, linePoints);
		}

		Point right = new Point(point.getX() + 1, point.getY());
		if ((right.getX() < grid[0].length) && grid[right.getY()][right.getX()] && !linePoints.contains(right)) {
			traverseFromPoint(right, linePoints);
		}

		Point down = new Point(point.getX(), point.getY() + 1);
		if ((down.getY() < grid.length) && grid[down.getY()][down.getX()] && !linePoints.contains(down)) {
			traverseFromPoint(down, linePoints);
		}

		Point left = new Point(point.getX() - 1, point.getY());
		if ((left.getX() >= 0) && grid[left.getY()][left.getX()] && !linePoints.contains(left)) {
			traverseFromPoint(left, linePoints);
		}
	}


	/**
	 * Method that builds the bounding box object given the points in a continuous line, then adds it to a set (set is used to discard duplicate boxes)
	 * @param linePoints - list of points for a continuous line
	 * @param boundingBoxes - set of bounding box to be added to
	 */
	private static void buildBoundingBox(List<Point> linePoints, Set<Box> boundingBoxes) {
		int minY = linePoints.stream().min(Comparator.comparing(Point::getY)).get().getY();
		int maxY = linePoints.stream().max(Comparator.comparing(Point::getY)).get().getY();
		int minX = linePoints.stream().min(Comparator.comparing(Point::getX)).get().getX();
		int maxX = linePoints.stream().max(Comparator.comparing(Point::getX)).get().getX();
    	boundingBoxes.add(new Box(minX, maxX, minY, maxY));
	}

	/**
	 * Method that builds a 2D boolean array given a list of lines from the file input (value of 'true' signifies an asterisk)
	 * @param inputLines - list of Strings from the input file. Each String is a line in the file.
	 */
    private static void build2dGrid(List<String> inputLines) {
		grid = new Boolean[inputLines.size()][inputLines.stream().findFirst().get().length()];
		for (int i = 0; i < inputLines.size(); i++) {
			char[] charArrayLine = inputLines.get(i).toCharArray();
			for (int j = 0; j < charArrayLine.length; j++) {
				grid[i][j] = (charArrayLine[j] == '*');
			}
		}
	}


	/**
	 * Method that grabs the input from stdin and converts it to a list of String
	 * @return - list of Strings
	 * @throws Exception - thrown when there are no lines to process
	 */
	private static List<String> getFileLines() throws Exception {
    	List<String> lineList = new ArrayList<>();
		BufferedReader input = new BufferedReader(new InputStreamReader(System.in));

		String line = null;
		while(Objects.nonNull(line = input.readLine()) && !line.trim().equals("")) {
			lineList.add(line);
		}

		return lineList;
	}








	/***********************************************
	 *
	 * 				SUPPORTING CLASSES
	 *
	 **********************************************/



	/**
	 * Class that signifies a point on the grid
	 */
	private static class Point {
    	private int x;
    	private int y;

    	Point(int x, int y) {
    		this.x = x;
    		this.y = y;
		}

		public int getX() {
			return x;
		}

		public void setX(int x) {
			this.x = x;
		}

		public int getY() {
			return y;
		}

		public void setY(int y) {
			this.y = y;
		}

		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof Point)) return false;
			Point other = (Point) obj;
			return Objects.equals(this.x, other.x) && Objects.equals(this.y, other.y);
		}

		@Override
		public String toString() {
			return "Point{" + "x=" + x + ", y=" + y + '}';
		}
	}


	/**
	 * Class that signifies a bounding box
	 */
	private static class Box {
    	private int minX;
    	private int maxX;
    	private int minY;
    	private int maxY;

    	Box(int minX, int maxX, int minY, int maxY) {
    		this.minX = minX;
    		this.maxX = maxX;
    		this.minY = minY;
    		this.maxY = maxY;
		}

		public int getArea() {
    		int xLength = maxX - minX;
    		int yLength = maxY - minY;
    		return xLength * yLength;
		}

		public int getMinX() {
			return minX;
		}

		public void setMinX(int minX) {
			this.minX = minX;
		}

		public int getMaxX() {
			return maxX;
		}

		public void setMaxX(int maxX) {
			this.maxX = maxX;
		}

		public int getMinY() {
			return minY;
		}

		public void setMinY(int minY) {
			this.minY = minY;
		}

		public int getMaxY() {
			return maxY;
		}

		public void setMaxY(int maxY) {
			this.maxY = maxY;
		}

		@Override
		public boolean equals(Object obj) {
    		if (this == obj) return true;
			if (!(obj instanceof Box)) return false;
			Box other = (Box) obj;
			return Objects.equals(this.minX, other.minX) && Objects.equals(this.maxX, other.maxX) && Objects.equals(this.minY, other.minY) && Objects.equals(this.maxY, other.maxY);
		}

		@Override
		public int hashCode() {
			return Objects.hash(minX, maxX, minY, maxY);
		}

		@Override
		public String toString() {
			return "(" + (minX+1) + "," + (minY+1) + ")(" + (maxX+1) + "," + (maxY+1) + ")";
		}
	}

}





/**

 ----- PROBLEM STATEMENT -----

 Write a program that takes input from stdin with the following properties:
 - Input is split into lines delimited by newline characters.
 - Every line has the same length.
 - Every line consists of an arbitrary sequence of hyphens ("-") and asterisks ("\*").
 - The final line of input is terminated by a newline character.

 In this challenge, each character in the input will have coordinates defined by `(line number, character number)`, starting at the top and left. So the first character on the first line will have the coordinates `(1,1)` and the fifth character on line 3 will have the coordinates `(3,5)`.

 The program should find a box (or boxes) in the input with the following properties:
 - The box must be defined by two pairs of coordinates corresponding to its top left and bottom right corners.
 - It must be the **minimum bounding box** for some contiguous group of asterisks, with each asterisk being horizontally or vertically (but not diagonally) adjacent to at least one other asterisk in the group. The box should not _strictly_ bound the group, so the coordinates for the box in the following input should be `(2,2)(3,3)` not `(1,1)(4,4)`
 ```
 ----
 -**-
 -**-
 ----
 ```
 - It should not overlap (i.e. share any characters with) any other minimum bounding boxes.
 - Of all the non-overlapping, minimum bounding boxes in the input, _it should be the largest_.

 If any boxes satisfying the conditions can be found in the input, the program should return an exit code of 0 and, for each box, print a line to stdout with the two pairs of coordinates.

 So, given the file “groups.txt” with the following content:
 ```
 **-------***
 -*--**--***-
 -----***--**
 -------***--
 ```

 Running your program with this input would look something like this:
 ```
 > cat groups.txt | bounding-box
 (1,1)(2,2)
 ```

 This is because the larger groups on the right of the input have overlapping bounding boxes, so the returned coordinates bound the smaller group on the top left.

 **/
