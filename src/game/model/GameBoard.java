package game.model;

import java.util.Iterator;

import game.lib.ai.AlphaBetaPruningComputer;
import game.lib.ai.AutoSearching;
import game.lib.ai.ComputerDecisionResult;
import game.lib.ai.MiniMaxComputer;

public class GameBoard implements IGameModel {
	private GameSquare[] gameBoard;
	private int index = 0;
	private Direction loopDirection;
	private LinkNode[] linkedNodeSquare;
	private LinkNode lastestLooped = null;
	private Direction lastestDirectionLooped = null;
	private AutoSearching computer;
	private int level = 1;

	public GameBoard() {
		linkedNodeSquare = new LinkNode[12];
		initGameSquares();
	}

	private void initGameSquares() {
		GameSquare BOSS_1 = new GameSquare(0, Player.PLAYER_2, 10, true);
		GameSquare M1_1 = new GameSquare(1, Player.PLAYER_2, 5, false);
		GameSquare M2_1 = new GameSquare(2, Player.PLAYER_2, 5, false);
		GameSquare M3_1 = new GameSquare(3, Player.PLAYER_2, 5, false);
		GameSquare M4_1 = new GameSquare(4, Player.PLAYER_2, 5, false);
		GameSquare M5_1 = new GameSquare(5, Player.PLAYER_2, 5, false);

		GameSquare BOSS_2 = new GameSquare(6, Player.PLAYER_1, 10, true);
		GameSquare M1_2 = new GameSquare(7, Player.PLAYER_1, 5, false);
		GameSquare M2_2 = new GameSquare(8, Player.PLAYER_1, 5, false);
		GameSquare M3_2 = new GameSquare(9, Player.PLAYER_1, 5, false);
		GameSquare M4_2 = new GameSquare(10, Player.PLAYER_1, 5, false);
		GameSquare M5_2 = new GameSquare(11, Player.PLAYER_1, 5, false);

		gameBoard = new GameSquare[12];
		gameBoard[0] = BOSS_1;
		gameBoard[1] = M1_1;
		gameBoard[2] = M2_1;
		gameBoard[3] = M3_1;
		gameBoard[4] = M4_1;
		gameBoard[5] = M5_1;
		gameBoard[6] = BOSS_2;
		gameBoard[7] = M5_2;
		gameBoard[8] = M4_2;
		gameBoard[9] = M3_2;
		gameBoard[10] = M2_2;
		gameBoard[11] = M1_2;
		initLinkedGameBoard();
	}

	private void initLinkedGameBoard() {

		linkedNodeSquare[0] = new LinkNode(gameBoard[0]); // boss1
		linkedNodeSquare[1] = new LinkNode(gameBoard[1]); // m1-1
		linkedNodeSquare[2] = new LinkNode(gameBoard[2]); // m2-1
		linkedNodeSquare[3] = new LinkNode(gameBoard[3]); // m3-1
		linkedNodeSquare[4] = new LinkNode(gameBoard[4]); // m4-1
		linkedNodeSquare[5] = new LinkNode(gameBoard[5]); // m5-1
		linkedNodeSquare[6] = new LinkNode(gameBoard[6]); // boss2
		linkedNodeSquare[7] = new LinkNode(gameBoard[11]); // m1-2
		linkedNodeSquare[8] = new LinkNode(gameBoard[10]); // m2-2
		linkedNodeSquare[9] = new LinkNode(gameBoard[9]); // m3-2
		linkedNodeSquare[10] = new LinkNode(gameBoard[8]);// m4-2
		linkedNodeSquare[11] = new LinkNode(gameBoard[7]);// m5-2

		linkedNodeSquare[0].fw = linkedNodeSquare[1];
		linkedNodeSquare[1].fw = linkedNodeSquare[2];
		linkedNodeSquare[2].fw = linkedNodeSquare[3];
		linkedNodeSquare[3].fw = linkedNodeSquare[4];
		linkedNodeSquare[4].fw = linkedNodeSquare[5];
		linkedNodeSquare[5].fw = linkedNodeSquare[6];
		linkedNodeSquare[6].fw = linkedNodeSquare[11];
		linkedNodeSquare[11].fw = linkedNodeSquare[10];
		linkedNodeSquare[10].fw = linkedNodeSquare[9];
		linkedNodeSquare[9].fw = linkedNodeSquare[8];
		linkedNodeSquare[8].fw = linkedNodeSquare[7];
		linkedNodeSquare[7].fw = linkedNodeSquare[0];

		linkedNodeSquare[0].bw = linkedNodeSquare[7];
		linkedNodeSquare[7].bw = linkedNodeSquare[8];
		linkedNodeSquare[8].bw = linkedNodeSquare[9];
		linkedNodeSquare[9].bw = linkedNodeSquare[10];
		linkedNodeSquare[10].bw = linkedNodeSquare[11];
		linkedNodeSquare[11].bw = linkedNodeSquare[6];
		linkedNodeSquare[6].bw = linkedNodeSquare[5];
		linkedNodeSquare[5].bw = linkedNodeSquare[4];
		linkedNodeSquare[4].bw = linkedNodeSquare[3];
		linkedNodeSquare[3].bw = linkedNodeSquare[2];
		linkedNodeSquare[2].bw = linkedNodeSquare[1];
		linkedNodeSquare[1].bw = linkedNodeSquare[0];

	}

