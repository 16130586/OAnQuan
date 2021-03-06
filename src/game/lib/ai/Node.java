package game.lib.ai;

import java.util.Iterator;

import game.model.Direction;
import game.model.GameBoard;
import game.model.GameSquare;
import game.model.Player;

public class Node {
	public Node parent;
	public GameBoard gameBoard;
	public Player p1, p2, currentPlayer;
	public int index;
	public Direction d;
	public int h;
	private boolean isMaximizing;
	private Node[] successors;

	public Node(Node parent, GameBoard gameBoard, Player p1, Player p2) {
		this.gameBoard = gameBoard;
		this.p1 = p1.cpy();
		this.p2 = p2.cpy();
		this.currentPlayer = this.p1;
		this.parent = parent;

	}

	public Node(GameBoard gameBoard, Player p1, Player p2) {
		this.gameBoard = gameBoard;
		this.p1 = p1.cpy();
		this.currentPlayer = this.p1;
		this.p2 = p2.cpy();
	}

	public Node(Node cur, Player curPlayer, Player other) {
		this.currentPlayer = curPlayer.cpy();
		this.p1 = currentPlayer;
		this.p2 = other.cpy();
		this.gameBoard = cur.gameBoard.cpy();
		this.h = cur.h;
		this.d = cur.d;
		this.index = cur.index;
		this.parent = cur.parent;
	}

	public void evulation() {
		Player computer = (p1.isComputer() ? p1 : p2);
		Player human = (p1.isComputer() ? p2 : p1);

		// h = (the greater of player's score and computer's score then plus number of
		// this empty square that is created by current player)
		// System.out.println(
		// "in evulation index: " + index + " comput " + computer.miltaries + " human "
		// + human.miltaries);
		h = (computer.miltaries) - (human.miltaries);
	}

	public Node[] successors() {
		if (successors == null)
			successors = new Node[5 * 2]; // always return maxmima work case
		else
			return successors;
		if (!gameBoard.stillHasOnBoardMilitary(currentPlayer)) {
			if (currentPlayer.miltaries > 5) {
				gameBoard.outMilitaries(currentPlayer);
			}
		}
		if (currentPlayer.equals(Player.PLAYER_1)) {
			for (int i = 7, sIndex = 0; i < 12; i++) {
				// System.out.println("on pl1 move left and right");
				successors[sIndex++] = moveSquare(this, gameBoard.cpy(), i, Direction.LEFT);
				successors[sIndex++] = moveSquare(this, gameBoard.cpy(), i, Direction.RIGHT);
			}
		}
		if (currentPlayer.equals(Player.PLAYER_2)) {
			for (int i = 1, sIndex = 0; i < 6; i++) {
				// System.out.println("on pl2 move left and right");
				successors[sIndex++] = moveSquare(this, gameBoard.cpy(), i, Direction.LEFT);
				successors[sIndex++] = moveSquare(this, gameBoard.cpy(), i, Direction.RIGHT);
			}
		}
		return successors;
	}

	private Node moveSquare(Node parent, GameBoard gb, int i, Direction moveDirection) {
		if (gb.getMilitaryAt(i) == 0) {
			return null;
		}
		Node res = new Node(parent, gb, p1, p2);
		res.index = i;
		res.d = moveDirection;
		Node.move(res.index, res.d, res.currentPlayer, res.gameBoard);
		res.evulation();
		// System.out.println("on moving " + res.p1 + " | " + res.p2 + " index: " +
		// res.index + " dir: " + res.d.name() + " evu " + res.h);
		return res;
	}

	public static void move(int index, Direction moveDirection, Player curPlayer, GameBoard gameBoard) {
		final int BOSS_2 = 6, BOSS_1 = 0;
		if (index == BOSS_1 || index == BOSS_2)
			return;
		gameBoard.setLoopDirection(moveDirection);
		gameBoard.setIndexLoop(index);

		int mitalries = gameBoard.getAndRemoveMilitaryAt(index);
		if (mitalries == 0)
			return;
		Iterator<GameSquare> squares = gameBoard.iterator();
		while (mitalries-- > 0) {
			GameSquare cur = squares.next();
			cur.setMiltaries(cur.getMilitaries() + 1);

		}
		GameSquare lastestLooped = squares.next();

		if (lastestLooped.getMilitaries() == 0 && lastestLooped.getIndex() != BOSS_1
				&& lastestLooped.getIndex() != BOSS_2) {
			GameSquare nextLoop = squares.next();
			if (nextLoop.isBossSquare()) {
				nextLoop.setBossSquare(false);

			}
			int nextMil = gameBoard.getMilitaryAt(nextLoop.getIndex());
			if (nextMil > 0) {
				curPlayer.miltaries += nextMil;
				gameBoard.removeMiltaryAt(nextLoop.getIndex());
				nextLoop = squares.next();
				while (nextLoop.getMilitaries() == 0) {
					nextLoop = squares.next();
					int n = gameBoard.getMilitaryAt(nextLoop.getIndex());
					if (n == 0) {
						break;
					}
					if (nextLoop.isBossSquare())
						nextLoop.setBossSquare(false);
					gameBoard.removeMiltaryAt(nextLoop.getIndex());
					curPlayer.miltaries += n;
					nextLoop = squares.next();
				}
			}
		}
		if (lastestLooped.getMilitaries() != 0) {
			move(lastestLooped.getIndex(), gameBoard.getLastestLoopedDirection(), curPlayer, gameBoard);
		}
	}

	@Override
	public String toString() {
		return gameBoard.toString();
	}

	public void setMaximizingTurn(boolean isMaximizing) {
		this.isMaximizing = isMaximizing;
	}

	public boolean isMaximizingTurn() {
		return this.isMaximizing;
	}

	public void changePlayer() {
		currentPlayer = p2;
		p2 = p1;
		p1 = currentPlayer;

	}

	public static ComputerDecisionResult getPath(Node cur) {
		System.out.println("On getPath : cur == null ? " + (cur == null));
		Node rs = null;
		while (cur.parent != null) {
			rs = cur;
			cur = cur.parent;
		}
		return new ComputerDecisionResult(rs.index, rs.d);

	}
}
