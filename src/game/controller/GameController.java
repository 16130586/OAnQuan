package game.controller;

import java.util.Iterator;

import javax.swing.JOptionPane;

import game.lib.ai.ComputerDecisionResult;
import game.model.Direction;
import game.model.GameSquare;
import game.model.GameState;
import game.model.IGameModel;
import game.model.Player;
import game.view.GameView;
import game.view.IView;

public class GameController implements IController {
	private IView view;
	private IGameModel gameModel;
	private Player curPlayer;

	public GameController() {

	}

	public GameController(IView v, IGameModel m) {
		this.view = v;
		this.gameModel = m;
	}

	@Override
	public GameState getGameState() {
		return gameModel.getGameState();
	}

	@Override
	public Player getCurPlayer() {
		return curPlayer;
	}

	@Override
	public void autoSearch() {
		ComputerDecisionResult rs = gameModel.autoSearch();
		move(rs.index, rs.dir, Player.PLAYER_2.isComputer() ? Player.PLAYER_2 : Player.PLAYER_1);
		System.out.println("Computer do move:" + rs.index + " "  + rs.dir);
	}

	@Override
	public boolean isHasComputer() {
		return curPlayer.isComputer();
	}

	@Override
	public boolean isValidMove(int c, int r) {
		return gameModel.isValidMove(c, r, curPlayer);
	}

	@Override
	public void move(int c, int r, Direction moveDirection, Player curPlayer) {
		if (r < 0 || r > 1 || c < 0 || c > 5)
			return;
		int firstIndex = c + (r * 5 + (r > 0 ? 1 : 0));
		System.out.println(firstIndex + " " + curPlayer.getName());
		move(firstIndex, moveDirection, curPlayer);
	}

	private void move(int index, Direction moveDirection, Player curPlayer) {
		final int BOSS_2 = 6, BOSS_1 = 0;
		if (index == BOSS_1 || index == BOSS_2)
			return;
		gameModel.setLoopDirection(moveDirection);
		gameModel.setIndexLoop(index);

		int mitalries = gameModel.getAndRemoveMilitaryAt(index);
		if (mitalries == 0)
			return;
		Iterator<GameSquare> squares = gameModel.iterator();
		while (mitalries-- > 0) {
			GameSquare curSquare = squares.next();
			curSquare.setMiltaries(curSquare.getMilitaries() + 1);
			view.updateView(true);

		}
		GameSquare lastestLoopedSquare = squares.next();
		// System.out.println("On gameModelApdater In Move : lastestLooped : " +
		// lastestLooped.getIndex() + " direction "
		// + gameModel.getLastestLoopedDirection().name());

		if (lastestLoopedSquare.getMilitaries() == 0
				&& lastestLoopedSquare.getIndex() != BOSS_1
				&& lastestLoopedSquare.getIndex() != BOSS_2) {
			GameSquare nextLoop = squares.next();

			int nextMil = gameModel.getMilitaryAt(nextLoop.getIndex());
			if (nextMil > 0) {
				if (nextLoop.isBossSquare()) {
					nextLoop.setBossSquare(false);
				}
				curPlayer.miltaries += nextMil;
				gameModel.removeMiltaryAt(nextLoop.getIndex());
				System.out.println("mutil getting reward at " + nextLoop.getIndex());
				nextLoop = squares.next();
				while (nextLoop.getMilitaries() == 0) {
					nextLoop = squares.next();
					int n = gameModel.getMilitaryAt(nextLoop.getIndex());
					if (n == 0) {
						break;
					}
					if (nextLoop.isBossSquare())
						nextLoop.setBossSquare(false);
					gameModel.removeMiltaryAt(nextLoop.getIndex());
					curPlayer.miltaries += n;
					view.updateView(true);
					nextLoop = squares.next();
				}
			}
		}
		if (lastestLoopedSquare.getMilitaries() != 0) {
			move(lastestLoopedSquare.getIndex(), gameModel.getLastestLoopedDirection(), curPlayer);
		}
	}

	@Override
	public boolean isOver() {
		return gameModel.isEndGame();
	}

	@Override
	public void reStart() {
		gameModel.reAssign();
	}

