/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uno;

import java.awt.Font;
import java.util.*;
import javax.swing.*;

/**
 *
 * @author Hemant Soni
 */
public class Game {

    private int currentPlayer;
    private String[] playerIds;

    private UnoDeck deck;
    private ArrayList<ArrayList<UnoCard>> playerHand;
    private ArrayList<UnoCard> stockpile;
    private UnoCard.Color validColor;
    private UnoCard.Value validValue;

    boolean gameDirection;

    public Game(String[] pids) {
        deck = new UnoDeck();
        deck.shuffle();
        stockpile = new ArrayList<>();
        playerIds = pids;
        currentPlayer = 0;
        gameDirection = false;

        playerHand = new ArrayList<>();

        for(int i = 0; i < pids.length; i++) {
            ArrayList<UnoCard> hand = new ArrayList<>(Arrays.asList(deck.drawCard(7)));
            playerHand.add(hand);
        }
    }

    public void start(Game game) {
        UnoCard card = deck.drawCard();
        validColor = card.getColor();
        validValue = card.getValue();

        if (card.getValue() == UnoCard.Value.Wild) {
            start(game);
        }
        if (card.getValue() == UnoCard.Value.Wild_Four || card.getValue() == UnoCard.Value.DrawTwo) {
            start(game);
        }
        if (card.getValue() == UnoCard.Value.Skip) {
            JLabel message = new JLabel(playerIds[currentPlayer] + " is skipped !");
            message.setFont(new Font("Arial", Font.BOLD, 48));
            JOptionPane.showMessageDialog(null, message);

            if (gameDirection == false) {
                currentPlayer = (currentPlayer + 1) % playerIds.length;
            } else if (gameDirection == true) {
                currentPlayer = (currentPlayer - 1) % playerIds.length;
                if (currentPlayer == -1) {
                    currentPlayer = playerIds.length - 1;
                }
            }
        }
        if (card.getValue() == UnoCard.Value.Reverse) {
            JLabel message = new JLabel(playerIds[currentPlayer] + " Game Direction is changed !");
            message.setFont(new Font("Arial", Font.BOLD, 48));
            JOptionPane.showMessageDialog(null, message);
            gameDirection ^= true;
            currentPlayer = playerIds.length - 1;
        }

        stockpile.add(card);
    }

    public UnoCard getTopCard() {
        return new UnoCard(validColor, validValue);
    }

    public ImageIcon getTopCardImage() {
        return new ImageIcon(validColor + "__" + validValue + ".png");
    }

    public boolean isGameOver() {
        for (String player : this.playerIds) {
            if (hasEmptyHand(player)) {
                return true;
            }
        }
        return false;
    }

    public String getCurrentPlayer() {
        return this.playerIds[this.currentPlayer];
    }

    public String getPreviousPlayer(int i) {
        int index = this.currentPlayer - i;
        if (index == -1) {
            index = playerIds.length - 1;
        }
        return this.playerIds[index];
    }

    public String[] getPlayer() {
        return playerIds;
    }

    public ArrayList<UnoCard> getPlayerHand(String pid) {
        int index = Arrays.asList(playerIds).indexOf(pid);
        return playerHand.get(index);
    }

    public int getPlayerHandSize(String pid) {
        return getPlayerHand(pid).size();
    }

    public UnoCard getPlayerCard(String pid, int choice) {
        ArrayList<UnoCard> hand = getPlayerHand(pid);
        return hand.get(choice);
    }

    public boolean hasEmptyHand(String pid) {
        return getPlayerHand(pid).isEmpty();
    }

    public boolean validCardPlay(UnoCard card) {
        return card.getColor() == validColor || card.getValue() == validValue;
    }

    public void checkPlayerTurn(String pid) throws InvalidPlayerTurnException {
        if (this.playerIds[this.currentPlayer] == null ? pid != null : !this.playerIds[this.currentPlayer].equals(pid)) {
            throw new InvalidPlayerTurnException("It is not " + pid + "'s turn", pid);
        }
    }

    public void submitDraw(String pid) throws InvalidPlayerTurnException {
        checkPlayerTurn(pid);
        if (deck.isEmpty()) {
            deck.replaceDeckWith(stockpile);
            deck.shuffle();
        }
        getPlayerHand(pid).add(deck.drawCard());
        if (gameDirection == false) {
            currentPlayer = (currentPlayer + 1) % playerIds.length;
        } else if (gameDirection == true) {
            currentPlayer = (currentPlayer - 1) % playerIds.length;
            if (currentPlayer == -1) {
                currentPlayer = playerIds.length - 1;
            }
        }
    }

    public void setCardColor(UnoCard.Color color) {
        validColor = color;
    }

