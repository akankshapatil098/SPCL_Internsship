import java.util.Scanner;
import java.util.Random;

public class NumberGuessingGame {

    private static final int MIN_RANGE = 1;
    private static final int MAX_RANGE = 100;
    private static  final int MAX_ATTEMPTS = 10;
    private static final int MAX_ROUNDS = 3;

    public static void main(String[] args) {
        Random random = new Random();
        Scanner sc = new Scanner(System.in);
        int totalScore =0;
        System.out.println("***********************************");
        System.out.println("------NUMBER GUESSING GAME------");
        System.out.println("***********************************");
        System.out.println("\n \u001B[4mGame Rules:\u001B[0m");
        System.out.println("Total number of rounds : 3");
        System.out.println("Attempt to guess number in each round:10\n");
        for(int i = 1; i <= MAX_ROUNDS; i++){
            int number=random.nextInt(MAX_RANGE) + MIN_RANGE;
            int attempts = 0;

            System.out.printf("Round %d: Guess the number between %d and %d in %d attempts. \n ",i,MIN_RANGE,MAX_RANGE,MAX_ATTEMPTS);
            while( attempts < MAX_ATTEMPTS)
            {
                System.out.println("Enter your guess:");
                int guess_number = sc.nextInt();
                attempts = attempts + 1;

                if(guess_number == number){
                    int score = MAX_ATTEMPTS - attempts;
                    totalScore = totalScore + score;
                    System.out.println("***********************************");
                    System.out.println("Congratulations, You won the game!");
                    System.out.printf("Hurray! Number Guessed Successfully. \n Attempts =%d \t Round Score =%d \n", attempts,score);
                    System.out.println("***********************************");

                    break;
                }
                else if(guess_number < number)
                {
                    System.out.printf("The number is greater than %d. Attenmpt left =%d. \n",guess_number,MAX_ATTEMPTS-attempts);

                }
            }
            if(attempts == MAX_ATTEMPTS){
                System.out.printf("\n Round = %d \n",i);
                System.out.println("Attempts =0");
                System.out.printf("The Random Number is : %d \n\n",number);
            }
        }
        System.out.printf("--------GAME OVER--------- \n Total Score = %d\n",totalScore);
        sc.close();
    }

}
