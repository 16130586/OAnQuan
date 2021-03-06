package game.lib.ai;

import java.util.ArrayList;
import java.util.List;

import game.model.GameBoard;
import game.model.Player;

public class AlphaBetaPruningComputer implements AutoSearching {
	// i define alpha is maximizing player that is computer, another is player

	@Override
	public ComputerDecisionResult doSearch(GameBoard gb, Player player1, Player player2, int maxDepth) {
		return doAlphaBetaPrunning(gb, player1, player2, maxDepth);
	}

	private ComputerDecisionResult doAlphaBetaPrunning(GameBoard gb, Player player1, Player player2, int maxDepth) {
		int alpha = Integer.MIN_VALUE; // maximizing
		int beta = Integer.MAX_VALUE; //

		Node root = new Node(gb, player1, player2);

		alpha = findMaxValue(root, alpha, beta, 0, maxDepth);
		
		List<Node> ap = new ArrayList<>();
		for (Node child : root.successors()) {
			if (child == null)
				continue;
			if (child.h == alpha) {
				ap.add(child);
				return Node.getPath(child);
			}
		}
		return null;
	}

	private int findMinValue(Node cur, int alpha, int beta, int curDepth, int maxDepth) {
		if (beta <= alpha)
			return Integer.MAX_VALUE;
		if (testTerminatedNode(cur, curDepth, maxDepth))
			return cur.h;

		for (Node child : cur.successors()) {
			if (child == null)
				continue;
			child.changePlayer();
			beta = Math.min(beta, findMaxValue(child, alpha, beta, curDepth + 1, maxDepth));
		}
		cur.h = beta;
		return beta;
	}

	private int findMaxValue(Node cur, int alpha, int beta, int curDepth, int maxDepth) {
		if (beta <= alpha)
			return Integer.MIN_VALUE;
		if (testTerminatedNode(cur, curDepth, maxDepth))
			return cur.h;

		for (Node child : cur.successors()) {
			if (child == null)
				continue;
			child.changePlayer();
			alpha = Math.max(alpha, findMinValue(child, alpha, beta, curDepth + 1, maxDepth));
		}
		cur.h = alpha;
		return alpha;
	}

	private boolean testTerminatedNode(Node child, int curDepth, int maxDepth) {
		if (curDepth == maxDepth)
			return true;
		return child.gameBoard.isEndGame();
	}

}
