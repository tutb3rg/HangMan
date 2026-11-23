import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class Main {
    private static final Random random = new Random();

    public static void main(String[] args) {
      Path dictionaryPath = Path.of("src/words.txt");
      String secretWord;

      try {
          secretWord = pickRandomWord(dictionaryPath);
      } catch (IOException e){
          System.out.println("Не удалось прочитать словарь из файла: " + dictionaryPath);
          return;
      }
        char[] guessedWord = new char[secretWord.length()];

        Arrays.fill(guessedWord, '*');
        int maxMistakes = 6;
        int currentMistakes = 0;
        System.out.println("Добро пожаловать в гэмбл");
        System.out.println("Слово: ");
        System.out.println(guessedWord);


        Scanner Scaner = new Scanner(System.in);
        while (currentMistakes < maxMistakes) {
            System.out.println("\nВведите букву: ");


            String input = Scaner.nextLine().toUpperCase();
            char letter = input.charAt(0);

            boolean isHit = false;
            for (int i = 0; i < secretWord.length(); i++) {
                if (secretWord.charAt(i) == letter) {
                    guessedWord[i] = letter;
                    isHit = true;

                }
            }
            if (!isHit) {
                currentMistakes++;
                System.out.println("Ты не попал! Ошибок: " + currentMistakes + "из" + maxMistakes);
            } else {
                System.out.println("Попал! Такая буква есть!");
            }
            System.out.println("Слово: ");
            System.out.println(guessedWord);

            boolean isWin = true;
            for (char c : guessedWord) {
                if (c == '*') {
                    isWin = false;
                    break;
                }
            }

                if (isWin) {
                    System.out.println("\nПоздравляю ты выиграл!");
                    System.out.println("Загаданное слово: " + secretWord);
                    break;
                }

        }


        if (currentMistakes == maxMistakes){
            System.out.println("\nТы проиграл! У тебя не осталось жизней. Загаданное слово: " + secretWord);
        }
        Scaner.close();


    }
    private static String pickRandomWord(Path path) throws IOException {
        List<String> words = Files.readAllLines(path, StandardCharsets.UTF_8).stream()
                .map(String::trim)
                .filter(word -> !word.isEmpty())
                .toList();
        if (words.isEmpty()){
            throw new IOException("Файл пуст: "+ path);

        }
        int randomIndex = random.nextInt(words.size());
        return words.get(randomIndex).toUpperCase();
    }


}