	@Override
	public Player whoWin(Player player1, Player player2) {
		int miltariesP1 = getAllMilitaryOfPlayer(player1);
		int miltariesP2 = getAllMilitaryOfPlayer(player2);
		return (miltariesP1 > miltariesP2 ? player1 : player2);
	}

	@Override
	public boolean stillHasOnBoardMilitary(Player player) {
		int miltaries = 0;
		for (GameSquare square : gameBoard) {
			// make sure this square isn't boss
			if (square.getIndex() != 0 && square.getIndex() != 6 && square.getPlayer() == player)
				miltaries += square.getMilitaries();
		}
		return miltaries > 0;
	}

	@Override
	public boolean isEndGame() {
		return (linkedNodeSquare[0].square.getMilitaries() + linkedNodeSquare[6].square.getMilitaries()) == 0;
	}

	@Override
	public GameState getGameState() {
		if (!isEndGame()) {
			return GameState.NORMAL;
		}
		if (isDraw())
			return GameState.DRAW;

		return (whoWin(Player.PLAYER_1, Player.PLAYER_2) == Player.PLAYER_1 ? GameState.PLAYER1_WIN
				: GameState.PLAYER2_WIN);
	}

	private int getAllMilitaryOfPlayer(Player p) {
		int totalMiltariesP = 0;
		for (GameSquare square : gameBoard) {
			// make sure this square isn't boss
			if (square.getIndex() != 0 && square.getIndex() != 6 && square.getPlayer() == p)
				totalMiltariesP += square.getMilitaries();
		}
		// includes reward military
		totalMiltariesP += p.miltaries;
		return totalMiltariesP;
	}

	private boolean isDraw() {
		int totalMiltariesP1 = getAllMilitaryOfPlayer(Player.PLAYER_1);
		int totalMiltariesP2 = getAllMilitaryOfPlayer(Player.PLAYER_2);
		return totalMiltariesP1 == totalMiltariesP2;
	}

	@Override
	public void getRewardInSquare(Player player, int index) {
		player.miltaries += linkedNodeSquare[index].square.getMilitaries();
		linkedNodeSquare[index].square.setMiltaries(0);
	}

	@Override
	public int getMilitaryAt(int index) {
		return linkedNodeSquare[index].square.getMilitaries();
	}

	@Override
	public void removeMiltaryAt(int index) {
		if (index < 0 || index >= gameBoard.length)
			return;
		linkedNodeSquare[index].square.setMiltaries(0);
	}

	@Override
	public GameSquare getLastestLoopedSquare() {
		int lastIndex = lastestLooped.square.getIndex();
		if (lastIndex > gameBoard[0].getIndex() && lastIndex < gameBoard[6].getIndex()) {
			if (getLastestLoopedDirection() == Direction.LEFT) {
				return lastestLooped.bw.square;
			} else
				return lastestLooped.fw.square;

		} else {
			if (getLastestLoopedDirection() == Direction.LEFT) {
				return lastestLooped.fw.square;
			} else
				return lastestLooped.bw.square;

		}
	}

	@Override
	public void addMiltaries(int index, int numberMiltary) {
		linkedNodeSquare[index].square.setMiltaries(gameBoard[index].getMilitaries() + numberMiltary);
	}

	@Override
	public Direction getLastestLoopedDirection() {
		return lastestDirectionLooped;
	}

	@Override
	public void setLoopDirection(Direction direction) {
		// System.out.println("first set direction loop " + direction);
		this.loopDirection = direction;
		this.lastestDirectionLooped = direction;
	}

