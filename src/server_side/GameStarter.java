package server_side;

import java.util.InputMismatchException;
import java.util.Scanner;

/**
 * belongs to 'mafia game'
 * this class is written for initializing and giving the rest to God class
 *
 * @author Sepehr Noey
 * @version 1.0
 */
public class GameStarter {
    public static void main(String[] args){
        God god = new God();

        System.out.println("Welcome to 'mafia game' !\nThis game is on a local network and number of players can be between 5 to 10: ");
        Scanner scanner = new Scanner(System.in);
        int num = 0;
        while (true) {
            try {
                System.out.println("Before continue , please enter the number of players:(between 5 to 10) ");
                num = scanner.nextInt();
                if (num < 5 || num > 10)


                break;
            } catch (InputMismatchException e) {

            }
        }
    }
}