	@Override
	public boolean canMoveAt(int c, int r) {
		int index = c + (r * 5 + (r > 0 ? 1 : 0));
		return gameModel.getMilitaryAt(index) > 0;
	}

	@Override
	public int getMilitary(Player p) {
		return p.miltaries;
	}

	@Override
	public GameSquare[] getGameSquares() {
		return gameModel.getGameSquares();
	}

	@Override
	public void changesOppositePlayer() {
		processGameState();
		if (!checkRentingMitaries()) {
			view.toMessage(getOppositePlayer().getName() + " Won!");
			view.updateView(false);
			showReplay();
		}
		curPlayer = getOppositePlayer();
		if (!checkRentingMitaries()) {
			view.toMessage(getOppositePlayer().getName() + " Won!");
			view.updateView(false);
			showReplay();
		}
	}

	private boolean checkRentingMitaries() {
		if (!gameModel.stillHasOnBoardMilitary(curPlayer)) {
			if (curPlayer.miltaries < 5)
				return false;
			System.out.println(curPlayer.getName() + " rentting");
			gameModel.outMilitaries(curPlayer);
		}
		return true;
	}

	private Player getOppositePlayer() {
		if (this.curPlayer == Player.PLAYER_1)
			return Player.PLAYER_2;
		else
			return Player.PLAYER_1;
	}

	@Override
	public void processGameState() {
		{
			GameState s = getGameState();
			switch (s) {
			case DRAW:
				view.toMessage("DRAWING GAME!");
				break;
			case PLAYER1_WIN:
				view.toMessage(Player.PLAYER_1.getName() + " Won!");
				break;
			case PLAYER2_WIN:
				view.toMessage(Player.PLAYER_2.getName() + " Won!");
				break;

			default:
				return;
			}
			showReplay();
		}
	}

	private void showReplay() {
		String[] options = new String[] { "Yes", "No" };
		int response = JOptionPane.showOptionDialog(null, "Do you want to replay?", "", JOptionPane.DEFAULT_OPTION,
				JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
		if (response == 0) {
			reStart();
		} else {
			System.exit(0);
		}
	}

	@Override
	public void setModel(IGameModel model) {
		this.gameModel = model;
	}

	@Override
	public void setCurView(GameView view) {
		this.view = view;
	}

	@Override
	public void runPlayersOptionalConfigurationGame() {
		String[] options = new String[] { "Yes", "No" };

		final int YES_OPTION = 0;
		final int NO_OPTION = 1;
		int wannaPlayFirst = JOptionPane.showOptionDialog(null, "Do you want to be first player?", "",
				JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);

		// the person is default player 1
		this.curPlayer = Player.PLAYER_1;

		int wannaHaveComputer = JOptionPane.showOptionDialog(null, "Do you want to play with computer?", "",
				JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);

		if (wannaHaveComputer == YES_OPTION) {
			getOppositePlayer().setComputer(true);
			System.out.println("oppositePlayer " + getOppositePlayer().getName());
			gameModel.setGameLevel(getInputGameLevel());
			curPlayer.setName("You");
			getOppositePlayer().setName("Computer");
			if (wannaPlayFirst == NO_OPTION) {
				Thread s = new Thread(new Runnable() {

					@Override
					public void run() {
						autoSearch();
						view.updateView(false);
					}
				});
				s.start();
			}
		}
	}

	private int getInputGameLevel() {
		String[] options = new String[] { "Easy", "Medium", "Hard", "Super Hard" };
		final int EASY_OP = 0, MEDIUM_OP = 1, HARD_OP = 2, SU_HAR_OP = 3;

		int inputLevel = JOptionPane.showOptionDialog(null, "Please choice your enemy!", "", JOptionPane.DEFAULT_OPTION,
				JOptionPane.PLAIN_MESSAGE, null, options, options[0]);

		int level = -1;
		if (inputLevel == EASY_OP)
			level = 1;
		if (inputLevel == MEDIUM_OP)
			level = 3;
		if (inputLevel == HARD_OP)
			level = 5;
		if (inputLevel == SU_HAR_OP)
			level = 7;
		return level;
	}

}