	@Override
	public Iterator<GameSquare> iterator() {
		return new Iterator<GameSquare>() {

			@Override
			public boolean hasNext() {
				return true;
			}

			@Override
			public GameSquare next() {
				GameSquare ret = null;
				if (lastestLooped == null) {
					lastestLooped = linkedNodeSquare[index];
				}
				if (index > 0 && index < 6) {
					if (loopDirection == Direction.LEFT) {
						ret = lastestLooped.bw.square;
						lastestLooped = lastestLooped.bw;
					}
					if (loopDirection == Direction.RIGHT) {
						ret = lastestLooped.fw.square;
						lastestLooped = lastestLooped.fw;
					}
				} else {
					if (loopDirection == Direction.LEFT) {
						ret = lastestLooped.fw.square;
						lastestLooped = lastestLooped.fw;
					}
					if (loopDirection == Direction.RIGHT) {
						ret = lastestLooped.bw.square;
						lastestLooped = lastestLooped.bw;

					}
				}
				if (lastestLooped.square.getIndex() == 0 || lastestLooped.square.getIndex() == 6) {
					// System.out.println("before " + lastestDirectionLooped + " after "
					// + lastestDirectionLooped.getOppositeDirection());
					lastestDirectionLooped = lastestDirectionLooped.getOppositeDirection();
				}

				return ret;
			}

		};

	}

	@Override
	public void setIndexLoop(int index) {
		lastestLooped = null;
		this.index = index;
		// System.out.println("set loop indexAt " + index);
	}

	class LinkNode {
		LinkNode fw;
		LinkNode bw;
		GameSquare square;

		LinkNode(GameSquare s) {
			this.square = s;
		}
	}

	@Override
	public GameSquare[] getGameSquares() {
		return gameBoard;
	}

	@Override
	public boolean isValidMove(int c, int r, Player curPlayer) {
		if (r == 0 && curPlayer == Player.PLAYER_2)
			return true;
		if (r == 1 && curPlayer == Player.PLAYER_1)
			return true;
		return false;
	}

	@Override
	public void reAssign() {
		initGameSquares();
		Player.PLAYER_1.miltaries = 0;
		Player.PLAYER_2.miltaries = 0;
	}

	public GameBoard cpy() {
		GameBoard res = new GameBoard();
		res.gameBoard[0] = new GameSquare(gameBoard[0]);
		res.gameBoard[1] = new GameSquare(gameBoard[1]);
		res.gameBoard[2] = new GameSquare(gameBoard[2]);
		res.gameBoard[3] = new GameSquare(gameBoard[3]);
		res.gameBoard[4] = new GameSquare(gameBoard[4]);
		res.gameBoard[5] = new GameSquare(gameBoard[5]);
		res.gameBoard[6] = new GameSquare(gameBoard[6]);
		res.gameBoard[7] = new GameSquare(gameBoard[7]);
		res.gameBoard[8] = new GameSquare(gameBoard[8]);
		res.gameBoard[9] = new GameSquare(gameBoard[9]);
		res.gameBoard[10] = new GameSquare(gameBoard[10]);
		res.gameBoard[11] = new GameSquare(gameBoard[11]);
		res.initLinkedGameBoard();
		res.index = this.index;
		return res;
	}

	@Override
	public String toString() {
		String s = "";
		for (GameSquare sq : gameBoard) {
			s += sq.toString() + "|";
		}
		return s;
	}

	@Override
	public ComputerDecisionResult autoSearch() {
		ComputerDecisionResult rs = null;
		if (level <= 3)
			if (computer == null)
				computer = new MiniMaxComputer();
		if (level > 3)
			if (computer == null || computer instanceof MiniMaxComputer)
				computer = new AlphaBetaPruningComputer();

		rs = computer.doSearch(this, Player.PLAYER_1.isComputer() ? Player.PLAYER_1 : Player.PLAYER_2,
				Player.PLAYER_1.isComputer() ? Player.PLAYER_2 : Player.PLAYER_1, level);
		return rs;
	}

	@Override
	public void setGameLevel(int inputGameLevel) {
		this.level = inputGameLevel;
	}

	@Override
	public void outMilitaries(Player curPlayer) {
		curPlayer.miltaries -= 5;
		if (curPlayer == Player.PLAYER_1) {
			for (int i = 7; i < 7 + 5; i++) {
				addMiltaries(i, 1);
			}
		}

		if (curPlayer == Player.PLAYER_2) {
			for (int i = 1; i < 1 + 5; i++) {
				addMiltaries(i, 1);
			}
		}
	}
}
