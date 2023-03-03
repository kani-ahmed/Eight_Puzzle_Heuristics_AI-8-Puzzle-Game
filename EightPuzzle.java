import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class EightPuzzle {
    private static final int[][] GOAL_STATE = {{0, 1, 2}, {3, 4, 5}, {6, 7, 8}};
    private int[][] currentState;
    private int iterations;
    private int expandedNodes;
    private int frontierNodes;

    public EightPuzzle() {
        // Constructor for initializing the puzzle with the goal state
        currentState = new int[3][3];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                currentState[i][j] = GOAL_STATE[i][j];
            }
        }
    }

    public static class Node implements Comparable<Node>{
        private int[][] state;
        private int depth;
        private Node parent;
        private int heuristic;
        private int cost;
        private int totalCost;


        public Node(int[][] state, int depth, Node parent, int heuristic, int cost) {
            this.state = state;
            this.depth = depth;
            this.parent = parent;
            this.heuristic = heuristic;
            this.cost = cost;
            this.totalCost = depth + heuristic;
        }

        public int[][] getState() {
            return state;
        }

        public Node getParent() {
            return parent;
        }

        public int getDepth() {
            return depth;
        }

        public int getHeuristic() {
            return heuristic;
        }

        public void setHeuristic(int heuristic) {
            this.heuristic = heuristic;
        }

        public int getCost() {
            return cost;
        }

        public int getTotalCost() {
            return totalCost;
        }

        public Node setTotalCost(int cost) {
            this.totalCost = cost;
            return this;
        }

        @Override
        public int compareTo(Node other) {
            return Integer.compare(totalCost, other.getTotalCost());
        }
    }


    public void setState(String stateStr) {
        // Method for setting the puzzle state based on a string input
        // Example input: "1b5234687"
        // Example output: {{1, 0, 5}, {2, 3, 4}, {6, 8, 7}}
        int row = 0, col = 0;
        for (int i = 0; i < stateStr.length(); i++) {
            char c = stateStr.charAt(i);
            if (c == 'b') {
                currentState[row][col] = 0;
            } else {
                int num = Character.getNumericValue(c);
                currentState[row][col] = num;
            }
            col++;
            if (col == 3) {
                col = 0;
                row++;
            }
        }
    }

    public void printState() {
        // Method for printing the current puzzle state
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                System.out.print(currentState[i][j] + " ");
            }
            System.out.println();
        }
    }

    private void printSolution(Node node) {
        //Helper method for printing the solution path
        Stack<Node> stack = new Stack<>();
        while (node != null) {
            stack.push(node);
            node = node.getParent();
        }
        //System.out.println(stack.size() - 1);
        while (!stack.isEmpty()) {
            Node current = stack.pop();
            if (current.getParent() != null) {
                int[] emptySpace = findEmptySpace(current.getState());
                int[] parentEmptySpace = findEmptySpace(current.getParent().getState());
                if (emptySpace[0] < parentEmptySpace[0]) {
                    System.out.println("up");
                } else if (emptySpace[0] > parentEmptySpace[0]) {
                    System.out.println("down");
                } else if (emptySpace[1] < parentEmptySpace[1]) {
                    System.out.println("left");
                } else {
                    System.out.println("right");
                }
            }
            printState(current.getState());
            //System.out.println();
        }
    }

    private String nodeToString(Node node) {
        // Helper method for converting a node to a string
        StringBuilder sb = new StringBuilder();
        int[][] state = node.getState();
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                sb.append(state[i][j]);
            }
        }
        return sb.toString();
    }

    private List<Node> getNeighbors(Node node) {
        // Helper method for getting the neighbor nodes of a given node
        List<Node> neighbors = new ArrayList<>();
        int[][] currentState = node.getState();
        int[] emptySpace = findEmptySpace(currentState);
        if (emptySpace[0] > 0) {
            int[][] newState = cloneState(node.getState());
            int temp = newState[emptySpace[0] - 1][emptySpace[1]];
            newState[emptySpace[0] - 1][emptySpace[1]] = 0;
            newState[emptySpace[0]][emptySpace[1]] = temp;
            Node neighbor = new Node(newState, node.getDepth() + 1, node, 0, 0);
            neighbors.add(neighbor);
        }
        if (emptySpace[0] < 2) {
            int[][] newState = cloneState(node.getState());
            int temp = newState[emptySpace[0] + 1][emptySpace[1]];
            newState[emptySpace[0] + 1][emptySpace[1]] = 0;
            newState[emptySpace[0]][emptySpace[1]] = temp;
            Node neighbor = new Node(newState, node.getDepth() + 1, node, 0, 0);
            neighbors.add(neighbor);
        }
        if (emptySpace[1] > 0) {
            int[][] newState = cloneState(node.getState());
            int temp = newState[emptySpace[0]][emptySpace[1] - 1];
            newState[emptySpace[0]][emptySpace[1] - 1] = 0;
            newState[emptySpace[0]][emptySpace[1]] = temp;
            Node neighbor = new Node(newState, node.getDepth() + 1, node, 0, 0);
            neighbors.add(neighbor);
        }
        if (emptySpace[1] < 2) {
            int[][] newState = cloneState(node.getState());
            int temp = newState[emptySpace[0]][emptySpace[1] + 1];
            newState[emptySpace[0]][emptySpace[1] + 1] = 0;
            newState[emptySpace[0]][emptySpace[1]] = temp;
            Node neighbor = new Node(newState, node.getDepth() + 1, node, 0, 0);
            neighbors.add(neighbor);
        }
        return neighbors;
    }

    private static int[] findEmptySpace(int[][] state) {
        // Helper method for finding the row and column of the empty space in a given state
        int[] emptySpace = new int[2];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (state[i][j] == 0) {
                    emptySpace[0] = i;
                    emptySpace[1] = j;
                }
            }
        }
        return emptySpace;
    }

    public static void move(Node node, String direction) {
        // Method for moving the empty space in a particular direction
        int[][] currentState = node.getState();
        int[] emptySpace = findEmptySpace(currentState);
        if (direction.equals("up") && emptySpace[0] > 0) {
            int temp = currentState[emptySpace[0] - 1][emptySpace[1]];
            currentState[emptySpace[0] - 1][emptySpace[1]] = 0;
            currentState[emptySpace[0]][emptySpace[1]] = temp;
        } else if (direction.equals("down") && emptySpace[0] < 2) {
            int temp = currentState[emptySpace[0] + 1][emptySpace[1]];
            currentState[emptySpace[0] + 1][emptySpace[1]] = 0;
            currentState[emptySpace[0]][emptySpace[1]] = temp;
        } else if (direction.equals("left") && emptySpace[1] > 0) {
            int temp = currentState[emptySpace[0]][emptySpace[1] - 1];
            currentState[emptySpace[0]][emptySpace[1] - 1] = 0;
            currentState[emptySpace[0]][emptySpace[1]] = temp;
        } else if (direction.equals("right") && emptySpace[1] < 2) {
            int temp = currentState[emptySpace[0]][emptySpace[1] + 1];
            currentState[emptySpace[0]][emptySpace[1] + 1] = 0;
            currentState[emptySpace[0]][emptySpace[1]] = temp;
        }
    }

    public void randomizeState(int n){
        // Method for randomizing the puzzle state by making n random moves from the goal state
        Random rand = new Random();
        for (int i = 0; i < n; i++) {
            int dir = rand.nextInt(4);
            if (dir == 0) {
                move(new Node(currentState, 0, null, 0, 0), "up");
            } else if (dir == 1) {
                move(new Node(currentState, 0, null, 0, 0), "down");
            } else if (dir == 2) {
                move(new Node(currentState, 0, null, 0, 0), "left");
            } else {
                move(new Node(currentState, 0, null, 0, 0), "right");
            }
        }
    }


    public int solveAStar(String heuristic, int maxNodes) {
        // Method for solving the puzzle using A* search with a specified heuristic function
        PriorityQueue<Node> frontier = new PriorityQueue<>();
        HashSet<String> visited = new HashSet<>();
        Node root = new Node(currentState, 0, null, 0, 0);
        frontier.add(root);
        int nodesExplored = 0;

        while (!frontier.isEmpty() && nodesExplored < maxNodes) {
            Node currentNode = frontier.poll();
            nodesExplored++;

            if (Arrays.deepEquals(currentNode.getState(), GOAL_STATE)) {
                printSolution(currentNode);
                System.out.println("Depth or Tile Moves : " + currentNode.getDepth());
                System.out.println("Iteration: " + nodesExplored);
                System.out.println("Expanded nodes: " + nodesExplored + " / " + (nodesExplored + frontier.size()));
                System.out.println("Frontier nodes: " + frontier.size() + " / " + (nodesExplored + frontier.size()));
                return currentNode.getDepth();
            }

            visited.add(nodeToString(currentNode));
            List<Node> neighbors = getNeighbors(currentNode);

            for (Node neighbor : neighbors) {
                if (!visited.contains(nodeToString(neighbor))) {
                    if (heuristic.equals("h1")) {
                        neighbor.setHeuristic(calculateMisplacedTiles(neighbor.getState()));
                    } else if (heuristic.equals("h2")) {
                        neighbor.setHeuristic(calculateManhattanDistance(neighbor.getState()));
                    }

                    // Calculate the total cost of the node
                    int totalCost = neighbor.getDepth() + neighbor.getHeuristic();

                    // Add the node to the frontier
                    frontier.add(neighbor.setTotalCost(totalCost));
                }
            }
        }
        //if you get here, that means we couldn't find any solution
        System.out.println("No solution found in the given node limit");
        return -1;
    }

    private int calculateMisplacedTiles(int[][] state) {
        // Helper method for calculating the number of misplaced tiles heuristic
        int count = 0;

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (state[i][j] != GOAL_STATE[i][j]) {
                    count++;
                }
            }
        }

        return count;
    }

    private int calculateManhattanDistance(int[][] state) {
        // Helper method for calculating the sum of Manhattan distances heuristic
        int distance = 0;

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (state[i][j] != 0) {
                    int[] goalPosition = findPosition(GOAL_STATE, state[i][j]);
                    distance += Math.abs(goalPosition[0] - i) + Math.abs(goalPosition[1] - j);
                }
            }
        }

        return distance;
    }

    private void printState(int[][] state) {
        // Helper method for printing a puzzle state with depth information
        System.out.println("-------------");
        for (int i = 0; i < 3; i++) {
            System.out.print("| ");
            for (int j = 0; j < 3; j++) {
                if (state[i][j] == 0) {
                    System.out.print("b | ");
                } else {
                    System.out.print(state[i][j] + " | ");
                }
            }
            System.out.println();
            System.out.println("-------------");
        }
    }

    private int[][] cloneState ( int[][] state){
            // Helper method for cloning a 2D array
            int[][] newState = new int[3][3];
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    newState[i][j] = state[i][j];
                }
            }
            return newState;
        }

        private int[] findPosition (int[][] state, int num){
            // Helper method for finding the row and column of a given number in a state
            int[] position = new int[2];
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    if (state[i][j] == num) {
                        position[0] = i;
                        position[1] = j;
                    }
                }
            }
            return position;
        }

    private Node localBeamSearch(int k, int maxNodes) {
        PriorityQueue<Node> frontier = new PriorityQueue<>();
        Random rand = new Random();
        HashSet<String> visited = new HashSet<>();

        Node[] currentNodes = new Node[k];
        Node root = new Node(currentState, 0, null, 0, 0);
        for (int i = 0; i < k; i++) {
            currentNodes[i] = new Node(currentState, 0, null, 0, 0);
            currentNodes[i].setHeuristic(calculateHeuristic(currentNodes[i]));
            frontier.add(currentNodes[i]);
        }

        int nodesExplored = 0;
        while (!frontier.isEmpty() && nodesExplored < maxNodes) {
            Node currentNode = frontier.poll();
            nodesExplored++;
            if (Arrays.deepEquals(currentNode.getState(), GOAL_STATE)) {
                System.out.println("Depth: " + currentNode.getDepth());
                System.out.println("Iteration: " + nodesExplored);
                System.out.println("Total nodes generated: " + (nodesExplored + frontier.size()));
                System.out.println("Expanded nodes: " + nodesExplored + " / " + (nodesExplored + frontier.size()));
                System.out.println("Frontier nodes: " + frontier.size() + " / " + (nodesExplored + frontier.size()));
                return currentNode;
            }
            visited.add(nodeToString(currentNode));
            List<Node> neighbors = getNeighbors(currentNode);
            List<Node> successors = new ArrayList<>();
            for (Node neighbor : neighbors) {
                if (!visited.contains(nodeToString(neighbor))) {
                    neighbor.setHeuristic(calculateHeuristic(neighbor));
                    successors.add(neighbor);
                }
            }

            Collections.sort(successors);
            for (int i = 0; i < k && i < successors.size(); i++) {
                currentNodes[i] = successors.get(i);
                frontier.add(currentNodes[i]);
            }

            for (int i = k; i < currentNodes.length; i++) {
                int randIndex = rand.nextInt(frontier.size());
                Node randomNode = (Node) frontier.toArray()[randIndex];
                frontier.remove(randomNode);
                currentNodes[i] = randomNode;
            }
        }

        return null;
    }
    public int solveLocalBeamSearch(int k, int maxNodes) {
        Node goalNode = localBeamSearch(k, maxNodes);
        if (goalNode == null) {
            System.out.println("Failed to find a solution within the specified number of nodes.");
        } else {
            printSolution(goalNode);
        }
        return goalNode.getDepth();
    }

    private int calculateHeuristic(Node node) {
        return calculateMisplacedTiles(node.getState()) + calculateManhattanDistance(node.getState());
    }
    public void runTests(String algorithm, String heuristic, int k, int numPuzzles) {
        int[][][] puzzles = new int[numPuzzles][3][3];
        int[] h1Results = new int[numPuzzles];
        int[] h2Results = new int[numPuzzles];
        int[] beamResults = new int[numPuzzles];

        for (int i = 0; i < numPuzzles; i++) {
            // Generate a random initial state
            randomizeState(100);
            puzzles[i] = cloneState(currentState);

            // Run the search algorithm
            int result;
            if (algorithm.equals("A-star")) {
                if (heuristic.equals("h1")) {
                    result = solveAStar("h1", Integer.MAX_VALUE);
                    h1Results[i] = result;
                } else if (heuristic.equals("h2")) {
                    result = solveAStar("h2", Integer.MAX_VALUE);
                    h2Results[i] = result;
                }
            } else if (algorithm.equals("beam")) {
                result = solveLocalBeamSearch(k, Integer.MAX_VALUE);
                beamResults[i] = result;
            }
        }

        // Print the results in a table
        System.out.println("Algorithm: " + algorithm + ", Heuristic: " + heuristic + ", k: " + k);
        System.out.println("Puzzle\t h1\t h2\t beam");
        for (int i = 0; i < numPuzzles; i++) {
            System.out.println(i + "\t" + h1Results[i] + "\t" + h2Results[i] + "\t" + beamResults[i]);
        }
    }


    //-----------------------------------------------------------------------------------------------------------------------
    public static void main(String[] args) throws FileNotFoundException {

        EightPuzzle puzzle = new EightPuzzle();
        puzzle.runTests("A-star", "h1", 0, 100); // A* search with h1 heuristic
        puzzle.runTests("A-star", "h2", 0, 100); // A* search with h2 heuristic
        puzzle.runTests("beam", "", 2, 100);     // Beam search with k=2
        puzzle.runTests("beam", "", 20, 100);     // Beam search with k=20

        File file = new File("/Users/kani/Desktop/8.txt");
        Scanner scanner = new Scanner(file);
        int maxNodes = Integer.MAX_VALUE;

        while (scanner.hasNext()) {
            String command = scanner.next();
            switch (command) {
                case "setState":
                    setStateCommand(puzzle, scanner.next());
                    break;
                case "printState":
                    printStateCommand(puzzle);
                    break;
                case "move":
                    moveCommand(puzzle, scanner.next());
                    break;
                case "randomizeState":
                    randomizeStateCommand(puzzle, scanner.nextInt());
                    break;
                case "solve":
                    solveCommand(puzzle, scanner, maxNodes);
                    break;
                case "maxNodes":
                    maxNodes = scanner.nextInt();
                    break;
                default:
                    System.out.println("Invalid command.");
                    break;
            }
        }
    }
    //-----------------------------------------------------------------------------------------
    private static void setStateCommand(EightPuzzle puzzle, String state) {
        puzzle.setState(state);
    }

    private static void printStateCommand(EightPuzzle puzzle) {
        puzzle.printState();
        System.out.println();
    }

    private static void moveCommand(EightPuzzle puzzle, String direction) {
        Node moveNode = new Node(puzzle.currentState, 0, null, 0, 0);
        puzzle.move(moveNode, direction);
    }

    private static void randomizeStateCommand(EightPuzzle puzzle, int moves) {
        puzzle.randomizeState(moves);
    }

    private static void solveCommand(EightPuzzle puzzle, Scanner scanner, int maxNodes) {
        String algorithm = scanner.next();
        switch (algorithm) {
            case "A-star":
                solveAStarCommand(puzzle, scanner, maxNodes);
                break;
            case "beam":
                solveLocalBeamSearchCommand(puzzle, scanner, maxNodes);
                break;
            default:
                System.out.println("Invalid algorithm.");
                break;
        }
    }


    private static void solveAStarCommand(EightPuzzle puzzle, Scanner scanner, int maxNodes) {
        String heuristic = scanner.next();
        switch (heuristic) {
            case "h1":
                puzzle.solveAStar("misplacedTiles", maxNodes);
                break;
            case "h2":
                puzzle.solveAStar("manhattanDistance", maxNodes);
                break;
            default:
                System.out.println("Invalid heuristic.");
                break;
        }
    }

    private static void solveLocalBeamSearchCommand(EightPuzzle puzzle, Scanner scanner, int maxNodes) {
        int k = scanner.nextInt();
        puzzle.solveLocalBeamSearch(k, maxNodes);
    }
}