    public void submitPlayerCard(String pid, UnoCard card, UnoCard.Color declaredColor)
            throws InvalidColorSubmissionException, InvalidValueSubmissionException, InvalidPlayerTurnException {
        checkPlayerTurn(pid);
        ArrayList<UnoCard> pHand = getPlayerHand(pid);

        if (!validCardPlay(card)) {
            if (card.getColor() == UnoCard.Color.Wild) {
                validColor = card.getColor();
                validValue = card.getValue();
            }

            if (card.getColor() != validColor) {
                JLabel message = new JLabel("Invalid Player move, expected color is " + validColor);
                message.setFont(new Font("Arial", Font.BOLD, 48));
                JOptionPane.showMessageDialog(null, message);
                throw new InvalidColorSubmissionException("Invalid Player move, expected color is " + validColor, card.getColor(), validColor);
            } else if (card.getValue() != validValue) {
                JLabel message = new JLabel("Invalid Player move, expected value is " + validValue);
                message.setFont(new Font("Arial", Font.BOLD, 48));
                JOptionPane.showMessageDialog(null, message);
                throw new InvalidValueSubmissionException("Invalid Player move, expected value is " + validValue, card.getValue(),validValue);
            }

        }

        pHand.remove(card);
        if (hasEmptyHand(this.playerIds[currentPlayer])) {
            JLabel message = new JLabel(this.playerIds[currentPlayer] + " is Winner !!");
            message.setFont(new Font("Arial", Font.BOLD, 48));
            JOptionPane.showMessageDialog(null, message);
            System.exit(0);
        }
        validColor = card.getColor();
        validValue = card.getValue();
        stockpile.add(card);

        if (gameDirection == false) {
            currentPlayer = (currentPlayer + 1) % playerIds.length;
        } else if (gameDirection == true) {
            currentPlayer = (currentPlayer - 1) % playerIds.length;
            if (currentPlayer == -1) {
                currentPlayer = playerIds.length - 1;
            }
        }
        if (card.getColor() == UnoCard.Color.Wild) {
            validColor = declaredColor;
        }
        if (card.getValue() == UnoCard.Value.DrawTwo) {
            pid = playerIds[currentPlayer];
            getPlayerHand(pid).add(deck.drawCard());
            getPlayerHand(pid).add(deck.drawCard());
            JLabel message = new JLabel(pid + " got 2 cards !!");
            message.setFont(new Font("Arial",Font.BOLD,48));
            JOptionPane.showMessageDialog(null,message);
        }
        if (card.getValue() == UnoCard.Value.Wild_Four) {
            pid = playerIds[currentPlayer];
            getPlayerHand(pid).add(deck.drawCard());
            getPlayerHand(pid).add(deck.drawCard());
            getPlayerHand(pid).add(deck.drawCard());
            getPlayerHand(pid).add(deck.drawCard());
            JLabel message = new JLabel(pid + " got 4 cards!!");
            message.setFont(new Font("Arial",Font.BOLD,48));
            JOptionPane.showMessageDialog(null,message);
        }

        if (card.getValue() == UnoCard.Value.Skip) {
            JLabel message = new JLabel(playerIds[currentPlayer] + " was skipped !!");
            message.setFont(new Font("Arial", Font.BOLD, 48));
            JOptionPane.showMessageDialog(null, message);

            if (gameDirection == false) {
                currentPlayer = (currentPlayer + 1) % playerIds.length;
            } else if (gameDirection == true) {
                currentPlayer = (currentPlayer - 1) % playerIds.length;
                if (currentPlayer == -1) {
                    currentPlayer = playerIds.length - 1;
                }
            }

        }
//        if (card.getValue() == UnoCard.Value.Reverse) {
//            JLabel message = new JLabel(playerIds[currentPlayer] + " changed game direction !!");
//            message.setFont(new Font("Arial", Font.BOLD, 48));
//            JOptionPane.showMessageDialog(null, message);
//
//            gameDirection ^= true;
//            if (gameDirection == false) {
//                currentPlayer = (currentPlayer + 2) % playerIds.length;
//            } else if (gameDirection == true) {
//                currentPlayer = (currentPlayer - 2) % playerIds.length;
//                if (currentPlayer == -1) {
//                    currentPlayer = playerIds.length - 1;
//                }
//                if (currentPlayer == -2) {
//                    currentPlayer = playerIds.length - 2;
//                }
//            }
//        }

         if (card.getValue() == UnoCard.Value.Reverse) {
            JLabel message = new JLabel(pid+ " changed game direction !!");
            message.setFont(new Font("Arial", Font.BOLD, 48));
            JOptionPane.showMessageDialog(null, message);

            gameDirection ^= true;
            if (gameDirection == false) {
                currentPlayer = (currentPlayer + 2) % playerIds.length;
            } else if (gameDirection == true) {
                currentPlayer = (currentPlayer - 2) % playerIds.length;
                if (currentPlayer == -1) {
                    currentPlayer = playerIds.length - 1;
                }
                if (currentPlayer == -2) {
                    currentPlayer = playerIds.length - 2;
                }
            }
        }
        
    }

//)
}

//}
class InvalidPlayerTurnException extends Exception {

    String playerId;

    public InvalidPlayerTurnException(String message, String pid) {
        super(message);
        playerId = pid;
    }

    public String getPid() {
        return playerId;
    }
}

class InvalidColorSubmissionException extends Exception {

    private UnoCard.Color expected;
    private UnoCard.Color actual;

    public InvalidColorSubmissionException(String message, UnoCard.Color actual, UnoCard.Color expected) {
        this.actual = actual;
        this.expected = expected;
    }
}

class InvalidValueSubmissionException extends Exception {

    private UnoCard.Value expected;
    private UnoCard.Value actual;

    public InvalidValueSubmissionException(String message, UnoCard.Value actual, UnoCard.Value expected) {
        this.actual = actual;
        this.expected = expected;
    }
}
